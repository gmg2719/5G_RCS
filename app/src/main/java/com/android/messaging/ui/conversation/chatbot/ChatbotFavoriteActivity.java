package com.android.messaging.ui.conversation.chatbot;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
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
import android.view.WindowManager;
import android.widget.ImageView;

import com.android.messaging.R;
import com.android.messaging.datamodel.ChatbotFavoriteTableUtils;
import com.android.messaging.ui.BugleActionBarActivity;
import com.android.messaging.ui.ConversationDrawables;
import com.android.messaging.util.LogUtil;
import com.yanzhenjie.recyclerview.OnItemClickListener;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.HashMap;
import java.util.List;

import static com.android.messaging.datamodel.MessagingContentProvider.CHATBOT_FAVORITE_URI;

public class ChatbotFavoriteActivity extends BugleActionBarActivity{
    private SwipeRecyclerView mRecyclerView;
    protected FavoriteCardItemViewAdapter mAdapter;
    protected List<ChatbotFavoriteEntity> mDataList;
    protected RecyclerView.LayoutManager mLayoutManager;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged(mDataList);
        }
    };

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
        mRecyclerView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int adapterPosition) {
                LogUtil.i("Junwang", "ChatbotFavoriteActivity adapterPosition "+ adapterPosition+" clicked.");
                ChatbotFavoriteDetailsActivity.start(ChatbotFavoriteActivity.this, null, mDataList.get(adapterPosition).getChatbot_fav_msg_id());
            }
        });

        queryChatbotFavorite();
        getContentResolver().registerContentObserver(CHATBOT_FAVORITE_URI, true, mContentObserver);
        if(mDataList == null || mDataList.size() == 0){
            LogUtil.i("Junwang", "ChatbotFavoriteActivity mDataList == null");
        }
        mAdapter = new FavoriteCardItemViewAdapter(this);
        mLayoutManager = createLayoutManager();

        mRecyclerView.setLayoutManager(mLayoutManager);
        setSpaceItem();
//        final int actionBarColor = ConversationDrawables.get().getActionbarColor();
//        actionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));

//        UiUtils.setStatusBarColor(this, actionBarColor);
    }

    private void queryChatbotFavorite(){
        new Thread(new Runnable(){
            @Override
            public void run() {
                mDataList = ChatbotFavoriteTableUtils.queryChatbotFavorite();
                Message msg = new Message();
                msg.arg1 = 1;
                mHandler.sendMessage(msg);
            }
        }).start();
    }

    private ContentObserver mContentObserver = new ContentObserver(mHandler) {

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            LogUtil.i("Junwang", "contentObserver onChange");
            queryChatbotFavorite();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        invalidateActionBar();
    }

    private void updateActionAndStatusBarColor(final ActionBar actionBar) {
        final int actionBarColor = ConversationDrawables.get().getActionbarColor();
        actionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));

//        UiUtils.setStatusBarColor(this, actionBarColor);
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
            LogUtil.i("Junwang", "ChatbotFavoriteActivity OnItemMenuClickListener position "+ position+" clicked.");
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
