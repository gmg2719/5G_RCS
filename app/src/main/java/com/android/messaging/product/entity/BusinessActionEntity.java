package com.android.messaging.product.entity;

public class BusinessActionEntity {
    private String mBusnActionName;
    private String mBusnWebUrl;

    public BusinessActionEntity(String mBusnActionName, String mBusnWebUrl) {
        this.mBusnActionName = mBusnActionName;
        this.mBusnWebUrl = mBusnWebUrl;
    }

    public String getBusnActionName(){
        return mBusnActionName;
    }

    public String getBusnWebUrl(){
        return mBusnWebUrl;
    }
}
