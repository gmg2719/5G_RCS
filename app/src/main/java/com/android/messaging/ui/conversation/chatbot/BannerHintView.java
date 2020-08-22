package com.android.messaging.ui.conversation.chatbot;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;

import com.yc.cn.ycbannerlib.banner.hintview.IconHintView;

public class BannerHintView extends IconHintView {
    private int focusResId;
    private int normalResId;
    private int size;

    public BannerHintView(Context context, @DrawableRes int focusResId, @DrawableRes int normalResId) {
        this(context, focusResId, normalResId, 0);
    }

    public BannerHintView(Context context, @DrawableRes int focusResId, @DrawableRes int normalResId, int size) {
        super(context, focusResId, normalResId, size);
        this.focusResId = focusResId;
        this.normalResId = normalResId;
        this.size = size;
    }

    @Override
    public Drawable makeFocusDrawable() {
        Drawable drawable = getContext().getResources().getDrawable(focusResId);
        return drawable;
    }

    @Override
    public Drawable makeNormalDrawable() {
        Drawable drawable = getContext().getResources().getDrawable(normalResId);
        return drawable;
    }
}
