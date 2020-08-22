package com.android.messaging.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import com.android.messaging.util.LogUtil;

public class MzRcsMsgReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String str = intent.getAction();
        LogUtil.i("RcsTransactionJuphoon", "MzRcsMsgReceiver handleIntent action = ");
        int i = -1;
        switch (str.hashCode())
        {
            case 0:
                if (!str.equals("com.android.mms.mzrcs.transaction.SEND_RCS_MESSAGE")) {
                    break;
                }
                break;
            case 1:
                if (!str.equals("com.android.mms.mzrcs.transaction.DOWNLOAD_ATTACHMENT")) {
                    break;
                }
            case 2:
                if (!str.equals("com.android.mms.mzrcs.transaction.DOWNLOAD_CHATBOT_FILE")) {
                    break;
                }
            case 3:
                if (!str.equals("rcs_action_cli_notify")) {
                    break;
                }
            case 4:
                if (!str.equals("rcs_action_im_notify")) {
                    break;
                }
            default:
                LogUtil.e("RcsTransactionJuphoon", "MzRcsMsgReceiver handleIntent unsupport action: " + str);
                break;
        }
//        Intent  intent1 = new Intent();
//        intent.setClass(context, MzRcsMsgReceiverService.class);
//        intent.putExtra("result", getResultCode());
//        a(context, intent);
    }

    static final Object a = new Object();
    static PowerManager.WakeLock b;

    public static void a(Context context, Intent intent)
    {
        synchronized (a)
        {
            if (b == null)
            {
                b = ((PowerManager)context.getSystemService(context.POWER_SERVICE)).newWakeLock(1, "myapp:StartingAlertService");
                b.setReferenceCounted(false);
            }
            b.acquire();
            context.startService(intent);
            return;
        }
    }
}
