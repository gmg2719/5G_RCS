package com.android.messaging.ui.conversation.chatbot;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.messaging.R;
import com.android.messaging.datamodel.BugleDatabaseOperations;
import com.android.messaging.datamodel.ChatbotFavoriteTableUtils;
import com.android.messaging.datamodel.ChatbotInfoTableUtils;
import com.android.messaging.datamodel.microfountain.sms.database.ChatbotInfoQueryHelper;
import com.android.messaging.ui.BugleActionBarActivity;
import com.android.messaging.ui.UIIntentsImpl;
import com.android.messaging.ui.conversation.santilayout.ExpandLayout;
import com.android.messaging.util.LogUtil;
import com.microfountain.rcs.aidl.broadcast.RcsChatbotBroadcast;
import com.microfountain.rcs.support.model.chatbot.Chatbot;
import com.microfountain.rcs.support.model.chatbot.ChatbotDiscoveryResult;
import com.microfountain.rcs.support.model.chatbot.ChatbotDiscoveryResultParser;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class ChatbotSearchActivity extends BugleActionBarActivity implements AdapterView.OnItemClickListener {
    ChatbotSearcherReceiver mSearcherReceiver;
    private ListView mChatbotListView;
    private SearchView mSearchView;
    private ChatbotListAdapter mAdapter;
    private ArrayList<Chatbot> mChatbotList;
    private TextView mCancelTV;
    private ChatbotEntity botEntity;
    private static List<ChatbotFavoriteEntity> mHistoryDataList;
    private boolean mIsFromHistory;

    private ExpandLayout history_expand_layout;
    private TagFlowLayout history_flowlayout;
    private TagAdapter<ChatbotFavoriteEntity> mHistoryAdapter;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1){
                case 1:
                    if(mChatbotList != null) {
                        mAdapter.setChatbotListItems(mChatbotList);
                    }else{
                        Toast.makeText(getApplicationContext(), "没有找到服务号", Toast.LENGTH_LONG).show();
                    }
                    mHistoryAdapter.notifyDataChanged();
                    break;
                case 2:
                    String conversationId = (String)msg.obj;
                    LogUtil.i("Junwang", "conversationId="+conversationId);
                    if(conversationId != null) {
                        Intent intent = UIIntentsImpl.getConversationActivityWithH5MsgInfoIntent(ChatbotSearchActivity.this, conversationId, null, false,
                                0, null, null, null, null, 0,
                                null, null, null, null, botEntity != null ? botEntity.getMenu() : null);
                        startActivity(intent);
                    }else{
                        String address = mChatbotList.get(msg.arg2).getChatbotSipUri();
                        Uri uri = Uri.parse("smsto:"+address);
                        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                        intent.putExtra("sms_body", "");
                        startActivity(intent);
                    }
                    finish();
                    break;
                default:
                    break;
            }

        }
    };



    public static final void start(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, ChatbotSearchActivity.class);
        context.startActivity(intent);
    }

    private void queryChatbotSearchHistory(){
        new Thread(new Runnable(){
            @Override
            public void run() {
                mHistoryDataList = ChatbotFavoriteTableUtils.queryChatbotSearchHistory();
                if(mHistoryDataList != null && mHistoryDataList.size() > 0){
                    for(int i=0; i<mHistoryDataList.size(); i++){
                        Message msg = new Message();
                        msg.arg1 = 1;
                        mHandler.sendMessage(msg);
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.search_chatbot_result);
        mChatbotListView = (ListView) findViewById(R.id.searchResultList);
//        lvContacts.setVisibility(View.GONE);
        mSearchView = (SearchView)findViewById(R.id.searchview);
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                LogUtil.i("Junwang", "onQueryTextSubmit query="+query);
                if(mChatbotList != null){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mChatbotList.clear();
                            ChatbotFavoriteEntity entity = new ChatbotFavoriteEntity();
                            entity.setChatbot_fav_name(query);
                            if(!mIsFromHistory){
                                if(mHistoryDataList.contains(entity)){
                                    LogUtil.i("Junwang", "mHistoryDataList contain entity.");
                                    mHistoryDataList.add(0, entity);
                                    for(int i=1; i<mHistoryDataList.size(); i++){
                                        if(mHistoryDataList.get(i).getChatbot_fav_name().equals(query)){
                                            mHistoryDataList.remove(i);
//                                            ChatbotFavoriteTableUtils.updateChatbotSearchHistoryTable(entity);
                                        }
                                    }
                                }else {
                                    mHistoryDataList.add(0, entity);
//                                    ChatbotFavoriteTableUtils.insertChatbotSearchHistoryTable(null, query, null, null, null, 0, null, null, null);
                                }
//                                mHistoryAdapter.notifyDataChanged();
                            }else{
//                                mHistoryAdapter.notifyDataChanged();
//                                ChatbotFavoriteTableUtils.updateChatbotSearchHistoryTable(entity);
                                mIsFromHistory = false;
                            }
                            ChatbotInfoQueryHelper.SearchChatbot(query);
                        }
                    }).start();
                    mHistoryAdapter.notifyDataChanged();
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                LogUtil.i("Junwang", "onQueryTextChange newText="+newText);
                if(mChatbotList != null){
                    mChatbotList.clear();
                }
                if(newText == null || newText.length() == 0){
//                    mAdapter.setChatbotListItems(null);
                    mChatbotListView.setVisibility(View.GONE);
                    mAdapter.clear();
                    mAdapter.notifyDataSetChanged();
                }else {
                    mChatbotListView.setVisibility(View.VISIBLE);
                    ChatbotInfoQueryHelper.SearchChatbot(newText);
                }
                return true;
            }
        });
        mChatbotList = new ArrayList();
        history_expand_layout = (ExpandLayout)findViewById(R.id.expand_history);
        history_flowlayout = (TagFlowLayout) findViewById(R.id.history_flow_layout);
        history_flowlayout.setMaxSelectCount(1);
