package com.android.messaging.ui.conversation.chatbot;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.messaging.R;
import com.android.messaging.product.utils.StatusBarUtil;
import com.android.messaging.ui.conversation.SantiVideoView;
import com.android.messaging.util.LogUtil;
import com.android.messaging.videoplayer.ijk.IjkPlayer;
import com.android.messaging.videoplayer.listener.OnVideoViewStateChangeListener;
import com.android.messaging.videoplayer.player.DanmuVideoView;
import com.android.messaging.videoplayer.player.PlayerFactory;
import com.android.messaging.videoplayer.ui.StandardVideoController;
import com.bumptech.glide.Glide;
import com.gyf.immersionbar.ImmersionBar;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class ChatbotVideoNewsDetailsActivity extends AppCompatActivity implements View.OnTouchListener/*, VideoListener*/ {
    public static final String URL = "url";
    public static final String TITLE= "title";
    public static final String DETAILS = "details";
    private ImageView mIVBack;
    private TextView mTVTitle;
    private String mUrl;
    private String mTitle;
    SantiVideoView mVideoView;
    private TextView mTVDetails;
    private String mDetails;
    private ImageView mCloseImage;
    private ImageView mCoverImage;
    private ImageView mShareVideo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
//            window.setNavigationBarColor(Color.TRANSPARENT);
//        }
//        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Window window = getWindow();
//            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        }

//        if (Build.VERSION.SDK_INT >= 21) {
//            View decorView = getWindow().getDecorView();
//            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
//            decorView.setSystemUiVisibility(option);
//            getWindow().setStatusBarColor(Color.TRANSPARENT);
//        }
//        getSupportActionBar().hide();

//        final Window window = this.getWindow();
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.setStatusBarColor(Color.TRANSPARENT);
//        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);


        setContentView(R.layout.activity_chatbot_video_news_card_details);
//        ImmersionBar.with(this).navigationBarColor(R.color.colorPrimary).init();
//        ImmersionBar.with(this)
//                .supportActionBar(true)
//                .statusBarColor(R.color.colorPrimary)
//                .init();
//        StatusBarUtils.setStatusBarTranslucent(this,true);
//        StateAppBar.translucentStatusBar(this, false);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        }


        mUrl = getIntent().getStringExtra(URL);
        mTitle = getIntent().getStringExtra(TITLE);
        mDetails = getIntent().getStringExtra(DETAILS);
        initView();
        ImmersionBar.with(this)
                .titleBar(R.id.toolbar, false)
                .transparentBar()
                .init();
    }

    public static void start(Context context, String url, String title, String details) {
        Intent intent = new Intent(context, ChatbotVideoNewsDetailsActivity.class);
        if(url != null) {
            intent.putExtra(URL, url);
        }
        if(title != null) {
            intent.putExtra(TITLE, title);
        }
        if(details !=null){
            intent.putExtra(DETAILS, details);
        }
        context.startActivity(intent);
    }

    private void initVideoView(){
        mVideoView = (SantiVideoView)findViewById(R.id.vv_video);
        StandardVideoController standardVideoController = new StandardVideoController(this);
        standardVideoController.setTitle(mTitle);
        mVideoView.setVideoController(standardVideoController);
//        mVideoView.getController().setOnTouchListener(this);
        mVideoView.setUrl(/*"rtmp://58.200.131.2:1935/livetv/hunantv"*/mUrl);
//        FutureTarget<Bitmap> bitmap = Glide.with(this)
//                .asBitmap()
//                .load("http://sms-agent.oss-cn-hangzhou.aliyuncs.com/sms_agent_temp/100001040/5f22963bad14f.jpg")
//                .submit();
//        try{
//            Bitmap bitmap1 = bitmap.get();
//            mVideoView.setVideoCover(bitmap1);
//        }catch (Exception e){
//            e.printStackTrace();
//        }

        mVideoView.setPlayerFactory(new PlayerFactory<IjkPlayer>() {
            @Override
            public IjkPlayer createPlayer() {
                return new IjkPlayer() {
                    @Override
                    public void setOptions() {
                        //精准seek
                        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);
                    }
                };
            }
        });
        //播放器配置，注意：此为全局配置，按需开启
