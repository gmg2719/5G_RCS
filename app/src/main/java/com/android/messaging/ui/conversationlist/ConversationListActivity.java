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

package com.android.messaging.ui.conversationlist;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.AsyncQueryHandler;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.messaging.R;
import com.android.messaging.datamodel.DownloadBusnCardBroadcastReceiver;
import com.android.messaging.datamodel.MessagingContentProvider;
import com.android.messaging.datamodel.microfountain.sms.ChatbotUtils;
import com.android.messaging.receiver.DownloadChatbotFileReceiver;
import com.android.messaging.receiver.XYRCSMsgReceiver;
import com.android.messaging.ui.ConversationDrawables;
import com.android.messaging.ui.UIIntents;
import com.android.messaging.ui.conversation.chatbot.ChatbotFavoriteActivity;
import com.android.messaging.util.DebugUtils;
import com.android.messaging.util.LogUtil;
import com.android.messaging.util.PhoneUtils;
import com.android.messaging.util.RCSUtil;
import com.android.messaging.util.Trace;
import com.android.messaging.util.UiUtils;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.lwy.smartupdate.UpdateManager;
import com.microfountain.rcs.aidl.broadcast.RcsChatbotBroadcast;
import com.microfountain.rcs.aidl.broadcast.RcsMessageBroadcast;
import com.microfountain.rcs.aidl.database.contract.RcsChatbotInfoTable;
import com.microfountain.rcs.rcskit.service.RcsSubscriptionInfo;
import com.microfountain.rcs.rcskit.service.RcsSubscriptionManager;
import com.microfountain.rcs.support.config.RcsServiceConfigXMLHelper;
import com.microfountain.rcs.support.model.chatbot.Chatbot;
import com.microfountain.rcs.support.model.chatbot.ChatbotInfoQueryResult;
import com.microfountain.rcs.support.model.chatbot.ChatbotInfoQueryResultParser;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;

import static com.microfountain.rcs.rcskit.service.RcsSubscriptionManager.getEnabledSubscriptionId;


public class  ConversationListActivity extends AbstractConversationListActivity implements View.OnClickListener{
    //add by junwang start
    ConversationListFragment mNormalConListFragment;
    ConversationListFragment mH5ConListFragment;
    Button mNormalSmsButton;
    Button mH5SmsButton;
    FragmentManager mFragmentManager;
    DownloadBusnCardBroadcastReceiver mReceiver;
    public static boolean isNormalConversationList = false;
    private XYRCSMsgReceiver mXYRCSMsgReceiver;
    private DownloadChatbotFileReceiver mDownloadChatbotFileReceiver;
    private Handler mHandler = new Handler();
    private QueryHandler mQueryHandler;
    private String mChatbotSipUri;
    private String mContactUri;
    private String mDomain;
    private Chatbot mChatbot;
    private PagerSlidingTabStrip tabs;
    private DisplayMetrics dm;

