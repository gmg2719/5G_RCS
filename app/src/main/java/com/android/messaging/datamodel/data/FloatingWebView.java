package com.android.messaging.datamodel.data;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.os.Build;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.PermissionListener;
import com.yhao.floatwindow.Screen;
import com.yhao.floatwindow.ViewStateListener;

public class FloatingWebView {
    public static void setWebViewSetting(WebView wv){
        WebSettings ws = wv.getSettings();
        ws.setRenderPriority(WebSettings.RenderPriority.HIGH);
        ws.setJavaScriptEnabled(true);
        ws.setAppCacheEnabled(true);
        ws.setGeolocationEnabled(true);
        ws.setDomStorageEnabled(true);
        ws.setJavaScriptCanOpenWindowsAutomatically(true);
        String cacheDirPath = wv.getContext().getFilesDir().getAbsolutePath()+"cache/";
        ws.setDatabasePath(cacheDirPath);
        ws.setDatabaseEnabled(true);
        //wv.addJavascriptInterface();
        ws.setUseWideViewPort(true);
        ws.setLoadWithOverviewMode(true);
        ws.setBuiltInZoomControls(true);
        ws.setSupportZoom(true);
        //ws.setNeedInitialFocus(false);
        //ws.setCacheMode();
        ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ws.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
    }

    public static WebView popupFloatingWindow(Context context, String url, float widthRatio, float heightRatio,
                                           float startXRatio, float startYRatio, int moveType,
                                           ViewStateListener vsl, PermissionListener pl){
        WebView wv = new WebView(context);
        setWebViewSetting(wv);
        wv.loadUrl(url);

        if(FloatWindow.get() == null) {
            FloatWindow
                    .with(context)
                    .setView(wv)
                    .setMoveStyle(3000, new TimeInterpolator() {
                        @Override
                        public float getInterpolation(float input) {
                            return (float)(Math.cos((input + 1) * Math.PI) / 2.0f) + 0.5f;
                        }
                    })
                    .setWidth(Screen.width, widthRatio)                               //设置控件宽高
                    .setHeight(Screen.height, heightRatio)
                    .setX(Screen.width, startXRatio)                                   //设置控件初始位置
                    .setY(Screen.height, startYRatio)
                    .setDesktopShow(false)                        //桌面显示
                    .setViewStateListener(/*mViewStateListener*/null)    //监听悬浮控件状态改变
                    .setPermissionListener(/*mPermissionListener*/null)  //监听权限申请结果
                    .setMoveType(moveType)
                    .build();
        }
        if(!FloatWindow.get().isShowing()) {
            FloatWindow.get().show();
        }
        return wv;
    }

    public static void hideWebView(){
        if(FloatWindow.get() != null){
            FloatWindow.get().hide();
            FloatWindow.destroy();
        }
    }
}
