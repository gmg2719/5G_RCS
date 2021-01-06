package com.android.messaging.datamodel.microfountain.sms.database;

import android.content.Intent;
import android.text.TextUtils;

import com.android.messaging.BuildConfig;
import com.android.messaging.datamodel.microfountain.sms.ChatbotUtils;
import com.android.messaging.util.LogUtil;
import com.microfountain.rcs.aidl.database.contract.RcsChatbotInfoTable;
import com.microfountain.rcs.aidl.service.chatbot.DiscoveryRequestParameter;
import com.microfountain.rcs.rcskit.service.RcsSubscriptionInfo;
import com.microfountain.rcs.rcskit.service.chatbot.RcsChatbotService;
import com.microfountain.rcs.support.config.RcsServiceConfigXMLHelper;

import static com.microfountain.rcs.rcskit.service.RcsSubscriptionManager.getEnabledSubscriptionId;
import static com.microfountain.rcs.rcskit.service.RcsSubscriptionManager.getEnabledSubscriptionInfo;

public class ChatbotInfoQueryHelper {
    private QueryHandler mQueryHandler;
    private String mDomain;
    private String mChatbotSipUri;
    public static String INTENT_ACTION_DISCOVERY_CHATBOT_LIST = "intent_action_discovery_chatbot_list";

    public ChatbotInfoQueryHelper(QueryHandler mQueryHandler, String mDomain, String mChatbotSipUri) {
        this.mQueryHandler = mQueryHandler;
        this.mDomain = mDomain;
        this.mChatbotSipUri = mChatbotSipUri;
    }

    private void startQuery() {
        startQueryChatbotInfo();
        startQuerySaveLocal(QueryHandler.TOKEN_QUERY_SAVE_LOCAL);
    }

    private void startQueryChatbotInfo() {
        LogUtil.i("Junwang", "startQueryChatbotInfo mDomain="+mDomain+", SipUri="+mChatbotSipUri);
        String selection;
        String[] selectionArgs;
        if (TextUtils.isEmpty(mDomain)) {
            selection = RcsChatbotInfoTable.Columns.CHATBOT_SIP_URI + " = ?";
            selectionArgs = new String[]{mChatbotSipUri};
        } else {
            selection = RcsChatbotInfoTable.Columns.DOMAIN + " = ?" + " AND " + RcsChatbotInfoTable.Columns.CHATBOT_SIP_URI + " = ?";
            selectionArgs = new String[]{mDomain, mChatbotSipUri};
        }

        mQueryHandler.startQuery(QueryHandler.TOKEN_QUERY_CHATBOT_INFO, null, RcsChatbotInfoTable.CONTENT_URI, null, selection, selectionArgs, null);
    }

    private void startQuerySaveLocal(int token) {
//        String selection = SmsDatabaseTables.SaveLocalItem.Columns.URI + "=?";
//        String[] selectionArgs = new String[]{mChatbotSipUri};
//        mQueryHandler.startQuery(token, null, SmsDatabaseTables.SaveLocalItem.CONTENT_URI, null, selection, selectionArgs, null);
    }

    public void requestChatbotInfo(String chatbotSipUri){
        RcsSubscriptionInfo rcsSubscriptionInfo = getEnabledSubscriptionInfo();

        if (rcsSubscriptionInfo != null) {

            boolean bRevoke = RcsChatbotService.requestChatbotInfoUpdate(rcsSubscriptionInfo.subscriptionId, chatbotSipUri);

            LogUtil.i("Junwang", "requestChatbotInfo bRevoke: " + bRevoke);

        } else {

            LogUtil.e("Junwang", "requestChatbotInfo rcsSubscriptionInfo == null");
        }
    }

    public void initData(String mDomain, String mChatbotSipUri){
        this.mChatbotSipUri = mChatbotSipUri;
        if (TextUtils.isEmpty(mChatbotSipUri)) {
            LogUtil.i("Junwang", "initData chatbotSipUri is null");
            this.mChatbotSipUri = "sip:10658139@botplatform.rcs.chinamobile.com";
        }

        this.mDomain = mDomain;
        LogUtil.i("Junwang", "initData mDomain: " + mDomain);

        if (TextUtils.isEmpty(mDomain)) {
            int subscriptionId = getEnabledSubscriptionId();
            if (subscriptionId > 0) {
                RcsServiceConfigXMLHelper xmlHelper = ChatbotUtils.getRcsServiceConfigXMLHelper(subscriptionId);
                if (xmlHelper != null) {
                    this.mDomain = xmlHelper.getChatbotInfoDomain();
                    LogUtil.i("Junwang", "mDomain= " + this.mDomain);
                }
            }
        }
    }

    public void execQuery(){
        initData(mDomain, mChatbotSipUri);
        requestChatbotInfo(mChatbotSipUri);
        startQuery();
    }

    public static void SearchChatbot(String keyString){
        //构建搜索参数
        DiscoveryRequestParameter parameter = new DiscoveryRequestParameter(); //关键字
        parameter.queryString = keyString;
        //分段开始的位置，如果为 0 则默认从第一项开始;如:已搜索了50个Chatbot，搜索下一段应传50
        parameter.startIndex = 0;
        //客户端请求的分段数据量，如果为0，服务端视配置默认返回一定数量的结果
        parameter.expectedNumberOfResults = 20; //自定义通知Intent，接收搜索结果
        Intent pendingIntent = new Intent(/*RcsReceiverHelper.*/INTENT_ACTION_DISCOVERY_CHATBOT_LIST);
        pendingIntent.setPackage(BuildConfig.APPLICATION_ID); //RCS可用SIM卡订阅ID
        int subscriptionId = getEnabledSubscriptionId();
        RcsChatbotService.discoverChatbotList(subscriptionId, parameter, pendingIntent);
    }
}
