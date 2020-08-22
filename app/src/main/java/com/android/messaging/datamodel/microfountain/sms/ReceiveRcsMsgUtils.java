package com.android.messaging.datamodel.microfountain.sms;

import android.content.ContentValues;
import android.provider.Telephony.Sms;

import com.android.messaging.datamodel.microfountain.sms.database.entity.message.MessageEntity;
import com.android.messaging.util.LogUtil;

public class ReceiveRcsMsgUtils {
    //for insert received RCS msg to database
    public static ContentValues buildReceivedMessageContentValues(MessageEntity msg){
        ContentValues values = new ContentValues();
        //消息所属SIM卡订阅ID
        values.put(Sms.SUBSCRIPTION_ID, msg.getSubId());
        //联系人URI
        values.put(Sms.ADDRESS, msg.getContactUri());
        //消息接收时间戳
        values.put(Sms.DATE, System.currentTimeMillis());
        //消息已读状态，默认已读
        values.put(Sms.Inbox.READ, msg.getRead());
        values.put(Sms.Inbox.SEEN, 0);
        //文本消息内容
        byte[] content = msg.getContentBody();
        if(content != null && content.length != 0) {
            values.put(Sms.BODY, new String(content));
        }
        //消息主题
//        LogUtil.i("Junwang", "rcs subject="+msg.getSubject());
//        values.put(Sms.SUBJECT, msg.getSubject());
        //消息发送时间戳
        values.put(Sms.DATE_SENT, msg.getDate());
        values.put(Sms.REPLY_PATH_PRESENT, 1);
        //短信中心
        values.put(Sms.SERVICE_CENTER, "+86057110086");
        values.put(Sms.PROTOCOL, 0);
        String content_type = ParseRcsUtils.parseMessageType(msg.getContentType(), msg.getContentBody());
        LogUtil.i("Junwang","origin content type is "+msg.getContentType()+", content body is "
                        + new String(msg.getContentBody()) + "parsed content_type is "+content_type
                );
        values.put("content_type", content_type);
        values.put("chatbot_rcsdb_msgid", msg.getMessageUuid());
        return values;
    }
}
