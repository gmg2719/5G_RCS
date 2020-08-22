package com.android.messaging.datamodel.microfountain.sms.database;

import android.Manifest;
import android.arch.persistence.room.Room;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.SubscriptionManager;

import com.android.messaging.util.LogUtil;

public class RcsDatabase {

    private static final String TAG = "RcsDatabase";

    /**
     * 系统SIM卡状态变化广播action
     */
    private static final String ACTION_SIM_STATE_CHANGED = "android.intent.action.SIM_STATE_CHANGED";

    private static final RcsDatabase SHARED_INSTANCE = new RcsDatabase();

    private static boolean INITIALIZED = false;

    public static RcsDatabase getSharedInstance() {
        return SHARED_INSTANCE;
    }

    private static final Object lock = new Object();

    public static void initialize(Context context) {

        synchronized (lock) {

            if (INITIALIZED) {

                return;
            }

            INITIALIZED = true;
        }

        SHARED_INSTANCE.init(context);
    }

    private SmsDatabase database;

    public SmsDatabase getDatabase() {
        return database;
    }

    private void init(final Context context) {
        database = Room.databaseBuilder(context, SmsDatabase.class, "sms_app.db").fallbackToDestructiveMigration().build();

        SimStateBroadcastReceiver simStateBroadcastReceiver = new SimStateBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(ACTION_SIM_STATE_CHANGED);
        context.registerReceiver(simStateBroadcastReceiver, intentFilter);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            LogUtil.i(TAG, "init");
            SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            subscriptionManager.addOnSubscriptionsChangedListener(new SubscriptionManager.OnSubscriptionsChangedListener() {
                @Override
                public void onSubscriptionsChanged() {
                    updateRcsSettings(context);
                }
            });
            updateRcsSettings(context);
        }
    }

    public void updateRcsSettings(Context context) {
//        if (SettingDBManager.getInstance() == null) {
//            LogUtil.e(TAG, "updateRcsSettings SettingDBManager is null");
//            return;
//        }
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
//            final SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
//            List<SubscriptionInfo> subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
//            LogUtil.e(TAG, "updateRcsSettings subscriptionInfoList.size: " + (subscriptionInfoList != null ? subscriptionInfoList.size() : 0));
//            if (subscriptionInfoList != null) {
//                for (SubscriptionInfo subscriptionInfo : subscriptionInfoList) {
//                    int subId = subscriptionInfo.getSubscriptionId();
//                    LogUtil.e(TAG, "updateRcsSettings subId: " + subId);
//                    SettingDBManager.getInstance().updateRcsSetting(subId);
//                }
//            } else {
//                SettingDBManager.getInstance().updateRcsSetting(-1);
//            }
//        }
    }

    private static class SimStateBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            RcsDatabase.getSharedInstance().updateRcsSettings(context);
        }
    }
}
