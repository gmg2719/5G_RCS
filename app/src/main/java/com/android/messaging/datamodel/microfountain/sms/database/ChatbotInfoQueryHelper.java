package com.android.messaging.datamodel.microfountain.sms.database;

import android.text.TextUtils;

import com.android.messaging.datamodel.microfountain.sms.ChatbotUtils;
import com.android.messaging.util.LogUtil;
import com.microfountain.rcs.aidl.database.contract.RcsChatbotInfoTable;
import com.microfountain.rcs.rcskit.service.RcsSubscriptionInfo;
import com.microfountain.rcs.rcskit.service.chatbot.RcsChatbotService;
import com.microfountain.rcs.support.config.RcsServiceConfigXMLHelper;

import static com.microfountain.rcs.rcskit.service.RcsSubscriptionManager.getEnabledSubscriptionId;
import static com.microfountain.rcs.rcskit.service.RcsSubscriptionManager.getEnabledSubscriptionInfo;

public class ChatbotInfoQueryHelper {
    private QueryHandler mQueryHandler;
    private String mDomain;
    private String mChatbotSipUri;

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
}
