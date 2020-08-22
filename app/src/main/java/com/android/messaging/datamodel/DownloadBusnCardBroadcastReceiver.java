package com.android.messaging.datamodel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.android.messaging.datamodel.data.BusinessCardService;
import com.android.messaging.util.LogUtil;

public class DownloadBusnCardBroadcastReceiver extends BroadcastReceiver {
    public static final String TAG = "DownloadBusnCardBroadcastReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        LogUtil.i(TAG, "action="+action);

        if (intent.getAction().equals(WifiManager.RSSI_CHANGED_ACTION)) {
            LogUtil.i(TAG, "wifi信号强度变化");
        }
        //wifi连接上与否
        if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {

            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                LogUtil.i(TAG, "wifi断开");
            } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                //获取当前wifi名称
                LogUtil.i(TAG, "连接到网络 " + wifiInfo.getSSID());
                Intent intent1 = new Intent(context, BusinessCardService.class);
                context.startService(intent1);
            }
        }
        //wifi打开与否
        if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
            int wifistate = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED);
            if (wifistate == WifiManager.WIFI_STATE_DISABLED) {
                LogUtil.i(TAG, "系统关闭wifi");
            } else if (wifistate == WifiManager.WIFI_STATE_ENABLED) {
                LogUtil.i(TAG, "系统开启wifi");
            }
        }
//        Intent intent1 = new Intent(context, BusinessCardService.class);
//        context.startService(intent1);
    }
}
