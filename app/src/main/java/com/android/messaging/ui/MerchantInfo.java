package com.android.messaging.ui;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class MerchantInfo {
    public static String SERVER_URL = "https://www.baidu.com";
    private static String TAG = "MerchanInfo";
    private String addr;
    private String phoneNum;
    private double latitude;
    private double longitude;
    private RequestQueue mQueue;

    public MerchantInfo() {
    }

    public String getAddr(){
        return addr;
    }

    public String getPhoneNum(){
        return phoneNum;
    }

    public double getLatitude(){
        return latitude;
    }

    public double getLongitude(){
        return longitude;
    }

    public void setAddr(String addr){
        this.addr = addr;
    }

    public void setPhoneNum(String phoneNum){
        this.phoneNum = phoneNum;
    }

    public void setLatitude(double latitude){
        this.latitude = latitude;
    }

    public void setLongitude(double longitude){
        this.longitude = longitude;
    }

    public void init(Context context, String url){
        mQueue = Volley.newRequestQueue(context);

        /*StringRequest stringRequest = new StringRequest(SERVER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("TAG", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        });*/

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String mJson = response.toString();
                        Log.d("TAG", mJson);
                        MerchantInfo mcInfo = JSON.parseObject(mJson, MerchantInfo.class);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        });

        mQueue.add(jsonObjectRequest);
    }

}
