package edu.lehigh.cse216.bug.backend;

public class StructuredResponse {
    public String mStatus;

    public String mMessage;

    public Object mData;

 
    public StructuredResponse(String status, String message, Object data) {
        mStatus = (status != null) ? status : "invalid";
        mMessage = message;
        mData = data;
    }
}
