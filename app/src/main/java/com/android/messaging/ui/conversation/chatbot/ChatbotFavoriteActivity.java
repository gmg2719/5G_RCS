package com.android.messaging.ui.conversation.chatbot;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.messaging.R;
import com.android.messaging.datamodel.ChatbotFavoriteTableUtils;
import com.android.messaging.ui.BugleActionBarActivity;
import com.android.messaging.ui.ConversationDrawables;
import com.android.messaging.util.LogUtil;
import com.android.messaging.util.UiUtils;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.HashMap;
import java.util.List;

public class ChatbotFavoriteActivity extends BugleActionBarActivity {
    private SwipeRecyclerView mRecyclerView;
    protected FavoriteCardItemViewAdapter mAdapter;
    protected List<ChatbotFavoriteEntity> mDataList;
    protected RecyclerView.LayoutManager mLayoutManager;
    private Handler mHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.i("Junwang", "ChatbotFavoriteActivity onCreate");
        setContentView(R.layout.activity_favorite_card);
//        ImmersionBar.with(this)
//                .titleBar(R.id.toolbar, false)
//                .transparentBar()
//                .init();
//        StatusBarUtil.setStatusBarColor(this, R.color.color_BDBDBD);
        mRecyclerView = findViewById(R.id.fav_recycler_view);

        mRecyclerView.setSwipeMenuCreator(swipeMenuCreator);
        mRecyclerView.setOnItemMenuClickListener(mMenuItemClickListener);

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged(mDataList);
            }
        };

        new Thread(new Runnable(){
            @Override
            public void run() {
                mDataList = ChatbotFavoriteTableUtils.queryChatbotFavorite();
//                for(int i=0; i<mDataList.size(); i++){
//                    LogUtil.i("Junwang", "favority activity "+mDataList.get(i).getChatbot_fav_card_description()+" "+mDataList.get(i).getChatbot_fav_msg_id());
//                }
                Message msg = new Message();
                msg.arg1 = 1;
                mHandler.sendMessage(msg);
            }
        }).start();


        if(mDataList == null || mDataList.size() == 0){
            LogUtil.i("Junwang", "ChatbotFavoriteActivity mDataList == null");
        }
        mAdapter = new FavoriteCardItemViewAdapter(this);
        mLayoutManager = createLayoutManager();

        mRecyclerView.setLayoutManager(mLayoutManager);
        setSpaceItem();
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateActionBar();
    }

    private void updateActionAndStatusBarColor(final ActionBar actionBar) {
        final int actionBarColor = ConversationDrawables.get().getActionbarColor();
        actionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));

        UiUtils.setStatusBarColor(this, actionBarColor);
    }

    @Override
    protected void updateActionBar(ActionBar actionBar) {
        LogUtil.i("Junwang", "favority update Actionbar");
//        super.updateActionBar(actionBar);
        updateActionAndStatusBarColor(actionBar);
        // We update this regardless of whether or not the action bar is showing so that we
        // don't get a race when it reappears.
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        View customView = ((LayoutInflater)
                this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.favorite_actionbar, null);
        ActionBar.LayoutParams lp =new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        actionBar.setCustomView(customView, lp);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setHomeAsUpIndicator(null);
        final ImageView back_icon = (ImageView)customView.findViewById(R.id.actionbar_arrow);
        back_icon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    protected RecyclerView.LayoutManager createLayoutManager() {
        return new LinearLayoutManager(this);
    }

    protected void setSpaceItem(){
        HashMap<String, Integer> stringIntegerHashMap = new HashMap<>();
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.TOP_DECORATION,30);//top间距

//        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.BOTTOM_DECORATION,100);//底部间距
//
//        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.LEFT_DECORATION,60);//左间距
//
//        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.RIGHT_DECORATION,100);//右间距

        mRecyclerView.addItemDecoration(new RecyclerViewSpacesItemDecoration(stringIntegerHashMap));


    }

    public static void start(Context context) {
        Intent intent = new Intent(context, ChatbotFavoriteActivity.class);
        context.startActivity(intent);
    }

    /**
     * 菜单创建器，在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int position) {
            int width = getResources().getDimensionPixelSize(R.dimen.delete_icon);

            // 1. MATCH_PARENT 自适应高度，保持和Item一样高;
            // 2. 指定具体的高，比如80;
            // 3. WRAP_CONTENT，自身高度，不推荐;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            // 添加左侧的，如果不添加，则左侧不会出现菜单。
//            {
//                SwipeMenuItem addItem = new SwipeMenuItem(ChatbotFavoriteActivity.this).setBackground(R.drawable.selector_green)
//                        .setImage(R.drawable.ic_action_add)
//                        .setWidth(width)
//                        .setHeight(height);
//                swipeLeftMenu.addMenuItem(addItem); // 添加菜单到左侧。
//
//                SwipeMenuItem closeItem = new SwipeMenuItem(ChatbotFavoriteActivity.this).setBackground(R.drawable.selector_red)
//                        .setImage(R.drawable.ic_action_close)
//                        .setWidth(width)
//                        .setHeight(height);
//                swipeLeftMenu.addMenuItem(closeItem); // 添加菜单到左侧。
//            }

            // 添加右侧的，如果不添加，则右侧不会出现菜单。
            {
                SwipeMenuItem deleteItem = new SwipeMenuItem(ChatbotFavoriteActivity.this)
                        .setImage(R.drawable.icon_delete)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(deleteItem);// 添加菜单到右侧。

//                SwipeMenuItem addItem = new SwipeMenuItem(ChatbotFavoriteActivity.this).setBackground(R.drawable.selector_green)
//                        .setText("添加")
//                        .setTextColor(Color.WHITE)
//                        .setWidth(width)
//                        .setHeight(height);
//                swipeRightMenu.addMenuItem(addItem); // 添加菜单到右侧。
            }
        }
    };

    /**
     * RecyclerView的Item的Menu点击监听。
     */
    private OnItemMenuClickListener mMenuItemClickListener = new OnItemMenuClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge, int position) {
            menuBridge.closeMenu();

            int direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
            int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。

            if (direction == SwipeRecyclerView.RIGHT_DIRECTION) {
//                Toast.makeText(ChatbotFavoriteActivity.this, "list第" + position + "; 右侧菜单第" + menuPosition, Toast.LENGTH_SHORT)
//                        .show();
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        ChatbotFavoriteTableUtils.deleteChatbotFavoriteInfo(null, mDataList.get(position).getChatbot_fav_msg_id());
                        mDataList.remove(position);
                        Message msg = new Message();
                        msg.arg1 = 2;
                        mHandler.sendMessage(msg);
                    }
                }).start();

            } else if (direction == SwipeRecyclerView.LEFT_DIRECTION) {
//                Toast.makeText(ChatbotFavoriteActivity.this, "list第" + position + "; 左侧菜单第" + menuPosition, Toast.LENGTH_SHORT)
//                        .show();
            }
        }
    };

}
