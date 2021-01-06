package com.android.messaging.datamodel.microfountain.sms;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.android.messaging.datamodel.BugleDatabaseOperations;
import com.android.messaging.datamodel.DataModel;
import com.android.messaging.datamodel.DatabaseWrapper;
import com.android.messaging.datamodel.data.MessageData;
import com.android.messaging.datamodel.data.ParticipantData;
import com.android.messaging.util.LogUtil;
import com.google.gson.Gson;
import com.microfountain.rcs.aidl.database.contract.RcsMessageTable;
import com.microfountain.rcs.aidl.database.types.RcsMessageDirection;
import com.microfountain.rcs.aidl.database.types.RcsMessageReadStatus;
import com.microfountain.rcs.aidl.database.types.RcsMessageSendStatus;
import com.microfountain.rcs.aidl.database.types.RcsMessageVisibility;
import com.microfountain.rcs.rcskit.service.message.RcsMessageService;
import com.microfountain.rcs.support.MessageTypes;
import com.microfountain.rcs.support.model.gson.message.Extra1;
import com.microfountain.rcs.support.model.xml.geo.GeoPushEnvelope;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SendRcsMsgUtils {
//    private Context mContext;
    public static ContentValues buildMessageContentValues(Context context, String conversationUuid, String
            contributionUuid, String contactUri, int contactIdentityType, String subject,
                                            int subscriptionId, String nativeAppDBMsgId) {
        ContentValues values = new ContentValues();
        //消息唯一ID
        String messageUuid = UUID.randomUUID().toString();
        values.put(RcsMessageTable.Columns.MESSAGE_UUID, messageUuid);
        //会话唯一ID
        values.put(RcsMessageTable.Columns.CONVERSATION_UUID, conversationUuid);
        //对话唯一ID
        values.put(RcsMessageTable.Columns.CONTRIBUTION_UUID, contributionUuid);
        //消息发送表示
        values.put(RcsMessageTable.Columns.DIRECTION, RcsMessageDirection.OUTGOING);
        //消息发送状态默认为待发送
        values.put(RcsMessageTable.Columns.SEND_STATUS,
                RcsMessageSendStatus.PENDING);
        //消息发送时间戳
        values.put(RcsMessageTable.Columns.DATE, System.currentTimeMillis());
        //当前登录RCS的手机号码
        TelephonyManager phoneManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNumber="";
//        try{
//            phoneNumber = phoneManager.getLine1Number();
//        }catch(SecurityException e){
//            LogUtil.i("Junwang", "No permission to get phone number");
//        }finally {
//
//        }
        phoneNumber = ChatbotUtils.getPhoneNumber();//getSelfNumber(nativeAppDBMsgId);
        if(phoneNumber == null || phoneNumber.length() == 0){
            phoneNumber = /*"+8613777496301"*/"+8615735796495";
        }
        LogUtil.i("Junwang", "phoneNumber="+phoneNumber);
        values.put(RcsMessageTable.Columns.FROM_URI, "tel:"+
                phoneNumber);
//        values.put(RcsMessageTable.Columns.FROM_URI, "tel:" + PhoneUtils.get(subscriptionId).
//                getPhoneNumber(subscriptionId));
        //消息接收者URI
        values.put(RcsMessageTable.Columns.TO_URI, contactUri);
        //消息者联系人类型，参考 com.microfountain.rcs.aidl.database.types.RcsMessageContactIdentityType
        values.put(RcsMessageTable.Columns.CONTACT_IDENTITY_TYPE,
                contactIdentityType);
        //联系人URI
        values.put(RcsMessageTable.Columns.CONTACT_URI, contactUri);
        //消息已读状态，默认已读
        values.put(RcsMessageTable.Columns.READ, RcsMessageReadStatus.READ);
        //消息主题
        values.put(RcsMessageTable.Columns.SUBJECT, subject);
        //MaaP消息流量类型标识
        values.put(RcsMessageTable.Columns.MAAP_TRAFFIC_TYPE, "");
        // 消息递送通知状态，默认空
        values.put(RcsMessageTable.Columns.IMDN_SUMMARY, "");
        //UI是否显示此条消息
        values.put(RcsMessageTable.Columns.VISIBILITY,
                RcsMessageVisibility.VISIBLE);
        //消息所属SIM卡订阅ID
        values.put(RcsMessageTable.Columns.SUB_ID, subscriptionId);
        //用于RcsService Application发送消息
        values.put(RcsMessageTable.Columns.PREFERRED_MESSAGING_TECH, "");
        //用于RcsService Application发送消息
        values.put(RcsMessageTable.Columns.SELECTED_MESSAGING_TECH, "");
        //用于RcsService Application发送消息
        values.put(RcsMessageTable.Columns.HTTP_CONTENT_SERVER_RESPONSE_BODY, new
        byte[]{});
        //Chatbot消息是否匿名模式，中移动服务器默认非匿名
        values.put(RcsMessageTable.Columns.ANONYMIZATION_ENABLED, 0);
        //上行建议回复或建议操作消息时用到此字段
        values.put(RcsMessageTable.Columns.EXTRA_1, new byte[]{});
        //扩展字段
        values.put(RcsMessageTable.Columns.EXTRA_2, nativeAppDBMsgId.getBytes());
        //扩展字段
        values.put(RcsMessageTable.Columns.EXTRA_3, new byte[]{});

        return values;
    }

    public static String getSelfNumber(String messageId){
        final DatabaseWrapper db = DataModel.get().getDatabase();
        final MessageData message = BugleDatabaseOperations.readMessage(db, messageId);
        if(message != null){
            final ParticipantData self = BugleDatabaseOperations.getExistingParticipant(
                    db, message.getSelfId());
            return self.getNormalizedDestination();
        }
        return null;
    }

    public static int insertMessage(Context context, ContentValues values) {
        Uri messageUri =
                context.getContentResolver().insert(RcsMessageTable.CONTENT_URI, values);
        if (messageUri != null) {
            //消息数据表中_id，MmsProvider Application实现消息数据表的ContentProvider，插 入新RCS消息时，将消息_id拼接到Uri中，用于通知RcsService Application发送消息
            String lastPathSegment = messageUri.getLastPathSegment();
            if (!TextUtils.isEmpty(lastPathSegment) &&
                    TextUtils.isDigitsOnly(lastPathSegment)) {
                //调用RcsService Application发送RCS消息接口
                int messageId = Integer.parseInt(lastPathSegment);
                RcsMessageService.sendMessage(context, messageId);
                return messageId;
            }
        }
        return 0;
    }

    public static int sendTextMessage(Context context, String contactUri, String conversationUuid, String
            contributionUuid, String text, int contactIdentityType, int subscriptionId, String nativeAppDBMsgId) {
        LogUtil.i("Junwang", "send RCS text message.");
        //构建消息
        ContentValues values = buildMessageContentValues(context, conversationUuid,
                contributionUuid, contactUri, contactIdentityType, text, subscriptionId, nativeAppDBMsgId);
        //文本消息类型
        values.put(RcsMessageTable.Columns.CONTENT_TYPE, MessageTypes.MIME_TEXT);
        //文本消息内容
        values.put(RcsMessageTable.Columns.CONTENT_BODY,
                text.getBytes(StandardCharsets.UTF_8));
        //文件路径CONTENT URI，默认为空
        values.put(RcsMessageTable.Columns.CONTENT_FILE_URI, "");
        //时长，默认为0
        values.put(RcsMessageTable.Columns.CONTENT_DURATION, 0);
        //文件名称，默认为空
        values.put(RcsMessageTable.Columns.CONTENT_DISPLAY_NAME, "");
        //缩略图内容标识，默认为空
        values.put(RcsMessageTable.Columns.THUMB_CONTENT_ID, "");
        //新消息入库并发送
        return insertMessage(context, values);
    }

    public static void sendImageMessage(Context context, String contactUri, String conversationUuid, String
            contributionUuid, Uri filePathUri, String dispalyName, String mimeType, int
                                  contactIdentityType, int subscriptionId, String nativeAppDBMsgId) {
        //消息主题，开发者自定义
        String subject = "[图片消息]";
        //构建消息
        ContentValues values = buildMessageContentValues(context, conversationUuid,
                contributionUuid, contactUri, contactIdentityType, subject, subscriptionId, nativeAppDBMsgId);
        //图片Mime类型
        values.put(RcsMessageTable.Columns.CONTENT_TYPE, mimeType);
        //内容默认为空
        values.put(RcsMessageTable.Columns.CONTENT_BODY, new byte[0]);
        //文件路径ContentUri
        values.put(RcsMessageTable.Columns.CONTENT_FILE_URI,
        filePathUri.toString()); //时长，默认为0
        values.put(RcsMessageTable.Columns.CONTENT_DURATION, 0);
        //图片文件名称
        values.put(RcsMessageTable.Columns.CONTENT_DISPLAY_NAME, dispalyName);
        //缩略图内容标识，默认为空
        values.put(RcsMessageTable.Columns.THUMB_CONTENT_ID, "");
        //新消息入库并发送
        insertMessage(context, values);
    }

    public static void sendVoiceMessage(Context context, String contactUri, String conversationUuid, String
            contributionUuid, Uri filePathUri, String dispalyName, int duration, String
                                  mimeType, int contactIdentityType, int subscriptionId, String nativeAppDBMsgId) {
        //消息主题，开发者自定义
        String subject = "[音频消息]";
        //构建消息
        ContentValues values = buildMessageContentValues(context, conversationUuid,
                contributionUuid, contactUri, contactIdentityType, subject, subscriptionId, nativeAppDBMsgId);
        //语音文件Mime类型
        values.put(RcsMessageTable.Columns.CONTENT_TYPE, mimeType);
        //内容默认为空
        values.put(RcsMessageTable.Columns.CONTENT_BODY, new byte[0]);
        //文件路径ContentUri
        values.put(RcsMessageTable.Columns.CONTENT_FILE_URI,
                filePathUri.toString());
        //时长
        values.put(RcsMessageTable.Columns.CONTENT_DURATION, duration);
        //文件名称
        values.put(RcsMessageTable.Columns.CONTENT_DISPLAY_NAME, dispalyName);
        //缩略图内容标识，默认为空
        values.put(RcsMessageTable.Columns.THUMB_CONTENT_ID, "");
        //新消息入库并发送
        insertMessage(context, values);
    }

    public static void sendVideoMessage(Context context, String contactUri, String conversationUuid, String
            contributionUuid, Uri videoUri, String dispalyName, int duration, String
                                  mimeType, int contactIdentityType, int subscriptionId, String nativeAppDBMsgId) {
        //消息主题，开发者自定义
        String subject = "[视频消息]";
        //构建消息
        ContentValues values = buildMessageContentValues(context, conversationUuid,
                contributionUuid, contactUri, contactIdentityType, subject, subscriptionId, nativeAppDBMsgId);
        //视频文件Mime类型
        values.put(RcsMessageTable.Columns.CONTENT_TYPE, mimeType);
        //内容默认为空
        values.put(RcsMessageTable.Columns.CONTENT_BODY, new byte[0]);
        //视频文件路径ContentUri
        values.put(RcsMessageTable.Columns.CONTENT_FILE_URI, videoUri.toString());
        //时长
        values.put(RcsMessageTable.Columns.CONTENT_DURATION, duration);
        //文件名称
        values.put(RcsMessageTable.Columns.CONTENT_DISPLAY_NAME, dispalyName);
        //缩略图内容标识
        values.put(RcsMessageTable.Columns.THUMB_CONTENT_ID, "");
        //新消息入库并发送
        insertMessage(context, values);
    }

    public static void sendLocationMessage(Context context, String contactUri, String conversationUuid, String
            contributionUuid, double lot, double lat, String label, float radius, int
                                     contactIdentityType, int subscriptionId, String nativeAppDBMsgId) {
        //消息主题，开发者自定义
        String subject = "[位置消息]";
        //构建消息
        ContentValues values = buildMessageContentValues(context, conversationUuid,
                contributionUuid, contactUri, contactIdentityType, subject, subscriptionId, nativeAppDBMsgId);
        GeoPushEnvelope envelope = new GeoPushEnvelope();
//        envelope.entity = loginUri;
        envelope.pushLocation = new GeoPushEnvelope.PushLocation();
        envelope.pushLocation.id = UUID.randomUUID().toString();
        envelope.pushLocation.label = label;
        envelope.pushLocation.geoPriv = new GeoPushEnvelope.PushLocation.GeoPriv();
        envelope.pushLocation.geoPriv.locationInfo = new
                GeoPushEnvelope.PushLocation.GeoPriv.LocationInfo();
        envelope.pushLocation.geoPriv.locationInfo.point = new
                GeoPushEnvelope.PushLocation.GeoPriv.LocationInfo.Point();
        envelope.pushLocation.geoPriv.locationInfo.point.srsName =
                "urn:ogc:def:crs:EPSG::4326";
        envelope.pushLocation.geoPriv.locationInfo.point.pos = lat + " " + lot;
        envelope.pushLocation.geoPriv.locationInfo.point.radius =
                String.valueOf(radius);
        String XML = GeoPushEnvelope.toString(envelope);
        if (XML != null) {
            byte[] contentBody = XML.getBytes(StandardCharsets.UTF_8);
            values.put(RcsMessageTable.Columns.CONTENT_TYPE,
                    MessageTypes.MIME_GEO_PUSH);
            values.put(RcsMessageTable.Columns.CONTENT_BODY, contentBody);
            values.put(RcsMessageTable.Columns.CONTENT_FILE_URI, "");
            values.put(RcsMessageTable.Columns.CONTENT_DURATION, 0);
            values.put(RcsMessageTable.Columns.CONTENT_DISPLAY_NAME, "");
            values.put(RcsMessageTable.Columns.THUMB_CONTENT_ID, "");
            insertMessage(context, values);
        } else {
            LogUtil.w("Junwang", "BAD Geo Push");
        }
    }

    public static void sendVCardMessage(Context context, String contactUri, String conversationUuid, String
            contributionUuid, String vCardName, Uri vCardContentUri, int
                                  contactIdentityType, int subscriptionId, String nativeAppDBMsgId) {
        //消息主题，开发者自定义
        String subject = "[名片消息]";
        //构建消息
        ContentValues values = buildMessageContentValues(context, conversationUuid,
                contributionUuid, contactUri, contactIdentityType, subject, subscriptionId, nativeAppDBMsgId);
        //消息类型
        values.put(RcsMessageTable.Columns.CONTENT_TYPE, MessageTypes.MIME_VCARD);
        //消息内容为空
        values.put(RcsMessageTable.Columns.CONTENT_BODY, new byte[0]);
        //名片文件CONTENT URI
        values.put(RcsMessageTable.Columns.CONTENT_FILE_URI,
                vCardContentUri.toString());
        //时长为0
        values.put(RcsMessageTable.Columns.CONTENT_DURATION, 0);
        //名片文件名称
        values.put(RcsMessageTable.Columns.CONTENT_DISPLAY_NAME, vCardName);
        //缩略图内容标识
        values.put(RcsMessageTable.Columns.THUMB_CONTENT_ID, "");
        //新消息入库并发送
        insertMessage(context, values);
    }

    public static void sendPDFMessage(Context context, String contactUri, String conversationUuid, String
            contributionUuid, String fileName, Uri contentUri, int contactIdentityType, int
                                subscriptionId, String nativeAppDBMsgId) {
        String subject = "[PDF消息]";
        ContentValues values = buildMessageContentValues(context, conversationUuid,
                contributionUuid,  contactUri, contactIdentityType, subject, subscriptionId, nativeAppDBMsgId);
        values.put(RcsMessageTable.Columns.CONTENT_TYPE, MessageTypes.MIME_PDF);
        values.put(RcsMessageTable.Columns.CONTENT_BODY, new byte[0]);
        values.put(RcsMessageTable.Columns.CONTENT_FILE_URI, contentUri.toString());
        values.put(RcsMessageTable.Columns.CONTENT_DURATION, 0);
        values.put(RcsMessageTable.Columns.CONTENT_DISPLAY_NAME, fileName);
        values.put(RcsMessageTable.Columns.THUMB_CONTENT_ID, "");
        insertMessage(context, values);
    }

    public static void sendFileMessage(Context context, String contactUri, String conversationUuid, String
            contributionUuid, Uri fileUri, String fileName, int contactIdentityType, int
                                 subscriptionId, String nativeAppDBMsgId) {
        //消息主题，开发者自定义
        String subject = "[文件消息]";
        //构建消息
        ContentValues values = buildMessageContentValues(context, conversationUuid,
                contributionUuid, contactUri, contactIdentityType, subject, subscriptionId, nativeAppDBMsgId);
        //消息类型
        values.put(RcsMessageTable.Columns.CONTENT_TYPE, MessageTypes.MIME_BINARY);
        //消息内容为空
        values.put(RcsMessageTable.Columns.CONTENT_BODY, new byte[0]);
        //文件CONTENT URI
        values.put(RcsMessageTable.Columns.CONTENT_FILE_URI, fileUri.toString());
        //时长为0
        values.put(RcsMessageTable.Columns.CONTENT_DURATION, 0);
        //文件名称
        values.put(RcsMessageTable.Columns.CONTENT_DISPLAY_NAME, fileName);
        //缩略图内容标识
        values.put(RcsMessageTable.Columns.THUMB_CONTENT_ID, "");
        //新消息入库并发送
        insertMessage(context, values);
    }

    public static void sendSuggestedChipResponseMessage(Context context, String chatbotUri, String
            conversationUuid, String contributionUuid, String maapTrafficType, String
                                                  subject, String jsonContent, boolean isVisible, int contactIdentityType, int
                                                  subscriptionId, String inReplyToContributionId, String nativeAppDBMsgId) {
        //构建消息，subject必须是建议回复/建议操作消息JSONObject的displayText节点值
        ContentValues values = buildMessageContentValues(context, conversationUuid,
                contributionUuid, chatbotUri, contactIdentityType, subject, subscriptionId, nativeAppDBMsgId);
        //消息流量类型，和下发建议回复或建议操作消息保持一致
        values.put(RcsMessageTable.Columns.MAAP_TRAFFIC_TYPE, maapTrafficType);
        //消息类型
        values.put(RcsMessageTable.Columns.CONTENT_TYPE,
                MessageTypes.MIME_BOT_SUGGESTION_RESPONSE);
        //消息内容
        values.put(RcsMessageTable.Columns.CONTENT_BODY,
                jsonContent.getBytes(StandardCharsets.UTF_8));
        //文件CONTENT URI
        values.put(RcsMessageTable.Columns.CONTENT_FILE_URI, "");
        //文件时长
        values.put(RcsMessageTable.Columns.CONTENT_DURATION, 0);
        //文件名称
        values.put(RcsMessageTable.Columns.CONTENT_DISPLAY_NAME, "");
        //缩略图内容标识
        values.put(RcsMessageTable.Columns.THUMB_CONTENT_ID, "");
        //UI是否显示该条消息
        int visibility = isVisible ? RcsMessageVisibility.VISIBLE :
                RcsMessageVisibility.INVISIBLE;
        values.put(RcsMessageTable.Columns.VISIBILITY, visibility);
        if (inReplyToContributionId != null && !inReplyToContributionId.isEmpty()) {
            Extra1 extra1 = new Extra1();
            List<Extra1.Header> sipHeaderList = new ArrayList<>();
            Extra1.Header inReplyToContributionIdHeader = new Extra1.Header();
            inReplyToContributionIdHeader.name = "InReplyTo-Contribution-ID";
            inReplyToContributionIdHeader.value = inReplyToContributionId;
            sipHeaderList.add(inReplyToContributionIdHeader);
            extra1.sipHeaders = sipHeaderList.toArray(new Extra1.Header[0]);
            String extra1JsonString = new Gson().toJson(extra1);
            values.put(RcsMessageTable.Columns.EXTRA_1,
                    extra1JsonString.getBytes(StandardCharsets.UTF_8));
        }
        //新消息入库并发送
        insertMessage(context, values);
    }

    boolean resendMessage(Context context, int messageId, String idString) {
        ContentValues values = new ContentValues(3);
        //消息发送状态更新为待发送
        values.put(RcsMessageTable.Columns.SEND_STATUS,
        RcsMessageSendStatus.PENDING);
        //消息发送时间戳
        values.put(RcsMessageTable.Columns.DATE, System.currentTimeMillis());
        //更新消息唯一ID
        values.put(RcsMessageTable.Columns.MESSAGE_UUID,
                UUID.randomUUID().toString());
        String where = RcsMessageTable.Columns._ID + "=?";
        //根据消息_id更新消息记录
        int rowsCount =
                context.getContentResolver().update(RcsMessageTable.CONTENT_URI, values, where,
                        new String[]{idString});
        boolean bUpdateSuccess = rowsCount > 0;
        if (bUpdateSuccess) {
//发送消息
            RcsMessageService.sendMessage(context, messageId);
        }
        return bUpdateSuccess;
    }
}
