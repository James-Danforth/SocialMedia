package edu.lehigh.cse216.bug.backend;

import java.util.Date;

public class UserRow {

    public final String mUId;

    public String mName;

    public String mEmail;

    public String mGI;

    public String mSO;

    public String mNote;

    public final Date mCreated;

    UserRow(String userID, String userName, String userEmail, String userGI, String userSO, String note) {
        mUId = userID;
        mName = userName;
        mEmail = userEmail;
        mGI = userGI;
        mSO = userSO;
        mCreated = new Date();
    }

    UserRow(UserRow data) {
        mUId = data.mUId;
        mName = data.mName;
        mEmail = data.mEmail;
        mGI = data.mGI;
        mSO = data.mSO;
        mCreated = data.mCreated;
    }
}
