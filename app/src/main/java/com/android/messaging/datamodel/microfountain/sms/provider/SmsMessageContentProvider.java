package com.android.messaging.datamodel.microfountain.sms.provider;

import android.arch.persistence.db.SupportSQLiteQuery;
import android.arch.persistence.db.SupportSQLiteQueryBuilder;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.android.messaging.datamodel.microfountain.sms.database.RcsDatabase;
import com.android.messaging.datamodel.microfountain.sms.database.SmsDatabase;
import com.android.messaging.datamodel.microfountain.sms.database.contract.Conversation;
import com.android.messaging.util.LogUtil;
import com.microfountain.rcs.aidl.database.contract.RcsImdnRequestInfoTable;
import com.microfountain.rcs.aidl.database.contract.RcsMessageRecipientsTable;
import com.microfountain.rcs.aidl.database.contract.RcsMessageTable;
import com.microfountain.rcs.aidl.database.contract.RcsPayloadTable;
import com.microfountain.rcs.aidl.flavors.ProductFlavor;

import java.util.Map;
import java.util.Set;

public class SmsMessageContentProvider extends ContentProvider {

    private static final String TAG = "SmsMessageContentProvider";

    /**
     * 会话
     */
    private static final int CODE_CONVERSATION = 1;

    /**
     * 消息
     */
    private static final int CODE_MESSAGE = 2;

    /**
     * payload
     */
    private static final int CODE_PAYLOAD = 3;

    private static final int CODE_IMDN = 4;

    private static final int CODE_MESSAGE_RECIPIENT = 5;

    /**
     * Query Only
     */
    private static final int CODE_MESSAGE_WITH_LIMIT = 200;

    private static final int CODE_MESSAGE_SINGLE_ITEM = 201;

    private static final int CODE_IMDN_WITH_LIMIT = 400;

    /**
     * Query Only
     */
    private static final int CODE_MESSAGE_WITH_PAYLOADS = 1000;

    @VisibleForTesting
    public static final String AUTHORITY =
            "com.android.messaging.datamodel.microfountain.sms.provider.SmsMessageContentProvider";
    public static final String CONTENT_AUTHORITY = "content://" + AUTHORITY + '/';

    private static final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        mUriMatcher.addURI(ProductFlavor.MESSAGE_AND_PAYLOAD_URI_AUTHORITY, Conversation.TABLE_NAME, CODE_CONVERSATION);

        mUriMatcher.addURI(ProductFlavor.MESSAGE_AND_PAYLOAD_URI_AUTHORITY, RcsMessageTable.TABLE_NAME, CODE_MESSAGE);
        mUriMatcher.addURI(ProductFlavor.MESSAGE_AND_PAYLOAD_URI_AUTHORITY, RcsPayloadTable.TABLE_NAME, CODE_PAYLOAD);
        mUriMatcher.addURI(ProductFlavor.MESSAGE_AND_PAYLOAD_URI_AUTHORITY, RcsImdnRequestInfoTable.TABLE_NAME, CODE_IMDN);

        mUriMatcher.addURI(ProductFlavor.MESSAGE_AND_PAYLOAD_URI_AUTHORITY, RcsMessageRecipientsTable.TABLE_NAME, CODE_MESSAGE_RECIPIENT);

        mUriMatcher.addURI(ProductFlavor.MESSAGE_AND_PAYLOAD_URI_AUTHORITY, RcsMessageTable.PATH_WITH_LIMIT + "/#", CODE_MESSAGE_WITH_LIMIT);
        mUriMatcher.addURI(ProductFlavor.MESSAGE_AND_PAYLOAD_URI_AUTHORITY, RcsMessageTable.PATH_SINGLE_ITEM + "/#", CODE_MESSAGE_SINGLE_ITEM);

