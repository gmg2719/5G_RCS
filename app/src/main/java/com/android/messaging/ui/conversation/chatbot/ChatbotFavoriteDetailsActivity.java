package com.android.messaging.ui.conversation.chatbot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.messaging.R;
import com.android.messaging.datamodel.data.ConversationMessageData;
import com.android.messaging.ui.BugleActionBarActivity;
import com.android.messaging.ui.ConversationDrawables;
import com.android.messaging.ui.conversation.ChatbotMsgParseUtils;
import com.android.messaging.util.LogUtil;

public class ChatbotFavoriteDetailsActivity extends BugleActionBarActivity {
    public static String FAV_MESSAGE_TEXT = "fav_message_text";
    public static String FAV_MESSAGE_ID = "fav_message_id";
    private String message_content;
    private String message_id;
    private ConversationMessageData cmd;
    private Handler mHandler;
//    private String mContentType;
//    private boolean mChatbotSubscribeStatus;
//    private boolean mChatbotVoteStatus;
//    private int mChatbotVotedItemPosition;
//    private String mChatbotRcsdbMsgId;
//    private boolean mChatbotCardInvalid;
//    private String mChatbotCardInvalidPrompt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite_card_details);
        message_content = getIntent().getStringExtra(FAV_MESSAGE_TEXT);

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                initView(cmd);
            }
        };
    }

    private void initView(ConversationMessageData cmd){
//        if(message_content.startsWith("{")){
//            if(message_content.indexOf("generalPurposeCardCarousel") != -1) {
//                LogUtil.i("Junwang", "multi card chatbot message");
//                if(ParseMultiCardChatbotMsg(cmd.getText())){
//                    return;
//                }
//            }else if(message_content.indexOf("generalPurposeCard") != -1){
//                LogUtil.i("Junwang", "single card chatbot message");
//                if(ParseSingleCardChatbotMsg(cmd.getText())){
//                    return;
//                }
//            }
//        }
        ChatbotMsgParseUtils.startParse(this, (LinearLayout) findViewById(R.id.card_details), cmd);
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateActionBar();
        message_id = getIntent().getStringExtra(FAV_MESSAGE_ID);
        if(message_id == null){
            ((LinearLayout)findViewById(R.id.card_details)).setVisibility(View.GONE);
            return;
        }
        new Thread(new Runnable(){
            @Override
            public void run() {
                Cursor cursor = ConversationMessageData.getMessage(message_id);
                if (cursor != null) {
                    if(cursor.moveToFirst() && cursor != null){
                        cmd = new ConversationMessageData();
                        cmd.bind(cursor);
                        Message msg = new Message();
                        msg.arg1 = 1;
                        mHandler.sendMessage(msg);
                        if(cursor != null){
                            cursor.close();
                        }
//                        mChatbotSubscribeStatus = (cursor.getInt(ConversationMessageData.INDEX_CHATBOT_SUBSCRIBE_STATUS) != 0);
//                        mChatbotVoteStatus = (cursor.getInt(ConversationMessageData.INDEX_CHATBOT_VOTE_STATUS) != 0);
//                        mChatbotVotedItemPosition = cursor.getInt(ConversationMessageData.INDEX_CHATBOT_VOTED_ITEM_POSITION);
//                        mChatbotRcsdbMsgId = cursor.getString(ConversationMessageData.INDEX_CHATBOT_RCSDB_MSGID);
//                        mChatbotCardInvalid = (cursor.getInt(ConversationMessageData.INDEX_CHATBOT_CARD_INVALID) != 0);
//                        mChatbotCardInvalidPrompt = cursor.getString(ConversationMessageData.INDEX_CHATBOT_CARD_INVALID_PROMPT);
                    }
                }
            }
        }).start();
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
        ((TextView)customView.findViewById(R.id.actionbar_title)).setText("详情");
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

    public static void start(Context context, String message_text, String messageId) {
        Intent intent = new Intent(context, ChatbotFavoriteDetailsActivity.class);
        intent.putExtra(FAV_MESSAGE_ID, messageId);
        intent.putExtra(FAV_MESSAGE_TEXT, message_text);
        context.startActivity(intent);
    }

    public static void refresh(Activity activity){
        Intent intent=new Intent(activity, ChatbotFavoriteDetailsActivity.class);
        String messageId = null;
        intent.putExtra(FAV_MESSAGE_ID, messageId);
        activity.startActivity(intent);
        activity.finish();//关闭自己
        activity.overridePendingTransition(0, 0);
    }
}
