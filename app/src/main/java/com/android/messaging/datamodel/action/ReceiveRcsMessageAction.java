package com.android.messaging.datamodel.action;

import android.content.ContentValues;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.android.messaging.Factory;

public class ReceiveRcsMessageAction extends Action implements Parcelable {
    private static final String KEY_MESSAGE_VALUES = "rcs_message_values";

    public ReceiveRcsMessageAction(final ContentValues messageValues) {
        actionParameters.putParcelable(KEY_MESSAGE_VALUES, messageValues);
    }

    @Override
    protected Object executeAction() {
        final Context context = Factory.get().getApplicationContext();
        final ContentValues messageValues = actionParameters.getParcelable(KEY_MESSAGE_VALUES);
        // Show a notification to let the user know a new message has arrived
//        BugleNotifications.updateRcs(false, conversationId, BugleNotifications.UPDATE_ALL);
//        BugleActionToasts.onMessageReceived(conversationId, sender, message);
        return super.executeAction();
    }

    private ReceiveRcsMessageAction(final Parcel in){super(in);}

    public static final Parcelable.Creator<ReceiveRcsMessageAction> CREATOR
            = new Parcelable.Creator<ReceiveRcsMessageAction>() {
        @Override
        public ReceiveRcsMessageAction createFromParcel(final Parcel in) {
            return new ReceiveRcsMessageAction(in);
        }

        @Override
        public ReceiveRcsMessageAction[] newArray(final int size) {
            return new ReceiveRcsMessageAction[size];
        }
    };

    @Override
    public void writeToParcel(final Parcel parcel, final int flags) {
        writeActionToParcel(parcel, flags);
    }
}
