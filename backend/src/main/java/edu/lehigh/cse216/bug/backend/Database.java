package edu.lehigh.cse216.bug.backend;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;

public class Database {
    private Connection mConnection;
    private PreparedStatement mSelectAll;
    private PreparedStatement mSelectOne;
    private PreparedStatement mDeleteOne;
    private PreparedStatement mInsertOne;
    private PreparedStatement mUpdateOne;
    private PreparedStatement mCreateTable;
    private PreparedStatement mDropTable;   
    
    private PreparedStatement mInsertUser;
    private PreparedStatement mSelectAllUser;
    private PreparedStatement mSelectOneUser;
    private PreparedStatement mUpdateOneUser;
    private PreparedStatement mDeleteOneUser;

    private PreparedStatement mInsertComments;
    private PreparedStatement mSelectAllComments;
    private PreparedStatement mSelectOneComments;
    private PreparedStatement mUpdateOneComments;
    private PreparedStatement mDeleteOneComments;

    private PreparedStatement mInsertAuth;
    private PreparedStatement mSelectAllAuth;
    private PreparedStatement mSelectOneAuth;
    private PreparedStatement mUpdateOneAuth;
    private PreparedStatement mDeleteOneCAuth;


    private Database() {
    }

    /**
    * Get a fully-configured connection to the database
    * 
    * @param host The IP address or hostname of the database server
    * @param port The port on the database server to which connection requests
    *             should be sent
    * @param path The path to use, can be null
    * @param user The user ID to use when connecting
    * @param pass The password to use when connecting
    * 
    * @return A Database object, or null if we cannot connect properly
    */
    static Database getDatabase(String host, String port, String path, String user, String pass) {
        if( path==null || "".equals(path) ){
            path="/";
        }

        // Create an un-configured Database object
        Database db = new Database();

        // Give the Database object a connection, fail if we cannot get one
        try {
            String dbUrl = "jdbc:postgresql://" + host + ':' + port + path;
            Connection conn = DriverManager.getConnection(dbUrl, user, pass);
            if (conn == null) {
                System.err.println("Error: DriverManager.getConnection() returned a null object");
                return null;
            }
            db.mConnection = conn;
        } catch (SQLException e) {
            System.err.println("Error: DriverManager.getConnection() threw a SQLException");
            e.printStackTrace();
            return null;
        }

        db = db.createPreparedStatements(db);
        return db;
    } 

    /**
    * Get a fully-configured connection to the database
    * 
    * @param db_url The url to the database
    * @param port_default port to use if absent in db_url
    * 
    * @return A Database object, or null if we cannot connect properly
    */
    static Database getDatabase(String db_url, String port_default) {
        try {
            URI dbUri = new URI(db_url);
            String username = dbUri.getUserInfo().split(":")[0];
            String password = dbUri.getUserInfo().split(":")[1];
            String host = dbUri.getHost();
            String path = dbUri.getPath();
            String port = dbUri.getPort() == -1 ? port_default : Integer.toString(dbUri.getPort());

            return getDatabase(host, port, path, username, password);
        } catch (URISyntaxException s) {
            System.out.println("URI Syntax Error");
            return null;
        }
    } 

