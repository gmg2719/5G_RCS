package com.android.messaging.datamodel;

import android.content.ContentValues;
import android.database.Cursor;

import com.android.messaging.ui.conversation.chatbot.ChatbotFavoriteEntity;
import com.android.messaging.util.LogUtil;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatbotFavoriteTableUtils {
    public static void insertChatbotFavoriteTable(String favSipUri, String favName, String favLogo, String favDescription,
                                                  String favImageUrl, long favSaveDate, String favChannelId, String favMsgId, String favConversationId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ContentValues cv = new ContentValues();

                cv.put(DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_SIP_URI, favSipUri);
                cv.put(DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_NAME, favName);
                cv.put(DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_LOGO, favLogo);
                cv.put(DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_CARD_DESCRIPTION, favDescription);
                cv.put(DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_IMAGE_URL, favImageUrl);
                cv.put(DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_SAVED_DATE, favSaveDate);
                cv.put(DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_CHANNEL_ID, favChannelId);
                cv.put(DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_MSG_ID, favMsgId);
                cv.put(DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_CONVERSATION_ID, favConversationId);

                DatabaseWrapper mdbWrapper = DataModel.get().getDatabase();
                mdbWrapper.insert(DatabaseHelper.CHATBOT_FAVORITE_TABLE, null, cv);
                Map<String, String> params = new HashMap<String, String>();
                params.put("msgId", favMsgId);
                String result = postRequest("http://testback.stvision.cn/xinhua/sms5g/my/collectMsg", params, "utf-8");
                LogUtil.i("Junwang", "post add chatbot favorite response " + result);
            }
        }).start();
    }

    //ps:此函数必须在线程中调用（因为函数抛出异常，调用时要包在try--catch里面）
    public static String postRequest(String requesturl, Map<String, String> params, String encode) {
        StringBuffer buffer = new StringBuffer();
        try {
            if (params != null && !params.isEmpty()) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    buffer.append(entry.getKey()).append("=").
                            append(URLEncoder.encode(entry.getValue(), encode)).
                            append("&");
                }
            }
            //删除最后一个字符&，多了一个;主体设置完毕
            buffer.deleteCharAt(buffer.length() - 1);
            LogUtil.i("Junwang", "post video card buffer="+buffer);
            //建立连接
            URL url = new URL(requesturl);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            //设置参数
            httpConn.setDoOutput(true);     //需要输出
            httpConn.setDoInput(true);      //需要输入
            httpConn.setUseCaches(false);   //不允许缓存
            httpConn.setRequestMethod("POST");      //设置POST方式连接
            //设置请求属性
            httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpConn.setRequestProperty("Charset", "UTF-8");
            //连接,也可以不用明文connect，使用下面的httpConn.getOutputStream()会自动connect
            httpConn.connect();
            //建立输入流，向指向的URL传入参数
            DataOutputStream dos = new DataOutputStream(httpConn.getOutputStream());
            dos.writeBytes(buffer.toString());
            dos.flush();
            dos.close();
            //获得响应状态
            int resultCode = httpConn.getResponseCode();
            if (HttpURLConnection.HTTP_OK == resultCode) {
                StringBuffer sb = new StringBuffer();
                String readLine = new String();
                BufferedReader responseReader = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "UTF-8"));
                while ((readLine = responseReader.readLine()) != null) {
                    sb.append(readLine).append("\n");
                }
                responseReader.close();
