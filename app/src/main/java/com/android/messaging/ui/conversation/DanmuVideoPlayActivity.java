package com.android.messaging.ui.conversation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.messaging.R;
import com.android.messaging.product.utils.StatusBarUtil;
import com.android.messaging.util.LogUtil;
import com.android.messaging.videoplayer.ijk.IjkPlayer;
import com.android.messaging.videoplayer.listener.OnVideoViewStateChangeListener;
import com.android.messaging.videoplayer.player.DanmuVideoView;
import com.android.messaging.videoplayer.player.PlayerFactory;
import com.android.messaging.videoplayer.ui.StandardVideoController;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class DanmuVideoPlayActivity extends /*BaseDanmuActivity<DanmuVideoView>*/Activity implements View.OnTouchListener/*, VideoListener*/ {
    public static final String URL = "url";
    public static final String TITLE= "title";
    private ImageView mIVBack;
    private TextView mTVTitle;
    private String mUrl;
    private String mTitle;
    SantiVideoView mVideoView;

    private SeekBar mSKbar;
    private TextView mTVInd;
    private Switch mDanmuOnOff;
    private AutoPollRecyclerView mRVComments;
    private List<DanmuComments> dcList;
    private TextView mSend;
    private EditText mSendDanduContent;
    DanmuCommentsRecyclerAdapter mDcAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.danmu_videoplay_activity);
        mUrl = getIntent().getStringExtra(URL);
        mTitle = getIntent().getStringExtra(TITLE);
        initView();

    }


//    @Override
//    protected int getLayoutResId() {
//        return R.layout.danmu_videoplay_activity;
//    }
//
//    @Override
//    protected int getTitleResId() {
//        return R.string.str_danmu;
//    }
//
//    @Override
//    protected void setActionBar() {
//        mUrl = getIntent().getStringExtra(URL);
//        mTitle = getIntent().getStringExtra(TITLE);
//        StatusBarUtil.setStatusBarColor(this, R.color.color_BDBDBD);
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setTitle(mTitle);
//            if (enableBack()) {
//                actionBar.setDisplayHomeAsUpEnabled(true);
//            }
//        }
//    }

    public static void start(Context context, String url, String title) {
        Intent intent = new Intent(context, DanmuVideoPlayActivity.class);
        if(url != null) {
            intent.putExtra(URL, url);
        }
        if(title != null) {
            intent.putExtra(TITLE, title);
        }
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void initVideoView(){
        mVideoView = (SantiVideoView)findViewById(R.id.mSantiVideoView);
        StandardVideoController standardVideoController = new StandardVideoController(this);
        standardVideoController.setTitle(mTitle);
        mVideoView.setVideoController(standardVideoController);
//        mVideoView.getController().setOnTouchListener(this);
        mVideoView.setUrl(/*"rtmp://58.200.131.2:1935/livetv/hunantv"*/mUrl);
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
                        simulateDanmu();
                    }
                } else if (playState == DanmuVideoView.STATE_PLAYBACK_COMPLETED) {
                    mHandler.removeCallbacksAndMessages(null);
//                    mVideoView.mDanmakuView.removeAllDanmakus(true);
//                    mVideoView.removeView(mVideoView.mDanmakuView);
                }
            }
        });
    }



//    @Override
    protected void initView(){
        StatusBarUtil.setStatusBarColor(this, R.color.color_BDBDBD);
        mIVBack = (ImageView) findViewById(R.id.danmu_title_iv_back);
        mIVBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mVideoView == null || !mVideoView.onBackPressed()) {
                    DanmuVideoPlayActivity.super.onBackPressed();
                }
                if(mSendDanduContent != null) {
                    InputMethodManager manager = ((InputMethodManager) mSendDanduContent.getContext().getSystemService(Context.INPUT_METHOD_SERVICE));
                    if (manager != null && manager.isActive()) {
                        LogUtil.i("Junwang", "back button pressed.");
                        manager.hideSoftInputFromWindow(mSendDanduContent.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }
            }
        });
        mTVTitle = (TextView)findViewById(R.id.danmu_title_string);
        mTVTitle.setText(mTitle);
        mTVTitle.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int i) {
                View decorView = getWindow().getDecorView();
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
        });
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
////                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                                | View.SYSTEM_UI_FLAG_FULLSCREEN
//                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//            }
//        });
        mSKbar = (SeekBar)findViewById(R.id.alpha_set);
