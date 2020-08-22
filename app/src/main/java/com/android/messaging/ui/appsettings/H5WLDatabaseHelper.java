package com.android.messaging.ui.appsettings;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import com.android.messaging.util.LogUtil;

public class H5WLDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "H5WLDatabaseHelper";
    public static final String H5WL_TABLENAME = "H5_Whitelist1";
    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + H5WL_TABLENAME +" ("
            + "_id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL ,"
            + "codenumber TEXT NOT NULL" + ")";

    public H5WLDatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        LogUtil.d(TAG, "create table");
        //db.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
