package com.android.messaging.datamodel.microfountain.sms.database;

import com.android.messaging.datamodel.microfountain.sms.database.entity.message.MessageEntity;
import com.android.messaging.util.LogUtil;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;


public class RcsDatabaseUtils {
    public static Observable<MessageEntity> queryMessage(final int message_id) {
        return Observable.create(new ObservableOnSubscribe<MessageEntity>() {
            @Override
            public void subscribe(ObservableEmitter<MessageEntity> emitter) {
                MessageEntity qurery = RcsDatabase.getSharedInstance().getDatabase().dao().getMessage(message_id);
                if (qurery == null) {
                    LogUtil.d("Junwang", "this id:" + message_id + "don't exist");
                    return;
                }
                emitter.onNext(qurery);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(Schedulers.io());
    }
}