    private static final int TOKEN_QUERY_CHATBOT_INFO = 0;
    private static final int TOKEN_UPDATE_SAVE_LOCAL = 1;
    private static final int TOKEN_QUERY_SAVE_LOCAL = 2;
    private static final int TOKEN_QUERY_SAVE_LOCAL_OPERATION = 3;
    private static final int TOKEN_INSERT_SAVE_LOCAL = 4;
    private static final int TOKEN_DELETE_SAVE_LOCAL = 5;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogUtil.i("Junwang", "onReceive:" + action);
            switch (action) {
                case RcsChatbotBroadcast.INTENT_ACTION_CHATBOT_INFO_CHANGED:
                    onChatbotInfoUpdateResult(intent);
                    break;
                case RcsChatbotBroadcast.INTENT_ACTION_SPECIFIC_CHATBOT_LIST_CHANGED:
                    //网络应急Chatbot和网络黑名单Chatbot变化
                    startQuery();
                    break;
                default:
                    break;
            }
        }
    };

    private void onChatbotInfoUpdateResult(Intent intent) {
        int responseCode = intent.getIntExtra(RcsChatbotBroadcast.INTENT_EXTRA_HTTP_RESPONSE_CODE, 0);
        LogUtil.i("Junwang", "onChatbotInfoUpdateResult responseCode:" + responseCode);
        if (responseCode >= HttpURLConnection.HTTP_OK && responseCode < HttpURLConnection.HTTP_MULT_CHOICE) {
            String chatbotSipUri = intent.getStringExtra(RcsChatbotBroadcast.INTENT_EXTRA_CHATBOT_SIP_URI_STRING);
            LogUtil.i("Junwang", "onChatbotInfoUpdateResult chatbotSipUri:" + chatbotSipUri);
            if (TextUtils.equals(chatbotSipUri, mChatbotSipUri)) {
                //请求刷新Chatbot详情成功，查询数据库
                startQuery();
            }
        } else {
            String extraData = intent.getStringExtra(RcsChatbotBroadcast.INTENT_EXTRA_EXTRA_DATA_STRING);
            LogUtil.i("Junwang", "onChatbotInfoUpdateResult extraData:" + extraData);
//            if (extraData != null && !extraData.isEmpty()) {
//                CmccChallengeInfo cmccChallengeInfo = new CmccChallengeInfo(extraData);
//                LogApp.i(TAG, "onChatbotInfoUpdateResult:" + cmccChallengeInfo);
//                // TODO: 2020/3/13 如果统一认证SDK报错，通过extraData上报错误信息，需要客户端处理相应的错误
//            }
        }
    }

    private ContentObserver mContentObserver = new ContentObserver(mHandler) {

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            startQuery();
            LogUtil.i("Junwang", "contentObserver onChange");
        }
    };

    private void startQuery() {
        startQueryChatbotInfo();
        startQuerySaveLocal(TOKEN_QUERY_SAVE_LOCAL);
    }

    private void startQueryChatbotInfo() {

        String selection;
        String[] selectionArgs;
        if (TextUtils.isEmpty(mDomain)) {
            selection = RcsChatbotInfoTable.Columns.CHATBOT_SIP_URI + " = ?";
            selectionArgs = new String[]{mChatbotSipUri};
        } else {
            selection = RcsChatbotInfoTable.Columns.DOMAIN + " = ?" + " AND " + RcsChatbotInfoTable.Columns.CHATBOT_SIP_URI + " = ?";
            selectionArgs = new String[]{mDomain, mChatbotSipUri};
        }

        mQueryHandler.startQuery(TOKEN_QUERY_CHATBOT_INFO, null, RcsChatbotInfoTable.CONTENT_URI, null, selection, selectionArgs, null);
    }

    private void startQuerySaveLocal(int token) {
//        String selection = SmsDatabaseTables.SaveLocalItem.Columns.URI + "=?";
//        String[] selectionArgs = new String[]{mChatbotSipUri};
//        mQueryHandler.startQuery(token, null, SmsDatabaseTables.SaveLocalItem.CONTENT_URI, null, selection, selectionArgs, null);
    }
    // 展示文字
    public Chatbot bind(Cursor cursor) {

        String chatbotSipUri = cursor.getString(cursor.getColumnIndex(RcsChatbotInfoTable.Columns.CHATBOT_SIP_URI));

        byte[] jsonData = cursor.getBlob(cursor.getColumnIndex(RcsChatbotInfoTable.Columns.JSON_DATA));
        cursor.getString(cursor.getColumnIndex(RcsChatbotInfoTable.Columns.DOMAIN));

        String email = "";
        String provider = "";
        String website = "";

        String name = "";
        String iconUrl = "";
        String sms = "";
        String description = "";

        boolean verified = false;

        ChatbotInfoQueryResult chatbotInfoQueryResult = ChatbotInfoQueryResultParser.parse(jsonData);

        if (chatbotInfoQueryResult != null) {

            verified = chatbotInfoQueryResult.isVerified();

            if (chatbotInfoQueryResult.chatbotInfo != null) {
                email = chatbotInfoQueryResult.chatbotInfo.getEmail();
                provider = chatbotInfoQueryResult.chatbotInfo.getProvider();
                website = chatbotInfoQueryResult.chatbotInfo.getWebsite();

                name = chatbotInfoQueryResult.chatbotInfo.getDisplayName();
                iconUrl = chatbotInfoQueryResult.chatbotInfo.getIconUrl();
                sms = chatbotInfoQueryResult.chatbotInfo.getSmsNumber();
                description = chatbotInfoQueryResult.chatbotInfo.getDescription();
//                String menuJson = chatbotInfoQueryResult.persistentMenu.toString();
                LogUtil.i("Junwang", "chatbotInfoQueryResult="+chatbotInfoQueryResult.toString());
            }
            if(chatbotInfoQueryResult.persistentMenu != null){
                LogUtil.i("Junwang", "persistentMenu="+chatbotInfoQueryResult.persistentMenu.toString());
            }else{
                LogUtil.i("Junwang", "persistentMenu is null");
            }

        }

        return new Chatbot(chatbotSipUri, name, iconUrl, verified, sms, email, "", provider, website, description);
    }

    private void onQueryResult(Cursor cursor) {
        LogUtil.i("Junwang", "onQueryResult cursor: " + cursor.getCount());
        if (cursor != null && cursor.moveToFirst()) {
            byte[] jsonData = cursor.getBlob(cursor.getColumnIndex(RcsChatbotInfoTable.Columns.JSON_DATA));
            mContactUri = cursor.getString(cursor.getColumnIndex(RcsChatbotInfoTable.Columns.CHATBOT_SIP_URI));
            if (jsonData != null && jsonData.length > 0) {
                Chatbot chatbot = bind(cursor);
                mChatbot = chatbot;
//                updateUI(chatbot);
            }
        }
    }

    private void insertSaveLocal() {
    }

    private void deleteSaveLocal() {
    }

    private void setSaveLocalText(boolean saveLocal) {
    }

    private void updateSaveLocalResult(int result) {
        LogUtil.v("Junwang", "updateSaveLocalResult: " + result);
        startQuery();
    }

    public void initData(String chatbotSipUri, String domain) {

        Intent intent = getIntent();

//        mChatbotSipUri = intent.getStringExtra(UIConfig.EXTRA_CHATBOT_SIP_URI);
        mChatbotSipUri = chatbotSipUri;
        if (TextUtils.isEmpty(mChatbotSipUri)) {
            Toast.makeText(this, "参数错误", Toast.LENGTH_LONG).show();

            mChatbotSipUri = "sip:10658139@botplatform.rcs.chinamobile.com";
        }

//        mDomain = intent.getStringExtra(UIConfig.EXTRA_CHATBOT_DOMAIN);
        mDomain = domain;
        LogUtil.i("Junwang", "initData mDomain: " + mDomain);

        if (TextUtils.isEmpty(mDomain)) {
            int subscriptionId = getEnabledSubscriptionId();
            if (subscriptionId > 0) {
                RcsServiceConfigXMLHelper xmlHelper = ChatbotUtils.getRcsServiceConfigXMLHelper(subscriptionId);
                if (xmlHelper != null) {
                    mDomain = xmlHelper.getChatbotInfoDomain();
                    LogUtil.i("Junwang", "mDomain= " + mDomain);
                }
            }
        }
    }

    private static final class QueryHandler extends AsyncQueryHandler {

        private final WeakReference<ConversationListActivity> mReference;

        public QueryHandler(ContentResolver cr, ConversationListActivity activity) {
            super(cr);
            mReference = new WeakReference<>(activity);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            super.onQueryComplete(token, cookie, cursor);

            if (mReference.get() == null) {
                return;
            }
            if (token == TOKEN_QUERY_CHATBOT_INFO) {
                mReference.get().onQueryResult(cursor);
            } else if (token == TOKEN_QUERY_SAVE_LOCAL) {
                if (cursor == null || cursor.getCount() == 0) {
                    mReference.get().setSaveLocalText(false);
                } else {
                    mReference.get().setSaveLocalText(true);
                }
            } else if (token == TOKEN_QUERY_SAVE_LOCAL_OPERATION) {
                if (cursor == null || cursor.getCount() == 0) {
                    mReference.get().insertSaveLocal();
                } else {
                    mReference.get().deleteSaveLocal();
                }
            }
        }

        @Override
        protected void onUpdateComplete(int token, Object cookie, int result) {
            super.onUpdateComplete(token, cookie, result);
            if (mReference.get() == null) {
                return;
            }
            if (token == TOKEN_UPDATE_SAVE_LOCAL) {
                mReference.get().updateSaveLocalResult(result);
            }
        }

        @Override
        protected void onInsertComplete(int token, Object cookie, Uri uri) {
            super.onInsertComplete(token, cookie, uri);
            if (mReference.get() == null) {
                return;
            }
            if (token == TOKEN_INSERT_SAVE_LOCAL) {
                mReference.get().setSaveLocalText(true);
            }
        }

        @Override
        protected void onDeleteComplete(int token, Object cookie, int result) {
            super.onDeleteComplete(token, cookie, result);
            if (mReference.get() == null) {
                return;
            }
            if (token == TOKEN_DELETE_SAVE_LOCAL) {
                mReference.get().setSaveLocalText(false);
            }
        }
    }

    public final static String[] imageUrls = new String[]{
            "https://img-my.csdn.net/uploads/201508/05/1438760758_3497.jpg",
            "https://img-my.csdn.net/uploads/201508/05/1438760758_6667.jpg",
            "https://img-my.csdn.net/uploads/201508/05/1438760757_3588.jpg",
            "https://img-my.csdn.net/uploads/201508/05/1438760756_3304.jpg",
            "https://img-my.csdn.net/uploads/201508/05/1438760755_6715.jpeg",
            "https://img-my.csdn.net/uploads/201508/05/1438760726_5120.jpg",
            "https://img-my.csdn.net/uploads/201508/05/1438760726_8364.jpg",
            "https://img-my.csdn.net/uploads/201508/05/1438760725_4031.jpg",
            "https://img-my.csdn.net/uploads/201508/05/1438760724_9463.jpg",
            "https://img-my.csdn.net/uploads/201508/05/1438760724_2371.jpg",
            "https://img-my.csdn.net/uploads/201508/05/1438760707_4653.jpg",
            "https://img-my.csdn.net/uploads/201508/05/1438760706_6864.jpg"
    };
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        Trace.beginSection("ConversationListActivity.onCreate");
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.conversation_list_activity);

        setContentView(R.layout.conversation_list_table);
        //add by junwang
        SDKInitializer.initialize(getApplicationContext());
        SDKInitializer.setCoordType(CoordType.BD09LL);
        ZXingLibrary.initDisplayOpinion(this);
        judgePermission();
        initRCSSDK();
        mQueryHandler = new QueryHandler(getApplication().getContentResolver(), this);
