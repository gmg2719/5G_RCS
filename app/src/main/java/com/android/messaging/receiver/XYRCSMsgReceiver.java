package com.android.messaging.receiver;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

import com.android.messaging.datamodel.action.ReceiveSmsMessageAction;
import com.android.messaging.datamodel.microfountain.sms.ReceiveRcsMsgUtils;
import com.android.messaging.datamodel.microfountain.sms.database.RcsDatabaseUtils;
import com.android.messaging.datamodel.microfountain.sms.database.entity.message.MessageEntity;
import com.android.messaging.util.LogUtil;
import com.microfountain.rcs.aidl.broadcast.RcsMessageBroadcast;

import io.reactivex.functions.Consumer;

public class XYRCSMsgReceiver extends BroadcastReceiver {
//    static int PreMsgId = 0;
    @Override
    public void onReceive(Context context, Intent intent) {
        int msgId = intent.getIntExtra(RcsMessageBroadcast.INTENT_EXTRA_RECEIVE_NEW_MESSAGE_MESSAGE_ID_INTEGER, 0);
        String msguuid = intent.getStringExtra(RcsMessageBroadcast.INTENT_EXTRA_RECEIVE_NEW_MESSAGE_MESSAGE_UUID_STRING);
        String uuid = intent.getStringExtra(RcsMessageBroadcast.INTENT_EXTRA_RECEIVE_NEW_MESSAGE_MESSAGE_UUID_STRING);
        LogUtil.i("Junwang", "XYRCSMsgReceiver msgId="+msgId+", msguuid="+msguuid+", uuid="+uuid);
//        SmsDatabaseDao smsdao = RcsDatabase.getSharedInstance().getDatabase().dao();
//        MessageEntity message = smsdao.getMessage(uuid);
//        if(message != null){
//            LogUtil.i("Junwang", "received message content: "+message.getSubject());
//        }
//        if(PreMsgId == msgId){
//            LogUtil.i("Junwang", "Received repeat RCS Message incoming broadcast.");
//        }else {
//            PreMsgId = msgId;
            RcsDatabaseUtils.queryMessage(msgId).subscribe(new Consumer<MessageEntity>() {
                @Override
                public void accept(MessageEntity messageEntity) throws Exception {
                    if (messageEntity != null) {
                        LogUtil.i("Junwang", "received message content:" + messageEntity.getSubject());
//                    final ReceiveRcsMessageAction action = new ReceiveRcsMessageAction(messageValues);
//                    action.start();
                        ContentValues messageContentValues = ReceiveRcsMsgUtils.buildReceivedMessageContentValues(messageEntity);
                        final ReceiveSmsMessageAction action = new ReceiveSmsMessageAction(messageContentValues);
                        action.start();
//                        ChatbotUtils.queryChatbotInfo(Factory.get().getApplicationContext(), messageEntity.getContactUri());
                    } else {
                        LogUtil.i("Junwang", "received message is null");
                    }
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    throwable.printStackTrace();
                }
            });
//        }
    }
}
