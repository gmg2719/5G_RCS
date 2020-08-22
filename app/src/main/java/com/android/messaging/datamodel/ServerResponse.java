package com.android.messaging.datamodel;

public class ServerResponse {
    ResponseData data;
    String msg;
    int code;
    public class ResponseData{
        int isValid;

        public int getIsValid() {
            return isValid;
        }
    }

    public ResponseData getData() {
        return data;
    }

    public String getMsg() {
        return msg;
    }

    public int getCode() {
        return code;
    }
}
