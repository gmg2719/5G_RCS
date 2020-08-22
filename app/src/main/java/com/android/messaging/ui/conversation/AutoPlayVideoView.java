package com.android.messaging.ui.conversation;

import android.content.Context;
import android.view.View;
import android.widget.VideoView;

import com.android.messaging.util.LogUtil;

public class AutoPlayVideoView extends VideoView {
    public AutoPlayVideoView(Context context) {
        super(context);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if(visibility == View.VISIBLE){
            LogUtil.i("Junwang", "AutoPlayVideoView change to VISIBLE");
            start();
        }else if(visibility == View.GONE || visibility == View.INVISIBLE){
            LogUtil.i("Junwang", "AutoPlayVideoView change to INVISIBLE");
            stopPlayback();
        }
    }
}