//        mSKbar.getProgressDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);
//        mSKbar.getThumb().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        mSendDanduContent = (EditText)findViewById(R.id.dmcontent);
        //点击软键盘外部，收起软键盘
//        mSendDanduContent.setOnFocusChangeListener(new LinearLayout.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(final View view, final boolean hasFocus) {
//                /*if(!hasFocus)*/{
//                    LogUtil.i("Junwang", "mSendDanduContent onFocusChange");
//                    InputMethodManager manager = ((InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE));
//                    if (manager != null && manager.isActive())
//                        manager.hideSoftInputFromWindow(view.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
//                    View decorView = getWindow().getDecorView();
//                    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//                }
//            }
//        });
        mSend = (TextView)findViewById(R.id.send_button);
        mSend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Editable et = mSendDanduContent.getText();
                if((et == null) || (et.toString() ==  null) || (et.toString().length() == 0)){
                    Toast.makeText(mSend.getContext(), "输入内容不能为空", Toast.LENGTH_SHORT).show();
                }else{
                    mVideoView.addDanmaku(et.toString(), true);
                    DanmuComments dcs = new DanmuComments("我：", et.toString());
                    dcList.add(dcs);
//                    mRVComments.setAdapter(new DanmuCommentsRecyclerAdapter(dcList));
//                    mRVComments.invalidate();
                    mDcAdapter.notifyDataSetChanged();
                    InputMethodManager manager = ((InputMethodManager)mSendDanduContent.getContext().getSystemService(Context.INPUT_METHOD_SERVICE));
                    if (manager != null)
                        manager.hideSoftInputFromWindow(view.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                    mSendDanduContent.setText(null);
                    View decorView = getWindow().getDecorView();
                    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                }
            }
        });
        mTVInd = (TextView)findViewById(R.id.tv_alphaInd);
        mTVInd.setText(String.valueOf(mSKbar.getProgress())+"%");
        mDanmuOnOff = (Switch)findViewById(R.id.switch_danmaku);
        mDanmuOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    showDanMu();
                }else{
                    hideDanMu();
                }
            }
        });
        mVideoView.setAlpha((float)mSKbar.getProgress()/100);
        mSKbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mTVInd.setText(String.valueOf(mSKbar.getProgress())+"%");
                mVideoView.setAlpha((float)mSKbar.getProgress()/100);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        dcList = new ArrayList<DanmuComments>();
        mRVComments = (AutoPollRecyclerView)findViewById(R.id.comments_view);
        mRVComments.start();
        mDcAdapter = new DanmuCommentsRecyclerAdapter(dcList);
        mRVComments.setAdapter(mDcAdapter);
        mRVComments.setLayoutManager(new LinearLayoutManager(this));
//        mRVComments.setAdapter(new RecyclerView.Adapter() {
//            @NonNull
//            @Override
//            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//                return null;
//            }
//
//            @Override
//            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
//            }
//
//            @Override
//            public int getItemCount() {
//                return 0;
//            }
//        });
//        String url = "https://flv2.bn.netease.com/videolib1/1811/26/OqJAZ893T/HD/OqJAZ893T-mobile.mp4";
//        String url = "https://vod-xhpfm.zhongguowangshi.com/NewsVideo/201910/443fc211710f494b977e1cdfce312a41.mp4";
//        if(mUrl == null){
//            mUrl = "https://vod-xhpfm.zhongguowangshi.com/NewsVideo/201910/443fc211710f494b977e1cdfce312a41.mp4";
//        }
//        mUrl = "rtmp://media3.sinovision.net:1935/live/livestream";
//        mUrl = "rtmp://58.200.131.2:1935/livetv/hunantv";
//        mVideoView.setPath(mUrl);
//        mVideoView.setVideoListener(this);
        //设置有进度条可以拖动快进
