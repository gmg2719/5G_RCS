package com.android.messaging.ui.conversation.chatbot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.messaging.R;
import com.android.messaging.datamodel.BugleDatabaseOperations;
import com.android.messaging.ui.UIIntentsImpl;
import com.android.messaging.util.LogUtil;

public class ChatbotIntroduceActivity extends AppCompatActivity implements View.OnClickListener{
    private static String CHATBOT_NUMBER = "chatbot_number";
    private ImageView iv_back;
    private TextView tv_chatbotNumber;
    private ImageView iv_goto;
    private String chatbotNumber;
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
        initView();
    }

    private void initView(){
        iv_back = (ImageView)findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);

        tv_chatbotNumber = (TextView)findViewById(R.id.chatbot_number);
        chatbotNumber = getIntent().getStringExtra(CHATBOT_NUMBER);
        String temp = chatbotNumber.substring(4);
        int i = temp.indexOf("@");
        tv_chatbotNumber.setText(temp.substring(0, i));

        iv_goto = (ImageView)findViewById(R.id.goto_icon);
        iv_goto.setOnClickListener(this);

    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String conversationId = (String)msg.obj;
            LogUtil.i("Junwang", "conversationId="+conversationId);
            Intent intent = UIIntentsImpl.getConversationActivityIntent(ChatbotIntroduceActivity.this, conversationId, null, false);
            startActivity(intent);
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
//                UIIntents.get().getIntentForConversationActivity(this, null, null);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String conversationId = BugleDatabaseOperations.getConversationId(chatbotNumber);
                        Message msg = new Message();
                        msg.obj = conversationId;
                        mHandler.sendMessage(msg);
                    }
                }).start();
                break;
            default:
                break;
        }
    }

    private void setStatusBarTransparent(Activity activity){
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        activity.getWindow()
                .getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

    }

    public static void start(Context context, String chatbotNumber) {
        Intent intent = new Intent(context, ChatbotIntroduceActivity.class);
        intent.putExtra(CHATBOT_NUMBER, chatbotNumber);
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
