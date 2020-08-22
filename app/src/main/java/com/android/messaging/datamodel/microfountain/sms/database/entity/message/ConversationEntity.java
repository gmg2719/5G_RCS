package com.android.messaging.datamodel.microfountain.sms.database.entity.message;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.android.messaging.datamodel.microfountain.sms.database.contract.Conversation;

@Entity(tableName = Conversation.TABLE_NAME, indices = {@Index(value = Conversation.Columns.CONTACT_URI)})
public class ConversationEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = Conversation.Columns._ID)
    private int _id;
    /**
     * 会话对象URI
     */
    @NonNull
    @ColumnInfo(name = Conversation.Columns.CONTACT_URI)
    private String contactUri;

    @ColumnInfo(name = Conversation.Columns.CONVERSATION_TYPE)
    private int conversationType;
    /**
     * 消息未读个数
     */
    @ColumnInfo(name = Conversation.Columns.UNREAD_COUNT)
    private int unreadCount;

    /**
     * 最后一条消息时间戳
     */
    @ColumnInfo(name = Conversation.Columns.DATE)
    private long date;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    @NonNull
    public String getContactUri() {
        return contactUri;
    }

    public void setContactUri(@NonNull String contactUri) {
        this.contactUri = contactUri;
    }

    public int getConversationType() {
        return conversationType;
    }

    public void setConversationType(int conversationType) {
        this.conversationType = conversationType;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "ConversationEntity{" +
                "_id=" + _id +
                ", contactUri='" + contactUri + '\'' +
                ", conversationType=" + conversationType +
                ", unreadCount=" + unreadCount +
                ", date=" + date +
                '}';
    }
}
