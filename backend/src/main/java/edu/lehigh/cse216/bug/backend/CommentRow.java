package edu.lehigh.cse216.bug.backend;

import java.util.Date;

public class CommentRow {
    public final int mId;
    public final int mPostId;
    public String mUsername;
    public String mMessage;
    public final Date mCreated;

    CommentRow(int id, int postId, String username, String message) {
        mId = id;
        mPostId = postId;
        mUsername = username;
        mMessage = message;
        mCreated = new Date();
    }

    CommentRow(CommentRow data) {
        mId = data.mId;
        mPostId = data.mPostId;
        mUsername = data.mUsername;
        mMessage = data.mMessage;
        mCreated = data.mCreated;
    }
}