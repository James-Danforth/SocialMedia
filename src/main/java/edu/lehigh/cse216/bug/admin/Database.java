package edu.lehigh.cse216.bug.admin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;


public class Database {
    /**
     * The connection to the database.  When there is no connection, it should
     * be null.  Otherwise, there is a valid open connection
     */
    private Connection mConnection;

    /**
     * A prepared statement for getting all data in the database
     */
    private PreparedStatement mSelectAllA;
    private PreparedStatement mSelectAllU;
    private PreparedStatement mSelectAllI;
    private PreparedStatement mSelectAllC;
    /**
     * A prepared statement for getting one row from the database
     */
     private PreparedStatement mSelectOneA; 
    private PreparedStatement mSelectOneU;
    private PreparedStatement mSelectOneI;
    private PreparedStatement mSelectOne; 
    private PreparedStatement mSelectOneC; 
    /**
     * A prepared statement for deleting a row from the database
     */
    private PreparedStatement mDeleteOneU;
    private PreparedStatement mDeleteOneI;
    private PreparedStatement mDeleteOneC;

    /**
     * A prepared statement for inserting into the database
     */
    private PreparedStatement mInsertOneI;
    /**
     * A prepared statement for inserting into the database
     */
    private PreparedStatement mInsertOneU;
    private PreparedStatement mInsertOneC;
    private PreparedStatement mInsertOneA;
    /**
     * A prepared statement for updating a single row in the database
     */
    private PreparedStatement mUpdateOneU;
    private PreparedStatement mUpdateOneCI;
    private PreparedStatement mUpdateOneCC;
    private PreparedStatement mUpdateOneLI;
    private PreparedStatement mUpdateOneDI;


    /**
     * A prepared statement for creating the table in our database
     */
    private PreparedStatement  mCreateTableA;
    private PreparedStatement  mCreateTableU;
    private PreparedStatement  mCreateTableI;
    private PreparedStatement  mCreateTableC;

    /**
     * A prepared statement for dropping the table in our database
     */
    private PreparedStatement mDropTableA;
    private PreparedStatement mDropTableU;
    private PreparedStatement mDropTableI;
    private PreparedStatement mDropTableC;
    //private PreparedStatement mCheckComments;

    /**
     * RowData is like a struct in C: we use it to hold data, and we allow 
     * direct access to its fields.  In the context of this Database, RowData 
     * represents the data we'd see in a row.
     * 
     * We make RowData a static class of Database because we don't really want
     * to encourage users to think of RowData as being anything other than an
     * abstract representation of a row of the database.  RowData and the 
     * Database are tightly coupled: if one changes, the other should too.
     */
    public static class RowData {
        /**
         * The ID of this row of the database
         */
        int mId;
        /**
         * The subject stored in this row
         */
         /**
          * String mSubject;
          */
        int mLikes;
         /**
         * The Dislikes stored in this row
         */
         /**
          * String mDislikes;
          */
        int mDislikes;
        /**
         * The message stored in this row
         */
        String mMessage;

        String mPid;

        int mCid;

        String mUsername;
        
        int mUid;
        String mEmail;
        String mGI;
        String mSI;
        String mNote;
        String mRSkey;

        public RowData(int user_id, String RSkey) {
            mUid = user_id;
            mRSkey = RSkey;
            
        }
        /**
         * Construct a RowData object by providing values for its fields
         */
        public RowData(int id, String message, int likes, int dislikes) {
            mId = id;
            mLikes = likes;
            mMessage = message;
            mDislikes = dislikes;
        }

        public RowData(int comment_id, String post_id, String username, String message) {
            mCid = comment_id;
            mPid = post_id;
            mUsername = username;
            mMessage = message;
        }

        public RowData(String username, int user_id, String email, String gI, String sI, String note) {
            mUsername = username;
            mUid = user_id;
            mEmail = email;
            mGI = gI;
            mSI = sI;
            mNote = note;
        }
    }

    /**
     * The Database constructor is private: we only create Database objects 
     * through the getDatabase() method.
     */
    private Database() {
    }

