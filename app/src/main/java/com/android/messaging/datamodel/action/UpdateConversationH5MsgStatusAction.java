package com.android.messaging.datamodel.action;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.android.messaging.datamodel.DataModel;
import com.android.messaging.datamodel.DatabaseWrapper;
import com.android.messaging.datamodel.MessagingContentProvider;
import com.android.messaging.util.Assert;

public class UpdateConversationH5MsgStatusAction extends Action {

    public static void SetH5MsgConversation(final String conversationId) {
        final UpdateConversationH5MsgStatusAction action =
                new UpdateConversationH5MsgStatusAction(conversationId, true /* isArchive */);
        action.start();
    }

    public static void UnsetH5MsgConversation(final String conversationId) {
        final UpdateConversationH5MsgStatusAction action =
                new UpdateConversationH5MsgStatusAction(conversationId, false /* isArchive */);
        action.start();
    }

    private static final String KEY_CONVERSATION_ID = "conversation_id";
    private static final String KEY_IS_H5MSG = "is_h5msg";

    protected UpdateConversationH5MsgStatusAction(
            final String conversationId, final boolean isH5Msg) {
        Assert.isTrue(!TextUtils.isEmpty(conversationId));
        actionParameters.putString(KEY_CONVERSATION_ID, conversationId);
        actionParameters.putBoolean(KEY_IS_H5MSG, isH5Msg);
    }

    @Override
    protected Object executeAction() {
        final String conversationId = actionParameters.getString(KEY_CONVERSATION_ID);
        final boolean isH5Msg = actionParameters.getBoolean(KEY_IS_H5MSG);

        final DatabaseWrapper db = DataModel.get().getDatabase();
        db.beginTransaction();
        try {
//            BugleDatabaseOperations.updateConversationH5MsgInTransaction(
//                    db, conversationId, isH5Msg);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        MessagingContentProvider.notifyConversationListChanged();
        MessagingContentProvider.notifyConversationMetadataChanged(conversationId);
        return null;
    }

    protected UpdateConversationH5MsgStatusAction(final Parcel in) {
        super(in);
    }

    public static final Parcelable.Creator<UpdateConversationH5MsgStatusAction> CREATOR
            = new Parcelable.Creator<UpdateConversationH5MsgStatusAction>() {
        @Override
        public UpdateConversationH5MsgStatusAction createFromParcel(final Parcel in) {
            return new UpdateConversationH5MsgStatusAction(in);
        }

        @Override
        public UpdateConversationH5MsgStatusAction[] newArray(final int size) {
            return new UpdateConversationH5MsgStatusAction[size];
        }
    };

    @Override
    public void writeToParcel(final Parcel parcel, final int flags) {
        writeActionToParcel(parcel, flags);
    }
}
