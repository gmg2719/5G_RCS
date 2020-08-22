package com.android.messaging.datamodel.microfountain.sms.database;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.android.messaging.datamodel.ChatbotInfoTableUtils;
import com.android.messaging.datamodel.microfountain.sms.ChatbotUtils;
import com.android.messaging.util.DownloadImageUtils;
import com.android.messaging.util.LogUtil;
import com.microfountain.rcs.aidl.database.contract.RcsChatbotInfoTable;
import com.microfountain.rcs.support.config.RcsServiceConfigXMLHelper;
import com.microfountain.rcs.support.model.chatbot.Chatbot;
import com.microfountain.rcs.support.model.chatbot.ChatbotInfoQueryResult;
import com.microfountain.rcs.support.model.chatbot.ChatbotInfoQueryResultParser;

import static com.microfountain.rcs.rcskit.service.RcsSubscriptionManager.getEnabledSubscriptionId;

public class BaseInvokeChatbotInfo {
    private String mDomain;
    private String mChatbotSipUri;
    private Context mContext;
    private String mLogoSavedPath;

    public BaseInvokeChatbotInfo(String mDomain, String mChatbotSipUri, Context mContext) {
        mLogoSavedPath = null;
        this.mContext = mContext;
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

    public void insertSaveLocal() {
        LogUtil.v("Junwang", "BaseInvokeChatbotInfo insertSaveLocal");
    }

    public void deleteSaveLocal() {
        LogUtil.v("Junwang", "BaseInvokeChatbotInfo deleteSaveLocal");
    }

    public void setSaveLocalText(boolean saveLocal) {
        LogUtil.v("Junwang", "BaseInvokeChatbotInfo setSaveLocalText");
    }

    public void onQueryResult(Cursor cursor) {
        LogUtil.v("Junwang", "BaseInvokeChatbotInfo onQueryResult");
        if(cursor != null) {
            if(!ChatbotInfoTableUtils.IsChatbotInfoExist(mChatbotSipUri)) {
                downloadChatbotLogo(cursor);
                ChatbotInfoTableUtils.insertChatbotInfoTable(cursor, mLogoSavedPath);
            }else{
                LogUtil.i("Junwang", "start update chatbot info table");
                downloadChatbotLogo(cursor);
                ChatbotInfoTableUtils.updateChatbotInfoTable(cursor);
            }
        }
    }

    public void updateSaveLocalResult(int result) {
        LogUtil.v("Junwang", "updateSaveLocalResult: " + result);
    }

    public void initData(String chatbotSipUri, String domain) {
        mChatbotSipUri = chatbotSipUri;
        if (TextUtils.isEmpty(mChatbotSipUri)) {
            LogUtil.i("Junwang", "initData chatbotSipUri is null");
            mChatbotSipUri = "sip:10658139@botplatform.rcs.chinamobile.com";
        }

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

    public Chatbot bind(Cursor cursor) {

        String chatbotSipUri = cursor.getString(cursor.getColumnIndex(RcsChatbotInfoTable.Columns.CHATBOT_SIP_URI));

        byte[] jsonData = cursor.getBlob(cursor.getColumnIndex(RcsChatbotInfoTable.Columns.JSON_DATA));

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
                LogUtil.i("Junwang", "iconUrl=" + iconUrl + ", chatbotInfoQueryResult="+chatbotInfoQueryResult.toString());
            }
            if(chatbotInfoQueryResult.persistentMenu != null){
                LogUtil.i("Junwang", "persistentMenu="+chatbotInfoQueryResult.persistentMenu.toString());
            }else{
                LogUtil.i("Junwang", "persistentMenu is null");
            }

        }

        return new Chatbot(chatbotSipUri, name, iconUrl, verified, sms, email, "", provider, website, description);
    }

    public void downloadChatbotLogo(Cursor cursor){
        LogUtil.i("Junwang", "onQueryResult cursor: " + cursor.getCount());
        if (cursor != null && cursor.moveToFirst()) {
            byte[] jsonData = cursor.getBlob(cursor.getColumnIndex(RcsChatbotInfoTable.Columns.JSON_DATA));
            if (jsonData != null && jsonData.length > 0) {
                Chatbot chatbot = bind(cursor);
                String iconUrl = chatbot.getIconUrl();
                if((iconUrl != null) && (iconUrl.length() > 0)) {
                    LogUtil.i("Junwang", "download chatbot logo from "+iconUrl);
                    DownloadImageUtils.saveImageToLocal(mContext, iconUrl);
                    mLogoSavedPath = mContext.getFilesDir()+"/"+iconUrl.substring(iconUrl.lastIndexOf("/")+1);
                }else{
                    LogUtil.e("Junwang", "chatbot logo url is null.");
                }
            }
        }
    }
}
