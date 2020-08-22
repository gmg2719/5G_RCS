package com.android.messaging.ui.appsettings;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.android.messaging.R;
import com.android.messaging.datamodel.DataModel;
import com.android.messaging.datamodel.DatabaseHelper;
import com.android.messaging.datamodel.DatabaseWrapper;
import com.android.messaging.datamodel.MessagingContentProvider;
import com.android.messaging.datamodel.data.BusinessCardService;
import com.android.messaging.ui.BugleActionBarActivity;
import com.android.messaging.ui.UIIntents;
import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.MoveType;
import com.yhao.floatwindow.Screen;

import java.util.ArrayList;
import java.util.List;

public class H5WhiteListSettingsActivity extends BugleActionBarActivity {
    private static final int TAB_LENGTH_MAX = 50;
    private H5WLListView myListView;

    //private H5CNAdapter adapter;
    private SimpleCursorAdapter mAdapter;
    private Cursor mCursor;

//    private H5WLDatabaseHelper mdb_helper;
//    private SQLiteDatabase mdb;
    private DatabaseWrapper mdbWrapper;
    private Handler mHandler;
    private MyReceiver mReceiver;


    private List<String> contentList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.h5wllistview_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final boolean topLevel = getIntent().getBooleanExtra(
                UIIntents.UI_INTENT_EXTRA_TOP_LEVEL_SETTINGS, false);
        /*if (topLevel)*/ {
            getSupportActionBar().setTitle(/*getString(R.string.settings_activity_title)*/"H5直显白名单");
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction("com.android.messaging.RefreshH5Settings");

        mReceiver = new MyReceiver();
        this.registerReceiver(mReceiver, filter);

        Intent intent = new Intent(this, BusinessCardService.class);
        intent.putExtra(DatabaseHelper.H5CodeNumberColumns.H5_DISPLAY_CODENUMBER, "+8615958120627");
        startService(intent);

        //initList();
        myListView = (H5WLListView) findViewById(R.id.my_list_view);
        myListView.setOnDeleteListener(new H5WLListView.OnDeleteListener() {
            @Override
            public void onDelete(int index) {
                //contentList.remove(index);
                //adapter.notifyDataSetChanged();
                deleteItem(index);
            }
        });
        //adapter = new H5CNAdapter(this, 0, contentList);
        //myListView.setAdapter(adapter);
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                mAdapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.h5wl_listview_item, mCursor,
                        new String[]{"codenumber"}, new int[]{R.id.text_view}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
                myListView.setAdapter(mAdapter);
            }
        };

//        mdb_helper = new H5WLDatabaseHelper(this, "h5wldb", null, 1);
//        mdb = mdb_helper.getWritableDatabase();
//        mCursor = mdb.query(H5WLDatabaseHelper.H5WL_TABLENAME, null, null, null, null, null, null);
        new Thread(new Runnable(){
            @Override
            public void run() {
                mdbWrapper = DataModel.get().getDatabase();
                mCursor = mdbWrapper.query(DatabaseHelper.H5WHITELIST_TABLE, null, null, null, null, null, null);
                Message msg = new Message();
                msg.arg1 = 1;
                mHandler.sendMessage(msg);
            }
        }).start();
