package com.android.messaging.ui.conversation.chatbot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.messaging.R;
import com.android.messaging.datamodel.BugleDatabaseOperations;
import com.android.messaging.datamodel.ChatbotInfoTableUtils;
import com.android.messaging.datamodel.media.FileImageRequestDescriptor;
import com.android.messaging.ui.ContactIconView;
import com.android.messaging.ui.UIIntentsImpl;
import com.android.messaging.ui.conversation.chatbot.chatbotconfig.ChatbotConfig;
import com.android.messaging.util.LogUtil;
import com.bumptech.glide.Glide;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ChatbotIntroduceActivity extends AppCompatActivity implements View.OnClickListener{
    private static String CHATBOT_NUMBER = "chatbot_number";
    private static String CHATBOT_BACKGROUND_URL = "chatbot_background_url";
    private static String CHATBOT_NAME = "chatbot_name";
    private ImageView iv_back;
    private TextView tv_chatbotNumber;
    private TextView tv_introduce;
    private ImageView iv_goto;
    private String chatbotNumber;
    private String chatbotName;
    private TextView readConversation;
    private FrameLayout introduceLayout;
    private LinearLayout logoLayout;
    private LinearLayout contentLayout;
    private RelativeLayout optionLayout;
    private ImageView layoutBackground;
    private String backgroundUrl;
    private ChatbotEntity botEntity;
    private TextView tv_chatbotName;
//    private ImageView chatbot_logo;
    private ContactIconView chatbot_logo;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatbot_introduce_activity);

//        ImmersionBar.with(this)
//            .statusBarDarkFont(true)
//            .navigationBarDarkIcon(true)
//            .transparentBar()
//            .init();
//        ImmersionBar.with(this).statusBarDarkFont(true).init();
//        setStatusBar();
        setStatusBarTransparent(this);
        chatbotNumber = getIntent().getStringExtra(CHATBOT_NUMBER);
        queryChatbotInfo();
    }

    private void queryChatbotInfo(){
        new Thread(new Runnable(){
            @Override
            public void run() {
                botEntity = ChatbotInfoTableUtils.queryChatbotInfoTable(chatbotNumber);
                Message msg = new Message();
                msg.arg1 = 1;
                mHandler.sendMessage(msg);
            }
        }).start();
    }

    private void initView(){
        iv_back = (ImageView)findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);

        tv_chatbotNumber = (TextView)findViewById(R.id.chatbot_number);
        String temp = chatbotNumber.substring(4);
        int i = temp.indexOf("@");
        tv_chatbotNumber.setText(temp.substring(0, i));

        tv_introduce = (TextView)findViewById(R.id.tv_introduce);
        if(botEntity != null) {
            String botInfoJson = botEntity.getJson();
            LogUtil.i("Junwang", "chatbot info json=" + botInfoJson);
            if (botInfoJson != null) {
                ChatbotConfig cbf = JSON.parseObject(botInfoJson, ChatbotConfig.class);
                String description = cbf.getBotinfo().getPcc().getOrg_details().getOrgDescription();
                if (description != null) {
                    tv_introduce.setText(description);
                }
//                chatbot_logo = (ImageView)findViewById(R.id.busn_logo);
//                chatbot_logo.setImageURI(Uri.parse(botEntity.getSms()));
                LogUtil.i("Junwang", "botEntity.getSms()="+botEntity.getSms());
                chatbot_logo = (ContactIconView) findViewById(R.id.busn_logo);
                final Resources resources = this.getResources();
                int mIconSize = (int) resources.getDimension(
                        R.dimen.contact_icon_view_large_size);
                chatbot_logo.setImageResourceId(new FileImageRequestDescriptor(botEntity.getSms(), mIconSize, mIconSize, true));
            }
            tv_chatbotName = (TextView)findViewById(R.id.chatbot_name);
            tv_chatbotName.setText(botEntity.getName());
        }

        backgroundUrl = getIntent().getStringExtra(CHATBOT_BACKGROUND_URL);

        iv_goto = (ImageView)findViewById(R.id.goto_icon);
        iv_goto.setOnClickListener(this);

        readConversation = (TextView)findViewById(R.id.read_conversation);
        readConversation.setOnClickListener(this);

        if(backgroundUrl != null) {
            introduceLayout = (FrameLayout) findViewById(R.id.introduce_layout);
            logoLayout = (LinearLayout) findViewById(R.id.logo_layout);
            contentLayout = (LinearLayout) findViewById(R.id.content_layout);
            optionLayout = (RelativeLayout) findViewById(R.id.option_layout);
            introduceLayout.setBackground(null);
            logoLayout.setBackground(null);
            contentLayout.setBackground(null);
            optionLayout.setBackground(null);
            layoutBackground = (ImageView)findViewById(R.id.layout_background);
//            layoutBackground.setMinimumHeight(getWindow().getDecorView().getHeight());

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//            setStatusBarTransparent(this);

            Glide.with(this)
                    .load(backgroundUrl)
                    .into(layoutBackground);
//            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    static void setMIUIBarDark(Window window, String key, boolean dark) {
        if (window != null) {
            Class<? extends Window> clazz = window.getClass();
            try {
                int darkModeFlag;
                Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField(key);
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (dark) {
                    //状态栏透明且黑色字体
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);
                } else {
                    //清除黑色字体
                    extraFlagField.invoke(window, 0, darkModeFlag);
                }
            } catch (Exception ignored) {

            }
        }
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1){
                case 1:
                    initView();
                    break;
                case 2:
                    String conversationId = (String)msg.obj;
                    LogUtil.i("Junwang", "conversationId="+conversationId);
                    Intent intent = UIIntentsImpl.getConversationActivityIntent(ChatbotIntroduceActivity.this, conversationId, null, false);
                    startActivity(intent);
                    break;
                default:
                    break;
            }

        }
    };

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.iv_back:
                finish();
                break;
            case R.id.goto_icon:
            case R.id.read_conversation:
//                UIIntents.get().getIntentForConversationActivity(this, null, null);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String conversationId = BugleDatabaseOperations.getConversationId(chatbotNumber);
                        Message msg = new Message();
                        msg.obj = conversationId;
                        msg.arg1 = 2;
                        mHandler.sendMessage(msg);
                    }
                }).start();
                break;
            default:
                break;
        }
    }

    private void setStatusBarTransparent(Activity activity){
//        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
//        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        activity.getWindow()
                .getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

    }

    public static void start(Context context, String chatbotNumber, String backgroundUrl) {
        Intent intent = new Intent(context, ChatbotIntroduceActivity.class);
        intent.putExtra(CHATBOT_NUMBER, chatbotNumber);
        intent.putExtra(CHATBOT_BACKGROUND_URL, backgroundUrl);
        context.startActivity(intent);
    }

    protected boolean useThemestatusBarColor = false;//是否使用特殊的标题栏背景颜色，android5.0以上可以设置状态栏背景色，如果不使用则使用透明色值
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
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
}