//        checkUpdate();
//        com.microfountain.rcs.rcskit. RcsKit.init(this, MessagingContentProvider.CONVERSATIONS_URI.toString()/*"content://com.android.messaging.datamodel.MessagingContentProvider"*/);
//        RcsKit.refreshServiceConnection();
//        Boolean isRCSSub = RcsKit.onUserActionsDetectedForRcsSubscription(PhoneUtils.getDefault()
//                .getDefaultSmsSubscriptionId()/*SubscriptionManager.getDefaultDataSubscriptionId()*/);
//        Boolean isRCSRegister = RCSUtil.isSubscriptionRcsRegistered(PhoneUtils.getDefault()
//                .getDefaultSmsSubscriptionId()/*SubscriptionManager.getDefaultDataSubscriptionId()*/);
//        Boolean isRCSSub = RCSUtil.isSubscriptionRcsEnabled(PhoneUtils.getDefault()
//                .getDefaultSmsSubscriptionId());
//        LogUtil.i("Junwang", "isRCSSub = "+isRCSSub+", isRCSRegister="+isRCSRegister);
        Trace.endSection();
//        invalidateActionBar();
//        mNormalSmsButton = (Button)findViewById(R.id.normal_sms);
//        mNormalSmsButton.setOnClickListener(this);
//        mH5SmsButton = (Button)findViewById(R.id.h5_sms);
//        mH5SmsButton.setOnClickListener(this);
//        ActionBar actionBar = getSupportActionBar();
//        final int actionBarColor = ConversationDrawables.get().getActionbarColor();
//        actionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));
//        UiUtils.setStatusBarColor(this, actionBarColor);
        mFragmentManager = getFragmentManager();
        dm = getResources().getDisplayMetrics();
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pager.setAdapter(new MyPagerAdapter(mFragmentManager));
        tabs.setViewPager(pager);
        setTabsValue();