    public Database createPreparedStatements(Database db){
        try {
            mCreateTable = db.mConnection.prepareStatement(
                    "CREATE TABLE tblData (id SERIAL PRIMARY KEY, likes int NOT NULL, dislikes int NOT NULL, message VARCHAR(500) NOT NULL)");
            mDropTable = db.mConnection.prepareStatement("DROP TABLE tblData");

            // Standard CRUD operations
            mInsertOne = db.mConnection.prepareStatement("INSERT INTO tblideas (post_id, content, likes, dislikes) VALUES (default, ?, ?, ?)");
            mSelectAll = db.mConnection.prepareStatement("SELECT * FROM tblideas");
            mSelectOne = db.mConnection.prepareStatement("SELECT * FROM tblideas WHERE id=?");
            mUpdateOne = db.mConnection.prepareStatement("UPDATE tblideas SET content = ?, likes = ?, dislikes = ? WHERE post_id = ?");
            mDeleteOne = db.mConnection.prepareStatement("DELETE FROM tblideas WHERE post_id = ?");

            //CRUD for user information
            mInsertUser = db.mConnection.prepareStatement("INSERT INTO tbluser (username, user_id, email, gender_identity, sexual_identity, note) VALUES (?, ?, ?, ?, ?, ?)");
            mSelectAllUser = db.mConnection.prepareStatement("SELECT * FROM tblData");
            mSelectOneUser = db.mConnection.prepareStatement("SELECT * FROM tbluser WHERE user_id=?");
            mUpdateOneUser = db.mConnection.prepareStatement("UPDATE tbluser SET username = ?, email = ?, gender_identity = ?, sexual_identity = ?, note = ? WHERE user_id = ?");
            mDeleteOneUser = db.mConnection.prepareStatement("DELETE FROM tbluser WHERE user_id = ?");

            //CRUD for authentication
            mInsertAuth = db.mConnection.prepareStatement("INSERT INTO tblauth (user_id, rskey) VALUES (?, ?)");
            mSelectAllAuth = db.mConnection.prepareStatement("SELECT * FROM tblauth");
            mSelectOneAuth = db.mConnection.prepareStatement("SELECT * FROM tblauth WHERE user_id = ?");
            mUpdateOneAuth = db.mConnection.prepareStatement("UPDATE tblauth SET rekey = ? WHERE user_id = ?");
            mDeleteOneCAuth = db.mConnection.prepareStatement("DELETE FROM tblauth WHERE user_id = ?");

            //CRUD for comments
            mInsertComments = db.mConnection.prepareStatement("INSERT INTO tblcomments (comment_id, post_id, username, comment) VALUES (default, ?, ?, ?)");
            mSelectAllComments = db.mConnection.prepareStatement("SELECT * FROM tblcomments");
            mSelectOneComments = db.mConnection.prepareStatement("SELECT * FROM tblcomments WHERE comment_id = ?");
            mUpdateOneComments = db.mConnection.prepareStatement("UPDATE tblcomments SET post_id = ?, username = ?, comment = ? WHERE comment_id = ?");
            mDeleteOneComments = db.mConnection.prepareStatement("DELETE FROM tblcomments WHERE comment_id = ?");


        } catch (SQLException e) {
            System.err.println("Error creating prepared statement");
            e.printStackTrace();
            db.disconnect();
            return null;
        }
        return db;
    }


    boolean disconnect() {
        if (mConnection == null) {
            System.err.println("Unable to close connection: Connection was null");
            return false;
        }
        try {
            mConnection.close();
        } catch (SQLException e) {
            System.err.println("Error: Connection.close() threw a SQLException");
            e.printStackTrace();
            mConnection = null;
            return false;
        }
        mConnection = null;
        return true;
    }