        mUriMatcher.addURI(ProductFlavor.MESSAGE_AND_PAYLOAD_URI_AUTHORITY, RcsImdnRequestInfoTable.PATH_WITH_LIMIT + "/#", CODE_IMDN_WITH_LIMIT);
        //marked by junwang
//        mUriMatcher.addURI(ProductFlavor.MESSAGE_AND_PAYLOAD_URI_AUTHORITY, MessageWithPayloads.PATH_BY_SEND_STATUS + "/#", CODE_MESSAGE_WITH_PAYLOADS);
    }

    private SmsDatabase smsDatabase = null;

    @Override
    public boolean onCreate() {

        LogUtil.i(TAG, "onCreate");

        Context context = getContext();

        if (context != null) {

            RcsDatabase.initialize(context);

            smsDatabase = RcsDatabase.getSharedInstance().getDatabase();
//            smsDatabase = Room.databaseBuilder(context,
//                    SmsDatabase.class, "rcs_database")
////                    .allowMainThreadQueries()//允许在主线程中查询
//                    .build();

            return true;
        }

        return false;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        LogUtil.i(TAG, "onConfigurationChanged");
    }

    private ContentResolver getContentResolver() {

        Context context = getContext();

        if (context != null) {

            return context.getContentResolver();
        }

        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        int code = mUriMatcher.match(uri);

        switch (code) {

            case CODE_CONVERSATION:
                return "vnd.android.cursor.dir/" + ProductFlavor.MESSAGE_AND_PAYLOAD_URI_AUTHORITY + "." + Conversation.TABLE_NAME;

            case CODE_MESSAGE:
            case CODE_MESSAGE_WITH_LIMIT:
                return "vnd.android.cursor.dir/" + ProductFlavor.MESSAGE_AND_PAYLOAD_URI_AUTHORITY + "." + RcsMessageTable.TABLE_NAME;

            case CODE_PAYLOAD:
                return "vnd.android.cursor.dir/" + ProductFlavor.MESSAGE_AND_PAYLOAD_URI_AUTHORITY + "." + RcsPayloadTable.TABLE_NAME;

            case CODE_IMDN:
            case CODE_IMDN_WITH_LIMIT:
                return "vnd.android.cursor.dir/" + ProductFlavor.MESSAGE_AND_PAYLOAD_URI_AUTHORITY + "." + RcsImdnRequestInfoTable.TABLE_NAME;

            case CODE_MESSAGE_RECIPIENT:
                return "vnd.android.cursor.dir/" + ProductFlavor.MESSAGE_AND_PAYLOAD_URI_AUTHORITY + "." + RcsMessageRecipientsTable.TABLE_NAME;

            case CODE_MESSAGE_SINGLE_ITEM:
                return "vnd.android.cursor.item/" + ProductFlavor.MESSAGE_AND_PAYLOAD_URI_AUTHORITY + "." + RcsMessageTable.TABLE_NAME;

            case CODE_MESSAGE_WITH_PAYLOADS:
                //marked by junwang
//                return "vnd.android.cursor.dir/" + ProductFlavor.MESSAGE_AND_PAYLOAD_URI_AUTHORITY + "." + MessageWithPayloads.ROOT_PATH;

            default:
                return null;
        }
    }

    private String getTableName(int code) {

        switch (code) {

            case CODE_CONVERSATION:
                return Conversation.TABLE_NAME;

            case CODE_MESSAGE:
            case CODE_MESSAGE_WITH_LIMIT:
            case CODE_MESSAGE_SINGLE_ITEM:
                return RcsMessageTable.TABLE_NAME;

            case CODE_PAYLOAD:
                return RcsPayloadTable.TABLE_NAME;

            case CODE_IMDN:
            case CODE_IMDN_WITH_LIMIT:
                return RcsImdnRequestInfoTable.TABLE_NAME;

            case CODE_MESSAGE_RECIPIENT:
                return RcsMessageRecipientsTable.TABLE_NAME;

            default:
                return "";
        }
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        int code = mUriMatcher.match(uri);

        LogUtil.v(TAG, "query:" + uri + " matching " + code);

        if (code == CODE_MESSAGE_WITH_LIMIT || code == CODE_IMDN_WITH_LIMIT) {

            if (smsDatabase != null) {

                String tableName = getTableName(code);

                SupportSQLiteQueryBuilder builder = SupportSQLiteQueryBuilder.builder(tableName).columns(projection).selection(selection, selectionArgs).orderBy(sortOrder);

                String lastPathSegment = uri.getLastPathSegment();

                if (lastPathSegment != null) {

                    builder.limit(lastPathSegment);
                }

                SupportSQLiteQuery query = builder.create();

                Cursor cursor = smsDatabase.getOpenHelper().getReadableDatabase().query(query);

                if (cursor != null) {

                    ContentResolver contentResolver = getContentResolver();

                    if (contentResolver != null) {

                        cursor.setNotificationUri(contentResolver, uri);
                    }

                    return cursor;
                }
            }

        } else if (code == CODE_MESSAGE_SINGLE_ITEM) {

            String lastPathSegment = uri.getLastPathSegment();

            if (lastPathSegment != null) {

                SupportSQLiteQuery query = SupportSQLiteQueryBuilder.builder(RcsMessageTable.TABLE_NAME).columns(projection).selection(RcsMessageTable.Columns._ID + " = ?", new String[]{lastPathSegment}).create();

                Cursor cursor = smsDatabase.getOpenHelper().getReadableDatabase().query(query);

                if (cursor != null) {

                    ContentResolver contentResolver = getContentResolver();

                    if (contentResolver != null) {

                        cursor.setNotificationUri(contentResolver, uri);
                    }

                    return cursor;
                }
            }

        } else if (code == CODE_MESSAGE_WITH_PAYLOADS) {

            if (smsDatabase != null) {

                String lastPathSegment = uri.getLastPathSegment();

                // TODO: 2019-09-24 use Contract static fields

                String queryString = "SELECT m.*, p._id AS payload_id, p.message_id AS payload_message_id, p.message_uuid AS payload_message_uuid, p.content_id AS payload_content_id, p.content_type AS payload_content_type, p.content_body AS payload_content_body, p.content_file_uri AS payload_content_file_uri FROM message m LEFT OUTER JOIN payload p ON m._id = p.message_id where send_status = " + lastPathSegment;

                return smsDatabase.getOpenHelper().getReadableDatabase().query(queryString);
            }

        } else {

            String tableName = getTableName(code);

            if (tableName.isEmpty()) {

                return null;
            }

            if (smsDatabase != null) {

                SupportSQLiteQuery query = SupportSQLiteQueryBuilder.builder(tableName).columns(projection).selection(selection, selectionArgs).orderBy(sortOrder).create();

                Cursor cursor = smsDatabase.getOpenHelper().getReadableDatabase().query(query);

                if (cursor != null) {

                    ContentResolver contentResolver = getContentResolver();

                    if (contentResolver != null) {

                        cursor.setNotificationUri(contentResolver, uri);
                    }

                    return cursor;
                }
            }
        }

        return null;
    }

    private void updateConversationByInsertedMessage(ContentValues values) {

        int contactIdentityType = values.getAsInteger(RcsMessageTable.Columns.CONTACT_IDENTITY_TYPE);

        String contactUri = values.getAsString(RcsMessageTable.Columns.CONTACT_URI);

        String[] projection = new String[]{Conversation.Columns._ID, Conversation.Columns.UNREAD_COUNT};

        String selection = Conversation.Columns.CONTACT_URI + "=?";

        String[] selectionArgs = new String[]{contactUri};

        Cursor cursor = query(Conversation.CONTENT_URI, projection, selection, selectionArgs, null);

        if (cursor != null) {

            try {

                if (cursor.moveToFirst()) {

                    int conversationIdColumnIndex = cursor.getColumnIndexOrThrow(Conversation.Columns._ID);

                    String idString = cursor.getString(conversationIdColumnIndex);

                    int unreadCountColumnIndex = cursor.getColumnIndexOrThrow(Conversation.Columns.UNREAD_COUNT);

                    int unreadCount = cursor.getInt(unreadCountColumnIndex);

                    unreadCount++;

                    ContentValues contentValues = new ContentValues();

                    contentValues.put(Conversation.Columns.CONVERSATION_TYPE, contactIdentityType);
                    contentValues.put(Conversation.Columns.UNREAD_COUNT, unreadCount);
                    contentValues.put(Conversation.Columns.DATE, System.currentTimeMillis());

                    String where = Conversation.Columns._ID + "=?";

                    selectionArgs = new String[]{idString};

                    LogUtil.i(TAG, "updateConversationByInsertedMessage idString: " + idString);

                    update(Conversation.CONTENT_URI, contentValues, where, selectionArgs);

                } else {

                    insertConversation(contactUri, contactIdentityType);
                }

            } catch (SQLException e) {

                LogUtil.e(TAG, "e:", e);

            } finally {

                cursor.close();
            }

        } else {
            LogUtil.i(TAG, "updateConversationByInsertedMessage cursor is null ");

            insertConversation(contactUri, contactIdentityType);
        }
    }

    private void insertConversation(String contactUri, int contactIdentityType) {

        ContentValues contentValues = new ContentValues();

        contentValues.put(Conversation.Columns.CONTACT_URI, contactUri);
        contentValues.put(Conversation.Columns.CONVERSATION_TYPE, contactIdentityType);
        contentValues.put(Conversation.Columns.UNREAD_COUNT, 1);
        contentValues.put(Conversation.Columns.DATE, System.currentTimeMillis());

        insert(Conversation.CONTENT_URI, contentValues);
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        if (values == null) {

            throw new IllegalArgumentException("insert NULL values");
        }

        LogUtil.v(TAG, "insert uri: " + uri);

        Set<Map.Entry<String, Object>> valueSet = values.valueSet();

        for (Map.Entry<String, Object> value :
                valueSet) {

            LogUtil.v(TAG, "value: " + value.getKey() + "/" + value.getValue());
        }

        int code = mUriMatcher.match(uri);

        LogUtil.v(TAG, "insert code: " + code);

        if (code == CODE_MESSAGE) {

            try {

                long rowId = smsDatabase.getOpenHelper().getWritableDatabase().insert(RcsMessageTable.TABLE_NAME, SQLiteDatabase.CONFLICT_ROLLBACK, values);

                LogUtil.i(TAG, "insert message rowId: " + rowId);

                if (rowId != -1) {

                    updateConversationByInsertedMessage(values);

                    // TODO: 2019-09-23 update Contact Info such as Bot features

                    ContentResolver contentResolver = getContentResolver();

                    if (contentResolver != null) {

                        contentResolver.notifyChange(uri, null);
                    }

                    return ContentUris.withAppendedId(uri, rowId);
                }

            } catch (SQLException e) {

                LogUtil.e(TAG, "e:", e);
            }

        } else {

            String tableName = getTableName(code);

            if (tableName.isEmpty()) {

                return null;
            }

            try {

                long ID = smsDatabase.getOpenHelper().getWritableDatabase().insert(tableName, SQLiteDatabase.CONFLICT_REPLACE, values);

                if (ID != - 1) {

                    ContentResolver contentResolver = getContentResolver();

                    if (contentResolver != null) {

                        contentResolver.notifyChange(uri, null);
                    }

                    return ContentUris.withAppendedId(uri, ID);
                }

            } catch (SQLException e) {

                LogUtil.e(TAG, "e:", e);
            }
        }

        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        LogUtil.i(TAG, "delete uri: " + uri);

        int code = mUriMatcher.match(uri);

        String tableName = getTableName(code);

        if (tableName.isEmpty()) {

            return 0;
        }

        int re = smsDatabase.getOpenHelper().getWritableDatabase().delete(tableName, selection, selectionArgs);

        ContentResolver contentResolver = getContentResolver();

        if (contentResolver != null) {

            contentResolver.notifyChange(uri, null);
        }

        return re;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        if (values == null) {

            LogUtil.e(TAG, "update NULL values ");

            return 0;
        }

        LogUtil.i(TAG, "update uri: " + uri);

        Set<Map.Entry<String, Object>> valueSet = values.valueSet();

        for (Map.Entry<String, Object> value :
                valueSet) {

            LogUtil.v(TAG, "value: " + value.getKey() + "/" + value.getValue());
        }

        int code = mUriMatcher.match(uri);

        String tableName = getTableName(code);

        if (tableName.isEmpty()) {

            return 0;
        }

        int re = smsDatabase.getOpenHelper().getWritableDatabase().update(tableName, SQLiteDatabase.CONFLICT_ABORT, values, selection, selectionArgs);

        ContentResolver contentResolver = getContentResolver();

        if (contentResolver != null) {

            contentResolver.notifyChange(uri, null);
        }

        return re;
    }
}
