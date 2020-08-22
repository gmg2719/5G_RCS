package com.android.messaging.datamodel.microfountain.sms.database.entity.message;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.microfountain.rcs.aidl.database.contract.RcsImdnRequestInfoTable;

@Entity(tableName = RcsImdnRequestInfoTable.TABLE_NAME,
        indices = {@Index(value = RcsImdnRequestInfoTable.Columns.MESSAGE_ID), @Index(value = RcsImdnRequestInfoTable.Columns.SENT)})
public class ImdnRequestInfoEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = RcsImdnRequestInfoTable.Columns._ID)
    public int _id;

    @ColumnInfo(name = RcsImdnRequestInfoTable.Columns.MESSAGE_ID)
    public int messageId;

    @ColumnInfo(name = RcsImdnRequestInfoTable.Columns.MESSAGE_UUID)
    public String messageUuid;

    @ColumnInfo(name = RcsImdnRequestInfoTable.Columns.MESSAGE_SUBSCRIPTION_ID)
    public int messageSubscriptionId;

    @ColumnInfo(name = RcsImdnRequestInfoTable.Columns.MESSAGE_CONVERSATION_ID)
    public String messageConversationId;

    @ColumnInfo(name = RcsImdnRequestInfoTable.Columns.MESSAGE_DATE)
    public long messageDate;

    @ColumnInfo(name = RcsImdnRequestInfoTable.Columns.MESSAGE_TECH)
    public String messageTech;

    @ColumnInfo(name = RcsImdnRequestInfoTable.Columns.REQUEST_TYPE)
    public String requestType;

    @ColumnInfo(name = RcsImdnRequestInfoTable.Columns.RESPONSE_STATUS)
    public String responseStatus;

    @ColumnInfo(name = RcsImdnRequestInfoTable.Columns.SENT)
    public int sent;

    @ColumnInfo(name = RcsImdnRequestInfoTable.Columns.CONTACT_URI)
    public String contactUri;
}
