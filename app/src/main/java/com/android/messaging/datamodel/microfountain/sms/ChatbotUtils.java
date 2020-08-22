package com.android.messaging.datamodel.microfountain.sms;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.text.TextUtils;

import com.android.messaging.util.LogUtil;
import com.microfountain.rcs.aidl.broadcast.RcsChatbotBroadcast;
import com.microfountain.rcs.aidl.database.contract.RcsChatbotInfoTable;
import com.microfountain.rcs.rcskit.service.RcsSubscriptionInfo;
import com.microfountain.rcs.rcskit.service.RcsSubscriptionManager;
import com.microfountain.rcs.rcskit.service.chatbot.RcsChatbotService;
import com.microfountain.rcs.rcskit.service.security.CmccChallengeInfo;
import com.microfountain.rcs.support.config.RcsServiceConfigXMLHelper;
import com.microfountain.rcs.support.model.chatbot.ChatbotInfoQueryResult;
import com.microfountain.rcs.support.model.chatbot.ChatbotInfoQueryResultParser;

import java.net.HttpURLConnection;

import static com.microfountain.rcs.rcskit.service.RcsSubscriptionManager.getEnabledSubscriptionInfo;

public class ChatbotUtils {
    public static RcsServiceConfigXMLHelper getRcsServiceConfigXMLHelper(int subscriptionId) {
        RcsSubscriptionInfo rcsSubscriptionInfo = RcsSubscriptionManager.getRcsSubscriptionInfo(subscriptionId);
        if (rcsSubscriptionInfo != null) {
            LogUtil.i("Junwang", "getRcsServiceConfigXMLHelper rcsSubscriptionInfo != null");
            int serviceConfigVersion = rcsSubscriptionInfo.getServiceConfigVersion();
            String rcsApplicationCharacteristicsXML = rcsSubscriptionInfo.getRcsApplicationCharacteristicsXML();
            String imsApplicationCharacteristicsXML = rcsSubscriptionInfo.getImsApplicationCharacteristicsXML();
            if (serviceConfigVersion > 0 && !TextUtils.isEmpty(rcsApplicationCharacteristicsXML) && !TextUtils.isEmpty(imsApplicationCharacteristicsXML)) {
                return RcsServiceConfigXMLHelper.getRcsServiceConfigXMLHelper(subscriptionId, serviceConfigVersion, rcsApplicationCharacteristicsXML, imsApplicationCharacteristicsXML);
            }
        }
        return null;
    }

    public static void requestChatbotInfo(String chatbotSipUri){
        RcsSubscriptionInfo rcsSubscriptionInfo = getEnabledSubscriptionInfo();

        if (rcsSubscriptionInfo != null) {

            boolean bRevoke = RcsChatbotService.requestChatbotInfoUpdate(rcsSubscriptionInfo.subscriptionId, chatbotSipUri);

            LogUtil.i("Junwang", "requestChatbotInfo bRevoke: " + bRevoke);

        } else {

            LogUtil.e("Junwang", "requestChatbotInfo rcsSubscriptionInfo == null");
        }
    }

    public static String getChatbotDomain(){
        LogUtil.i("Junwang", "getChatbotDomain");
        RcsSubscriptionInfo rcsSubscriptionInfo = getEnabledSubscriptionInfo();
        //domain区分不同运营商Chatbot
        String domain = null;
        if (rcsSubscriptionInfo != null) {
            RcsServiceConfigXMLHelper xmlHelper = getRcsServiceConfigXMLHelper(rcsSubscriptionInfo.subscriptionId);
            domain = xmlHelper.getChatbotInfoDomain();
        }
        if(domain != null){
//            domain = "maap01.hdn.rcs.chinamobile.com";
            LogUtil.i("Junwang", "domain="+domain);
        }else{
            LogUtil.e("Junwang", "getChatbotDomain domain is null");
            domain = "botinfo01.hdn.rcs.chinamobile.com:443/chatbotserver";
        }
        return domain;
    }

