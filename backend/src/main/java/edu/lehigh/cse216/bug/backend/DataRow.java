package edu.lehigh.cse216.bug.backend;

import java.util.Date;
public class DataRow {
    public final int mId;

    public String mContent;

    public int mLikes;

    public int mDislikes;

    public final Date mCreated;

    DataRow(int id, int likes, int dislikes, String content) {
        mId = id;
        mContent = content;
        mLikes = likes;
        mDislikes = dislikes;
        mCreated = new Date();
    }

    DataRow(DataRow data) {
        mId = data.mId;
        mLikes = data.mLikes;
        mDislikes = data.mDislikes;
        mContent = data.mContent;
        mCreated = data.mCreated;
    }
    

}