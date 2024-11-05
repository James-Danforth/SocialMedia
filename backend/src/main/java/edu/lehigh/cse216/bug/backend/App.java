package edu.lehigh.cse216.bug.backend;
import spark.Spark;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Map;

import com.google.gson.*;

public class App {
    private static final String DEFAULT_PORT_DB = "5432";
    private static final int DEFAULT_PORT_SPARK = 4567;   
    
    /**
     * Set up CORS headers for the OPTIONS verb, and for every response that the
     * server sends.  This only needs to be called once.
     * 
     * @param origin The server that is allowed to send requests to this server
     * @param methods The allowed HTTP verbs from the above origin
     * @param headers The headers that can be sent with a request from the above
     *                origin
     */
    private static void enableCORS(String origin, String methods, String headers) {
        // Create an OPTIONS route that reports the allowed CORS headers and methods
        Spark.options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }
            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return "OK";
        });

        // 'before' is a decorator, which will run before any 
        // get/post/put/delete.  In our case, it will put three extra CORS
        // headers into the response
        Spark.before((request, response) -> {
            response.header("Access-Control-Allow-Origin", origin);
            response.header("Access-Control-Request-Method", methods);
            response.header("Access-Control-Allow-Headers", headers);
        });
    }

    /**
    * Get an integer environment variable if it exists, and otherwise return the
    * default value.
    * 
    * @envar      The name of the environment variable to get.
    * @defaultVal The integer value to use as the default if envar isn't found
    * 
    * @returns The best answer we could come up with for a value for envar
    */
    static int getIntFromEnv(String envar, int defaultVal) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get(envar) != null) {
            return Integer.parseInt(processBuilder.environment().get(envar));
        }
        return defaultVal;
    }

    /**
    * Get a fully-configured connection to the database, or exit immediately
    * Uses the Postgres configuration from environment variables
    * 
    * NB: now when we shutdown the server, we no longer lose all data
    * 
    * @return null on failure, otherwise configured database object
    */
    private static Database getDatabaseConnection(){
        if( System.getenv("DATABASE_URL") != null ){
            return Database.getDatabase(System.getenv("DATABASE_URL"), DEFAULT_PORT_DB);
        }

        Map<String, String> env = System.getenv();
        String ip = env.get("POSTGRES_IP");
        String port = env.get("POSTGRES_PORT");
        String user = env.get("POSTGRES_USER");
        String pass = env.get("POSTGRES_PASS");
        return Database.getDatabase(ip, port, "", user, pass);
    } 
    
    public static void main( String[] args ){
        // Set the port on which to listen for requests from the environment
        Spark.port(getIntFromEnv("PORT", DEFAULT_PORT_SPARK));

        Database db = getDatabaseConnection();
    
        // Set up the location for serving static files
        String static_location_override = System.getenv("STATIC_LOCATION");
        if (static_location_override == null) {
            Spark.staticFileLocation("/web");
        } else {
            Spark.staticFiles.externalLocation(static_location_override);
        }

        // Set up a route for serving the main page
        Spark.get("/", (req, res) -> {
            res.redirect("/index_simple.html");
            return "";
        }); 

        if ("True".equalsIgnoreCase(System.getenv("CORS_ENABLED"))) {
            final String acceptCrossOriginRequestsFrom = "*";
            final String acceptedCrossOriginRoutes = "GET,PUT,POST,DELETE,OPTIONS";
            final String supportedRequestHeaders = "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin";
            enableCORS(acceptCrossOriginRequestsFrom, acceptedCrossOriginRoutes, supportedRequestHeaders);
        }
    
            
        final Gson gson = new Gson();
        //final DataStore dataStore = new DataStore();

        // GET route that returns all posts titles and their ID's.  All we do is get 
        // the data, embed it in a StructuredResponse, turn it into JSON, and 
        // return it.  If there's no data, we return "[]", so there's no need 
        // for error handling.
        Spark.get("/posts", (request, response) -> {
            response.status(200);
            response.type("application/json");
            return gson.toJson(new StructuredResponse("ok", null, db.selectAll()));
        });

        // GET route that returns everything for a single row in the DataStore.
        // The ":id" suffix in the first parameter to get() becomes 
        // request.params("id"), so that we can get the requested row ID.  If 
        // ":id" isn't a number, Spark will reply with a status 500 Internal
        // Server Error.  Otherwise, we have an integer, and the only possible 
        // error is that it doesn't correspond to a row with data.
        Spark.get("/posts/:id", (request, response) -> {
            int idx = Integer.parseInt(request.params("id"));
            response.status(200);
            response.type("application/json");
            DataRow data = db.selectOne(idx);
            if (data == null) {
                return gson.toJson(new StructuredResponse("error", idx + " not found", null));
            } else {
                return gson.toJson(new StructuredResponse("ok", null, data));
            }
        });

        // POST route for adding a new element to the DataStore.  This will read
        // JSON from the body of the request, turn it into a SimpleRequest 
        // object, extract the title and message, insert them, and return the 
        // ID of the newly created row.
        Spark.post("/posts", (request, response) -> {
            // Extract the token from the Authorization header
            String authorizationHeader = request.headers("Authorization");
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                response.status(401); // Unauthorized
                return gson.toJson(new StructuredResponse("error", "No authorization token provided", null));
            }
            String idTokenString = authorizationHeader.substring(7); // Remove "Bearer " prefix

            try {
                // Verify the ID token
                GoogleOAuthVerifier.verifyToken(idTokenString);
            } catch (GeneralSecurityException | IOException e) {
                System.err.println("Error verifying ID token: " + e.getMessage());
                response.status(403); // Forbidden
                return gson.toJson(new StructuredResponse("error", "Invalid ID token", null));
            }

            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            response.status(200);
            response.type("application/json");
            int newId = db.insertRow(req.mPost, req.mLikes, req.mDislikes);
            if (newId == -1) {
                return gson.toJson(new StructuredResponse("error", "error performing insertion", null));
            } else {
                return gson.toJson(new StructuredResponse("ok", "" + newId, null));
            }
        });

        // PUT route for updating a row in the DataStore.  This is almost exactly the same as POST
        Spark.put("/posts/:id", (request, response) -> {
            // Extract the token from the Authorization header
            String authorizationHeader = request.headers("Authorization");
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                response.status(401); // Unauthorized
                return gson.toJson(new StructuredResponse("error", "No authorization token provided", null));
            }
            String idTokenString = authorizationHeader.substring(7); // Remove "Bearer " prefix

            try {
                // Verify the ID token
                GoogleOAuthVerifier.verifyToken(idTokenString);
            } catch (GeneralSecurityException | IOException e) {
                System.err.println("Error verifying ID token: " + e.getMessage());
                response.status(403); // Forbidden
                return gson.toJson(new StructuredResponse("error", "Invalid ID token", null));
            }

            int idx = Integer.parseInt(request.params("id"));
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            response.status(200);
            response.type("application/json");
            int result = db.updateOne(idx, req.mPost, req.mLikes, req.mDislikes);
            if (result == -1) {
                return gson.toJson(new StructuredResponse("error", "unable to update row " + idx, null));
            } else {
                return gson.toJson(new StructuredResponse("ok", null, result));
            }
        });


        Spark.delete("/posts/:id", (request, response) -> {
            // Extract the token from the Authorization header
            String authorizationHeader = request.headers("Authorization");
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                response.status(401); // Unauthorized
                return gson.toJson(new StructuredResponse("error", "No authorization token provided", null));
            }
            String idTokenString = authorizationHeader.substring(7); // Remove "Bearer " prefix

            try {
                // Verify the ID token
                GoogleOAuthVerifier.verifyToken(idTokenString);
            } catch (GeneralSecurityException | IOException e) {
                System.err.println("Error verifying ID token: " + e.getMessage());
                response.status(403); // Forbidden
                return gson.toJson(new StructuredResponse("error", "Invalid ID token", null));
            }

            int idx = Integer.parseInt(request.params("id"));
            response.status(200);
            response.type("application/json");
            int result = db.deleteRow(idx);
            if (result == 0) {
                return gson.toJson(new StructuredResponse("error", "unable to delete row " + idx, null));
            } else {
                return gson.toJson(new StructuredResponse("ok", null, null));
            }
        });
        
        //retrieving all comments
        Spark.get("/comments", (request, response) -> {
            response.type("application/json");
            ArrayList<CommentRow> comments = db.selectAllComments();
            if (comments.isEmpty()) {
                response.status(404); // Not found
                return gson.toJson(new StructuredResponse("error", "No comments found", null));
            }
            return gson.toJson(new StructuredResponse("ok", null, comments));
        });

        //retrieving one single comments
        Spark.get("/comments/:id", (request, response) -> {
            int commentId = Integer.parseInt(request.params(":comment_id"));
            response.type("application/json");
            CommentRow comment = db.SelectOneComment(commentId); // Implement this method in the Database class
            if (comment == null) {
                response.status(404); // Not found
                return gson.toJson(new StructuredResponse("error", "Comment not found", null));
            }
            return gson.toJson(new StructuredResponse("ok", null, comment));
        });

        // POST route for adding a new comment to a post.
        Spark.post("/comments", (request, response) -> {
            // Extract the token from the Authorization header
            String authorizationHeader = request.headers("Authorization");
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                response.status(401); // Unauthorized
                return gson.toJson(new StructuredResponse("error", "No authorization token provided", null));
            }
            String idTokenString = authorizationHeader.substring(7); // Remove "Bearer " prefix

            try {
                // Verify the ID token
                GoogleOAuthVerifier.verifyToken(idTokenString);
            } catch (GeneralSecurityException | IOException e) {
                System.err.println("Error verifying ID token: " + e.getMessage());
                response.status(403); // Forbidden
                return gson.toJson(new StructuredResponse("error", "Invalid ID token", null));
            }

            // Parse the JSON body of the request into a CommentRequest object
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            response.status(200); // HTTP 200 OK
            response.type("application/json");
            int newCommentId = db.insertRow(req.mCommentID, req.mPostID, req.mUsername, req.mMessage);
            if (newCommentId == -1) {
                return gson.toJson(new StructuredResponse("error", "Error performing insertion", null));
            } else {
                return gson.toJson(new StructuredResponse("ok", "Comment added with ID: " + newCommentId, null));
            }
        });




    }

    
}