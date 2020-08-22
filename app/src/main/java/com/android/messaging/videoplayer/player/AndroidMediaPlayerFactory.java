package com.android.messaging.videoplayer.player;

//import tv.danmaku.ijk.media.player.AndroidMediaPlayer;

public class AndroidMediaPlayerFactory extends com.android.messaging.videoplayer.player.PlayerFactory<AndroidMediaPlayer> {

    public static AndroidMediaPlayerFactory create() {
        return new AndroidMediaPlayerFactory();
    }

    @Override
    public AndroidMediaPlayer createPlayer() {
        return new AndroidMediaPlayer();
    }
}
