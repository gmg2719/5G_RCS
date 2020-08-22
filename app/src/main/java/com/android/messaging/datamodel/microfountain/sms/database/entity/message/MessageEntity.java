package com.android.messaging.datamodel.microfountain.sms.database.entity.message;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.microfountain.rcs.aidl.database.contract.RcsMessageTable;

import java.util.Arrays;

/**
 * 消息数据表
 */
@Entity(tableName = RcsMessageTable.TABLE_NAME, indices = {@Index(value = RcsMessageTable.Columns._ID, unique = true)})
public class MessageEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = RcsMessageTable.Columns._ID)
    private int _id;

    /**
     * 消息唯一ID
     */
    @NonNull
    @ColumnInfo(name = RcsMessageTable.Columns.MESSAGE_UUID)
    private String messageUuid;

    /**
     * 会话ID， 唯一标识主被叫用户间的聊天
     */
    @NonNull
    @ColumnInfo(name = RcsMessageTable.Columns.CONVERSATION_UUID)
    private String conversationUuid;

    /**
     * 标识一次会话，用户每发起一个新的业务呼叫，使用一个新的 Contribution-ID，群组固定一个Contribution-ID
     */
    @NonNull
    @ColumnInfo(name = RcsMessageTable.Columns.CONTRIBUTION_UUID)
    private String contributionUuid;

    /**
     * 消息收发类型，取值参考{@link com.microfountain.rcs.aidl.database.types.RcsMessageDirection}
     */
    @ColumnInfo(name = RcsMessageTable.Columns.DIRECTION)
    private int direction;

    /**
     * 消息状态，取值参考{@link com.microfountain.rcs.aidl.database.types.RcsMessageSendStatus}
     */
    @ColumnInfo(name = RcsMessageTable.Columns.SEND_STATUS)
    private int sendStatus;

    /**
     * 消息时间戳
     */
    @ColumnInfo(name = RcsMessageTable.Columns.DATE)
    private long date;

    /**
     * 消息发送者URI
     */
    @ColumnInfo(name = RcsMessageTable.Columns.FROM_URI)
    private String fromUri;

    /**
     * 消息接收者URI
     */
    @ColumnInfo(name = RcsMessageTable.Columns.TO_URI)
    private String toUri;

    /**
     * 消息会话联系人类型
     */
    @ColumnInfo(name = RcsMessageTable.Columns.CONTACT_IDENTITY_TYPE)
    private int contactIdentityType;

    /**
     * 消息会话联系人 URI
     */
    @ColumnInfo(name = RcsMessageTable.Columns.CONTACT_URI)
    private String contactUri;

    /**
     * 消息会话联系人昵称
     */
    @ColumnInfo(name = RcsMessageTable.Columns.CONTACT_ALIAS)
    public String contactAlias;

    /**
     * 消息已读状态，取值参考{@link com.microfountain.rcs.aidl.database.types.RcsMessageReadStatus}
     */
    @ColumnInfo(name = RcsMessageTable.Columns.READ)
    private int read;

    /**
     * 消息主题，用于会话列表展示
     */
    @ColumnInfo(name = RcsMessageTable.Columns.SUBJECT)
    private String subject;

    /**
     * 消息类型，取值参考{@link com.microfountain.rcs.support.MessageTypes}
     */
    @ColumnInfo(name = RcsMessageTable.Columns.CONTENT_TYPE)
    private String contentType;

    /**
     * 消息内容
     */
    @ColumnInfo(name = RcsMessageTable.Columns.CONTENT_BODY)
    private byte[] contentBody;

    /**
     * 消息源文件本地路径URI
     */
    @ColumnInfo(name = RcsMessageTable.Columns.CONTENT_FILE_URI)
    private String contentFileUri;

    /**
     * 媒体资源时长，以毫秒为单位
     */
    @ColumnInfo(name = RcsMessageTable.Columns.CONTENT_DURATION)
    private int contentDuration;

    /**
     * 媒体文件显示名称
     */
    @ColumnInfo(name = RcsMessageTable.Columns.CONTENT_DISPLAY_NAME)
    private String contentDisplayName;

    /**
     * 缩略图资源ID
     */
    @ColumnInfo(name = RcsMessageTable.Columns.THUMB_CONTENT_ID)
    private String thumbContentId;

    @ColumnInfo(name = RcsMessageTable.Columns.MAAP_TRAFFIC_TYPE)
    private String maapTrafficType;

    /**
     * 消息递送状态
     */
    @ColumnInfo(name = RcsMessageTable.Columns.IMDN_SUMMARY)
    private String imdnSummary;

    /**
     * 消息是否在UI展示，取值参考{@link com.microfountain.rcs.aidl.database.types.RcsMessageVisibility}
     */
    @ColumnInfo(name = RcsMessageTable.Columns.VISIBILITY)
    public int visibility;

    @ColumnInfo(name = RcsMessageTable.Columns.SUB_ID)
    private int subId;

    @ColumnInfo(name = RcsMessageTable.Columns.PREFERRED_MESSAGING_TECH)
    public String preferredMessageTech;

    @ColumnInfo(name = RcsMessageTable.Columns.SELECTED_MESSAGING_TECH)
    public String selectedMessageTech;

    @ColumnInfo(name = RcsMessageTable.Columns.HTTP_CONTENT_SERVER_RESPONSE_BODY)
    public byte[] httpContentServerResponseBody;

    @ColumnInfo(name = RcsMessageTable.Columns.ANONYMIZATION_ENABLED)
    public int anonymizationEnabled;

    @ColumnInfo(name = RcsMessageTable.Columns.EXTRA_1)
    public byte[] extra1;

    @ColumnInfo(name = RcsMessageTable.Columns.EXTRA_2)
    public byte[] extra2;

    @ColumnInfo(name = RcsMessageTable.Columns.EXTRA_3)
    public byte[] extra3;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    @NonNull
    public String getMessageUuid() {
        return messageUuid;
    }

    public void setMessageUuid(@NonNull String messageUuid) {
        this.messageUuid = messageUuid;
    }

    @NonNull
    public String getConversationUuid() {
        return conversationUuid;
    }

    public void setConversationUuid(@NonNull String conversationUuid) {
        this.conversationUuid = conversationUuid;
    }

    @NonNull
    public String getContributionUuid() {
        return contributionUuid;
    }

    public void setContributionUuid(@NonNull String contributionUuid) {
        this.contributionUuid = contributionUuid;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(int sendStatus) {
        this.sendStatus = sendStatus;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getFromUri() {
        return fromUri;
    }

    public void setFromUri(String fromUri) {
        this.fromUri = fromUri;
    }

    public String getToUri() {
        return toUri;
    }

    public void setToUri(String toUri) {
        this.toUri = toUri;
    }

    public int getContactIdentityType() {
        return contactIdentityType;
    }

    public void setContactIdentityType(int contactIdentityType) {
        this.contactIdentityType = contactIdentityType;
    }

    public String getContactUri() {
        return contactUri;
    }

    public void setContactUri(String contactUri) {
        this.contactUri = contactUri;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public byte[] getContentBody() {
        return contentBody;
    }

    public void setContentBody(byte[] contentBody) {
        this.contentBody = contentBody;
    }

    public String getContentFileUri() {
        return contentFileUri;
    }

    public void setContentFileUri(String contentFileUri) {
        this.contentFileUri = contentFileUri;
    }

    public int getContentDuration() {
        return contentDuration;
    }

    public void setContentDuration(int contentDuration) {
        this.contentDuration = contentDuration;
    }

    public String getContentDisplayName() {
        return contentDisplayName;
    }

    public void setContentDisplayName(String contentDisplayName) {
        this.contentDisplayName = contentDisplayName;
    }

    public String getThumbContentId() {
        return thumbContentId;
    }

    public void setThumbContentId(String thumbContentId) {
        this.thumbContentId = thumbContentId;
    }

    public String getMaapTrafficType() {
        return maapTrafficType;
    }

    public void setMaapTrafficType(String maapTrafficType) {
        this.maapTrafficType = maapTrafficType;
    }

    public String getImdnSummary() {
        return imdnSummary;
    }

    public void setImdnSummary(String imdnSummary) {
        this.imdnSummary = imdnSummary;
    }

    public int getSubId() {
        return subId;
    }

    public void setSubId(int subId) {
        this.subId = subId;
    }

    public byte[] getExtra2() {
        return extra2;
    }

    public MessageEntity(@NonNull String messageUuid, @NonNull String conversationUuid, @NonNull String contributionUuid) {

        this.messageUuid = messageUuid;

        this.conversationUuid = conversationUuid;

        this.contributionUuid = contributionUuid;
    }

    @Override
    public String toString() {
        return "MessageEntity{" +
                "_id=" + _id +
                ", messageUuid='" + messageUuid + '\'' +
                ", conversationUuid='" + conversationUuid + '\'' +
                ", contributionUuid='" + contributionUuid + '\'' +
                ", direction=" + direction +
                ", sendStatus=" + sendStatus +
                ", date=" + date +
                ", fromUri='" + fromUri + '\'' +
                ", toUri='" + toUri + '\'' +
                ", contactIdentityType=" + contactIdentityType +
                ", contactUri='" + contactUri + '\'' +
                ", read=" + read +
                ", subject='" + subject + '\'' +
                ", contentType='" + contentType + '\'' +
                ", contentBody=" + Arrays.toString(contentBody) +
                ", contentFileUri='" + contentFileUri + '\'' +
                ", contentDuration=" + contentDuration +
                ", contentDisplayName='" + contentDisplayName + '\'' +
                ", thumbContentId='" + thumbContentId + '\'' +
                ", maapTrafficType='" + maapTrafficType + '\'' +
                ", imdnSummary='" + imdnSummary + '\'' +
                ", subId=" + subId +
                '}';
    }
}