//        queryChatbotSearchHistory();
        mHistoryDataList = new ArrayList<ChatbotFavoriteEntity>();
        mHistoryAdapter = new TagAdapter<ChatbotFavoriteEntity>(mHistoryDataList) {
            @Override
            public View getView(FlowLayout parent, int position, ChatbotFavoriteEntity entity) {
                final LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
                TextView tv = (TextView) mInflater.inflate(R.layout.tv, history_flowlayout, false);
                tv.setText(entity.getChatbot_fav_name());
                return tv;
            }
        };
        history_flowlayout.setAdapter(mHistoryAdapter);
        history_flowlayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                if(mHistoryDataList != null) {
                    mHistoryDataList.add(0, mHistoryDataList.get(position));
                    mHistoryDataList.remove(position+1);
                    mIsFromHistory = true;
                    mSearchView.setQuery(mHistoryDataList.get(0).getChatbot_fav_name(), true);
                }
                return false;
            }
        });
        mCancelTV = (TextView)findViewById(R.id.search_cancel_button);
        mCancelTV.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mAdapter = new ChatbotListAdapter(getApplicationContext());

        mChatbotListView.setAdapter(mAdapter);
//        mChatbotListView.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                showKeyboard(false);
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//            }
//        });
//        findViewById(R.id.global_search_root).setOnTouchListener(new View.OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                    finish();
//                    return true;
//                }
//                return false;
//            }
//        });

        mSearcherReceiver = new ChatbotSearcherReceiver();
        IntentFilter filter = new IntentFilter(ChatbotInfoQueryHelper.INTENT_ACTION_DISCOVERY_CHATBOT_LIST);
        registerReceiver(mSearcherReceiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (mSearchView != null) {
//            mSearchView.clearFocus();
//        }
        setStatusBar();
    }


    protected boolean useThemestatusBarColor = true;//是否使用特殊的标题栏背景颜色，android5.0以上可以设置状态栏背景色，如果不使用则使用透明色值
    protected boolean useStatusBarColor = true;//是否使用状态栏文字和图标为暗色，如果状态栏采用了白色系，则需要使状态栏和图标为暗色，android6.0以上可以设置

    protected void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0及以上
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            //根据上面设置是否对状态栏单独设置颜色
            if (useThemestatusBarColor) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.action_bar_background_color));
            } else {
                getWindow().setStatusBarColor(Color.TRANSPARENT);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4到5.0
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && useStatusBarColor) {//android6.0以后可以对状态栏文字颜色和图标进行修改
            getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }



    /**
     * 显示键盘
     *
     * @param isShow true:显示 false:不显示
     */
    protected void showKeyboard(boolean isShow) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (isShow) {
            if (getCurrentFocus() == null) {
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            } else {
                imm.showSoftInput(getCurrentFocus(), 0);
            }
        } else {
            if (getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    public class ChatbotSearcherReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            //获取搜索结果应答码
            int responseCode = intent.getIntExtra(RcsChatbotBroadcast.INTENT_EXTRA_HTTP_RESPONSE_CODE, 0);
            LogUtil.i("Junwang", "Search chatbot responseCode="+responseCode);
            if (responseCode >= HttpURLConnection.HTTP_OK && responseCode < HttpURLConnection.HTTP_MULT_CHOICE) {
                //搜索成功，获取列表数据
                String queryResultJson =
                        intent.getStringExtra(RcsChatbotBroadcast.INTENT_EXTRA_DISCOVERED_CHATBOT_LIST);
                if (queryResultJson != null) {
                    ChatbotDiscoveryResult discoveryResult =
                            ChatbotDiscoveryResultParser.parse(queryResultJson);
                    if (discoveryResult != null) {
                        //Chatbot列表
                        List<Chatbot> chatbots = discoveryResult.getChatbots(); //推荐Chatbot列表
                        for(int i=0; i<chatbots.size(); i++){
                            mChatbotList.add(chatbots.get(i));
                            LogUtil.i("Junwang", "onReceive chatbot name="+chatbots.get(i).getName()+", chatbotNumber="+chatbots.get(i).getChatbotSipUri());
                        }
                        List<Chatbot> recommendBots =
                                discoveryResult.getRecommendBots();
                        //TODO:根据搜索的目的选择不同的列表
                    }else{
                        LogUtil.i("Junwang", "can't find this chatbot.");
                    }
                }else{
                    LogUtil.i("Junwang", "can't find this chatbot2.");
                }
                Message message = new Message();
                message.arg1 = 1;
                mHandler.sendMessage(message);
            }
            else {
                LogUtil.i("Junwang", "Search chatbot error!");
                //获取附带的错误信息，如:GBA鉴权失败
                String extraData =
                        intent.getStringExtra(RcsChatbotBroadcast.INTENT_EXTRA_EXTRA_DATA_STRING);
                if(extraData != null && !extraData.isEmpty()) {
                    LogUtil.i("Junwang", "Search chatbot error reason is " + extraData);
                }
            }
        }
    }

    class ChatbotListAdapter extends ArrayAdapter<Chatbot>{
        public ChatbotListAdapter(@NonNull Context context) {
            super(context, R.layout.h5wl_listview_item);
        }

        public void setChatbotListItems(final List<Chatbot> newList) {
            clear();
            addAll(newList);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View itemView;
            if (convertView != null) {
                itemView = convertView;
            } else {
                final LayoutInflater inflater = (LayoutInflater) getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                itemView = inflater.inflate(
                        R.layout.h5wl_listview_item, parent, false);
            }
            final TextView textView = (TextView)itemView.findViewById(R.id.text_view);
            final Chatbot item = mChatbotList.get(position);
            textView.setText(item.getName());
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String conversationId = BugleDatabaseOperations.getConversationId(mChatbotList.get(position).getChatbotSipUri());
                            botEntity = ChatbotInfoTableUtils.queryChatbotInfoTable(mChatbotList.get(position).getChatbotSipUri());
                            Message msg = new Message();
                            if(conversationId != null){
                                msg.obj = conversationId;
                            }else{
                                msg.arg2 = position;//mChatbotList.get(position).getChatbotSipUri();
                            }
                            msg.arg1 = 2;
                            mHandler.sendMessage(msg);
                        }
                    }).start();
                }
            });
            return itemView;
        }
    }
}
