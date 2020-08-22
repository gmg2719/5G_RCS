package com.android.messaging.ui.conversation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.messaging.R;
import com.android.messaging.ui.VideoListener;
import com.android.messaging.util.LogUtil;

import java.io.IOException;
import java.util.HashMap;

import tv.danmaku.ijk.media.player.IMediaPlayer;

public class DanmakuVideoPlayActivity extends Activity implements View.OnTouchListener, VideoListener {
    public static final String URL = "url";
    private String mUrl;
    PowerVideoView mVideoView;

    private SeekBar mSKbar;
    private TextView mTVInd;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        View decorView = getWindow().getDecorView();
//        int option = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_FULLSCREEN;
//        decorView.setSystemUiVisibility(option);
//        if (Build.VERSION.SDK_INT >= 21) {
//            View decorView = getWindow().getDecorView();
//            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
//            decorView.setSystemUiVisibility(option);
//            getWindow().setNavigationBarColor(Color.TRANSPARENT);
//            getWindow().setStatusBarColor(Color.TRANSPARENT);
//        }
        setContentView(R.layout.danmaku_videoplay_activity);
        mUrl = getIntent().getStringExtra(URL);
        initView();

    }



    public static void start(Context context, String url) {
        Intent intent = new Intent(context, DanmakuVideoPlayActivity.class);
        intent.putExtra(URL, url);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    private void initView(){
        mVideoView = (PowerVideoView)findViewById(R.id.mVideoView);
        mSKbar = (SeekBar)findViewById(R.id.alpha_set);
        mTVInd = (TextView)findViewById(R.id.tv_alphaInd);
        mTVInd.setText(String.valueOf(mSKbar.getProgress())+"%");
        mSKbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mTVInd.setText(String.valueOf(mSKbar.getProgress())+"%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
//        String url = "https://flv2.bn.netease.com/videolib1/1811/26/OqJAZ893T/HD/OqJAZ893T-mobile.mp4";
//        String url = "https://vod-xhpfm.zhongguowangshi.com/NewsVideo/201910/443fc211710f494b977e1cdfce312a41.mp4";
//        if(mUrl == null){
//            mUrl = "https://vod-xhpfm.zhongguowangshi.com/NewsVideo/201910/443fc211710f494b977e1cdfce312a41.mp4";
//        }
//        mUrl = "rtmp://media3.sinovision.net:1935/live/livestream";
//        mUrl = "rtmp://58.200.131.2:1935/livetv/hunantv";
        mVideoView.setPath(mUrl);
        mVideoView.setVideoListener(this);
        //设置有进度条可以拖动快进
//        MediaController localMediaController = new MediaController(this);
//        mVideoView.setMediaController(localMediaController);
        getPlayTime(mUrl);
//        mVideoView.start();
        try {
            mVideoView.load();
        }catch(IOException e){
            Toast.makeText(this,"播放失败",Toast.LENGTH_SHORT);
            LogUtil.e("Junwang", ""+e.toString());
        }
        mVideoView.setOnTouchListener(this);
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

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
//            View decorView = getWindow().getDecorView();
//            decorView.setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mVideoView != null){
//            mVideoView.stopPlayback();
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

    @Override
    public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {

    }

    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {

    }

    @Override
    public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onPrepared(IMediaPlayer iMediaPlayer) {
        mVideoView.start();
    }

    @Override
    public void onSeekComplete(IMediaPlayer iMediaPlayer) {

    }

    @Override
    public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {

    }

    private void  getPlayTime(String mUri)
    {
        android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();
        try {
            if (mUri != null)
            {
                HashMap<String, String> headers = null;
                if (headers == null)
                {
                    headers = new HashMap<String, String>();
                    headers.put("User-Agent", "Mozilla/5.0 (Linux; U; Android 4.4.2; zh-CN; MW-KW-001 Build/JRO03C) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 UCBrowser/1.0.0.001 U4/0.8.0 Mobile Safari/533.1");
                }
                mmr.setDataSource(mUri, headers);
            } else
            {
                //mmr.setDataSource(mFD, mOffset, mLength);
            }
//            mmr.setDataSource(mUri);

            String duration = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);//时长(毫秒)
            String width = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);//宽
            String height = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);//高

            LogUtil.i("Junwang", "playtime:"+ duration+", w="+width+", h="+height);

        } catch (Exception ex)
        {
            LogUtil.e("Junwang", "MediaMetadataRetriever exception " + ex);
        } finally {
            mmr.release();
        }

    }
}