//        setTabSelection(0);
//        startReceiver();
//        Intent intent = new Intent(this, BusinessCardService.class);
//        startService(intent);
//        ArrayList al = new ArrayList(Arrays.asList(imageUrls));
//        DownloadImageUtils.saveImagesToLocal(this, al);
//        BusinessCardService.BusinessCard.loadData();
    }

    private void setTabsValue() {
        // 设置Tab是自动填充满屏幕的
        tabs.setShouldExpand(true);
        // 设置Tab的分割线是透明的
        tabs.setDividerColor(Color.TRANSPARENT);
        // 设置Tab底部线的高度
        tabs.setUnderlineHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 1, dm));
        // 设置Tab Indicator的高度
        tabs.setIndicatorHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 2, dm));
        // 设置Tab标题文字的大小
        tabs.setTextSize((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 16, dm));
        // 设置Tab Indicator的颜色
        tabs.setIndicatorColor(Color.parseColor("#4F7BFF"));
        // 设置选中Tab文字的颜色 (这是我自定义的一个方法)
//        tabs.setSelectedTextColor(Color.parseColor("#45c01a"));
        // 取消点击Tab时的背景色
        tabs.setTabBackground(0);
    }


    public class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private final String[] titles = { "通知", "个人"};

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public Fragment getItem(int position) {
            LogUtil.i("Junwang", "position="+position);
            switch(position){
                case 0:
                    if(mNormalConListFragment == null) {
                        mNormalConListFragment = ConversationListFragment.createConversationListFragment(null, ConversationListFragment.BUNDLE_NORMAL_MESSAGE_MODE);
                    }
                    return mNormalConListFragment;
                case 1:
                    if(mH5ConListFragment == null){
                        mH5ConListFragment = ConversationListFragment.createConversationListFragment(null, ConversationListFragment.BUNDLE_H5_MESSAGE_MODE);
                    }
                    return mH5ConListFragment;
                default:
                    return null;
            }
        }
    }


    private String manifestJsonUrl = "https://raw.githubusercontent.com/itlwy/AppSmartUpdate/master/resources/app/UpdateManifest.json";
    private void checkUpdate() {
        UpdateManager.getInstance().update(this, manifestJsonUrl);
    }

    private void startReceiver() {
        LogUtil.i("tag", "startReceiver");
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        mReceiver = new DownloadBusnCardBroadcastReceiver();
        this.registerReceiver(mReceiver, filter);
    }

    private void stopReceiver() {
        Log.i("tag", "stopReceiver");
        if (mReceiver != null) {
            this.unregisterReceiver(mReceiver);
            mReceiver = null;
        }

        if(mXYRCSMsgReceiver != null){
            this.unregisterReceiver(mXYRCSMsgReceiver);
            mXYRCSMsgReceiver = null;
        }
        if(mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
            getContentResolver().unregisterContentObserver(mContentObserver);
        }
        if(mDownloadChatbotFileReceiver != null){
            this.unregisterReceiver(mDownloadChatbotFileReceiver);
            mDownloadChatbotFileReceiver = null;
        }
    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.normal_sms:
//                setTabSelection(0);
//                break;
//            case R.id.h5_sms:
////                ChatbotInfoTableUtils.deleteChatbotInfoTable();
//                setTabSelection(1);
//                break;
//            default:
//                break;
//        }
    }

    public static boolean IsNormalConversationList(){
        return isNormalConversationList;
    }

    public static void setNormalConversationList(boolean isNormalConList){
        isNormalConversationList = isNormalConList;
    }

    private void setTabSelection(int index){
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        hideFragments(ft);
        switch (index){
            case 0:
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        BusinessCardService.getBusnCard();
//                    }
//                }).start();
                isNormalConversationList = true;
                mNormalSmsButton.setBackgroundResource(R.drawable.border_button);
                mH5SmsButton.setBackground(null);
                //mNormalConListFragment = new ConversationListFragment();
                mNormalConListFragment = ConversationListFragment.createConversationListFragment(null, ConversationListFragment.BUNDLE_NORMAL_MESSAGE_MODE);
//                mNormalConListFragment = ConversationListFragment.createConversationListFragment(null, ConversationListFragment.BUNDLE_H5_MESSAGE_MODE);
                ft.add(R.id.conversation_list_content, mNormalConListFragment);
                ft.commit();
                break;
            case 1:
//                Intent intent = new Intent(this, BusinessCardService.class);
//                startService(intent);
                isNormalConversationList = false;
                mNormalSmsButton.setBackground(null);
                mH5SmsButton.setBackgroundResource(R.drawable.border_button);
                mH5ConListFragment = ConversationListFragment.createConversationListFragment(null, ConversationListFragment.BUNDLE_H5_MESSAGE_MODE);
//                mH5ConListFragment = ConversationListFragment.createConversationListFragment(null, ConversationListFragment.BUNDLE_NORMAL_MESSAGE_MODE);
                ft.add(R.id.conversation_list_content, mH5ConListFragment);
                ft.commit();
                Boolean isRCSRegister = RCSUtil.isSubscriptionRcsRegistered(PhoneUtils.getDefault()
                        .getDefaultSmsSubscriptionId()/*SubscriptionManager.getDefaultDataSubscriptionId()*/);
                Boolean isRCSSub = RCSUtil.isSubscriptionRcsEnabled(PhoneUtils.getDefault()
                        .getDefaultSmsSubscriptionId());
                TelephonyManager tm = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
//                String authen = tm.getIccAuthentication(TelephonyManager.APPTYPE_USIM, TelephonyManager.AUTHTYPE_EAP_AKA, "9plxBx4sjRqQDfDkirL9siLGVjX18HJMYOsZdUkfhI=");
                LogUtil.i("Junwang", "isRCSSub1 = "+isRCSSub+", isRCSRegister="+isRCSRegister);
                break;
            default:
                break;
        }
    }

    public void initRCSSDK(){
        new ContextWrapper(this).grantUriPermission("com.microfountain.rcs.service", /*Uri.parse("content://xy_rcs/")*/MessagingContentProvider.CHATBOT_LOGOS_URI, Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        com.microfountain.rcs.rcskit. RcsKit.init(this, /*MessagingContentProvider.CONVERSATIONS_URI.toString()*//*SmsMessageContentProvider.CONTENT_AUTHORITY*//*"content://xy_rcs/"*/MessagingContentProvider.CHATBOT_LOGOS_URI.toString());
        Boolean isRCSRegister = RCSUtil.isSubscriptionRcsRegistered(PhoneUtils.getDefault()
                .getDefaultSmsSubscriptionId()/*SubscriptionManager.getDefaultDataSubscriptionId()*/);
        Boolean isRCSSub = RCSUtil.isSubscriptionRcsEnabled(PhoneUtils.getDefault()
                .getDefaultSmsSubscriptionId());
        LogUtil.i("Junwang", "isRCSSub = "+isRCSSub+", isRCSRegister="+isRCSRegister);
        registerRCSReceiver();
        registerDownloadChatbotFileReceiver();

        RcsSubscriptionManager.addOnSubscriptionsChangedListener(new RcsSubscriptionManager.OnSubscriptionsChangedListener(){
            @Override
            public void onRcsSubscriptionsChanged() {
                RcsSubscriptionInfo rsi = RcsSubscriptionManager.getRcsSubscriptionInfo(PhoneUtils.getDefault()
                        .getDefaultSmsSubscriptionId());
                if(rsi != null) {
                    LogUtil.i("Junwang", "ProvisioningStatus=" + rsi.getProvisioningStatus() + ", SubscriptionStatus=" + rsi.getSubscriptionStatus());
                }
            }
        });
        //send text rcs message
//        SendRcsMsgUtils.sendTextMessage(this, "sip:1065051121176@botplatform.rcs.chinamobile.com",
//                "0b8ff2b9-f16d-4bd5-84e0-dba76b0d54e2", UUID.randomUUID().toString(), "send to chatbot content 123",
//                RcsMessageContactIdentityType.CHATBOT, PhoneUtils.getDefault()
//                        .getDefaultSmsSubscriptionId());
    }

    public void registerRCSReceiver(){
        mXYRCSMsgReceiver = new XYRCSMsgReceiver();
        IntentFilter filter = new IntentFilter(RcsMessageBroadcast.INTENT_ACTION_RECEIVE_NEW_MESSAGE);
        registerReceiver(mXYRCSMsgReceiver, filter);

        IntentFilter intentFilter = new IntentFilter(RcsChatbotBroadcast.INTENT_ACTION_CHATBOT_INFO_CHANGED);
        intentFilter.addCategory(RcsChatbotBroadcast.INTENT_ACTION_SPECIFIC_CHATBOT_LIST_CHANGED);
        registerReceiver(mBroadcastReceiver, intentFilter);

        getContentResolver().registerContentObserver(RcsChatbotInfoTable.CONTENT_URI, true, mContentObserver);
    }

    public void registerDownloadChatbotFileReceiver(){
        mDownloadChatbotFileReceiver = new DownloadChatbotFileReceiver();
        IntentFilter filter = new IntentFilter(RcsMessageBroadcast.INTENT_ACTION_MESSAGE_DOWNLOAD_RESULT);
        registerReceiver(mDownloadChatbotFileReceiver, filter);
    }

    private void hideFragments(FragmentTransaction transaction) {
        if (mNormalConListFragment != null) {
            transaction.hide(mNormalConListFragment);
        }
        if (mH5ConListFragment != null) {
            transaction.hide(mH5ConListFragment);
        }
    }

    private void updateActionAndStatusBarColor(final ActionBar actionBar) {
        final int actionBarColor = ConversationDrawables.get().getActionbarColor();
        actionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));

        UiUtils.setStatusBarColor(this, actionBarColor);
    }

    @Override
    protected void updateActionBar(final ActionBar actionBar) {
//        actionBar.setTitle(getString(R.string.app_name));
//        actionBar.setDisplayShowTitleEnabled(true);
//        actionBar.setDisplayHomeAsUpEnabled(false);
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
//        actionBar.setBackgroundDrawable(new ColorDrawable(
//                getResources().getColor(R.color.action_bar_background_color)));
//        actionBar.show();
//        super.updateActionBar(actionBar);
        updateActionAndStatusBarColor(actionBar);
        // We update this regardless of whether or not the action bar is showing so that we
        // don't get a race when it reappears.
//        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        View customView = ((LayoutInflater)
                this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.action_bar_conversation, null);
        ActionBar.LayoutParams lp =new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        actionBar.setCustomView(customView, lp);
