package com.android.messaging.datamodel.microfountain.sms.database.contract;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import com.android.messaging.datamodel.microfountain.sms.database.entity.message.MessageEntity;
import com.android.messaging.datamodel.microfountain.sms.database.entity.message.PayloadEntity;
import com.microfountain.rcs.aidl.database.contract.RcsMessageTable;
import com.microfountain.rcs.aidl.database.contract.RcsPayloadTable;

import java.util.List;

/**
 * 查询消息的聚合数据
 *
 * @author zfm
 */
public class MessageWithPayload {

    @Embedded
    private MessageEntity messageEntity;

    public MessageEntity getMessageEntity() {
        return messageEntity;
    }

    public void setMessageEntity(MessageEntity messageEntity) {
        this.messageEntity = messageEntity;
    }

    @Relation(entity = PayloadEntity.class, entityColumn = RcsPayloadTable.Columns.MESSAGE_UUID, parentColumn = RcsMessageTable.Columns.MESSAGE_UUID)
    private List<PayloadEntity> payloads;


    public void setPayloads(List<PayloadEntity> payloads) {
        this.payloads = payloads;
    }

    public List<PayloadEntity> getPayloads() {
        return payloads;
    }

    @Override
    public String toString() {
        return "MessageWithPayloadsObject{" +
                "messageEntity=" + messageEntity +
                ", payloads=" + payloads +
                '}';
    }
}
