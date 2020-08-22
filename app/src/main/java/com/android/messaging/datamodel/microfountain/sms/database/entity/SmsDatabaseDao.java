package com.android.messaging.datamodel.microfountain.sms.database.entity;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.android.messaging.datamodel.microfountain.sms.database.entity.message.MessageEntity;

@Dao
public interface SmsDatabaseDao {
    @Query("SELECT * FROM message WHERE _id=:message_id")
    public MessageEntity getMessage(int message_id);
}
