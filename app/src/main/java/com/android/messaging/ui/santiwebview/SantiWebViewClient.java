package com.android.messaging.ui.santiwebview;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.alipay.sdk.app.H5PayCallback;
import com.alipay.sdk.app.PayTask;
import com.alipay.sdk.util.H5PayResultModel;
import com.android.messaging.util.LogUtil;

public class SantiWebViewClient extends WebViewClient {
    private Context mContext;
    private Activity mActivity;
    private WebView mWebView;
    private boolean mUpdatedText;

    public SantiWebViewClient(Context context, Activity activity, WebView webview, boolean needUpdateText) {
        mContext = context;
        mActivity = activity;
        mWebView = webview;
        mUpdatedText = needUpdateText;
    }

    //    @Override
//    public void onPageFinished(WebView view, String url) {
//        if(url != null && url.startsWith("https://news.sina.cn")) {
//            view.loadUrl(ADD_CLICKLISTENERONVIDEO_SCRIPT);
//        }
//    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        if(request == null){
            return false;
        }
        String url = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            url = request.getUrl().toString();
        } else {
            url = request.toString();
        }

        if(url == null){
            return true;
        }
        LogUtil.d("Junwang", "loadUrl url="+url);
        if(url.startsWith("weixin://wap/pay?") || url.startsWith("https://wx.tenpay.com")){
//            WXPay(view, url);
            return true;
        }
        try{
            if(url.startsWith("http://") || url.startsWith("https://") || url.startsWith("rtsp://")){
                LogUtil.d("Junwang", "loadUrl url1="+url);

                final PayTask task = new PayTask((Activity)mWebView.getContext());
                boolean isIntercepted = task.payInterceptorWithUrl(url, true, new H5PayCallback() {
                    @Override
                    public void onPayResult(final H5PayResultModel result) {
                        final String url=result.getReturnUrl();
                        if(!TextUtils.isEmpty(url)){
                            ((Activity)mWebView.getContext()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mWebView.loadUrl(url);
                                }
                            });
                        }
                    }
                });

                LogUtil.d("Junwang", "isIntercepted = "+isIntercepted);
                if(!isIntercepted)
                    mWebView.loadUrl(url);
                //return true;
                return false;
            }else {
                if(mUpdatedText){
                    mUpdatedText = false;
                    if(!url.startsWith("xhpfm://") &&
                            !url.startsWith("baiduboxlite://")) {
                        return false;
                    }
                }
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                        mContext.startActivity(intent);
                    }else {
                        final Activity a = mActivity;
                        if (a != null) {
                            a.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(a, "没有找到应用可以打开，请到应用市场下载！", Toast.LENGTH_LONG);
                                }
                            });
                        }
                        mWebView.stopLoading();
                    }
                    LogUtil.i("Junwang", "Above 6.0 shouldOverrideUrlLoading");
                } catch (ActivityNotFoundException e) {
                    LogUtil.d("Junwang", "can't find activity to open url");
                }
                return false;
            }
        }catch(Exception e){
            return false;
        }
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        LogUtil.d("Junwang", "shouldOverrideUrlLoading loadUrl url.");
        if(url == null){
            return true;
        }
        LogUtil.d("Junwang", "loadUrl url="+url);
        try{
            if(url.startsWith("http://") || url.startsWith("https://") || url.startsWith("rtsp://")){
                LogUtil.d("Junwang", "loadUrl url1="+url);
                /**
                 * for Alipay
                 * 推荐采用的新的二合一接口(payInterceptorWithUrl),只需调用一次
                 */
                                        /*final PayTask task = new PayTask((Activity)mMessageWebView.getContext());
                                        boolean isIntercepted = task.payInterceptorWithUrl(url, true, new H5PayCallback() {
                                            @Override
                                            public void onPayResult(final H5PayResultModel result) {
                                                final String url=result.getReturnUrl();
                                                if(!TextUtils.isEmpty(url)){
                                                    //mMessageWebView.loadUrl(url);
                                                    ((Activity)mMessageWebView.getContext()).runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            mMessageWebView.loadUrl(url);
                                                        }
                                                    });
                                                }
                                            }
                                        });

                                        /**
                                         * 判断是否成功拦截
                                         * 若成功拦截，则无需继续加载该URL；否则继续加载
                                         */
                return false;
            }else {
                if(mUpdatedText){
                    mUpdatedText = false;
                    if(!url.startsWith("xhpfm://") &&
                            !url.startsWith("baiduboxlite://")) {
                        return false;
                    }
                }
                return false;
            }
        }catch(Exception e){
            return false;
        }
    }

    @Nullable
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        return super.shouldInterceptRequest(view, request);
    }
}
