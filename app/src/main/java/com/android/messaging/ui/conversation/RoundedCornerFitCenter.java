package com.android.messaging.ui.conversation;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.android.messaging.util.LogUtil;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;

import java.security.MessageDigest;

public class RoundedCornerFitCenter extends BitmapTransformation {
    private int radius;
    private float ratio;

    public RoundedCornerFitCenter(int radius) {
        this.radius = radius;
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        LogUtil.i("Junwang", "outWidth = "+outWidth+", outHeight="+outHeight+", toTransform.getWidth()="+toTransform.getWidth()+", toTransform.getHeight()="+toTransform.getHeight());
        Bitmap bitmap = TransformationUtils.fitCenter(pool, toTransform, outWidth, outHeight);
        ratio = toTransform.getWidth()/toTransform.getHeight();
        return TransformationUtils.roundedCorners(pool, bitmap, radius);
    }

    public float getRatio() {
        return ratio;
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

    }
}
