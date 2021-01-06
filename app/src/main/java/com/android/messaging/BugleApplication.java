/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.messaging;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.mms.CarrierConfigValuesLoader;
import android.support.v7.mms.MmsManager;
import android.telephony.CarrierConfigManager;

import com.android.messaging.datamodel.DataModel;
import com.android.messaging.datamodel.MessagingContentProvider;
import com.android.messaging.product.hotfix.ShotFix;
import com.android.messaging.receiver.SmsReceiver;
import com.android.messaging.sms.ApnDatabase;
import com.android.messaging.sms.BugleApnSettingsLoader;
import com.android.messaging.sms.BugleUserAgentInfoLoader;
import com.android.messaging.sms.MmsConfig;
import com.android.messaging.ui.ConversationDrawables;
import com.android.messaging.util.BugleGservices;
import com.android.messaging.util.BugleGservicesKeys;
import com.android.messaging.util.BuglePrefs;
import com.android.messaging.util.BuglePrefsKeys;
import com.android.messaging.util.DebugUtils;
import com.android.messaging.util.LogUtil;
import com.android.messaging.util.OsUtil;
import com.android.messaging.util.PhoneUtils;
import com.android.messaging.util.RCSUtil;
import com.android.messaging.util.RestartAPPTool;
import com.android.messaging.util.Trace;
import com.google.common.annotations.VisibleForTesting;
import com.lwy.smartupdate.Config;
import com.lwy.smartupdate.UpdateManager;
import com.lwy.smartupdate.api.OkhttpManager;
import com.microfountain.rcs.log.ILogger;
import com.microfountain.rcs.rcskit.service.RcsSubscriptionInfo;
import com.microfountain.rcs.rcskit.service.RcsSubscriptionManager;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;

/**
 * The application object
 */
public class BugleApplication extends Application implements UncaughtExceptionHandler {
    private static final String TAG = LogUtil.BUGLE_TAG;

    private UncaughtExceptionHandler sSystemUncaughtExceptionHandler;
    private static boolean sRunningTests = false;

    @VisibleForTesting
    protected static void setTestsRunning() {
        sRunningTests = true;
    }

    /**
     * @return true if we're running unit tests.
     */
    public static boolean isRunningTests() {
        return sRunningTests;
    }

//    static{
//
////        System.loadLibrary("dexmmb");
//        System.loadLibrary("cmcc_rcs_statistics");
//        System.loadLibrary("callme");
//        System.loadLibrary("kh");
////        System.loadLibrary("SmsCore");
////        System.loadLibrary("plugin_phone");
//        System.loadLibrary("xypatch");
//    }

    //add by junwang
    private static Context mContext;
//    private XYRCSMsgReceiver mXYRCSMsgReceiver;

    @Override
    public void onCreate() {
        Trace.beginSection("app.onCreate");
        super.onCreate();

        //add by junwang
//        startRcsService();
        mContext = getApplicationContext();

        // Note onCreate is called in both test and real application environments
        if (!sRunningTests) {
            // Only create the factory if not running tests
            FactoryImpl.register(getApplicationContext(), this);
        } else {
            LogUtil.e(TAG, "BugleApplication.onCreate: FactoryImpl.register skipped for test run");
        }
        //add by junwang
//        requestRuntimePermissions();
        ShotFix.hotFix(BugleApplication.this);
        updatePrepare();
        sSystemUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        Trace.endSection();
        com.microfountain.rcs.log.RcsLogger.setILogger(new ILogger(){
            @Override
            public void v(String s, String s1) {
                LogUtil.v(s, s1);
            }

            @Override
            public void v(String s, String s1, Throwable throwable) {

            }

            @Override
            public void d(String s, String s1) {
                LogUtil.d(s, s1);
            }

            @Override
            public void d(String s, String s1, Throwable throwable) {

            }

            @Override
            public void i(String s, String s1) {
                LogUtil.i(s, s1);
            }

            @Override
            public void i(String s, String s1, Throwable throwable) {

            }

            @Override
            public void w(String s, String s1) {
                LogUtil.w(s, s1);
            }

            @Override
            public void w(String s, String s1, Throwable throwable) {

            }

            @Override
            public void w(String s, Throwable throwable) {

            }

            @Override
            public void e(String s, String s1) {
                LogUtil.e(s, s1);
            }

            @Override
            public void e(String s, String s1, Throwable throwable) {

            }

            @Override
            public int getLogLevel() {
                return 0;
            }
        });

//        RcsKit.refreshServiceConnection();
//        Boolean isRCSSub = RcsKit.onUserActionsDetectedForRcsSubscription(PhoneUtils.getDefault()
//                .getDefaultSmsSubscriptionId()/*SubscriptionManager.getDefaultDataSubscriptionId()*/);

    }

