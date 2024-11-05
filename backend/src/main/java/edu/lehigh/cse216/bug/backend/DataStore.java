package edu.lehigh.cse216.bug.backend;

import java.util.ArrayList;
public class DataStore {

    private ArrayList<DataRow> mRows;

    private int mCounter;


    DataStore() {
        mCounter = 0; // Update this to make sure ID's are always numbered consecutively
        mRows = new ArrayList<>();
    }


    public synchronized int createEntry(String content) {
        if (content == null)
            return -1;
        // NB: we can safely assume that id is greater than the largest index in 
        //     mRows, and thus we can use the index-based add() method
        int id = mCounter++;
        DataRow data = new DataRow(id, 0, 0, content);
        mRows.add(id, data);
        return id;
    }

  
    public synchronized DataRow readOne(int id) {
        if (id >= mRows.size())
            return null;
        DataRow data = mRows.get(id);
        if (data == null)
            return null;
        return new DataRow(data);
    }

    public synchronized ArrayList<DataRowLite> readAll() {
        ArrayList<DataRowLite> data = new ArrayList<>();
        // NB: we copy the data, so that our ArrayList only has ids, titles and likes
        for (DataRow row : mRows) {
            if (row != null)
                data.add(new DataRowLite(row));
        }
        return data;
    }
    public synchronized DataRow updateOne(int id, String content, int likes, int dislikes) {
        if (content == null)
            return null;
        if (id >= mRows.size())
            return null;
        DataRow data = mRows.get(id);
        if (data == null)
            return null;
        data.mContent = content;
        data.mLikes = likes;
        data.mDislikes = dislikes;
        return new DataRow(data);
    }


    public synchronized boolean deleteOne(int id) {
        if (id >= mRows.size())
            return false;
        if (mRows.get(id) == null)
            return false;
        mRows.set(id, null);
        return true;
    }

   // Come back and configure unliking a post
}