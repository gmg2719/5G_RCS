package com.android.messaging.datamodel.microfountain.sms.database.entity.message;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.microfountain.rcs.aidl.database.contract.RcsMessageRecipientsTable;

@Entity(tableName = RcsMessageRecipientsTable.TABLE_NAME,
        indices = {@Index(value = {RcsMessageRecipientsTable.Columns.MESSAGE_ID}), @Index(value = {RcsMessageRecipientsTable.Columns.CONTACT_URI, RcsMessageRecipientsTable.Columns.SELECTED_MESSAGE_UUID})})
public class MessageRecipientsEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = RcsMessageRecipientsTable.Columns._ID)
    public int _id;

    @ColumnInfo(name = RcsMessageRecipientsTable.Columns.MESSAGE_ID)
    public int messageId;

    @ColumnInfo(name = RcsMessageRecipientsTable.Columns.CONTACT_URI)
    public String contactUri;

    /**
     * 消息状态，取值参考{@link com.microfountain.rcs.aidl.database.types.RcsMessageSendStatus}
     */
    @ColumnInfo(name = RcsMessageRecipientsTable.Columns.SEND_STATUS)
    public int sendStatus;

    @ColumnInfo(name = RcsMessageRecipientsTable.Columns.SELECTED_SUBSCRIPTION_ID)
    public int selectedSubscriptionId;

    @ColumnInfo(name = RcsMessageRecipientsTable.Columns.SELECTED_CONVERSATION_UUID)
    public String selectedConversationUuid;

    @ColumnInfo(name = RcsMessageRecipientsTable.Columns.SELECTED_MESSAGE_UUID)
    public String selectedMessageUuid;

    @ColumnInfo(name = RcsMessageRecipientsTable.Columns.MESSAGE_TECH)
    public String messageTech;

    @ColumnInfo(name = RcsMessageRecipientsTable.Columns.IMDN_DELIVERY_STATUS)
    public String imdnDeliveryStatus;

    @ColumnInfo(name = RcsMessageRecipientsTable.Columns.IMDN_PROCESSING_STATUS)
    public String imdnProcessingStatus;

    @ColumnInfo(name = RcsMessageRecipientsTable.Columns.IMDN_INTERWORKING_STATUS)
    public String imdnInterworkingStatus;

    @ColumnInfo(name = RcsMessageRecipientsTable.Columns.IMDN_DISPLAY_STATUS)
    public String imdnDisplayStatus;

    @ColumnInfo(name = RcsMessageRecipientsTable.Columns.REVOCATION_SUPPORT)
    public int revocationSupport;

    @ColumnInfo(name = RcsMessageRecipientsTable.Columns.DELIVERY_ASSURANCE_STATUS)
    public int deliveryAssuranceStatus;


}