//        MediaController localMediaController = new MediaController(this);
//        mVideoView.setMediaController(localMediaController);
//        getPlayTime(mUrl);
////        mVideoView.start();
//        try {
//            mVideoView.load();
//        }catch(IOException e){
//            Toast.makeText(this,"播放失败",Toast.LENGTH_SHORT);
//            LogUtil.e("Junwang", ""+e.toString());
//        }
//        mVideoView.setOnTouchListener(this);
//        mVideoView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener(){
//            @Override
//            public void onSystemUiVisibilityChange(int visibility) {
//                View decorView = getWindow().getDecorView();
//                decorView.setSystemUiVisibility(
//                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
////                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                                | View.SYSTEM_UI_FLAG_FULLSCREEN
//                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//            }
//        });
    }

    public static boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = { 0, 0 };
            v.getLocationInWindow(leftTop);
            int left = leftTop[0], top = leftTop[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public static Boolean hideInputMethod(Context context, View v) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            return imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
        return false;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                if(hideInputMethod(this, v)) {
                    return true; //隐藏键盘时，其他控件不响应点击事件==》注释则不拦截点击事件
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }



    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        LogUtil.i("Junwang", "DanmuVideoPlayActivity onDestroy()");
    }



    public void showDanMu() {
        mVideoView.showDanMu();
    }

    public void hideDanMu() {
        mVideoView.hideDanMu();
    }

    public void addDanmakuWithDrawable(View view) {
        mVideoView.addDanmakuWithDrawable();
    }

    public void addDanmaku(View view) {
        mVideoView.addDanmaku("这是一条文字弹幕~", true);
    }


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
//            mDcAdapter.notifyDataSetChanged();
//            mRVComments.invalidate();
        }
    };



    /**
     * 模拟弹幕
     */
    private void simulateDanmu() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                int time = new Random().nextInt(300);
                String content = "我的测试弹幕" + time + time;
                mVideoView.addDanmaku(content, false);
                int name = new Random().nextInt(50);
                DanmuComments dcs = new DanmuComments("user"+name+":", content);
                /*if(dcList.size()<50)*/ {
                    dcList.add(dcs);
                    mDcAdapter.notifyDataSetChanged();
                }
//                mRVComments.setAdapter(new DanmuCommentsRecyclerAdapter(dcList));
                mHandler.postDelayed(this, 1000);
            }
        });
    }

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

//    @Override
//    public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {
//
//    }
//
//    @Override
//    public void onCompletion(IMediaPlayer iMediaPlayer) {
//
//    }
//
//    @Override
//    public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
//        return false;
//    }
//
//    @Override
//    public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
//        return false;
//    }
//
//    @Override
//    public void onPrepared(IMediaPlayer iMediaPlayer) {
//        mVideoView.start();
//    }
//
//    @Override
//    public void onSeekComplete(IMediaPlayer iMediaPlayer) {
//
//    }
//
//    @Override
//    public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {
//
//    }
//
//    private void  getPlayTime(String mUri)
//    {
//        android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();
//        try {
//            if (mUri != null)
//            {
//                HashMap<String, String> headers = null;
//                if (headers == null)
//                {
//                    headers = new HashMap<String, String>();
//                    headers.put("User-Agent", "Mozilla/5.0 (Linux; U; Android 4.4.2; zh-CN; MW-KW-001 Build/JRO03C) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 UCBrowser/1.0.0.001 U4/0.8.0 Mobile Safari/533.1");
//                }
//                mmr.setDataSource(mUri, headers);
//            } else
//            {
//                //mmr.setDataSource(mFD, mOffset, mLength);
//            }
////            mmr.setDataSource(mUri);
//
//            String duration = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);//时长(毫秒)
//            String width = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);//宽
//            String height = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);//高
//
//            LogUtil.i("Junwang", "playtime:"+ duration+", w="+width+", h="+height);
//
//        } catch (Exception ex)
//        {
//            LogUtil.e("Junwang", "MediaMetadataRetriever exception " + ex);
//        } finally {
//            mmr.release();
//        }
//
//    }
}
