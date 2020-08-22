package com.android.messaging.datamodel.action;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import com.android.messaging.datamodel.BugleNotifications;
import com.android.messaging.datamodel.DataModel;
import com.android.messaging.datamodel.DatabaseHelper;
import com.android.messaging.datamodel.DatabaseHelper.MessageColumns;
import com.android.messaging.datamodel.DatabaseWrapper;
import com.android.messaging.util.LogUtil;

public class MarkAsSubscribedAction extends Action implements Parcelable {
    private static final String TAG = LogUtil.BUGLE_DATAMODEL_TAG;

    private static final String KEY_RCSDB_MSG_ID = "rcsdb_msg_id";

    /**
     * Mark all the messages as read for a particular conversation.
     */
    public static void markAsSubscribed(final String rcsDBMsgId) {
        final MarkAsSubscribedAction action = new MarkAsSubscribedAction(rcsDBMsgId);
        action.start();
    }

    private MarkAsSubscribedAction(final String rcsDBMsgId) {
        actionParameters.putString(KEY_RCSDB_MSG_ID, rcsDBMsgId);
    }

    @Override
    protected Object executeAction() {
        final String rcsDBMsgId = actionParameters.getString(KEY_RCSDB_MSG_ID);

        // TODO: Consider doing this in background service to avoid delaying other actions
        final DatabaseWrapper db = DataModel.get().getDatabase();

        // Mark all messages in thread as read in telephony
//        final long threadId = BugleDatabaseOperations.getThreadId(db, conversationId);
//        if (threadId != -1) {
//            MmsUtils.updateSmsReadStatus(threadId, Long.MAX_VALUE);
//        }

        // Update local db
        db.beginTransaction();
        try {
            final ContentValues values = new ContentValues();
            values.put(MessageColumns.CHATBOT_RCSDB_MSGID, rcsDBMsgId);
            values.put(MessageColumns.CHATBOT_SUBSCRIBE_STATUS, 1);

            final int count = db.update(DatabaseHelper.MESSAGES_TABLE, values,
                    "(" + MessageColumns.CHATBOT_SUBSCRIBE_STATUS + " !=1 ) AND " +
                            MessageColumns.CHATBOT_RCSDB_MSGID + "=?",
                    new String[] { rcsDBMsgId });
//            if (count > 0) {
//                MessagingContentProvider.notifyMessagesChanged(conversationId);
//            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        // After marking messages as read, update the notifications. This will
        // clear the now stale notifications.
        BugleNotifications.update(false/*silent*/, BugleNotifications.UPDATE_ALL);
        return null;
    }

    private MarkAsSubscribedAction(final Parcel in) {
        super(in);
    }

    public static final Parcelable.Creator<MarkAsSubscribedAction> CREATOR
            = new Parcelable.Creator<MarkAsSubscribedAction>() {
        @Override
        public MarkAsSubscribedAction createFromParcel(final Parcel in) {
            return new MarkAsSubscribedAction(in);
        }

        @Override
        public MarkAsSubscribedAction[] newArray(final int size) {
            return new MarkAsSubscribedAction[size];
        }
    };

    @Override
    public void writeToParcel(final Parcel parcel, final int flags) {
        writeActionToParcel(parcel, flags);
    }
}
