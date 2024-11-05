package edu.lehigh.cse216.bug.admin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Map;

/**
 * App is our basic admin app.  For now, it is a demonstration of the six key 
 * operations on a database: connect, insert, update, query, delete, disconnect
 */
public class App {

    /**
     * Print the menu for our program
     */
    static void menu() {
        System.out.println("Main Menu");
        System.out.println("  [T] Create A Table");
        System.out.println("  [C] Create User");
        System.out.println("  [D] Drop A Table");
        System.out.println("  [1] Query for a specific row");
        System.out.println("  [*] Query for all rows");
        System.out.println("  [+] Insert a new row");
        System.out.println("  [~] Update a row");
        System.out.println("  [L] Update a likes");
        System.out.println("  [|] Update a dislikes");
        System.out.println("  [U] Enter User");
        System.out.println("  [q] Quit Program");
        System.out.println("  [?] Help (this message)");
    }

    /**
     * Ask the user to enter a menu option; repeat until we get a valid option
     * 
     * @param in A BufferedReader, for reading from the keyboard
     * 
     * @return The character corresponding to the chosen menu option
     */
    static char prompt(BufferedReader in) {
        // The valid actions:
        String actions = "TCD1*+~q?L|U";

        // We repeat until a valid single-character option is selected        
        while (true) {
            System.out.print("[" + actions + "] :> ");
            String action;
            try {
                action = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            if (action.length() != 1)
                continue;
            if (actions.contains(action)) {
                return action.charAt(0);
            }
            System.out.println("Invalid Command");
        }
    }

    /**
     * Ask the user to enter a String message
     * 
     * @param in A BufferedReader, for reading from the keyboard
     * @param message A message to display when asking for input
     * 
     * @return The string that the user provided.  May be "".
     */
    static String getString(BufferedReader in, String message) {
        String s;
        try {
            System.out.print(message + " :> ");
            s = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return s;
    }

    /**
     * Ask the user to enter an integer
     * 
     * @param in A BufferedReader, for reading from the keyboard
     * @param message A message to display when asking for input
     * 
     * @return The integer that the user provided.  On error, it will be -1
    */
    static int getInt(BufferedReader in, String message) {
        int i = -1;
        try {
            System.out.print(message + " :> ");
            i = Integer.parseInt(in.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return i;
    }

    /**
     * The main routine runs a loop that gets a request from the user and
     * processes it
     * 
     * @param argv Command-line options.  Ignored by this program.
     */
    public static void main(String[] argv) {
        // get the Postgres configuration from the environment
        Map<String, String> env = System.getenv();
        String ip = env.get("POSTGRES_IP");
        String port = env.get("POSTGRES_PORT");
        String user = env.get("POSTGRES_USER");
        String pass = env.get("POSTGRES_PASS");

        // Get a fully-configured connection to the database, or exit 
        // immediately
        Database db = Database.getDatabase(ip, port, user, pass);
        if (db == null)
            return;

        // Start our basic command-line interpreter:
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            // Get the user's request, and do it
            //
            // NB: for better testability, each action should be a separate
            //     function call
            char action = prompt(in);
            if (action == '?') {
                menu();
            } else if (action == 'q') {
                break;
            } else if (action == 'T') {
                int tbl = getInt(in, "Which table? 1: tblAuth, 2: tblUser, 3: tblIdeas, 4: tblComment");
                if ( tbl == 1){
                    db.createTableA();
                    System.out.println("TblAuth created"); 
                }else if (tbl == 2){
                    db.createTableU();
                    System.out.println("TblUser created"); 
                }else if (tbl == 3){
                    db.createTableI();
                    System.out.println("TblIdeas created");
                }else if(tbl == 4){
                    db.createTableC();
                    System.out.println("TblComments created"); 
                }else{
                    System.out.println("Invalid option");
                }
                

            } else if(action == 'C'){

                String RSkey = getString(in, "Enter random session key");
                    if (RSkey.equals(""))
                        continue;
                    if(RSkey.length() <= 2048){
                        int res = db.insertSessionKey(RSkey);
                        System.out.println(res + " RSkey added");
                    }

            }else if (action == 'D') {
                int tbl = getInt(in, "Which table? 1: tblAuth, 2: tblUser, 3: tblIdeas, 4: tblComment");
                if ( tbl == 1){
                    db.dropTableA();
                    System.out.println("TblAuth dropped"); 
                }else if (tbl == 2){
                    db.dropTableU();
                    System.out.println("TblUser dropped"); 
                }else if (tbl == 3){
                    db.dropTableI();
                    System.out.println("TblIdeas dropped");
                }else if(tbl == 4){
                    db.dropTableC();
                    System.out.println("TblComments dropped"); 
                }else{
                    System.out.println("Invalid option");
                }

            } else if (action == '1') {
                int tbl = getInt(in, "Which table? 1: tblAuth, 2: tblUser, 3: tblIdeas, 4: tblComment");
                if ( tbl == 1){
                    int id = getInt(in, "Enter the User ID");
                    if (id == -1)
                        continue;
                    Database.RowData res = db.selectOneA(id);
                    if (res != null) {
                        System.out.println("  [" + res.mUid + "] " + res.mRSkey);
                    }
                    
                }else if (tbl == 2){
                    int id = getInt(in, "Enter the User ID");
                    if (id == -1)
                        continue;
                    Database.RowData res = db.selectOneU(id);
                    if (res != null) {
                        System.out.println( res.mUsername + " [" + res.mUid + "] " + res.mEmail + " \nGender Identity: " + res.mGI + "\nSexual Identity: " + res.mSI + "\nAccount Note: " + res.mNote );
                    }

                }else if (tbl == 3){
                    int id = getInt(in, "Enter the Post ID");
                    if (id == -1)
                        continue;
                    Database.RowData res = db.selectOneI(id);
                    if (res != null) {
                        System.out.println("  [" + res.mId + "] " + res.mMessage + " " + res.mLikes + " " + res.mDislikes);
                    }
                }else if(tbl == 4){
                    int id = getInt(in, "Enter the Comment ID");
                    if (id == -1)
                        continue;
                    Database.RowData res = db.selectOneC(id);
                    if (res != null) {
                        System.out.println("  [" + res.mCid + "] " + res.mPid + " " + res.mUsername + " " + res.mMessage);
                    }
                }else{
                    System.out.println("Invalid option");
                }
                

            } else if (action == '*') {


                int tbl = getInt(in, "Which table? 1: tblAuth, 2: tblUser, 3: tblIdeas, 4: tblComment");
                if ( tbl == 1){
                    ArrayList<Database.RowData> res = db.selectAllA();
                    if (res == null)
                        continue;
                    System.out.println("  Current Database Contents");
                    System.out.println("  -------------------------");
                    for (Database.RowData rd : res) {
                        System.out.println("  [" + rd.mUid + "] " + rd.mRSkey);
                    }
                    
                }else if (tbl == 2){
                    ArrayList<Database.RowData> res = db.selectAllU();
                    if (res == null)
                        continue;
                    System.out.println("  Current Database Contents");
                    System.out.println("  -------------------------");
                    for (Database.RowData rd : res) {
                        System.out.println( rd.mUsername + " [" + rd.mUid + "] " + rd.mEmail + " \nGender Identity: " + rd.mGI + "\nSexual Identity: " + rd.mSI + "\nAccount Note: " + rd.mNote );
                    }

                }else if (tbl == 3){
                    ArrayList<Database.RowData> res = db.selectAllI();
                    if (res == null)
                        continue;
                    System.out.println("  Current Database Contents");
                    System.out.println("  -------------------------");
                    for (Database.RowData rd : res) {
                        System.out.println("  [" + rd.mId + "] " + rd.mMessage + " " + rd.mLikes + " " + rd.mDislikes);
                    }
                }else if(tbl == 4){
                    ArrayList<Database.RowData> res = db.selectAllC();
                    if (res == null)
                        continue;
                    System.out.println("  Current Database Contents");
                    System.out.println("  -------------------------");
                    for (Database.RowData rd : res) {
                        System.out.println("  [" + rd.mCid + "] " + rd.mPid + " " + rd.mUsername + " " + rd.mMessage);
                    }
                }else{
                    System.out.println("Invalid option");
                }
            
            } else if (action == '+') {
                int tbl = getInt(in, "Which table?  1: tblIdeas or 2: tblComments");
                int like = 0;
                if (tbl == 1){
                    String message = getString(in, "Enter the Post");
                    boolean badWord = db.checkComment(message);
                    System.out.println(badWord);
                    if(badWord == false){
                        if (like == -1 || message.equals(""))
                        continue;
                        if(message.length() <= 1024){
                            int res = db.insertRow(message,like, 0);
                            System.out.println(res + " rows added");
                        }
                    }else{
                        System.err.println("The Post contains a BAD WORD and cannot be posted");
                        continue;
                    }
                   
                }else if (tbl == 2){
                    String message = getString(in, "Enter the Comment");
                    String username = getString(in, "Enter username");
                    int post_id = getInt(in, "Enter the post ID");
                    boolean badWord = db.checkComment(message);
                    if(badWord == false){
                        if (like == -1 || message.equals(""))
                            continue;
                        if(message.length() <= 1024){
                            int res = db.insertComment(post_id,username, message);
                            System.out.println(res + " rows added");
                        }
                    }else{
                        System.err.println("The Comment contains a BAD WORD and cannot be posted");
                        continue;
                    }
                }
                
                
            } else if (action == '~') {
                int tbl = getInt(in, "Which table?  1: tblIdeas or 2: tblComments");
               
                if(tbl == 1){
                    int post_id = getInt(in, "Enter the Post ID");
                    String message = getString(in, "Enter the message");
                    if (message.equals(""))
                        continue;
                    if(message.length() <= 2048){
                        int res = db.updateOneCI(post_id,message);
                        System.out.println(res + " rows added");
                    }

                }else if (tbl == 2){
                   String postIdString = getString(in, "Enter the Post ID");
                    int postId;
                    try {
                        postId = Integer.parseInt(postIdString);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a valid integer for comment ID.");
                        continue; // Assuming this is inside a loop, to continue with the next iteration
                    }
                    // Check if the provided postId exists in tblIdeas
                    Database.RowData idea = db.selectOneI(postId); // Assuming tblIdeas is table 2


                    if (idea == null) {
                        System.out.println("Error: comment ID does not exist in tblComments. Please enter a valid comment ID.");
                        continue; // Skip to the next iteration of the loop
                    }

                    // Post ID exists, proceed with getting username and message
                    String message = getString(in, "Enter the new message");
                    if (message.equals("")) {
                        System.out.println("Error: Message cannot be empty.");
                        continue; // Skip to the next iteration of the loop
                    }

                    if (message.length() > 2048) {
                        System.out.println("Error: Message length exceeds maximum limit.");
                        continue; // Skip to the next iteration of the loop
                    }
                    
                    // Insert the comment into tblComments
                    int res = db.updateOneCC(message, postId);
                    System.out.println(res + " rows added");
                }

            }else if(action == 'L'){
                int id = getInt(in, "Enter the post ID ");
                int res = db.updateOne(id);
                if (res == -1)
                    continue;
                System.out.println("  " + res + " rows updated");
                
                
               
            }else if (action == '|'){
                int id = getInt(in, "Enter the row ID ");
                if (id == -1)
                    continue;
                int res = db.updateOneD(id);
                if (res == -1)
                    continue;
                System.out.println("  " + res + " rows updated");
            }else if(action == 'U'){
                String userIdString = getString(in, "Enter the user ID");
                int userId;

                    try {
                        userId = Integer.parseInt(userIdString);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a valid integer for post ID.");
                        continue; // Assuming this is inside a loop, to continue with the next iteration
                    }
                    // Check if the provided userId exists in tblAUTH
                    Database.RowData userFound = db.selectOneU(userId); // Assuming tblIdeas is table 2

                    if (userFound == null) {
                        System.out.println("Error: User ID does not exist in tblAuth. Please enter a valid User ID.");
                        continue; // Skip to the next iteration of the loop
                    }
                String username = getString(in, "Enter the Username");
                String email = getString(in, "Enter email ");
                String GI = getString(in, "Enter Gender Identity ");
                String SI = getString(in, "Enter Sexual Identity ");
                String note = getString(in, "Enter a account note ");
                int res = db.insertUser(username,userId,email,GI,SI,note);
                    System.out.println(res + " User added");
            }
        }
        // Always remember to disconnect from the database when the program 
        // exits
        db.disconnect();
    }
}