    public void initRCSSDK(){
        new ContextWrapper(mContext).grantUriPermission("com.microfountain.rcs.service"/*"com.microfountain.rcs.rcskit"*/, MessagingContentProvider.CONVERSATIONS_URI, Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        com.microfountain.rcs.rcskit. RcsKit.init(mContext, "com.android.messaging.datamodel.microfountain.sms.provider.SmsMessageContentProvider"/*MessagingContentProvider.CONVERSATIONS_URI.toString()*//*"content://com.android.messaging.datamodel.MessagingContentProvider"*/);
        Boolean isRCSRegister = RCSUtil.isSubscriptionRcsRegistered(PhoneUtils.getDefault()
                .getDefaultSmsSubscriptionId()/*SubscriptionManager.getDefaultDataSubscriptionId()*/);
        Boolean isRCSSub = RCSUtil.isSubscriptionRcsEnabled(PhoneUtils.getDefault()
                .getDefaultSmsSubscriptionId());
        LogUtil.i("Junwang", "isRCSSub = "+isRCSSub+", isRCSRegister="+isRCSRegister);
//        registerRCSReceiver();

        RcsSubscriptionManager.addOnSubscriptionsChangedListener(new RcsSubscriptionManager.OnSubscriptionsChangedListener(){
            @Override
            public void onRcsSubscriptionsChanged() {
                RcsSubscriptionInfo rsi = RcsSubscriptionManager.getRcsSubscriptionInfo(PhoneUtils.getDefault()
                        .getDefaultSmsSubscriptionId());
                LogUtil.i("Junwang", "ProvisioningStatus="+rsi.getProvisioningStatus()+", SubscriptionStatus="+rsi.getSubscriptionStatus());
            }
        });
    }

    public void startRcsService(){
        ComponentName rcsService =new ComponentName("com.microfountain.rcs.service", "com.microfountain.rcs.service.RcsService");

        Intent intent =new Intent();

        intent.setComponent(rcsService);

        startService(intent);
    }
//    public void registerRCSReceiver(){
//        mXYRCSMsgReceiver = new XYRCSMsgReceiver();
//        IntentFilter filter = new IntentFilter(RcsMessageBroadcast.INTENT_ACTION_RECEIVE_NEW_MESSAGE);
//        registerReceiver(mXYRCSMsgReceiver, filter);
//    }

    public void updatePrepare(){
        Config config = new Config.Builder()
                .isDebug(true)
//                .isShowInternalDialog(false)     // 需要自定义弹框时打开注释
                .httpManager(new OkhttpManager())
                .build(this);
        UpdateManager.getInstance().init(config);
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Update conversation drawables when changing writing systems
        // (Right-To-Left / Left-To-Right)
        ConversationDrawables.get().updateDrawables();
    }

    // Called by the "real" factory from FactoryImpl.register() (i.e. not run in tests)
    public void initializeSync(final Factory factory) {
        Trace.beginSection("app.initializeSync");
        final Context context = factory.getApplicationContext();
        final BugleGservices bugleGservices = factory.getBugleGservices();
        final BuglePrefs buglePrefs = factory.getApplicationPrefs();
        final DataModel dataModel = factory.getDataModel();
        final CarrierConfigValuesLoader carrierConfigValuesLoader =
                factory.getCarrierConfigValuesLoader();

        maybeStartProfiling();

        BugleApplication.updateAppConfig(context);

        // Initialize MMS lib
        initMmsLib(context, bugleGservices, carrierConfigValuesLoader);
        // Initialize APN database
        ApnDatabase.initializeAppContext(context);
        // Fixup messages in flight if we crashed and send any pending
        dataModel.onApplicationCreated();
        // Register carrier config change receiver
        if (OsUtil.isAtLeastM()) {
            registerCarrierConfigChangeReceiver(context);
        }

        Trace.endSection();
    }

    private static void registerCarrierConfigChangeReceiver(final Context context) {
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                LogUtil.i(TAG, "Carrier config changed. Reloading MMS config.");
                MmsConfig.loadAsync();
            }
        }, new IntentFilter(CarrierConfigManager.ACTION_CARRIER_CONFIG_CHANGED));
    }

    private static void initMmsLib(final Context context, final BugleGservices bugleGservices,
            final CarrierConfigValuesLoader carrierConfigValuesLoader) {
        MmsManager.setApnSettingsLoader(new BugleApnSettingsLoader(context));
        MmsManager.setCarrierConfigValuesLoader(carrierConfigValuesLoader);
        MmsManager.setUserAgentInfoLoader(new BugleUserAgentInfoLoader(context));
        MmsManager.setUseWakeLock(true);
        // If Gservices is configured not to use mms api, force MmsManager to always use
        // legacy mms sending logic
        MmsManager.setForceLegacyMms(!bugleGservices.getBoolean(
                BugleGservicesKeys.USE_MMS_API_IF_PRESENT,
                BugleGservicesKeys.USE_MMS_API_IF_PRESENT_DEFAULT));
        bugleGservices.registerForChanges(new Runnable() {
            @Override
            public void run() {
                MmsManager.setForceLegacyMms(!bugleGservices.getBoolean(
                        BugleGservicesKeys.USE_MMS_API_IF_PRESENT,
                        BugleGservicesKeys.USE_MMS_API_IF_PRESENT_DEFAULT));
            }
        });
    }

    public static void updateAppConfig(final Context context) {
        // Make sure we set the correct state for the SMS/MMS receivers
        SmsReceiver.updateSmsReceiveHandler(context);
    }

    // Called from thread started in FactoryImpl.register() (i.e. not run in tests)
    public void initializeAsync(final Factory factory) {
        // Handle shared prefs upgrade & Load MMS Configuration
        Trace.beginSection("app.initializeAsync");
        maybeHandleSharedPrefsUpgrade(factory);
        MmsConfig.load();
        Trace.endSection();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        if (LogUtil.isLoggable(TAG, LogUtil.DEBUG)) {
            LogUtil.d(TAG, "BugleApplication.onLowMemory");
        }
        Factory.get().reclaimMemory();
    }

    @Override
    public void uncaughtException(final Thread thread, final Throwable ex) {
        final boolean background = getMainLooper().getThread() != thread;
        if (background) {
            LogUtil.e(TAG, "Uncaught exception in background thread " + thread, ex);

            final Handler handler = new Handler(getMainLooper());
            handler.post(new Runnable() {

                @Override
                public void run() {
                    sSystemUncaughtExceptionHandler.uncaughtException(thread, ex);
                }
            });
        } else {
//            sSystemUncaughtExceptionHandler.uncaughtException(thread, ex);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(getApplicationContext().getPackageName());
                    LaunchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(LaunchIntent);
                }
            }, 1000);// 1秒钟后重启应用
            RestartAPPTool.restartAPP(getApplicationContext(),100);
        }
    }

    private void maybeStartProfiling() {
        // App startup profiling support. To use it:
        //  adb shell setprop log.tag.BugleProfile DEBUG
        //  #   Start the app, wait for a 30s, download trace file:
        //  adb pull /data/data/com.android.messaging/cache/startup.trace /tmp
        //  # Open trace file (using adt/tools/traceview)
        if (android.util.Log.isLoggable(LogUtil.PROFILE_TAG, android.util.Log.DEBUG)) {
            // Start method tracing with a big enough buffer and let it run for 30s.
            // Note we use a logging tag as we don't want to wait for gservices to start up.
            final File file = DebugUtils.getDebugFile("startup.trace", true);
            if (file != null) {
                android.os.Debug.startMethodTracing(file.getAbsolutePath(), 160 * 1024 * 1024);
                new Handler(Looper.getMainLooper()).postDelayed(
                       new Runnable() {
                            @Override
                            public void run() {
                                android.os.Debug.stopMethodTracing();
                                // Allow world to see trace file
                                DebugUtils.ensureReadable(file);
                                LogUtil.d(LogUtil.PROFILE_TAG, "Tracing complete - "
                                     + file.getAbsolutePath());
                            }
                        }, 30000);
            }
        }
    }

    private void maybeHandleSharedPrefsUpgrade(final Factory factory) {
        final int existingVersion = factory.getApplicationPrefs().getInt(
                BuglePrefsKeys.SHARED_PREFERENCES_VERSION,
                BuglePrefsKeys.SHARED_PREFERENCES_VERSION_DEFAULT);
        final int targetVersion = Integer.parseInt(getString(R.string.pref_version));
        if (targetVersion > existingVersion) {
            LogUtil.i(LogUtil.BUGLE_TAG, "Upgrading shared prefs from " + existingVersion +
                    " to " + targetVersion);
            try {
                // Perform upgrade on application-wide prefs.
                factory.getApplicationPrefs().onUpgrade(existingVersion, targetVersion);
                // Perform upgrade on each subscription's prefs.
                PhoneUtils.forEachActiveSubscription(new PhoneUtils.SubscriptionRunnable() {
                    @Override
                    public void runForSubscription(final int subId) {
                        factory.getSubscriptionPrefs(subId)
                                .onUpgrade(existingVersion, targetVersion);
                    }
                });
                factory.getApplicationPrefs().putInt(BuglePrefsKeys.SHARED_PREFERENCES_VERSION,
                        targetVersion);
            } catch (final Exception ex) {
                // Upgrade failed. Don't crash the app because we can always fall back to the
                // default settings.
                LogUtil.e(LogUtil.BUGLE_TAG, "Failed to upgrade shared prefs", ex);
            }
        } else if (targetVersion < existingVersion) {
            // We don't care about downgrade since real user shouldn't encounter this, so log it
            // and ignore any prefs migration.
            LogUtil.e(LogUtil.BUGLE_TAG, "Shared prefs downgrade requested and ignored. " +
                    "oldVersion = " + existingVersion + ", newVersion = " + targetVersion);
        }
    }

    //add by junwang
    public static Context getContext() {
        return mContext;
    }

    //add by junwang
    //6.0之后要动态获取权限，重要！！！
