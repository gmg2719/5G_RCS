package com.android.messaging.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.android.messaging.util.LogUtil;
import com.google.gson.Gson;
import com.microfountain.rcs.aidl.broadcast.RcsMessageBroadcast;
import com.microfountain.rcs.aidl.service.message. MessageDownloadResult;

public class DownloadChatbotFileReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.i("Junwang", "DownloadChatbotFileReceiver");
        String jsonString = intent.getStringExtra(RcsMessageBroadcast.INTENT_EXTRA_MESSAGE_DOWNLOAD_RESULT_JSON_STRING);
        MessageDownloadResult result = new Gson().fromJson(jsonString, MessageDownloadResult.class);
        if(result != null) {
            LogUtil.i("Junwang", "DownloadChatbotFileReceiver statusCode="+result.statusCode);
            if (result.statusCode >= 200 && result.statusCode <= 209) {
                LogUtil.i("Junwang", "DownloadChatbotFile successfully.");
            } else if (result.statusCode == 302 || result.statusCode == 401 || result.statusCode == 403
                    || result.statusCode == 404 || result.statusCode == 410) {
                LogUtil.i("Junwang", "can't Download ChatbotFile and can't retry.");
            } else if (result.statusCode == 416) {
                LogUtil.i("Junwang", "Download ChatbotFile fail, please delete the local temp file and retry");
            } else {
                LogUtil.i("Junwang", "error status code = " + result.statusCode);
            }
            LogUtil.i("Junwang", "DownloadChatbotFileReceiver extra = " + result.extra);
        }
    }
}