//                mAdapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.h5wl_listview_item, mCursor,
//                        new String[]{"codenumber"}, new int[]{R.id.text_view}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
//                myListView.setAdapter(mAdapter);
//        mdbWrapper = DataModel.get().getDatabase();
//        mdbWrapper.query(H5WLDatabaseHelper.H5WL_TABLENAME, null, null, null, null, null, null);
//        mAdapter = new SimpleCursorAdapter(this, R.layout.h5wl_listview_item, mCursor,
//                new String[]{"codenumber"}, new int[]{R.id.text_view}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
//        myListView.setAdapter(mAdapter);
    }
    class MyReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshListView();
        }
    }

    private void startFloatingWindow(){
        WebView wv = new WebView(this);
//        wv.loadUrl("http://xhpfmapi.zhongguowangshi.com/vh512/scene/6374845");
//        wv.loadUrl("https://img-my.csdn.net/uploads/201508/05/1438760758_3497.jpg");
//        wv.loadUrl("http://meishi.meituan.com/i/poi/175374674");
//        wv.loadUrl("https://www.baidu.com");
        wv.loadUrl("https://detail.m.tmall.com/item.htm?spm=a222t.7711473.5683327597.1&pos=1&acm=05901.1003.1.1523603&id=37417616936&scm=1007.12144.81309.17665_0_0");

        WebSettings ws = wv.getSettings();
        ws.setRenderPriority(WebSettings.RenderPriority.HIGH);
        ws.setJavaScriptEnabled(true);
        ws.setAppCacheEnabled(true);
        ws.setGeolocationEnabled(true);
        ws.setDomStorageEnabled(true);
        ws.setJavaScriptCanOpenWindowsAutomatically(true);
        String cacheDirPath = wv.getContext().getFilesDir().getAbsolutePath()+"cache/";
        ws.setDatabasePath(cacheDirPath);
        ws.setDatabaseEnabled(true);
        //mMessageWebView.addJavascriptInterface();
        ws.setUseWideViewPort(true);
        ws.setLoadWithOverviewMode(true);
        //ws.setBuiltInZoomControls(true);
        ws.setSupportZoom(true);
        //ws.setNeedInitialFocus(false);
        //ws.setCacheMode();
        ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        if(FloatWindow.get() == null) {
            FloatWindow
                    .with(getApplicationContext())
                    .setView(wv)
                    .setWidth(Screen.width, 1.0f)                               //设置控件宽高
                    .setHeight(Screen.height, 0.6f)
                    .setX(Screen.width, 0.0f)                                   //设置控件初始位置
                    .setY(Screen.height, 0.2f)
                    .setDesktopShow(true)                        //桌面显示
                    .setViewStateListener(/*mViewStateListener*/null)    //监听悬浮控件状态改变
                    .setPermissionListener(/*mPermissionListener*/null)  //监听权限申请结果
                    .setMoveType(MoveType.inactive)
                    .build();
        }
        FloatWindow.get().show();
    }


    /*@Override
    public void onContentChanged() {
        mCursor = mdb.query("H5_Whitelist", null, null, null, null, null, null);
        mAdapter.changeCursor(mCursor);
    }*/

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                mdbWrapper = DataModel.get().getDatabase();
//                mCursor = mdbWrapper.query(DatabaseHelper.H5WHITELIST_TABLE, null, null, null, null, null, null);
//                Message msg = new Message();
//                msg.arg1 = 1;
//                mHandler.sendMessage(msg);
//            }
//        }).start();
//    }

    private void refreshListView(){
//        mCursor = mdb.query(H5WLDatabaseHelper.H5WL_TABLENAME, null, null,
//                null, null, null, null);
        mCursor = mdbWrapper.query(DatabaseHelper.H5WHITELIST_TABLE, null, null, null, null, null, null);
        mAdapter.changeCursor(mCursor);
        MessagingContentProvider.notifyConversationListChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (super.onCreateOptionsMenu(menu)) {
            return true;
        }
        getMenuInflater().inflate(R.menu.add_h5_white_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.add_h5_wl:
                buildEditDialog();
//                startFloatingWindow();
                return true;
            case android.R.id.home:
                onBackPressed();
                //MessagingContentProvider.notifyConversationListChanged();
                return true;
            default:
               break;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void addItem(String addNumber){
        ContentValues cv = new ContentValues();
        String tempNumber = addNumber.replaceAll(" ", "");
        String addtodbnumber = null;
        ContentValues cv1 = new ContentValues();

        if(tempNumber.length() <= 6 || tempNumber.startsWith("106") || tempNumber.startsWith("+86") ){
            addtodbnumber = tempNumber;
            cv.put(DatabaseHelper.H5CodeNumberColumns.H5_DISPLAY_CODENUMBER, addtodbnumber);
        }else if(tempNumber.startsWith("86")){
            addtodbnumber = "+"+tempNumber;
            cv.put(DatabaseHelper.H5CodeNumberColumns.H5_DISPLAY_CODENUMBER, addtodbnumber);
        }else{
            addtodbnumber = "+86"+tempNumber;
            cv.put(DatabaseHelper.H5CodeNumberColumns.H5_DISPLAY_CODENUMBER, addtodbnumber);
        }
        cv1.put(DatabaseHelper.ConversationColumns.IS_H5_MESSAGE, 1);
        //cv.put("codenumber", addNumber);
//        mdb.insert(H5WLDatabaseHelper.H5WL_TABLENAME, null, cv);
        mdbWrapper.insert(DatabaseHelper.H5WHITELIST_TABLE, null, cv);

        mdbWrapper.update(DatabaseHelper.CONVERSATIONS_TABLE, cv1,
                DatabaseHelper.ConversationColumns.OTHER_PARTICIPANT_NORMALIZED_DESTINATION
                        + " = ?", new String[]{addtodbnumber});

//        mdbWrapper.update(DatabaseHelper.CONVERSATIONS_TABLE, cv1,
//                DatabaseHelper.ConversationColumns.OTHER_PARTICIPANT_NORMALIZED_DESTINATION
//                + " = " + addtodbnumber, null);
//        mdbWrapper.execSQL("UPDATE " + DatabaseHelper.CONVERSATIONS_TABLE + " SET "
//                            + DatabaseHelper.ConversationColumns.IS_H5_MESSAGE + " = 1 "
//                            + "WHERE " + DatabaseHelper.ConversationColumns.OTHER_PARTICIPANT_NORMALIZED_DESTINATION
//                            + " = " + addtodbnumber);
//        mdbWrapper.execSQL("UPDATE " + ConversationListItemData.getConversationListView() + " SET "
//                + DatabaseHelper.ConversationColumns.IS_H5_MESSAGE + " = 1 "
//                + "WHERE " + DatabaseHelper.ConversationColumns.OTHER_PARTICIPANT_NORMALIZED_DESTINATION
//                + " = " + addtodbnumber);
        refreshListView();
    }

    public void deleteItem(int positon) {
        Cursor mCursor = mAdapter.getCursor();
        mCursor.moveToPosition(positon);
        int itemId = mCursor.getInt(mCursor.getColumnIndex("_id"));
        String deleteFromDB = mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.H5CodeNumberColumns.H5_DISPLAY_CODENUMBER));
//        mdb.delete(H5WLDatabaseHelper.H5WL_TABLENAME, "_id=?", new String[]{itemId + ""});
        mdbWrapper.delete(DatabaseHelper.H5WHITELIST_TABLE, "_id=?", new String[]{itemId + ""});
        if(deleteFromDB != null){
            ContentValues cv = new ContentValues();
            cv.put(DatabaseHelper.ConversationColumns.IS_H5_MESSAGE, 0);
            mdbWrapper.update(DatabaseHelper.CONVERSATIONS_TABLE, cv,
                    DatabaseHelper.ConversationColumns.OTHER_PARTICIPANT_NORMALIZED_DESTINATION
                            + " = ?", new String[]{deleteFromDB});
//            mdbWrapper.execSQL("UPDATE " + DatabaseHelper.CONVERSATIONS_TABLE + " SET "
//                    + DatabaseHelper.ConversationColumns.IS_H5_MESSAGE + " = 0 "
//                    + "WHERE " + DatabaseHelper.ConversationColumns.OTHER_PARTICIPANT_NORMALIZED_DESTINATION
//                    + " = " + deleteFromDB);
//            mdbWrapper.execSQL("UPDATE " + ConversationListItemData.getConversationListView() + " SET "
//                    + DatabaseHelper.ConversationColumns.IS_H5_MESSAGE + " = 0 "
//                    + "WHERE " + DatabaseHelper.ConversationColumns.OTHER_PARTICIPANT_NORMALIZED_DESTINATION
//                    + " = " + deleteFromDB);
        }
        refreshListView();
    }

    private void buildEditDialog(){
        final EditText text = new EditText(this);
        new android.app.AlertDialog.Builder(this).setTitle(/*R.string.new_tab*/"您要添加的号码是：")
                .setView(text)
                .setPositiveButton(/*R.string.ok*/"确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String string = text.getText().toString();
                        int size = string.length();
                        if(size == 0){
                            buildEditDialog();
                            Toast.makeText(myListView.getContext(), "您的输入为空，请重新输入！", Toast.LENGTH_LONG).show();
                        }else if(size > TAB_LENGTH_MAX){
                            buildEditDialog();
                            //Toast.makeText(this, getResources().getString(R.string.check_length), 500).show();
                            Toast.makeText(myListView.getContext(), "您输入的长度超过了50个字符，请检查", Toast.LENGTH_LONG).show();
                        }else{
                            addItem(string);
                        }

                    }
                })
                .setNegativeButton(/*R.string.cancel*/"取消", null)
                .show();
    }

    @Override
    protected void onStop() {
//        mCursor.close();
//        mdb_helper.close();
//        mdb.close();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mCursor.close();
        if(FloatWindow.get() != null) {
            FloatWindow.get().hide();
            FloatWindow.destroy();;
        }
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }
}