//    private void requestRuntimePermissions(){
//        ActivityCompat.requestPermissions(this.getApplicationContext(), new String[]{
//                "android.permission.READ_PHONE_STATE" ,
//                "android.permission.CHANGE_NETWORK_STATE" ,
//                "android.permission.ACCESS_NETWORK_STATE" ,
//                "android.permission.ACCESS_WIFI_STATE" ,
//                "android.permission.INTERNET",
//                "android.permission.ACCESS_FINE_LOCATION",
//                "android.permission.ACCESS_COARSE_LOCATION",
//                "android.permission.RECEIVE_SMS",
//                "android.permission.CALL_PHONE"
//        }, 1);
//    }
//    protected void judgePermission() {
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            // 检查该权限是否已经获取
//            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
//
//            // sd卡权限
//            String[] SdCardPermission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
//            if (ContextCompat.checkSelfPermission(this, SdCardPermission[0]) != PackageManager.PERMISSION_GRANTED) {
//                // 如果没有授予该权限，就去提示用户请求
//                ActivityCompat.requestPermissions(this, SdCardPermission, 100);
//            }
//
//            //手机状态权限
//            String[] readPhoneStatePermission = {Manifest.permission.READ_PHONE_STATE};
//            if (ContextCompat.checkSelfPermission(this, readPhoneStatePermission[0]) != PackageManager.PERMISSION_GRANTED) {
//                // 如果没有授予该权限，就去提示用户请求
//                ActivityCompat.requestPermissions(this, readPhoneStatePermission, 200);
//            }
//
//            //定位权限
//            String[] locationPermission = {Manifest.permission.ACCESS_FINE_LOCATION};
//            if (ContextCompat.checkSelfPermission(this, locationPermission[0]) != PackageManager.PERMISSION_GRANTED) {
//                // 如果没有授予该权限，就去提示用户请求
//                ActivityCompat.requestPermissions(this, locationPermission, 300);
//            }
//
//            String[] ACCESS_COARSE_LOCATION = {Manifest.permission.ACCESS_COARSE_LOCATION};
//            if (ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION[0]) != PackageManager.PERMISSION_GRANTED) {
//                // 如果没有授予该权限，就去提示用户请求
//                ActivityCompat.requestPermissions(this, ACCESS_COARSE_LOCATION, 400);
//            }
//
//
//            String[] READ_EXTERNAL_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE};
//            if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE[0]) != PackageManager.PERMISSION_GRANTED) {
//                // 如果没有授予该权限，就去提示用户请求
//                ActivityCompat.requestPermissions(this, READ_EXTERNAL_STORAGE, 500);
//            }
//
//            String[] WRITE_EXTERNAL_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
//            if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE[0]) != PackageManager.PERMISSION_GRANTED) {
//                // 如果没有授予该权限，就去提示用户请求
//                ActivityCompat.requestPermissions(this, WRITE_EXTERNAL_STORAGE, 600);
//            }
//
//            String[] CAMERAPERMISSION = {Manifest.permission.CAMERA};
//            if(ContextCompat.checkSelfPermission(this, CAMERAPERMISSION[0]) != PackageManager.PERMISSION_GRANTED){
//                ActivityCompat.requestPermissions(this, CAMERAPERMISSION, 700);
//            }
//
//            String[] SMSRECEIVERPERMISSION = {Manifest.permission.RECEIVE_SMS};
//            if(ContextCompat.checkSelfPermission(this, SMSRECEIVERPERMISSION[0]) != PackageManager.PERMISSION_GRANTED){
//                ActivityCompat.requestPermissions(this, SMSRECEIVERPERMISSION, 800);
//            }
//
//            String[] CALLPERMISSION = {Manifest.permission.CALL_PHONE};
//            if(ContextCompat.checkSelfPermission(this, CALLPERMISSION[0]) != PackageManager.PERMISSION_GRANTED){
//                ActivityCompat.requestPermissions(this, CALLPERMISSION, 900);
//            }
//
//            String[] OVERLAYPERMISSION = {Settings.ACTION_MANAGE_OVERLAY_PERMISSION};
//            if(ContextCompat.checkSelfPermission(this, CALLPERMISSION[0]) != PackageManager.PERMISSION_GRANTED){
//                ActivityCompat.requestPermissions(this, CALLPERMISSION, 1000);
//            }
//
//        }else{
//
//        }
//
//    }
}