    /**
     * Get a fully-configured connection to the database
     * 
     * @param ip   The IP address of the database server
     * @param port The port on the database server to which connection requests
     *             should be sent
     * @param user The user ID to use when connecting
     * @param pass The password to use when connecting
     * 
     * @return A Database object, or null if we cannot connect properly
     */
    static Database getDatabase(String ip, String port, String user, String pass) {
        // Create an un-configured Database object
        Database db = new Database();

        // Give the Database object a connection, fail if we cannot get one
        try {
            Connection conn = DriverManager.getConnection("jdbc:postgresql://" + ip + ":" + port + "/", user, pass);
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

        // Attempt to create all of our prepared statements.  If any of these 
        // fail, the whole getDatabase() call should fail
        try {
            // NB: we can easily get ourselves in trouble here by typing the
            //     SQL incorrectly.  We really should have things like "tblUser"
            //     as constants, and then build the strings for the statements
            //     from those constants.

            // Note: no "IF NOT EXISTS" or "IF EXISTS" checks on table 
            // creation/deletion, so multiple executions will cause an exception

            db.mCreateTableA = db.mConnection.prepareStatement(
                    "CREATE TABLE tblAUTH(user_id SERIAL PRIMARY KEY, RSkey VARCHAR(50) NOT NULL)"  // change the refrence
                    );
            db.mDropTableA = db.mConnection.prepareStatement("DROP TABLE IF EXISTS tblAUTH");
            db.mInsertOneA = db.mConnection.prepareStatement("INSERT INTO tblAUTH VALUES (default, ?)");
            db.mSelectOneA = db.mConnection.prepareStatement("SELECT * from tblAUTH WHERE user_id=?");
            db.mSelectAllA = db.mConnection.prepareStatement("SELECT user_id, rskey FROM tblAuth");

            db.mCreateTableU = db.mConnection.prepareStatement(
                    "CREATE TABLE tblUser(username VARCHAR(50) PRIMARY KEY, user_id INTEGER, email VARCHAR(50) NOT NULL, Gender_Identity VARCHAR(50) NOT NULL,  Sexual_Identity VARCHAR(50) NOT NULL, note VARCHAR(50) NOT NULL" +
                    "FOREIGN KEY (user_id) REFERENCES tblAUTH(user_id)" // change the refrence
                    );
            db.mDropTableU = db.mConnection.prepareStatement("DROP TABLE IF EXISTS tblUser");

            // Standard CRUD operations
            db.mDeleteOneU = db.mConnection.prepareStatement("DELETE FROM tblUser WHERE user_id = ?");
            db.mInsertOneU = db.mConnection.prepareStatement("INSERT INTO tblUser VALUES (?, ?, ?,?,?,?)");
            db.mSelectAllU = db.mConnection.prepareStatement("SELECT username, user_id,email,gender_identity,sexual_identity,note FROM tblUser");
            db.mSelectOneU = db.mConnection.prepareStatement("SELECT * from tblUser WHERE user_id=?");
           // db.mUpdateOneU = db.mConnection.prepareStatement("UPDATE tblUser SET message = ? WHERE username = ?");
            //db.mSelectOne = db.mConnection.prepareStatement("SELECT * from tblUser WHERE id=?");

            db.mCreateTableI = db.mConnection.prepareStatement(
                    "CREATE TABLE tblIdeas(post_id SERIAL PRIMARY KEY, messages VARCHAR(50) NOT NULL, likes int DEFAULT(0) "
                    + "dislikes INT DEFAULT (0))");
            db.mDropTableI = db.mConnection.prepareStatement("DROP TABLE tblIdeas");

            // Standard CRUD operations for ideas table
            db.mInsertOneI = db.mConnection.prepareStatement("INSERT INTO tblideas VALUES (default, ?, ?, ?)");
            db.mSelectAllI = db.mConnection.prepareStatement("SELECT post_id,content,likes,	dislikes FROM tblIdeas");
            db.mSelectOneI = db.mConnection.prepareStatement("SELECT * from tblideas WHERE post_id=?");
            db.mUpdateOneCI = db.mConnection.prepareStatement("UPDATE tblIdeas SET  content = ? WHERE post_id = ?");
            db.mUpdateOneLI = db.mConnection.prepareStatement("UPDATE tblIdeas SET likes = likes + 1 WHERE post_id = ?");
            db.mUpdateOneDI = db.mConnection.prepareStatement("UPDATE tblIdeas SET dislikes = dislikes + 1 WHERE post_id = ?");

            db.mCreateTableC = db.mConnection.prepareStatement(
                    "CREATE TABLE tblComments(comment_id SERIAL PRIMARY KEY, post_id INT, username VARCHAR(50) NOT NULL, message VARCHAR(50) NOT NULL," +
                    "FOREIGN KEY (post_id) REFERENCES tblIdeas(post_id)"
                    );
            db.mDropTableC = db.mConnection.prepareStatement("DROP TABLE tblComments");
            // Standard CRUD operations for Comments table
            db.mInsertOneC = db.mConnection.prepareStatement("INSERT INTO tblComments VALUES (default, ?, ?, ?)");
            db.mSelectAllC = db.mConnection.prepareStatement("SELECT comment_id, post_id, username, message FROM tblcomments");
            db.mSelectOneC = db.mConnection.prepareStatement("SELECT * from tblcomments WHERE comment_id=?");
            db.mUpdateOneCC = db.mConnection.prepareStatement("UPDATE tblComments SET message = ? WHERE post_id = ?");
            db.mUpdateOneLI = db.mConnection.prepareStatement("UPDATE tblIdeas SET likes = likes + 1 WHERE post_id = ?");
            db.mUpdateOneDI = db.mConnection.prepareStatement("UPDATE tblIdeas SET dislikes = dislikes + 1 WHERE post_id = ?");
        } catch (SQLException e) {
            System.err.println("Error creating prepared statement");
            e.printStackTrace();
            db.disconnect();
            return null;
        }
        return db;
         
    }

    /**
     * Check if a comment contains any bad words
     * 
     * @param message The comment message to check
     * @return True if the comment contains bad words, false otherwise
    */
    boolean checkComment(String message) {
        String[] badWords = {"Fuck", "FUCK", "fuck", "Bitch", "BITCH", "bitch", "shit", "SHIT", "Shit", "SLUT", "slut", "Slut", "WHORE", "PENIS", "DICK", "ASS", "ASSHOLE", "BASTARD","BULLSHIT","COCK", "DICK HEAD", "DIKE", "HELL", "PUSSY", "WANKER", "RETARD"};
        
        // Convert the message to lowercase for case-insensitive comparison
        String lowercaseMessage = message.toLowerCase();
        
        // Iterate through the bad words array to check if any bad word is present in the message
        for (String badWord : badWords) {
            if (lowercaseMessage.contains(badWord.toLowerCase())) {
                return true;
            }
        }
        
        // If no bad word is found, return false
        return false;
    }

    /**
     * Close the current connection to the database, if one exists.
     * 
     * NB: The connection will always be null after this call, even if an 
     *     error occurred during the closing operation.
     * 
     * @return True if the connection was cleanly closed, false otherwise
     */
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

    int insertSessionKey(String RSkey ) {
        int count = 0;
        try {
            mInsertOneA.setString(1, RSkey);
            count += mInsertOneA.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

   
    /**
     * Insert a row into the database
     * 
     * @param likes The subject for this new row
     * @param message The message body for this new row
     * 
     * @return The number of rows that were inserted
    */
    int insertRow(String content, int like, int dislikes) {
        int count = 0;
        try {
            mInsertOneI.setString(1, content);
            mInsertOneI.setInt(2, like);
            mInsertOneI.setInt(3, dislikes);

            count += mInsertOneI.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    int insertComment(int post_id, String username, String message) {
        int count = 0;
        try {
            if (checkComment(message) == false) {
                mInsertOneC.setInt(1, post_id);
                mInsertOneC.setString(2, username);
                mInsertOneC.setString(3, message);
                count += mInsertOneC.executeUpdate();
            } else {
                count = 0;
                throw new IllegalArgumentException("Bad word detected in the comment");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the database insertion error here if needed
        }
        return count;
        
    }

    int insertUser(String username,int user_id, String email, String GI,String SI, String note ) {
        int count = 0;
        char desiredCharacter = '@';
        String lehigh = "@lehigh.edu";
        // Find the index of the desired character
        int indexOfCharacter = email.indexOf(desiredCharacter);
        if (indexOfCharacter != -1){
            String emailDomain = email.substring(indexOfCharacter);
            
            if (emailDomain.equalsIgnoreCase(lehigh) == true){
            
                try {
                    mInsertOneU.setString(1, username);
                    mInsertOneU.setInt(2,user_id);
                    mInsertOneU.setString(3, email);
                    mInsertOneU.setString(4, GI);
                    mInsertOneU.setString(5, SI);
                    mInsertOneU.setString(6, note);

                    count += mInsertOneU.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }else{
                count = 0;
                System.out.println("Not a lehigh email");
                //continue;
            }
        }else {
            count = 0;
            System.out.println("does not have an @");
            //continue;
        }
        return count;
    }
    
    /**
     * Query the database for a list of all subjects and their IDs
     * 
     * @return All rows, as an ArrayList
     */
    ArrayList<RowData> selectAllA() {
        ArrayList<RowData> res = new ArrayList<RowData>();
        try {
            
            ResultSet rs = mSelectAllA.executeQuery();
            while (rs.next()){
                int user_id = rs.getInt("user_id");
                String RSkey = rs.getString("RSkey");
                
                res.add(new RowData(user_id, RSkey)); // Add data to res ArrayList
            }
            rs.close();

            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    ArrayList<RowData> selectAllU() {
        ArrayList<RowData> res = new ArrayList<RowData>();
        try {
            
            ResultSet rs = mSelectAllU.executeQuery();
            while (rs.next()){
                String username = rs.getString("username");
                int user_id = rs.getInt("user_id");
                String email = rs.getString("email");
                String gender_identity = rs.getString("gender_identity");
                String sexual_identity = rs.getString("sexual_identity");
                String note = rs.getString("note");
                
                res.add(new RowData(username, user_id, email, gender_identity, sexual_identity, note)); // Add data to res ArrayList
                
            }
            rs.close();

            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    ArrayList<RowData> selectAllI() {
        ArrayList<RowData> res = new ArrayList<RowData>();
        try {
            
            ResultSet rs = mSelectAllI.executeQuery();
            while (rs.next()){
                int post_id = rs.getInt("post_id");
                String content = rs.getString("content");
                int likes = rs.getInt("likes");
                int dislikes = rs.getInt("dislikes");
                
                res.add(new RowData(post_id, content, likes, dislikes)); // Add data to res ArrayList
            }
            rs.close();

            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    ArrayList<RowData> selectAllC() {
        ArrayList<RowData> res = new ArrayList<RowData>();
        try {
            
            ResultSet rs = mSelectAllC.executeQuery();
            while (rs.next()){
                int comment_id = rs.getInt("comment_id");
                String post_id = rs.getString("post_id");
                String username = rs.getString("username");
                String message = rs.getString("message");
                
                res.add(new RowData(comment_id, post_id, username, message)); // Add data to res ArrayList
            }
            rs.close();

            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get all data for a specific row, by ID
     * 
     * @param id The id of the row being requested
     * 
     * @return The data for the requested row, or null if the ID was invalid
     */
    RowData selectOneA(int id) {
        RowData res = null;
        try {
            mSelectOneA.setInt(1, id);
            ResultSet rs = mSelectOneA.executeQuery();
            if (rs.next()) {
                res = new RowData(rs.getInt("user_id"), rs.getString("RSkey"));
            }
        
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }   
    RowData selectOneU(int id) {
        RowData res = null;
        try {
            mSelectOneU.setInt(1, id);
            ResultSet rs = mSelectOneU.executeQuery();
            if (rs.next()) {
                res = new RowData(rs.getString("username"), rs.getInt("user_id"), rs.getString("email"), rs.getString("gender_identity"), rs.getString("sexual_identity"), rs.getString("note"));
            }
            
        
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    } 
    RowData selectOneI(int id) {
        RowData res = null;
        try {
            mSelectOneI.setInt(1, id);
            ResultSet rs = mSelectOneI.executeQuery();
            if (rs.next()) {
                res = new RowData(rs.getInt("post_id"), rs.getString("content"), rs.getInt("likes"), rs.getInt("dislikes"));
            }
        
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    } 

    RowData selectOneC(int id) {
        RowData res = null;
        try {
            mSelectOneC.setInt(1, id);
            ResultSet rs = mSelectOneC.executeQuery();
            if (rs.next()) {
                res = new RowData(rs.getInt("comment_id"), rs.getString("post_id"), rs.getString("username"), rs.getString("message"));
            }
            
        
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    } 

    
    /**
     * Update the message for a row in the database
     * 
     * @param id The id of the row to update
     * @param message The new message contents
     * 
     * @return The number of rows that were updated.  -1 indicates an error.
    */
    int updateOne(int id) {
        int res = -1;
        try {
            mUpdateOneLI.setInt(1, id);
            res = mUpdateOneLI.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    int updateOneD(int id) {
        int res = -1;
        try {
            mUpdateOneDI.setInt(1, id);
            res = mUpdateOneDI.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    int updateOneCI(int id, String message) {
        int res = -1;
        try {
            mUpdateOneCI.setString(1, message);
            mUpdateOneCI.setInt(2, id);
            res = mUpdateOneCI.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    int updateOneCC(String message, int id ) {
        int res = -1;
        try {
            mUpdateOneCC.setInt(2, id);
            mUpdateOneCC.setString(1, message);
            res = mUpdateOneCC.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }
    int updateOneU( String note ) {
        int res = -1;
        try {
            mUpdateOneU.setString(1, note);
            res = mUpdateOneU.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }
    /**
     * Create tblUser.  If it already exists, this will print an error
     */
    void createTableA() {
        try {
            mCreateTableA.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Create tblUser.  If it already exists, this will print an error
     */
    void createTableU() {
        try {
            mCreateTableU.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Create tblUser.  If it already exists, this will print an error
     */
    void createTableI() {
        try {
            mCreateTableI.execute();
        
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Create tblUser.  If it already exists, this will print an error
     */
    void createTableC() {
        try {
            mCreateTableC.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Remove tblUser from the database.  If it does not exist, this will print
     * an error.
     */
    void dropTableA() {
        try {
            mDropTableA.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void dropTableU() {
        try {
            mDropTableU.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    void dropTableI() {
        try {
            mDropTableI.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void dropTableC() {
        try {
            mDropTableC.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}