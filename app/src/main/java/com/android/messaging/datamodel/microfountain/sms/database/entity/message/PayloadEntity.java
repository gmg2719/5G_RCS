package com.android.messaging.datamodel.microfountain.sms.database.entity.message;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.microfountain.rcs.aidl.database.contract.RcsMessageTable;
import com.microfountain.rcs.aidl.database.contract.RcsPayloadTable;

import java.util.Arrays;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = RcsPayloadTable.TABLE_NAME,
        indices = {@Index(value = RcsPayloadTable.Columns._ID), @Index(value = RcsPayloadTable.Columns.MESSAGE_ID), @Index(value = RcsPayloadTable.Columns.MESSAGE_UUID)},
        foreignKeys = {@ForeignKey(entity = MessageEntity.class, parentColumns = RcsMessageTable.Columns._ID, childColumns = RcsPayloadTable.Columns.MESSAGE_ID, onDelete = CASCADE, onUpdate = CASCADE)})
public class PayloadEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = RcsPayloadTable.Columns._ID)
    private int _id;

    @ColumnInfo(name = RcsPayloadTable.Columns.MESSAGE_ID)
    private int messageId;

    /**
     * 消息唯一ID
     */
    @NonNull
    @ColumnInfo(name = RcsPayloadTable.Columns.MESSAGE_UUID)
    private String messageUuid;
    /**
     * 资源ID
     */
    @ColumnInfo(name = RcsPayloadTable.Columns.CONTENT_ID)
    private String contentId;
    /**
     * 内容类型
     */
    @ColumnInfo(name = RcsPayloadTable.Columns.CONTENT_TYPE)
    private String contentType;
    /**
     * 内容
     */
    @ColumnInfo(name = RcsPayloadTable.Columns.CONTENT_BODY)
    private byte[] contentBody;
    /**
     * 文件本地路径
     */
    @ColumnInfo(name = RcsPayloadTable.Columns.CONTENT_FILE_URI)
    private String contentFileUri;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    @NonNull
    public String getMessageUuid() {
        return messageUuid;
    }

    public void setMessageUuid(@NonNull String messageUuid) {
        this.messageUuid = messageUuid;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
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

    @Override
    public String toString() {
        return "PayloadEntity{" +
                "_id=" + _id +
                ", messageId=" + messageId +
                ", messageUuid='" + messageUuid + '\'' +
                ", contentId='" + contentId + '\'' +
                ", contentType='" + contentType + '\'' +
                ", contentBody=" + Arrays.toString(contentBody) +
                ", contentFileUri='" + contentFileUri + '\'' +
                '}';
    }
}