//            System.out.println(sb.toString());
                LogUtil.i("Junwang", "get postVoteRequest response " + sb.toString());
                return sb.toString();
            }
        } catch (Exception e) {
            LogUtil.i("Junwang", "post request exception " + e.toString());
        }
        return null;
    }

    public static void deleteChatbotFavoriteInfo(String colletId, String msgId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DatabaseWrapper mdbWrapper = DataModel.get().getDatabase();
                mdbWrapper.delete(DatabaseHelper.CHATBOT_FAVORITE_TABLE, DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_MSG_ID
                        + " = ?", new String[]{msgId});
                Map<String, String> params = new HashMap<String, String>();
                params.put("collect_id", colletId);
                params.put("msgId", msgId);
                String result = postRequest("http://testback.stvision.cn/xinhua/sms5g/my/delCollect", params, "utf-8");
                LogUtil.i("Junwang", "post delete chatbot favorite response " + result);
            }
        }).start();
    }

    public static void deleteChatbotFavoriteInfoByConversationId(String colletId, String conversationId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DatabaseWrapper mdbWrapper = DataModel.get().getDatabase();
                mdbWrapper.delete(DatabaseHelper.CHATBOT_FAVORITE_TABLE, DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_CONVERSATION_ID
                        + " = ?", new String[]{conversationId});
                Map<String, String> params = new HashMap<String, String>();
//                params.put("collect_id", colletId);
//                params.put("msgId", msgId);
//                String result = postRequest("http://testback.stvision.cn/xinhua/sms5g/my/delCollect", params, "utf-8");
//                LogUtil.i("Junwang", "post delete chatbot favorite response " + result);
            }
        }).start();
    }

    public static List<ChatbotFavoriteEntity> queryChatbotFavorite() {
        DatabaseWrapper mdbWrapper = DataModel.get().getDatabase();
        Cursor cursor = mdbWrapper.rawQuery("SELECT * FROM " + DatabaseHelper.CHATBOT_FAVORITE_TABLE + " ORDER BY "
                + DatabaseHelper.CHATBOT_FAVORITE_TABLE + '.' + DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_SAVED_DATE + " DESC", null);
        List<ChatbotFavoriteEntity> favEntityList = new ArrayList<>();
        ChatbotFavoriteEntity favEnt;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                favEnt = new ChatbotFavoriteEntity();
//                LogUtil.i("Junwang", "query description="+cursor.getString(cursor.getColumnIndex(DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_CARD_DESCRIPTION))+", msgid="+cursor.getString(cursor.getColumnIndex(DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_MSG_ID)));
                favEnt.setChatbot_fav_sip_uri(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_SIP_URI)));
                favEnt.setChatbot_fav_name(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_NAME)));
                favEnt.setChatbot_fav_logo(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_LOGO)));
                favEnt.setChatbot_fav_card_description(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_CARD_DESCRIPTION)));
                favEnt.setChatbot_fav_image_url(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_IMAGE_URL)));
                favEnt.setChatbot_fav_saved_date(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_SAVED_DATE)));
                favEnt.setChatbot_fav_channel_id(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_CHANNEL_ID)));
                favEnt.setChatbot_fav_msg_id(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_MSG_ID)));
                favEnt.setChatbot_fav_conversation_id(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_CONVERSATION_ID)));
                favEntityList.add(favEnt);
            }
            cursor.close();
//            for(int i=0; i<favEntityList.size(); i++)
//                LogUtil.i("Junwang", "query description="+favEntityList.get(i).getChatbot_fav_card_description()+", msgid="+favEntityList.get(i).getChatbot_fav_msg_id());
        }
        return favEntityList;
    }

    public static List<ChatbotFavoriteEntity> queryChatbotSearchHistory() {
        DatabaseWrapper mdbWrapper = DataModel.get().getDatabase();
        Cursor cursor = mdbWrapper.rawQuery("SELECT * FROM " + DatabaseHelper.CHATBOT_SEARCH_HISTORY_TABLE + " ORDER BY "
                + "_id" + " DESC", null);
        List<ChatbotFavoriteEntity> favEntityList = new ArrayList<>();
        ChatbotFavoriteEntity favEnt;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                favEnt = new ChatbotFavoriteEntity();
//                LogUtil.i("Junwang", "query description="+cursor.getString(cursor.getColumnIndex(DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_CARD_DESCRIPTION))+", msgid="+cursor.getString(cursor.getColumnIndex(DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_MSG_ID)));
                favEnt.setChatbot_fav_sip_uri(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_SIP_URI)));
                favEnt.setChatbot_fav_name(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_NAME)));
                favEnt.setChatbot_fav_logo(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_LOGO)));
                favEnt.setChatbot_fav_card_description(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_CARD_DESCRIPTION)));
                favEnt.setChatbot_fav_image_url(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_IMAGE_URL)));
                favEnt.setChatbot_fav_saved_date(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_SAVED_DATE)));
                favEnt.setChatbot_fav_channel_id(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_CHANNEL_ID)));
                favEnt.setChatbot_fav_msg_id(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_MSG_ID)));
                favEnt.setChatbot_fav_conversation_id(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_CONVERSATION_ID)));
                favEntityList.add(favEnt);
            }
            cursor.close();