    public static void queryChatbotInfo(Context context, String chatbotSipUri) {
        LogUtil.i("Junwang", "queryChatbotInfo chatbotSipUri="+chatbotSipUri);
        RcsSubscriptionInfo rcsSubscriptionInfo = getEnabledSubscriptionInfo();
        //domain区分不同运营商Chatbot
        String domain = null;
        if (rcsSubscriptionInfo != null) {
            RcsServiceConfigXMLHelper xmlHelper = getRcsServiceConfigXMLHelper(rcsSubscriptionInfo.subscriptionId);
            domain = xmlHelper.getChatbotInfoDomain();
        }
        if(domain != null){
//            domain = "maap01.hdn.rcs.chinamobile.com";
            LogUtil.i("Junwang", "domain="+domain);
        }

        String selection;
        String[] selectionArgs;
        if (TextUtils.isEmpty(domain)) {
            selection = RcsChatbotInfoTable.Columns.CHATBOT_SIP_URI + "=?";
            selectionArgs = new String[]{chatbotSipUri};
        } else {
            selection = RcsChatbotInfoTable.Columns.DOMAIN + "=?" + " AND " +
                    RcsChatbotInfoTable.Columns.CHATBOT_SIP_URI + "=?";
            selectionArgs = new String[]{domain, chatbotSipUri};
        }
        Cursor cursor = context.getContentResolver().query(RcsChatbotInfoTable.CONTENT_URI,
                null, selection, selectionArgs, null);
        if(cursor != null && cursor.moveToFirst()) {
            LogUtil.i("Junwang", "queryChatbotInfo cursor != null");
            byte[] jsonData =
                    cursor.getBlob(cursor.getColumnIndex(RcsChatbotInfoTable.Columns.JSON_DATA));
            LogUtil.i("Junwang", "queryChatbotInfo jsonData ="+new String(jsonData));
            ChatbotInfoQueryResult chatbotInfoQueryResult =
                    ChatbotInfoQueryResultParser.parse(jsonData);
            if(chatbotInfoQueryResult != null) {
                // TODO:刷新UI
                String iconUrl = chatbotInfoQueryResult.chatbotInfo.getIconUrl();
                String backgroundImage = chatbotInfoQueryResult.chatbotInfo.getBackgroundImage();
                String displayName = chatbotInfoQueryResult.chatbotInfo.getDisplayName();
                String sipUri = chatbotInfoQueryResult.chatbotInfo.getSipUri();
                LogUtil.i("Junwang", "Chatbot iconUrl="+iconUrl+", backgroundImage="+backgroundImage
                                +", displayName="+displayName+", sipUri="+sipUri);
            }
        }else{
            LogUtil.i("Junwang", chatbotSipUri+" chatbot info is null.");
        }
    }

    public void updateChatbotInfo(Context context, Intent intent, String mChatbotSipUri){
        //获取请求状态码
        int responseCode = intent.getIntExtra(RcsChatbotBroadcast.INTENT_EXTRA_HTTP_RESPONSE_CODE, 0);
        if (responseCode >= HttpURLConnection.HTTP_OK && responseCode <
                HttpURLConnection.HTTP_MULT_CHOICE) {
            //请求成功
            String chatbotSipUri =
                    intent.getStringExtra(RcsChatbotBroadcast.INTENT_EXTRA_CHATBOT_SIP_URI_STRING);
            //对比广播携带的Chatbot SIP URI和当前使用的Chatbot SIP URI
            if (TextUtils.equals(chatbotSipUri, mChatbotSipUri)) {
                //查询数据库并刷新UI
                LogUtil.i("Junwang", "updateChatbotInfo queryChatbotInfo");
                queryChatbotInfo(context, chatbotSipUri);
            }
        } else { //获取错误信息
            String extraData =
                    intent.getStringExtra(RcsChatbotBroadcast.INTENT_EXTRA_EXTRA_DATA_STRING);
            if (extraData != null && !extraData.isEmpty()) {
                CmccChallengeInfo cmccChallengeInfo = new
                        CmccChallengeInfo(extraData);
                LogUtil.i("Junwang", "onChatbotInfoUpdateResult:" + cmccChallengeInfo);
                // TODO: 如果统一认证SDK报错，需要客户端处理相应的错误
            }
        }
    }
}
