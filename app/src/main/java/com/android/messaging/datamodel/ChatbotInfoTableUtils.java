package com.android.messaging.datamodel;

import android.content.ContentValues;
import android.database.Cursor;

import com.android.messaging.datamodel.mygsonconverter.GsonConverterFactory;
import com.android.messaging.ui.chatbotservice.ChatbotMenuRetrofitService;
import com.android.messaging.ui.chatbotservice.GetChatbotMenuApi;
import com.android.messaging.ui.conversation.chatbot.ChatbotEntity;
import com.android.messaging.ui.conversation.chatbot.ChatbotFavoriteEntity;
import com.android.messaging.util.LogUtil;
import com.google.gson.Gson;
import com.microfountain.rcs.aidl.database.contract.RcsChatbotInfoTable;
import com.microfountain.rcs.support.model.chatbot.ChatbotInfoQueryResult;
import com.microfountain.rcs.support.model.chatbot.ChatbotInfoQueryResultParser;

import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class ChatbotInfoTableUtils {
    public static void insertChatbotInfoTable(Cursor cursor, String logoSavedPath){
        if(cursor != null && cursor.moveToFirst()){
            String domain = cursor.getString(cursor.getColumnIndex(RcsChatbotInfoTable.Columns.DOMAIN));
            String chatbotSipUri = cursor.getString(cursor.getColumnIndex(RcsChatbotInfoTable.Columns.CHATBOT_SIP_URI));
            String expireTime = cursor.getString(cursor.getColumnIndex(RcsChatbotInfoTable.Columns.EXPIRY_TIME));
            String etag = cursor.getString(cursor.getColumnIndex(RcsChatbotInfoTable.Columns.ETAG));
            byte[] jsonData = cursor.getBlob(cursor.getColumnIndex(RcsChatbotInfoTable.Columns.JSON_DATA));
//            String jsonData = cursor.getString(cursor.getColumnIndex(RcsChatbotInfoTable.Columns.JSON_DATA));
            String name = cursor.getString(cursor.getColumnIndex(RcsChatbotInfoTable.ExtendedColumns.NAME));
            LogUtil.i("Junwang", "insertChatbotInfoTable chatbot name = "+name);
            String sms = cursor.getString(cursor.getColumnIndex(RcsChatbotInfoTable.ExtendedColumns.SMS));

            ContentValues cv = new ContentValues();

            cv.put(DatabaseHelper.ChatbotInfoColumns.CHATBOT_DOMAIN, domain);
            cv.put(DatabaseHelper.ChatbotInfoColumns.CHATBOT_SIP_URI, chatbotSipUri);
            cv.put(DatabaseHelper.ChatbotInfoColumns.CHATBOT_EXPIRY_TIME, expireTime);
            cv.put(DatabaseHelper.ChatbotInfoColumns.CHATBOT_ETAG, etag);
            cv.put(DatabaseHelper.ChatbotInfoColumns.CHATBOT_JSON, new String(jsonData));
            cv.put(DatabaseHelper.ChatbotInfoColumns.CHATBOT_NAME, name);
//        cv.put(DatabaseHelper.ChatbotInfoColumns.CHATBOT_SMS, sms);
            //change sms columns for save chatbot logo path
            cv.put(DatabaseHelper.ChatbotInfoColumns.CHATBOT_SMS, logoSavedPath);

            DatabaseWrapper mdbWrapper = DataModel.get().getDatabase();
            ChatbotInfoQueryResult chatbotInfoQueryResult = ChatbotInfoQueryResultParser.parse(jsonData);
            if(chatbotInfoQueryResult != null && chatbotInfoQueryResult.persistentMenu != null){
                cv.put(DatabaseHelper.ChatbotInfoColumns.CHATBOT_MENU, chatbotInfoQueryResult.persistentMenu.toString());
                mdbWrapper.insert(DatabaseHelper.CHATBOT_INFO_TABLE, null, cv);

            }else{
                mdbWrapper.insert(DatabaseHelper.CHATBOT_INFO_TABLE, null, cv);
                //get chatbot menu from our web server
                getChatbotMenuFromServer();
            }
            MessagingContentProvider.notifyConversationListChanged();
        }
    }

    public static void updateChatbotInfoLogoPath(String logoPath, String chatbotSipUri){
        LogUtil.i("Junwang", "updateChatbotInfoLogoPath logoPath="+logoPath+", chatbotSipUri="+chatbotSipUri);
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.ChatbotInfoColumns.CHATBOT_SMS, logoPath);
        DatabaseWrapper mdbWrapper = DataModel.get().getDatabase();
        mdbWrapper.update(DatabaseHelper.CHATBOT_INFO_TABLE, cv,
                DatabaseHelper.ChatbotInfoColumns.CHATBOT_SIP_URI
                        + " = ?", new String[]{chatbotSipUri});
        MessagingContentProvider.notifyConversationListChanged();
    }

    public static void updateChatbotInfoTable(Cursor cursor){
        if(cursor != null && cursor.moveToFirst()){
            String domain = cursor.getString(cursor.getColumnIndex(RcsChatbotInfoTable.Columns.DOMAIN));
            String chatbotSipUri = cursor.getString(cursor.getColumnIndex(RcsChatbotInfoTable.Columns.CHATBOT_SIP_URI));
            String expireTime = cursor.getString(cursor.getColumnIndex(RcsChatbotInfoTable.Columns.EXPIRY_TIME));
            String etag = cursor.getString(cursor.getColumnIndex(RcsChatbotInfoTable.Columns.ETAG));
//            String jsonData = cursor.getString(cursor.getColumnIndex(RcsChatbotInfoTable.Columns.JSON_DATA));
            byte[] jsonData = cursor.getBlob(cursor.getColumnIndex(RcsChatbotInfoTable.Columns.JSON_DATA));
            String name = cursor.getString(cursor.getColumnIndex(RcsChatbotInfoTable.ExtendedColumns.NAME));
//            String sms = cursor.getString(cursor.getColumnIndex(RcsChatbotInfoTable.ExtendedColumns.SMS));

            ContentValues cv = new ContentValues();

            cv.put(DatabaseHelper.ChatbotInfoColumns.CHATBOT_DOMAIN, domain);
            cv.put(DatabaseHelper.ChatbotInfoColumns.CHATBOT_SIP_URI, chatbotSipUri);
            cv.put(DatabaseHelper.ChatbotInfoColumns.CHATBOT_EXPIRY_TIME, expireTime);
            cv.put(DatabaseHelper.ChatbotInfoColumns.CHATBOT_ETAG, etag);
            cv.put(DatabaseHelper.ChatbotInfoColumns.CHATBOT_JSON, new String(jsonData));
            cv.put(DatabaseHelper.ChatbotInfoColumns.CHATBOT_NAME, name);
//            cv.put(DatabaseHelper.ChatbotInfoColumns.CHATBOT_SMS, sms);

            DatabaseWrapper mdbWrapper = DataModel.get().getDatabase();

            ChatbotInfoQueryResult chatbotInfoQueryResult = ChatbotInfoQueryResultParser.parse(jsonData);
            if(chatbotInfoQueryResult != null && chatbotInfoQueryResult.persistentMenu != null){
                cv.put(DatabaseHelper.ChatbotInfoColumns.CHATBOT_MENU, chatbotInfoQueryResult.persistentMenu.toString());
                mdbWrapper.update(DatabaseHelper.CHATBOT_INFO_TABLE, cv,
                        DatabaseHelper.ChatbotInfoColumns.CHATBOT_SIP_URI
                                + " = ?", new String[]{chatbotSipUri});
            }else{
                mdbWrapper.update(DatabaseHelper.CHATBOT_INFO_TABLE, cv,
//                    DatabaseHelper.ConversationColumns.OTHER_PARTICIPANT_NORMALIZED_DESTINATION
                        DatabaseHelper.ChatbotInfoColumns.CHATBOT_SIP_URI
                                + " = ?", new String[]{chatbotSipUri});
                //get chatbot menu from our web server
                getChatbotMenuFromServer();
            }
            MessagingContentProvider.notifyConversationListChanged();
        }
    }

    public static ChatbotFavoriteEntity getChatbotInfo(String chatbotSip){
        ChatbotFavoriteEntity cbi = null;
        DatabaseWrapper mdbWrapper = DataModel.get().getDatabase();
        String sql = "SELECT * FROM " + DatabaseHelper.CHATBOT_INFO_TABLE + " WHERE "+ DatabaseHelper.ChatbotInfoColumns.CHATBOT_SIP_URI + " = ?";
        Cursor cursor = mdbWrapper.rawQuery(sql, new String[]{chatbotSip});
        if(cursor != null) {
            cursor.moveToFirst();
            cbi = new ChatbotFavoriteEntity();
            LogUtil.i("Junwang", "sip name="+cursor.getString(6)+", logo="+cursor.getString(7));
            cbi.setChatbot_fav_name(cursor.getString(6));
            cbi.setChatbot_fav_logo(cursor.getString(7));
            cursor.close();
        }
        return cbi;
    }

    public static ChatbotEntity queryChatbotInfoTable(String chatbot_sip_uri) {
        DatabaseWrapper mdbWrapper = DataModel.get().getDatabase();
        Cursor cursor = mdbWrapper.rawQuery("SELECT * FROM " + DatabaseHelper.CHATBOT_INFO_TABLE + " WHERE "
                + DatabaseHelper.CHATBOT_INFO_TABLE + '.' + DatabaseHelper.ChatbotInfoColumns.CHATBOT_SIP_URI + " = ?", new String[]{chatbot_sip_uri});
        ChatbotEntity botEntity = null;
        if (cursor != null) {
            if (cursor.moveToNext()) {
                botEntity = new ChatbotEntity();
                botEntity.setDomain(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ChatbotInfoColumns.CHATBOT_DOMAIN)));
//                botEntity.setSip_uri(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ChatbotInfoColumns.CHATBOT_SIP_URI)));
                botEntity.setEtag(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ChatbotInfoColumns.CHATBOT_ETAG)));
                botEntity.setExpiry_time(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ChatbotInfoColumns.CHATBOT_EXPIRY_TIME)));
                botEntity.setJson(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ChatbotInfoColumns.CHATBOT_JSON)));
                botEntity.setMenu(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ChatbotInfoColumns.CHATBOT_MENU)));
                botEntity.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ChatbotInfoColumns.CHATBOT_NAME)));
                botEntity.setSms(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ChatbotInfoColumns.CHATBOT_SMS)));
            }
            cursor.close();
        }
        return botEntity;
    }


    public static void deleteChatbotInfo(String chatbot_sip_uri){
        DatabaseWrapper mdbWrapper = DataModel.get().getDatabase();
        mdbWrapper.delete(DatabaseHelper.CHATBOT_INFO_TABLE, DatabaseHelper.ChatbotInfoColumns.CHATBOT_SIP_URI
                + " = ?", new String[]{chatbot_sip_uri});
        MessagingContentProvider.notifyConversationListChanged();
    }

    public static void deleteChatbotInfoTable(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                DatabaseWrapper mdbWrapper = DataModel.get().getDatabase();
                mdbWrapper.execSQL("DELETE FROM "+ DatabaseHelper.CHATBOT_INFO_TABLE);
                MessagingContentProvider.notifyConversationListChanged();
            }
        }).start();

    }

    public static boolean IsChatbotInfoExist(String chatbot_sip_uri){
        DatabaseWrapper mdbWrapper = DataModel.get().getDatabase();
        Cursor cursor = null;
        try {
            cursor = mdbWrapper.rawQuery("SELECT "+ DatabaseHelper.ChatbotInfoColumns.CHATBOT_SIP_URI +" FROM " + DatabaseHelper.CHATBOT_INFO_TABLE + " WHERE "+ DatabaseHelper.ChatbotInfoColumns.CHATBOT_SIP_URI + " = ?", new String[]{chatbot_sip_uri});
            if(cursor != null && cursor.moveToFirst()) {
                if (cursor.getCount() > 0) {
                    LogUtil.i("Junwang", chatbot_sip_uri + "has already exist.");
                    return true;
                }
            }
        }catch (Exception e){
            LogUtil.e("Junwang", e.toString());
            if(cursor != null){
                cursor.close();
            }
        }
       return false;
    }

    public static void updateChatbotCardInvalidStatus(String rcsdb_msgId){
        final DatabaseWrapper db = DataModel.get().getDatabase();
        // Update local db
        db.beginTransaction();
        try {
            final ContentValues values = new ContentValues();

            values.put(DatabaseHelper.MessageColumns.CHATBOT_CARD_INVALID, 1);

            final int count = db.update(DatabaseHelper.MESSAGES_TABLE, values,
                    "(" + DatabaseHelper.MessageColumns.CHATBOT_CARD_INVALID + " !=1 ) AND " +
                            DatabaseHelper.MessageColumns.CHATBOT_RCSDB_MSGID + "=?",
                    new String[] { rcsdb_msgId });
//            if (count > 0) {
//                MessagingContentProvider.notifyMessagesChanged(conversationId);
//            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public static final String BASE_URL = "http://testxhs.supermms.cn/api/sms5g/my/";
    public static void getChatbotMenuFromServer(){
        LogUtil.i("Junwang", "getChatbotMenuFromServer");
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).
                addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ChatbotMenuRetrofitService service = retrofit.create(ChatbotMenuRetrofitService.class);

        service.getChatbotMenu("getMenu").subscribe(new Consumer<GetChatbotMenuApi>() {
            @Override
            public void accept(GetChatbotMenuApi getChatbotMenuApi) throws Exception {
                if(getChatbotMenuApi == null){
                    return;
                }
                LogUtil.i("Junwang", "code="+getChatbotMenuApi.getRet_code()+", msg="+getChatbotMenuApi.getMessage());
                String json = new Gson().toJson(getChatbotMenuApi.getData());
//                String json = getChatbotMenuApi.getData().toString();
//                String json = "{\n" +
//                        "    \"menu\":{\n" +
//                        "        \"entries\":[\n" +
//                        "            {\n" +
//                        "                \"menu\":{\n" +
//                        "                    \"displayText\":\"现场云\",\n" +
//                        "                    \"entries\":[\n" +
//                        "                        {\n" +
//                        "                            \"reply\":{\n" +
//                        "                                \"displayText\":\"央企服务\",\n" +
//                        "                                \"postback\":{\n" +
//                        "                                    \"data\":\"http://testxhs.supermms.cn/api/sms5g/my/viewProduct?msgId=1572676328973&option=1\"\n" +
//                        "                                }\n" +
//                        "                            }\n" +
//                        "                        },\n" +
//                        "                        {\n" +
//                        "                            \"reply\":{\n" +
//                        "                                \"displayText\":\"民族品牌\",\n" +
//                        "                                \"postback\":{\n" +
//                        "                                    \"data\":\"http://testxhs.supermms.cn/api/sms5g/my/viewProduct?msgId=1572676328973&option=1\"\n" +
//                        "                                }\n" +
//                        "                            }\n" +
//                        "                        },\n" +
//                        "                        {\n" +
//                        "                            \"reply\":{\n" +
//                        "                                \"displayText\":\"区县融媒体\",\n" +
//                        "                                \"postback\":{\n" +
//                        "                                    \"data\":\"http://testxhs.supermms.cn/api/sms5g/my/viewProduct?msgId=1572676328973&option=1\"\n" +
//                        "                                }\n" +
//                        "                            }\n" +
//                        "                        },\n" +
//                        "                        {\n" +
//                        "                            \"reply\":{\n" +
//                        "                                \"displayText\":\"中纪委专刊\",\n" +
//                        "                                \"postback\":{\n" +
//                        "                                    \"data\":\"http://testxhs.supermms.cn/api/sms5g/my/viewProduct?msgId=1572676328973&option=1\"\n" +
//                        "                                }\n" +
//                        "                            }\n" +
//                        "                        },\n" +
//                        "                        {\n" +
//                        "                            \"reply\":{\n" +
//                        "                                \"displayText\":\"现场云直播\",\n" +
//                        "                                \"postback\":{\n" +
//                        "                                    \"data\":\"http://testxhs.supermms.cn/api/sms5g/my/viewProduct?msgId=1572676328973&option=1\"\n" +
//                        "                                }\n" +
//                        "                            }\n" +
//                        "                        }\n" +
//                        "                    ]\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            {\n" +
//                        "                \"menu\":{\n" +
//                        "                    \"displayText\":\"新华99\",\n" +
//                        "                    \"entries\":[\n" +
//                        "                        {\n" +
//                        "                            \"reply\":{\n" +
//                        "                                \"displayText\":\"厂直优品\",\n" +
//                        "                                \"postback\":{\n" +
//                        "                                    \"data\":\"http://testxhs.supermms.cn/api/sms5g/my/viewProduct?msgId=1572676328973&option=1\"\n" +
//                        "                                }\n" +
//                        "                            }\n" +
//                        "                        },\n" +
//                        "                        {\n" +
//                        "                            \"reply\":{\n" +
//                        "                                \"displayText\":\"地标特产\",\n" +
//                        "                                \"postback\":{\n" +
//                        "                                    \"data\":\"http://testxhs.supermms.cn/api/sms5g/my/viewProduct?msgId=1572676328973&option=1\"\n" +
//                        "                                }\n" +
//                        "                            }\n" +
//                        "                        }\n" +
//                        "                    ]\n" +
//                        "                }\n" +
//                        "            },\n" +
//                        "            {\n" +
//                        "                \"menu\":{\n" +
//                        "                    \"displayText\":\"我的\",\n" +
//                        "                    \"entries\":[\n" +
//                        "                        {\n" +
//                        "                            \"reply\":{\n" +
//                        "                                \"displayText\":\"我的服务\",\n" +
//                        "                                \"postback\":{\n" +
//                        "                                    \"data\":\"http://testxhs.supermms.cn/api/sms5g/my/viewProduct?msgId=1572676328973&option=1\"\n" +
//                        "                                }\n" +
//                        "                            }\n" +
//                        "                        },\n" +
//                        "                        {\n" +
//                        "                            \"reply\":{\n" +
//                        "                                \"displayText\":\"我的订单\",\n" +
//                        "                                \"postback\":{\n" +
//                        "                                    \"data\":\"http://testxhs.supermms.cn/api/sms5g/my/viewProduct?msgId=1572676328973&option=1\"\n" +
//                        "                                }\n" +
//                        "                            }\n" +
//                        "                        }\n" +
//                        "                    ]\n" +
//                        "                }\n" +
//                        "            }\n" +
//                        "        ]\n" +
//                        "    }\n" +
//                        "}";
                ContentValues cv = new ContentValues();
                cv.put(DatabaseHelper.ChatbotInfoColumns.CHATBOT_MENU, json);
                LogUtil.i("Junwang", "chatbot menu json="+json);

                DatabaseWrapper mdbWrapper = DataModel.get().getDatabase();
                mdbWrapper.update(DatabaseHelper.CHATBOT_INFO_TABLE, cv,
                        DatabaseHelper.ChatbotInfoColumns.CHATBOT_SIP_URI
                                + " = ?", new String[]{"sip:1065051121304@botplatform.rcs.chinamobile.com"});
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                throwable.printStackTrace();
            }
        });
    }
}
