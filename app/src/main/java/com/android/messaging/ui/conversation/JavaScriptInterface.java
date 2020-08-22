package com.android.messaging.ui.conversation;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;

import java.util.HashMap;
import java.util.Map;


public class JavaScriptInterface {
    private Handler mHandler;

    /** Instantiate the interface and set the context */
    public JavaScriptInterface(Handler mHandler) {
        this.mHandler = mHandler;
    }

    /** Show a toast from the web page */
    @JavascriptInterface
    public void showToast(final String toast){
        Log.i("TAG", "调用成功");
    }

    @JavascriptInterface
    public void callAndroidAction(String action, String url,String json){
        Map<String, String> params = new HashMap<String, String>();

        if(!TextUtils.isEmpty(url)){
            params.put("url", url);
        }

        if(!TextUtils.isEmpty(json)){
            params.put("json", json);
        }

        Message msg = Message.obtain();
        msg.what = Integer.valueOf(action);
        msg.obj = params;
        mHandler.sendMessage(msg);
    }
}