//        actionBar.setDisplayShowHomeEnabled(false);
//        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setHomeAsUpIndicator(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Invalidate the menu as items that are based on settings may have changed
        // while not in the app (e.g. Talkback enabled/disable affects new conversation
        // button)
        supportInvalidateOptionsMenu();
        invalidateActionBar();
        //add by junwang
//        if(isNormalConversationList){
//            mNormalSmsButton.setBackgroundResource(R.drawable.border_button);
//            mH5SmsButton.setBackground(null);
//        }else{
//            mNormalSmsButton.setBackground(null);
//            mH5SmsButton.setBackgroundResource(R.drawable.border_button);
//        }
//        initData(/*"sip:1065051121304@botplatform.rcs.chinamobile.com"*/"sip:1065051121306@botplatform.rcs.chinamobile.com", "botinfo01.hdn.rcs.chinamobile.com:443/chatbotserver");
//        ChatbotUtils.requestChatbotInfo(/*"sip:1065051121304@botplatform.rcs.chinamobile.com"*/"sip:1065051121306@botplatform.rcs.chinamobile.com");
//
//        startQuery();
    }

    @Override
    public void onBackPressed() {
        if (isInConversationListSelectMode()) {
            exitMultiSelectState();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        if (super.onCreateOptionsMenu(menu)) {
            return true;
        }
        getMenuInflater().inflate(R.menu.conversation_list_fragment_menu, menu);
        final MenuItem item = menu.findItem(R.id.action_debug_options);
        if (item != null) {
            final boolean enableDebugItems = DebugUtils.isDebugEnabled();
            item.setVisible(enableDebugItems).setEnabled(enableDebugItems);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.action_start_new_conversation:
                //add by junwang
                setNormalConversationList(true);
                onActionBarStartNewConversation();
                return true;
            case R.id.action_settings:
                onActionBarSettings();
                return true;
            case R.id.my_favorite:
                ChatbotFavoriteActivity.start(this);
                return true;
            case R.id.action_search_chatbot:
                ChatbotSearchActivity.start(this);
                break;
//            case R.id.action_debug_options:
//                onActionBarDebug();
//                return true;
//            case R.id.action_show_blocked_contacts:
//                onActionBarBlockedParticipants();
//                return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onActionBarHome() {
        exitMultiSelectState();
    }

    public void onActionBarStartNewConversation() {
        UIIntents.get().launchCreateNewConversationActivity(this, null);
    }

    public void onActionBarSettings() {
        UIIntents.get().launchSettingsActivity(this);
    }

    public void onActionBarBlockedParticipants() {
        UIIntents.get().launchBlockedParticipantsActivity(this);
    }

    public void onActionBarArchived() {
        UIIntents.get().launchArchivedConversationsActivity(this);
    }

    @Override
    public boolean isSwipeAnimatable() {
        return !isInConversationListSelectMode();
    }

//    @Override
//    public void onWindowFocusChanged(final boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        final ConversationListFragment conversationListFragment =
//                (ConversationListFragment) getFragmentManager().findFragmentById(
//                        R.id.conversation_list_fragment);
//        // When the screen is turned on, the last used activity gets resumed, but it gets
//        // window focus only after the lock screen is unlocked.
//        if (hasFocus && conversationListFragment != null) {
//            conversationListFragment.setScrolledToNewestConversationIfNeeded();
//        }
//    }
@Override
    public void onWindowFocusChanged(final boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if(mNormalConListFragment != null && mNormalConListFragment.isVisible()){
                mNormalConListFragment.setScrolledToNewestConversationIfNeeded();
            }else if(mH5ConListFragment != null && mH5ConListFragment.isVisible()) {
                mH5ConListFragment.setScrolledToNewestConversationIfNeeded();
            }
        }
    }

    //add by junwang
    //6.0之后要动态获取权限，重要！！！
    protected void judgePermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 检查该权限是否已经获取
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝

            // sd卡权限
            String[] SdCardPermission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (ContextCompat.checkSelfPermission(this, SdCardPermission[0]) != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                ActivityCompat.requestPermissions(this, SdCardPermission, 100);
            }

            //手机状态权限
            String[] readPhoneStatePermission = {Manifest.permission.READ_PHONE_STATE};
            if (ContextCompat.checkSelfPermission(this, readPhoneStatePermission[0]) != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                ActivityCompat.requestPermissions(this, readPhoneStatePermission, 200);
            }

            //定位权限
            String[] locationPermission = {Manifest.permission.ACCESS_FINE_LOCATION};
            if (ContextCompat.checkSelfPermission(this, locationPermission[0]) != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                ActivityCompat.requestPermissions(this, locationPermission, 300);
            }

            String[] ACCESS_COARSE_LOCATION = {Manifest.permission.ACCESS_COARSE_LOCATION};
            if (ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION[0]) != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                ActivityCompat.requestPermissions(this, ACCESS_COARSE_LOCATION, 400);
            }


            String[] READ_EXTERNAL_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE};
            if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE[0]) != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                ActivityCompat.requestPermissions(this, READ_EXTERNAL_STORAGE, 500);
            }

            String[] WRITE_EXTERNAL_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE[0]) != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                ActivityCompat.requestPermissions(this, WRITE_EXTERNAL_STORAGE, 600);
            }

            String[] CAMERAPERMISSION = {Manifest.permission.CAMERA};
            if(ContextCompat.checkSelfPermission(this, CAMERAPERMISSION[0]) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, CAMERAPERMISSION, 700);
            }

            String[] SMSRECEIVERPERMISSION = {Manifest.permission.RECEIVE_SMS};
            if(ContextCompat.checkSelfPermission(this, SMSRECEIVERPERMISSION[0]) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, SMSRECEIVERPERMISSION, 800);
            }

            String[] CALLPERMISSION = {Manifest.permission.CALL_PHONE};
            if(ContextCompat.checkSelfPermission(this, CALLPERMISSION[0]) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, CALLPERMISSION, 900);
            }

            String[] OVERLAYPERMISSION = {Settings.ACTION_MANAGE_OVERLAY_PERMISSION};
            if(ContextCompat.checkSelfPermission(this, CALLPERMISSION[0]) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, CALLPERMISSION, 1000);
            }

            String[] WIFICHANGEPERMISSION = {Manifest.permission.CHANGE_WIFI_STATE};
            if(ContextCompat.checkSelfPermission(this, WIFICHANGEPERMISSION[0]) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, WIFICHANGEPERMISSION, 1100);
            }

            String[] BINDRCSSERVICEPERMISSION = {"com.microfountain.rcs.BIND_RCS_SERVICE"};
            if(ContextCompat.checkSelfPermission(this, BINDRCSSERVICEPERMISSION[0]) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, BINDRCSSERVICEPERMISSION, 1200);
            }

            String[] XYRCVRCSBROADCASTPERMISSION = {"com.microfountain.rcs.RECEIVE_RCS_BROADCAST"};
            if(ContextCompat.checkSelfPermission(this, XYRCVRCSBROADCASTPERMISSION[0]) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, XYRCVRCSBROADCASTPERMISSION, 1300);
            }

            String[] RCSPROVIDERDATAREADPERMISSSION = {"com.microfountain.rcs.provider.data.READ"};
            if(ContextCompat.checkSelfPermission(this, RCSPROVIDERDATAREADPERMISSSION[0]) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, RCSPROVIDERDATAREADPERMISSSION, 1400);
            }

            String[] RCSPROVIDERDATAWRITEPERMISSSION = {"com.microfountain.rcs.provider.data.WRITE"};
            if(ContextCompat.checkSelfPermission(this, RCSPROVIDERDATAWRITEPERMISSSION[0]) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, RCSPROVIDERDATAWRITEPERMISSSION, 1500);
            }

            String[] RCSPROVIDERFILEREADPERMISSSION = {"com.microfountain.rcs.provider.file.READ"};
            if(ContextCompat.checkSelfPermission(this, RCSPROVIDERFILEREADPERMISSSION[0]) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, RCSPROVIDERFILEREADPERMISSSION, 1600);
            }

            String[] RCSPROVIDERFILEWRITEPERMISSSION = {"com.microfountain.rcs.provider.file.WRITE"};
            if(ContextCompat.checkSelfPermission(this, RCSPROVIDERFILEWRITEPERMISSSION[0]) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, RCSPROVIDERFILEWRITEPERMISSSION, 1700);
            }
        }else{

        }

    }

    @Override
    protected void onDestroy() {
//        if(mReceiver != null){
//            stopReceiver();
//        }
        stopReceiver();
        super.onDestroy();
    }
}