    //post and likes
    int insertRow(String content, int likes, int dislikes) {
        int count = 0;
        try {
            mInsertOne.setString(1, content);
            mInsertOne.setInt(2, likes);
            mInsertOne.setInt(3, dislikes);
            count += mInsertOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    //user information
    int insertRow(String username, String userID, String email, String GI, String SO, String note) {
        int count = 0;
        try {
            mInsertUser.setString(1, username);
            mInsertUser.setString(2, userID);
            mInsertUser.setString(3, email);
            mInsertUser.setString(4, GI);
            mInsertUser.setString(5, SO);
            mInsertUser.setString(6, note);
            count += mInsertUser.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    //user authentication
    int insertRow(String userID, String rskey){
        int count = 0;
        try {
            mInsertAuth.setString(1, userID);
            mInsertAuth.setString(2, rskey);
            count += mInsertAuth.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    //user comments
    int insertRow(int commentID, int postID, String username, String message){
        int count = 0;
        try {
            mInsertComments.setInt(1, commentID);
            mInsertComments.setInt(2, postID);
            mInsertComments.setString(3, username);
            mInsertComments.setString(4, message);
            count += mInsertAuth.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    //ideas and likes
    ArrayList<DataRow> selectAll() {
        ArrayList<DataRow> res = new ArrayList<DataRow>();
        try {
            ResultSet rs = mSelectAll.executeQuery();
            while (rs.next()) {
                res.add(new DataRow(rs.getInt("id"), rs.getInt("likes"), rs.getInt("dislikes"), null));
            }
            rs.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    //user information
    ArrayList<UserRow> selectAllUser() {
        ArrayList<UserRow> res = new ArrayList<UserRow>();
        try {
            ResultSet rs = mSelectAllUser.executeQuery();
            while (rs.next()) {
                res.add(new UserRow(rs.getString("username"), rs.getString("userID"), rs.getString("email"), rs.getString("GI"), rs.getString("SO"), rs.getString("note")));
            }
            rs.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    //user authentication
    ArrayList<AuthRow> selectAllAuth() {
        ArrayList<AuthRow> res = new ArrayList<AuthRow>();
        try {
            ResultSet rs = mSelectAllAuth.executeQuery();
            while (rs.next()) {
                res.add(new AuthRow(rs.getString("userID"), rs.getString("rskey")));
            }
            rs.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    //user comments
    ArrayList<CommentRow> selectAllComments() {
        ArrayList<CommentRow> res = new ArrayList<CommentRow>();
        try {
            ResultSet rs = mSelectAllComments.executeQuery();
            while (rs.next()) {
                res.add(new CommentRow(rs.getInt("commentID"), rs.getInt("postID"), rs.getString("username"), rs.getString("message")));
            }
            rs.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    //ideas and likes
    DataRow selectOne(int id) {
        DataRow res = null;
        try {
            mSelectOne.setInt(1, id);
            ResultSet rs = mSelectOne.executeQuery();
            if (rs.next()) {
                res = new DataRow(rs.getInt("id"), rs.getInt("likes"), rs.getInt("dislikes"), rs.getString("message"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    //user information
    UserRow selectOneUser(String userID) {
        UserRow res = null;
        try {
            mSelectOneUser.setString(1, userID);
            ResultSet rs = mSelectOneUser.executeQuery();
            if (rs.next()) {
                res = new UserRow(rs.getString("username"), rs.getString("userID"), rs.getString("email"), rs.getString("GI"), rs.getString("SO"), rs.getString("note"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    //user authentication
    AuthRow SelectOneAuth(String userID){
        AuthRow res = null;
        try {
            mSelectOneAuth.setString(1, userID);
            ResultSet rs = mSelectOneAuth.executeQuery();
            if (rs.next()) {
                res = new AuthRow(rs.getString("userID"), rs.getString("rskey"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    //user comments
    CommentRow SelectOneComment(int commentID){
        CommentRow res = null;
        try {
            mSelectOneComments.setInt(1, commentID);
            ResultSet rs = mSelectOneComments.executeQuery();
            if (rs.next()) {
                res = new CommentRow(rs.getInt("commentID"), rs.getInt("postID"), rs.getString("username"), rs.getString("message"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }


    //delete ideas
    int deleteRow(int id) {
        int res = -1;
        try {
            mDeleteOne.setInt(1, id);
            res = mDeleteOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    //delete comment
    int deleteComment(int id){
        int res = -1;
        try {
            mDeleteOneComments.setInt(1, id);
            res = mDeleteOneComments.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    //update ideas
    int updateOne(int postID, String content, int likes, int dislikes) {
        int res = -1;
        try {
            mUpdateOne.setInt(1, postID);
            mUpdateOne.setString(2, content);
            mUpdateOne.setInt(3, likes);
            mUpdateOne.setInt(4, dislikes);
            res = mUpdateOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    int updateOne(int commentID, int postID, String username, String message) {
        int res = -1;
        try {
            mUpdateOne.setInt(1, commentID);
            mUpdateOne.setInt(2, postID);
            mUpdateOne.setString(3, username);
            mUpdateOne.setString(4, message);
            res = mUpdateOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }


    // Come back and configure unliking a post

    void createTable() {
        try {
            mCreateTable.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void dropTable() {
        try {
            mDropTable.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}