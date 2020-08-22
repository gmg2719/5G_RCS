/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.messaging.datamodel.action;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Telephony.Sms;
import android.text.TextUtils;

import com.android.messaging.Factory;
import com.android.messaging.datamodel.BugleDatabaseOperations;
import com.android.messaging.datamodel.BugleNotifications;
import com.android.messaging.datamodel.DataModel;
import com.android.messaging.datamodel.DatabaseHelper;
import com.android.messaging.datamodel.DatabaseWrapper;
import com.android.messaging.datamodel.MessagingContentProvider;
import com.android.messaging.datamodel.SyncManager;
import com.android.messaging.datamodel.data.BusinessCardService;
import com.android.messaging.datamodel.data.MessageData;
import com.android.messaging.datamodel.data.ParticipantData;
import com.android.messaging.datamodel.microfountain.sms.ChatbotUtils;
import com.android.messaging.datamodel.microfountain.sms.database.BaseInvokeChatbotInfo;
import com.android.messaging.datamodel.microfountain.sms.database.ChatbotInfoQueryHelper;
import com.android.messaging.datamodel.microfountain.sms.database.QueryHandler;
import com.android.messaging.sms.MmsSmsUtils;
import com.android.messaging.util.ContentType;
import com.android.messaging.util.LogUtil;
import com.android.messaging.util.OsUtil;


/**
 * Action used to "receive" an incoming message
 */
public class ReceiveSmsMessageAction extends Action implements Parcelable {
    private static final String TAG = LogUtil.BUGLE_DATAMODEL_TAG;

    private static final String KEY_MESSAGE_VALUES = "message_values";

    /**
     * Create a message received from a particular number in a particular conversation
     */
    public ReceiveSmsMessageAction(final ContentValues messageValues) {
        actionParameters.putParcelable(KEY_MESSAGE_VALUES, messageValues);
    }