//        VideoViewManager.setConfig(VideoViewConfig.newBuilder()
//                .setLogEnabled(BuildConfig.DEBUG)
//                .setPlayerFactory(IjkPlayerFactory.create())
//                .setPlayerFactory(IjkPlayerFactory.create())
//                .setEnableOrientation(true)
//                .setEnableMediaCodec(true)
//                .setUsingSurfaceView(true)
//                .setEnableParallelPlay(true)
//                .setEnableAudioFocus(false)
//                .setScreenScaleType(VideoView.SCREEN_SCALE_MATCH_PARENT)
//                .build());
        mVideoView.start();

        mVideoView.addOnVideoViewStateChangeListener(new OnVideoViewStateChangeListener() {
            @Override
            public void onPlayerStateChanged(int playerState) {

            }

            @Override
            public void onPlayStateChanged(int playState) {
                if (playState == DanmuVideoView.STATE_PREPARED) {
                    if(!mVideoView.isReplay()) {
                    }
                } else if (playState == DanmuVideoView.STATE_PLAYBACK_COMPLETED) {
                    mHandler.removeCallbacksAndMessages(null);
                }
            }
        });
    }

    //    @Override
    protected void initView(){
        StatusBarUtil.setStatusBarColor(this, /*R.color.color_BDBDBD*/Color.parseColor("#FFFFFF"));
//        mIVBack = (ImageView) findViewById(R.id.danmu_title_iv_back);
//        mIVBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (mVideoView == null || !mVideoView.onBackPressed()) {
//                    ChatbotVideoNewsDetailsActivity.super.onBackPressed();
//                }
//            }
//        });
        mTVTitle = (TextView)findViewById(R.id.video_title);
        mTVDetails = (TextView)findViewById(R.id.video_details);
        mTVDetails.setMovementMethod(ScrollingMovementMethod.getInstance());
        mTVTitle.setText(mTitle);
        mTVDetails.setText(mDetails);
        mCloseImage = (ImageView)findViewById(R.id.close_img);
        mCloseImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mShareVideo = (ImageView)findViewById(R.id.share_video);
        mShareVideo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, mUrl);
                shareIntent.setType("text/plain");
                final CharSequence title = getResources().getText(R.string.action_share);
                startActivity(Intent.createChooser(shareIntent, title));
            }
        });

//        mCoverImage = (ImageView)findViewById(R.id.iv_img);
//        mCoverImage.setAlpha(0.5f);
//                    Glide.with(this).load("http://sms-agent.oss-cn-hangzhou.aliyuncs.com/sms_agent_temp/100001040/5f22963bad14f.jpg")
//                    .centerCrop()
//                    .into(mCoverImage);
//        mTVTitle.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
//            @Override
//            public void onSystemUiVisibilityChange(int i) {
//                View decorView = getWindow().getDecorView();
//                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//            }
//        });
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        initVideoView();
//        mVideoView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener(){
//            @Override
//            public void onSystemUiVisibilityChange(int visibility) {
//                View decorView = getWindow().getDecorView();
//                decorView.setSystemUiVisibility(
//                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                                | View.SYSTEM_UI_FLAG_FULLSCREEN
//                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//            }
//        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
//            View decorView = getWindow().getDecorView();
//            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        LogUtil.i("Junwang", "DanmuVideoPlayActivity onDestroy()");
    }


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
        }
    };


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mVideoView != null) {
            mVideoView.resume();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoView != null) {
            mVideoView.pause();
        }
    }

    @Override
    public void onBackPressed() {
        if (mVideoView == null || !mVideoView.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        int videowidth = v.getWidth();
        int videoHeight = v.getHeight();
        LogUtil.i("Junwang", "touchX="+touchX+", touchY="+touchY+", videowidth="+videowidth+", videoHeight="+videoHeight);
        if((touchX/videowidth <= 0.5) && (touchY/videoHeight <= 0.5)){
            Toast.makeText(this, "点中了第一象限", Toast.LENGTH_SHORT).show();
            LogUtil.i("Junwang", "touch on video 1st quadrant");
        }else if((touchX/videowidth > 0.5) && (touchY/videoHeight <= 0.5)){
            LogUtil.i("Junwang", "touch on video 2nd quadrant");
            Toast.makeText(this, "点中了第二象限", Toast.LENGTH_SHORT).show();
        }else if((touchX/videowidth <= 0.5) && (touchY/videoHeight > 0.5)){
            LogUtil.i("Junwang", "touch on video 3rd quadrant");
            Toast.makeText(this, "点中了第三象限", Toast.LENGTH_SHORT).show();
        }else if((touchX/videowidth > 0.5) && (touchY/videoHeight > 0.5)){
            LogUtil.i("Junwang", "touch on video 4th quadrant");
            Toast.makeText(this, "点中了第四象限", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
