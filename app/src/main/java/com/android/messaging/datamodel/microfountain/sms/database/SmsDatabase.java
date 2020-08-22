package com.android.messaging.datamodel.microfountain.sms.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.android.messaging.datamodel.microfountain.sms.database.entity.SmsDatabaseDao;
import com.android.messaging.datamodel.microfountain.sms.database.entity.message.ConversationEntity;
import com.android.messaging.datamodel.microfountain.sms.database.entity.message.ImdnRequestInfoEntity;
import com.android.messaging.datamodel.microfountain.sms.database.entity.message.MessageEntity;
import com.android.messaging.datamodel.microfountain.sms.database.entity.message.MessageRecipientsEntity;
import com.android.messaging.datamodel.microfountain.sms.database.entity.message.PayloadEntity;
import com.android.messaging.datamodel.microfountain.sms.database.entity.setting.SettingsEntity;

/**
 * Created by hk on 2019/9/27.
 */
@Database(entities = {SettingsEntity.class, ConversationEntity.class,
        MessageEntity.class, PayloadEntity.class, MessageRecipientsEntity.class, ImdnRequestInfoEntity.class}, exportSchema = false, version = 4)
public abstract class SmsDatabase extends RoomDatabase {
    public abstract SmsDatabaseDao dao();
}