//            for(int i=0; i<favEntityList.size(); i++)
//                LogUtil.i("Junwang", "query description="+favEntityList.get(i).getChatbot_fav_card_description()+", msgid="+favEntityList.get(i).getChatbot_fav_msg_id());
        }
        return favEntityList;
    }

    public static void insertChatbotSearchHistoryTable(String favSipUri, String favName, String favLogo, String favDescription,
                                                       String favImageUrl, long favSaveDate, String favChannelId, String favMsgId, String favConversationId) {
        ContentValues cv = new ContentValues();

        cv.put(DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_SIP_URI, favSipUri);
        cv.put(DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_NAME, favName);
        cv.put(DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_LOGO, favLogo);
        cv.put(DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_CARD_DESCRIPTION, favDescription);
        cv.put(DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_IMAGE_URL, favImageUrl);
        cv.put(DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_SAVED_DATE, favSaveDate);
        cv.put(DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_CHANNEL_ID, favChannelId);
        cv.put(DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_MSG_ID, favMsgId);
        cv.put(DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_CONVERSATION_ID, favConversationId);

        DatabaseWrapper mdbWrapper = DataModel.get().getDatabase();
        mdbWrapper.insert(DatabaseHelper.CHATBOT_FAVORITE_TABLE, null, cv);
    }

    public static void deleteChatbotSearchHistoryItem(String chatbotName){
        DatabaseWrapper mdbWrapper = DataModel.get().getDatabase();
        int result = mdbWrapper.delete( DatabaseHelper.CHATBOT_SEARCH_HISTORY_TABLE , DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_NAME
                + " = ?", new String[]{chatbotName});
    }

    public static boolean updateChatbotSearchHistoryTable(ChatbotFavoriteEntity entity){
        DatabaseWrapper mdbWrapper = DataModel.get().getDatabase();
        try {
            // 开启事务
            mdbWrapper.beginTransaction();

            mdbWrapper.delete( DatabaseHelper.CHATBOT_SEARCH_HISTORY_TABLE , DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_NAME
                    + " = ?", new String[]{entity.getChatbot_fav_name()});

            ContentValues cv = new ContentValues();

            cv.put(DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_SIP_URI, entity.getChatbot_fav_sip_uri());
            cv.put(DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_NAME, entity.getChatbot_fav_name());
            cv.put(DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_LOGO, entity.getChatbot_fav_logo());
            cv.put(DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_CARD_DESCRIPTION, entity.getChatbot_fav_card_description());
            cv.put(DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_IMAGE_URL, entity.getChatbot_fav_image_url());
            cv.put(DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_SAVED_DATE, entity.getChatbot_fav_saved_date());
            cv.put(DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_CHANNEL_ID, entity.getChatbot_fav_channel_id());
            cv.put(DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_MSG_ID, entity.getChatbot_fav_msg_id());
            cv.put(DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_CONVERSATION_ID, entity.getChatbot_fav_conversation_id());
            mdbWrapper.insert(DatabaseHelper.CHATBOT_FAVORITE_TABLE, null, cv);
            //设置事务标志为成功，当结束事务时就会提交事务
            mdbWrapper.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.i("Junwang", "Businesscard save to db error!");
            return false;
        } finally {
            // 结束事务
            mdbWrapper.endTransaction();
        }
        return true;
    }

    public static boolean queryIsExistInChatbotFavorite(String messageId) {
        DatabaseWrapper mdbWrapper = DataModel.get().getDatabase();
        Cursor cursor = mdbWrapper.rawQuery("SELECT * FROM " + DatabaseHelper.CHATBOT_FAVORITE_TABLE + " WHERE "+ DatabaseHelper.ChatbotFavoriteColumns.CHATBOT_FAV_MSG_ID  +" = ?", new String[]{messageId});
        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            return true;
        }
        return false;
    }
}
