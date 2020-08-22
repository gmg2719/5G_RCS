package com.android.messaging.product.api;

import com.android.messaging.product.entity.H5CardItem;

import java.util.ArrayList;

public class H5CardApi{
    private int ret_code;
    private String message;
    private ArrayList<H5CardItem> h5_card;

    public int getRet_code() {
        return ret_code;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<H5CardItem> getH5_card() {
        return h5_card;
    }
}
