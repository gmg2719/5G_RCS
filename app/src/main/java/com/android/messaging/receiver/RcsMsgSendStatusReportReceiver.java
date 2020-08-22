package com.android.messaging.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.android.messaging.datamodel.action.SendMessageAction;
import com.android.messaging.datamodel.microfountain.sms.database.RcsDatabaseUtils;
import com.android.messaging.datamodel.microfountain.sms.database.entity.message.MessageEntity;
import com.android.messaging.util.LogUtil;
import com.microfountain.rcs.aidl.broadcast.RcsMessageBroadcast;

import io.reactivex.functions.Consumer;

public class RcsMsgSendStatusReportReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int msgId = intent.getIntExtra(RcsMessageBroadcast.INTENT_EXTRA_MESSAGE_DATA_UPDATE_MESSAGE_ID_INTEGER, 0);
        String uuid = intent.getStringExtra(RcsMessageBroadcast.INTENT_EXTRA_MESSAGE_DATA_UPDATE_MESSAGE_UUID_STRING);
        boolean isSendSuccess = intent.getBooleanExtra(RcsMessageBroadcast.INTENT_EXTRA_MESSAGE_DATA_UPDATE_MESSAGE_IS_SUCCESS_BOOLEAN, false);
        LogUtil.i("Junwang", "RcsMsgSendStatusReportReceiver msgId="+msgId+", uuid="+uuid+", isSendSuccess="+isSendSuccess);
        RcsDatabaseUtils.queryMessage(msgId).subscribe(new Consumer<MessageEntity>() {
            @Override
            public void accept(MessageEntity messageEntity) throws Exception {
                if (messageEntity != null) {
                    byte[] ext2 = messageEntity.getExtra2();
                    if((ext2 != null) && (ext2.length > 0)) {
                        String msgId = new String(ext2);
                        LogUtil.i("Junwang", "update msgId="+msgId+" status");
                        SendMessageAction.updateRcsSendStatus(msgId);
                    }
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
//        if(msgId != 0) {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    SendMessageAction.updateRcsSendStatus(msgId);
//                }
//            }).start();
//        }
    }
}
