package edu.lehigh.cse216.bug.backend;

import java.util.Date;

public class AuthRow {
    public final String mUId;
    public String mSessionKey;
    public final Date mCreated;

    AuthRow(String userID, String sessionKey) {
        mUId = userID;
        mSessionKey = sessionKey;
        mCreated = new Date();
    }

    AuthRow(AuthRow data) {
        mUId = data.mUId;
        mSessionKey = data.mSessionKey;
        mCreated = data.mCreated;
    }

}