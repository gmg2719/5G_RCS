package com.android.messaging.datamodel.microfountain.sms.database.entity.setting;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.microfountain.rcs.aidl.database.contract.RcsSettingsTable;

@Entity(tableName = RcsSettingsTable.TABLE_NAME)
public class SettingsEntity {

    @PrimaryKey(autoGenerate = true)
    private int _id;

    /**
     * RCS功能开关
     */
    @ColumnInfo(name = RcsSettingsTable.Columns.RCS_ENABLED)
    private int rcsEnabled;

    /**
     * RCS送达报告开关
     */
    @ColumnInfo(name = RcsSettingsTable.Columns.IMDN_ENABLED)
    private int imdnEnabled;

    /**
     * 发送RCS消息已读通知开关
     */
//    @ColumnInfo(name = SettingCustomization.Columns.SENDING_DISPLAY_NOTIFICATION_ENABLED)
    @ColumnInfo(name = "sending_display_notification_enabled")
    private int sendingDisplayNotificationEnabled;

    /**
     * 接收到文件自动下载开关
     */
    @ColumnInfo(name = RcsSettingsTable.Columns.AUTOMATIC_FILE_DOWNLOAD_ENABLED)
    private int automaticFileDownloadEnabled;

    @ColumnInfo(name = RcsSettingsTable.ExtendedColumns.SUB_ID)
    public int subId;

    @ColumnInfo(name = RcsSettingsTable.ExtendedColumns.GROUP_CHAT_ENABLED)
    private int groupChatEnabled;

    @ColumnInfo(name = RcsSettingsTable.ExtendedColumns.USER_ALIAS)
    public String userAlias;

    @ColumnInfo(name = RcsSettingsTable.ExtendedColumns.CLIENT_SUPPORTED_EXTENDED_MEDIA_TYPES_FOR_ONE_TO_ONE_CHAT)
    private String mediaTypesForOneToOneChat;

    @ColumnInfo(name = RcsSettingsTable.ExtendedColumns.CLIENT_SUPPORTED_EXTENDED_MEDIA_TYPES_FOR_GROUP_CHAT)
    private String mediaTypesForGroupChat;

    @ColumnInfo(name = RcsSettingsTable.ExtendedColumns.CLIENT_SUPPORTED_EXTENDED_MEDIA_TYPES_FOR_PUBLIC_ACCOUNT)
    private String mediaTypesForPublicAccount;

    @ColumnInfo(name = RcsSettingsTable.ExtendedColumns.CLIENT_SUPPORTED_EXTENDED_MEDIA_TYPES_FOR_CHAT_BOT_CONVERSATION)
    private String mediaTypesForChatbot;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getRcsEnabled() {
        return rcsEnabled;
    }

    public void setRcsEnabled(int rcsEnabled) {
        this.rcsEnabled = rcsEnabled;
    }

    public int getImdnEnabled() {
        return imdnEnabled;
    }

    public void setImdnEnabled(int imdnEnabled) {
        this.imdnEnabled = imdnEnabled;
    }

    public int getSendingDisplayNotificationEnabled() {
        return sendingDisplayNotificationEnabled;
    }

    public void setSendingDisplayNotificationEnabled(int sendingDisplayNotificationEnabled) {
        this.sendingDisplayNotificationEnabled = sendingDisplayNotificationEnabled;
    }

    public int getAutomaticFileDownloadEnabled() {
        return automaticFileDownloadEnabled;
    }

    public void setAutomaticFileDownloadEnabled(int automaticFileDownloadEnabled) {
        this.automaticFileDownloadEnabled = automaticFileDownloadEnabled;
    }

    public int getGroupChatEnabled() {
        return groupChatEnabled;
    }

    public void setGroupChatEnabled(int groupChatEnabled) {
        this.groupChatEnabled = groupChatEnabled;
    }

    public String getMediaTypesForOneToOneChat() {
        return mediaTypesForOneToOneChat;
    }

    public void setMediaTypesForOneToOneChat(String mediaTypesForOneToOneChat) {
        this.mediaTypesForOneToOneChat = mediaTypesForOneToOneChat;
    }

    public String getMediaTypesForGroupChat() {
        return mediaTypesForGroupChat;
    }

    public void setMediaTypesForGroupChat(String mediaTypesForGroupChat) {
        this.mediaTypesForGroupChat = mediaTypesForGroupChat;
    }

    public String getMediaTypesForPublicAccount() {
        return mediaTypesForPublicAccount;
    }

    public void setMediaTypesForPublicAccount(String mediaTypesForPublicAccount) {
        this.mediaTypesForPublicAccount = mediaTypesForPublicAccount;
    }

    public String getMediaTypesForChatbot() {
        return mediaTypesForChatbot;
    }

    public void setMediaTypesForChatbot(String mediaTypesForChatbot) {
        this.mediaTypesForChatbot = mediaTypesForChatbot;
    }

    public int getSubId() {
        return subId;
    }

    public void setSubId(int subId) {
        this.subId = subId;
    }

    public String getUserAlias() {
        return userAlias;
    }

    public void setUserAlias(String userAlias) {
        this.userAlias = userAlias;
    }

    @Override
    public String toString() {
        return "SettingsEntity{" +
                "_id=" + _id +
                ", rcsEnabled=" + rcsEnabled +
                ", imdnEnabled=" + imdnEnabled +
                ", sendingDisplayNotificationEnabled=" + sendingDisplayNotificationEnabled +
                ", automaticFileDownloadEnabled=" + automaticFileDownloadEnabled +
                ", subId=" + subId +
                ", groupChatEnabled=" + groupChatEnabled +
                ", userAlias='" + userAlias + '\'' +
                ", mediaTypesForOneToOneChat='" + mediaTypesForOneToOneChat + '\'' +
                ", mediaTypesForGroupChat='" + mediaTypesForGroupChat + '\'' +
                ", mediaTypesForPublicAccount='" + mediaTypesForPublicAccount + '\'' +
                ", mediaTypesForChatbot='" + mediaTypesForChatbot + '\'' +
                '}';
    }
}
