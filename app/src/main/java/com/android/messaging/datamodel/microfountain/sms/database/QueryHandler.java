package com.android.messaging.datamodel.microfountain.sms.database;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import java.lang.ref.WeakReference;

public class QueryHandler extends AsyncQueryHandler {

    public static final int TOKEN_QUERY_CHATBOT_INFO = 0;
    public static final int TOKEN_UPDATE_SAVE_LOCAL = 1;
    public static final int TOKEN_QUERY_SAVE_LOCAL = 2;
    public static final int TOKEN_QUERY_SAVE_LOCAL_OPERATION = 3;
    public static final int TOKEN_INSERT_SAVE_LOCAL = 4;
    public static final int TOKEN_DELETE_SAVE_LOCAL = 5;

    private final WeakReference<BaseInvokeChatbotInfo> mReference;

    public QueryHandler(ContentResolver cr, BaseInvokeChatbotInfo baseInvokeChatbotInfo) {
        super(cr);
        mReference = new WeakReference<>(baseInvokeChatbotInfo);
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        super.onQueryComplete(token, cookie, cursor);

        if (mReference.get() == null) {
            return;
        }
        if (token == TOKEN_QUERY_CHATBOT_INFO) {
            mReference.get().onQueryResult(cursor);
        } else if (token == TOKEN_QUERY_SAVE_LOCAL) {
            if (cursor == null || cursor.getCount() == 0) {
                mReference.get().setSaveLocalText(false);
            } else {
                mReference.get().setSaveLocalText(true);
            }
        } else if (token == TOKEN_QUERY_SAVE_LOCAL_OPERATION) {
            if (cursor == null || cursor.getCount() == 0) {
                mReference.get().insertSaveLocal();
            } else {
                mReference.get().deleteSaveLocal();
            }
        }
    }

    @Override
    protected void onUpdateComplete(int token, Object cookie, int result) {
        super.onUpdateComplete(token, cookie, result);
        if (mReference.get() == null) {
            return;
        }
        if (token == TOKEN_UPDATE_SAVE_LOCAL) {
            mReference.get().updateSaveLocalResult(result);
        }
    }

    @Override
    protected void onInsertComplete(int token, Object cookie, Uri uri) {
        super.onInsertComplete(token, cookie, uri);
        if (mReference.get() == null) {
            return;
        }
        if (token == TOKEN_INSERT_SAVE_LOCAL) {
            mReference.get().setSaveLocalText(true);
        }
    }

    @Override
    protected void onDeleteComplete(int token, Object cookie, int result) {
        super.onDeleteComplete(token, cookie, result);
        if (mReference.get() == null) {
            return;
        }
        if (token == TOKEN_DELETE_SAVE_LOCAL) {
            mReference.get().setSaveLocalText(false);
        }
    }
}