    @Override
    protected Object executeAction() {
        final Context context = Factory.get().getApplicationContext();
        final ContentValues messageValues = actionParameters.getParcelable(KEY_MESSAGE_VALUES);
        final DatabaseWrapper db = DataModel.get().getDatabase();

        // Get the SIM subscription ID
        Integer subId = messageValues.getAsInteger(Sms.SUBSCRIPTION_ID);
        if (subId == null) {
            subId = ParticipantData.DEFAULT_SELF_SUB_ID;
        }
        // Make sure we have a sender address
        String address = messageValues.getAsString(Sms.ADDRESS);
        //add by junwang
        String content_type = ContentType.TEXT_PLAIN;
        if (TextUtils.isEmpty(address)) {
            LogUtil.w(TAG, "Received an SMS without an address; using unknown sender.");
            address = ParticipantData.getUnknownSenderDestination();
            messageValues.put(Sms.ADDRESS, address);
        }
        //add by junwang for RCS
        else if(address.startsWith("sip:")){
            content_type = messageValues.getAsString("content_type");
            LogUtil.i("Junwang", "insert db content_type ="+content_type);
        }
        //add by junwang end
        LogUtil.w(TAG, "Junwang Received an SMS with an address " + address);
        //add by junwang
        /*if(!DatabaseHelper.isAddrInWebViewWhiteList(address, address)){
            LogUtil.w(TAG, "Received an SMS with an address not exists in H5 White List.");
            return true;
        }*/
//        if(IsH5Contact(db, address)){
//            LogUtil.w(TAG, "Received an SMS with an address exists in H5 White List.");
//            ConversationListActivity.setNormalConversationList(false);
//        }else{
//            LogUtil.w(TAG, "Received an SMS with an address not exists in H5 White List.");
//            ConversationListActivity.setNormalConversationList(true);
//        }
        final ParticipantData rawSender = ParticipantData.getFromRawPhoneBySimLocale(
                address, subId);

        // TODO: Should use local timestamp for this?
        final long received = messageValues.getAsLong(Sms.DATE);
        // Inform sync that message has been added at local received timestamp
        final SyncManager syncManager = DataModel.get().getSyncManager();
        syncManager.onNewMessageInserted(received);

        // Make sure we've got a thread id
        final long threadId = MmsSmsUtils.Threads.getOrCreateThreadId(context, address);
        messageValues.put(Sms.THREAD_ID, threadId);
        final boolean blocked = BugleDatabaseOperations.isBlockedDestination(
                db, rawSender.getNormalizedDestination());
        final String conversationId = BugleDatabaseOperations.
                getOrCreateConversationFromRecipient(db, threadId, blocked, rawSender);

        final boolean messageInFocusedConversation =
                DataModel.get().isFocusedConversation(conversationId);
        final boolean messageInObservableConversation =
                DataModel.get().isNewMessageObservable(conversationId);

        MessageData message = null;
        // Only the primary user gets to insert the message into the telephony db and into bugle's
        // db. The secondary user goes through this path, but skips doing the actual insert. It
        // goes through this path because it needs to compute messageInFocusedConversation in order
        // to calculate whether to skip the notification and play a soft sound if the user is
        // already in the conversation.
        LogUtil.i("Junwang", "received message");
        if (!OsUtil.isSecondaryUser()) {
            LogUtil.i("Junwang", "saved to DB");
            final boolean read = messageValues.getAsBoolean(Sms.Inbox.READ)
                    || messageInFocusedConversation;
            // If you have read it you have seen it
            final boolean seen = read || messageInObservableConversation || blocked;
            messageValues.put(Sms.Inbox.READ, read ? Integer.valueOf(1) : Integer.valueOf(0));

            // incoming messages are marked as seen in the telephony db
            messageValues.put(Sms.Inbox.SEEN, 1);

            // Insert into telephony
            final Uri messageUri = context.getContentResolver().insert(Sms.Inbox.CONTENT_URI,
                    messageValues);

            if (messageUri != null) {
                if (LogUtil.isLoggable(TAG, LogUtil.DEBUG)) {
                    LogUtil.d(TAG, "ReceiveSmsMessageAction: Inserted SMS message into telephony, "
                            + "uri = " + messageUri);
                }
            } else {
                LogUtil.e(TAG, "ReceiveSmsMessageAction: Failed to insert SMS into telephony!");
            }

            final String text = messageValues.getAsString(Sms.BODY);
            final String subject = messageValues.getAsString(Sms.SUBJECT);
            final long sent = messageValues.getAsLong(Sms.DATE_SENT);
            final ParticipantData self = ParticipantData.getSelfParticipant(subId);
            final Integer pathPresent = messageValues.getAsInteger(Sms.REPLY_PATH_PRESENT);
            final String smsServiceCenter = messageValues.getAsString(Sms.SERVICE_CENTER);
            String conversationServiceCenter = null;
            // Only set service center if message REPLY_PATH_PRESENT = 1
            if (pathPresent != null && pathPresent == 1 && !TextUtils.isEmpty(smsServiceCenter)) {
                conversationServiceCenter = smsServiceCenter;
            }
            db.beginTransaction();
            try {
                final String participantId =
                        BugleDatabaseOperations.getOrCreateParticipantInTransaction(db, rawSender);
                final String selfId =
                        BugleDatabaseOperations.getOrCreateParticipantInTransaction(db, self);

                String chatbot_rcsdb_msgid = messageValues.getAsString("chatbot_rcsdb_msgid");

                message = MessageData.createReceivedSmsMessage(messageUri, conversationId,
                        participantId, selfId, text, subject, sent, received, seen, read, content_type, chatbot_rcsdb_msgid);

                BugleDatabaseOperations.insertNewMessageInTransaction(db, message);
                //add by junwang for add chatbot rcsdb msgid to message table
                BugleDatabaseOperations.updateChatbotRcsdbMsgid(db, chatbot_rcsdb_msgid, message.getMessageId());

                //add by junwang for handle business card
                //Context context = Factory.get().getApplicationContext();
                String normalizedDestination = rawSender.getNormalizedDestination();
                LogUtil.i("Junwang", "normalizedDestination = " + normalizedDestination);
                if(/*normalizedDestination != null && normalizedDestination.startsWith("106") || normalizedDestination.startsWith("+86106")*/true) {
                    Intent intent = new Intent(context, BusinessCardService.class);
                    intent.putExtra(DatabaseHelper.H5CodeNumberColumns.H5_DISPLAY_CODENUMBER, normalizedDestination);
                    context.startService(intent);
                }
                if(normalizedDestination != null && normalizedDestination.startsWith("sip:")){
                    BaseInvokeChatbotInfo bici = new BaseInvokeChatbotInfo(ChatbotUtils.getChatbotDomain(), normalizedDestination, context);
                    QueryHandler mQueryHandler = new QueryHandler(Factory.get().getApplicationContext().getContentResolver(), bici);
                    ChatbotInfoQueryHelper ciqh = new ChatbotInfoQueryHelper(mQueryHandler, ChatbotUtils.getChatbotDomain(), normalizedDestination);
                    ciqh.execQuery();
                }
                //add by junwang
//                BugleDatabaseOperations.updateMessagePartsJson(text, message.getMessageId(),true);

                BugleDatabaseOperations.updateConversationMetadataInTransaction(db, conversationId,
                        message.getMessageId(), message.getReceivedTimeStamp(), blocked,
                        conversationServiceCenter, true /* shouldAutoSwitchSelfId */);

                final ParticipantData sender = ParticipantData.getFromId(db, participantId);
                BugleActionToasts.onMessageReceived(conversationId, sender, message);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
            LogUtil.i("Junwang", "ReceiveSmsMessageAction: Received SMS message " + message.getMessageId()
                    + " in conversation " + message.getConversationId()
                    + ", uri = " + messageUri);

            ProcessPendingMessagesAction.scheduleProcessPendingMessagesAction(false, this);
        } else {
            if (LogUtil.isLoggable(TAG, LogUtil.DEBUG)) {
                LogUtil.d(TAG, "ReceiveSmsMessageAction: Not inserting received SMS message for "
                        + "secondary user.");
            }
        }
        // Show a notification to let the user know a new message has arrived
        BugleNotifications.update(false/*silent*/, conversationId, BugleNotifications.UPDATE_ALL);
        //modified by junwang
        MessagingContentProvider.notifyMessagesChanged(conversationId);
        MessagingContentProvider.notifyPartsChanged();
//        if(!DatabaseHelper.isAddrInWebViewWhiteList(address, address)){
//            LogUtil.w(TAG, "Received an SMS with an address not exists in H5 White List, don't notify messages changed.");
//        }else{
//            MessagingContentProvider.notifyMessagesChanged(conversationId);
//            MessagingContentProvider.notifyPartsChanged();
//        }


        return message;
    }
    //add by junwang
//    public boolean IsH5Contact(DatabaseWrapper db, String contact){
//        DatabaseWrapper dbWrapper;
//        if(contact == null){
//            return false;
//        }
//        if(db == null) {
//            dbWrapper = DataModel.get().getDatabase();
//        }else{
//            dbWrapper = db;
//        }
//        Cursor cursor = null;
//        int count = 0;
//        try {
//            cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.H5WHITELIST_TABLE + " WHERE codenumber LIKE ?", new String[]{ "%" + "+86" + "%" +contact.replaceAll(" ", "")});
////            cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.H5WHITELIST_TABLE + " WHERE codenumber = ?", new String[]{contact.replaceAll(" ", "")});
//            count = cursor.getCount();
//            Assert.inRange(count, 0, 1);
//        } finally {
//            if (cursor != null) {
//                cursor.close();
//                if(count > 0) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }

    private ReceiveSmsMessageAction(final Parcel in) {
        super(in);
    }

    public static final Parcelable.Creator<ReceiveSmsMessageAction> CREATOR
            = new Parcelable.Creator<ReceiveSmsMessageAction>() {
        @Override
        public ReceiveSmsMessageAction createFromParcel(final Parcel in) {
            return new ReceiveSmsMessageAction(in);
        }

        @Override
        public ReceiveSmsMessageAction[] newArray(final int size) {
            return new ReceiveSmsMessageAction[size];
        }
    };

    @Override
    public void writeToParcel(final Parcel parcel, final int flags) {
        writeActionToParcel(parcel, flags);
    }
}
