package edu.lehigh.cse216.bug.backend;

public class DataRowLite{

    public int mId;

    public int mLikes;

    public int mDislikes;

    public DataRowLite(DataRow data){
    this.mId = data.mId;
    this.mLikes = data.mLikes;
    this.mDislikes = data.mDislikes;
    }
}
