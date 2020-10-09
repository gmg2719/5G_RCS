package com.android.messaging.product.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.H5PayCallback;
import com.alipay.sdk.app.PayTask;
import com.alipay.sdk.util.H5PayResultModel;
import com.android.messaging.R;
import com.android.messaging.datamodel.microfountain.sms.ChatbotUtils;
import com.android.messaging.datamodel.microfountain.sms.SendRcsMsgUtils;
import com.android.messaging.product.utils.StatusBarUtil;
import com.android.messaging.ui.santiwebview.SantiWebChromeClient;
import com.android.messaging.util.LogUtil;
import com.android.messaging.util.UiUtils;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class WebViewNewsActivity extends Activity implements View.OnClickListener{
    public static final String URL = "url";
    public static final String TITLE= "title";
    public static final String MSGID = "msgId";

    private ImageView mIVBack;
    private WebView mWebView;
    private TextView mTVTitle;
    private String mUrl;
    private String mTitle;
    private ProgressBar mPbLoading;
    private String mMsgId;
//    private boolean mUpdatedText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_news_activity);
        Intent intent = getIntent();
        mTitle = intent.getStringExtra(TITLE);
        initView();
        mUrl = intent.getStringExtra(URL);
        if(mUrl != null){
            initWebViewSetting();
        }
        mMsgId = intent.getStringExtra(MSGID);
        UiUtils.setStatusBarColor(this, Color.parseColor("#FFFFFF"));
    }

    /**
     * 打开第三方app。如果没安装则跳转到应用市场
     * @param url
     */
    private void startThirdpartyApp(String url)
    {
        try {
            Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME); // 注释1
            if (getPackageManager().resolveActivity(intent, 0) == null)
            {  // 如果手机还没安装app，则跳转到应用市场
//                intent = new Intent(Intent.ACTION_VIEW, Uri.parse
//                        ("market://details?id=" + intent.getPackage())); // 注释2
                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(WebViewNewsActivity.this, "没有找到应用可以打开，请到应用市场下载！", Toast.LENGTH_LONG);
                                    }
                                });
                                mWebView.stopLoading();
            }
            startActivity(intent);
        }
        catch (Exception e)
        {
            LogUtil.e("Junwang", e.getMessage());
        }
    }

    private void initView(){
        StatusBarUtil.setStatusBarColor(this, R.color.color_BDBDBD);
        mIVBack = (ImageView) findViewById(R.id.iv_back);
        mIVBack.setOnClickListener(this);
        if(mTitle != null && mTitle.length() > 0){
            mTVTitle = (TextView)findViewById(R.id.tv_title);
            mTVTitle.setText(mTitle);
            mTVTitle.setVisibility(View.VISIBLE);
        }
        mPbLoading = (ProgressBar)findViewById(R.id.pb_loading);
//        mUpdatedText = true;
        mWebView = (WebView)findViewById(R.id.wv_content);
        mWebView.setWebChromeClient(new SantiWebChromeClient(this, this));
        mWebView.setWebViewClient(new WebViewClient(){
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//                return super.shouldOverrideUrlLoading(view, request);
//            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mPbLoading.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mPbLoading.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
//                mWebView.loadDataWithBaseURL(null, "升级维护中", "text/html", "utf-8", null);
                mWebView.setVisibility(View.INVISIBLE);
                ((TextView)findViewById(R.id.tv1)).setVisibility(View.VISIBLE);
            }

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
                    WXPay(view, url);
                    return true;
                }
                try{
                    if(url.startsWith("http://") || url.startsWith("https://") || url.startsWith("rtsp://")){
                        LogUtil.d("Junwang", "loadUrl url1="+url);

                        final PayTask task = new PayTask(WebViewNewsActivity.this);
                        boolean isIntercepted = task.payInterceptorWithUrl(url, true, new H5PayCallback() {
                            @Override
                            public void onPayResult(final H5PayResultModel result) {
                                final String url=result.getReturnUrl();
                                if(!TextUtils.isEmpty(url)){
                                    runOnUiThread(new Runnable() {
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
                        startThirdpartyApp(url);
                        return true;
//                        if(!url.startsWith("xhpfm://") &&
//                                !url.startsWith("baiduboxlite://")
//                                && !url.startsWith("tmall://")
//                        && !url.startsWith("tb://")) {
//                            return true;
//                        }

//                        try {
//                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                            if (intent.resolveActivity(getPackageManager()) != null) {
//                                startActivity(intent);
//                            }else {
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Toast.makeText(WebViewNewsActivity.this, "没有找到应用可以打开，请到应用市场下载！", Toast.LENGTH_LONG);
//                                    }
//                                });
//                                mWebView.stopLoading();
//                            }
//                            LogUtil.i("Junwang", "Above 6.0 shouldOverrideUrlLoading");
//                        } catch (ActivityNotFoundException e) {
//                            LogUtil.d("Junwang", "can't find activity to open url");
//                        }
//                        return false;
                    }
                }catch(Exception e){
                    return true;
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                LogUtil.d("Junwang", "shouldOverrideUrlLoading loadUrl url.");
                if(url == null){
                    return true;
                }
                LogUtil.d("Junwang", "loadUrl url="+url);
                final PayTask task = new PayTask(WebViewNewsActivity.this);
                boolean isIntercepted = task.payInterceptorWithUrl(url, true, new H5PayCallback() {
                    @Override
                    public void onPayResult(final H5PayResultModel result) {
                        final String url=result.getReturnUrl();
                        if(!TextUtils.isEmpty(url)){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mWebView.loadUrl(url);
                                }
                            });
                        }
                    }
                });

                LogUtil.d("Junwang", "isIntercepted = "+isIntercepted);
                if(!isIntercepted) {
                    if (url.startsWith("weixin://wap/pay?") || url.startsWith("http://weixin/wap/pay") ) {
                        try {
                            startActivity(new Intent("android.intent.action.VIEW", Uri.parse(url)));
                            return true;
                        } catch (Exception e) {
//                        new CancelOrOkDialog(H5PayActivity.this, getString(R.string.h5_pay_no_pay_app)) {
//                            @Override
//                            public void ok() {
//                                Intent intent = new Intent(H5PayActivity.this, MainActivity.class);
//                                intent.putExtra(Constants.SELECTTAB, 0);
//                                startActivity(intent);
//                            }
//                        };
                            LogUtil.i("Junwang", "weixin pay exception "+e.toString());
                        }
                    } else {
                        Map<String, String> extraHeaders = new HashMap<String, String>();
                        extraHeaders.put("Referer", "http://testxhs.supermms.cn");
                        if (url.startsWith("https://mclient.alipay.com") || url.startsWith("https://mclient.alipay.com")/*url.startsWith("alipays:") || url.startsWith("alipay")*/) {
                            mWebView.loadUrl(url);
                            return false;
                        } else {
                            if(url.startsWith("http://") || url.startsWith("https://") || url.startsWith("rtsp://")) {
                                view.loadUrl(url, extraHeaders);
                            }else{
                                startThirdpartyApp(url);
                                return true;
                            }
                        }
                    }
                    // ------- 处理结束 -------
                    if (!(url.startsWith("http") || url.startsWith("https"))) {
                        return true;
                    }
                }else{
                    startThirdpartyApp(url);
                    return true;
                }

                return true;
//                try {
//                    if (url.startsWith("weixin://") || url.startsWith("alipays://")) {
//                        Intent intent = new Intent();
//                        intent.setAction(Intent.ACTION_VIEW);
//                        intent.setData(Uri.parse(url));
//                        startActivity(intent);
//                        return true;
//                    }
//                } catch (Exception e) {
//                    return false;
//                }
//
//                if (url.contains("https://wx.tenpay.com")) {
//                    Map<String, String> extraHeaders = new HashMap<>();
//                    extraHeaders.put("Referer", "http://testxhs.supermms.cn");
//                    view.loadUrl(url, extraHeaders);
//                    return true;
//                }
//                view.loadUrl(url);
//                return true;
//                try{
//                    if(url.startsWith("http://") || url.startsWith("https://") || url.startsWith("rtsp://")
//                            ){
//                        LogUtil.d("Junwang", "loadUrl url1="+url);
////                        if (url.startsWith("weixin://wap/pay?") || url.startsWith("https://wx.tenpay.com")) {
////                            Map<String, String> webviewHead = new HashMap<>();
////                            webviewHead.put("Referer", "http://testxhs.supermms.cn");
////                            mWebView.loadUrl(url, webviewHead);
////                        } else {
////                            mWebView.loadUrl(url);
////                        }
//                        final PayTask task = new PayTask(WebViewNewsActivity.this);
//                        boolean isIntercepted = task.payInterceptorWithUrl(url, true, new H5PayCallback() {
//                            @Override
//                            public void onPayResult(final H5PayResultModel result) {
//                                final String url=result.getReturnUrl();
//                                if(!TextUtils.isEmpty(url)){
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            mWebView.loadUrl(url);
//                                        }
//                                    });
//                                }
//                            }
//                        });
//
//                        LogUtil.d("Junwang", "isIntercepted = "+isIntercepted);
//                        if(!isIntercepted) {
//                            if (url.startsWith("https://wx.tenpay.com")) {
//                                Map<String, String> webviewHead = new HashMap<>();
//                                webviewHead.put("Referer", "http://testxhs.supermms.cn");
//                                mWebView.loadUrl(url, webviewHead);
//                            } else if(url.startsWith("weixin://wap/pay?"))
//                            {
//
//                            }else {
//                                mWebView.loadUrl(url);
//                            }
//                        }
//                        //return true;
//                        return true;
//                    }else {
//                        startThirdpartyApp(url);
//                        return true;
////                        if(!url.startsWith("xhpfm://") &&
////                                !url.startsWith("baiduboxlite://")
////                                && !url.startsWith("tmall://")
////                        && !url.startsWith("tb://")) {
////                            return true;
////                        }
//
////                        try {
////                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
////                            if (intent.resolveActivity(getPackageManager()) != null) {
////                                startActivity(intent);
////                            }else {
////                                runOnUiThread(new Runnable() {
////                                    @Override
////                                    public void run() {
////                                        Toast.makeText(WebViewNewsActivity.this, "没有找到应用可以打开，请到应用市场下载！", Toast.LENGTH_LONG);
////                                    }
////                                });
////                                mWebView.stopLoading();
////                            }
////                            LogUtil.i("Junwang", "Above 6.0 shouldOverrideUrlLoading");
////                        } catch (ActivityNotFoundException e) {
////                            LogUtil.d("Junwang", "can't find activity to open url");
////                        }
////                        return false;
//                    }
//                }catch(Exception e){
//                    return true;
//                }
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                mPbLoading.setProgress(newProgress);
            }
        });
    }

    private static String getStringFromUrl(String s) throws IOException {
        StringBuffer buffer = new StringBuffer();
        // 通过js的执行路径获取后台数据进行解析
        java.net.URL url = new URL(s);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setDoOutput(true);
        http.setDoInput(true);
        http.setUseCaches(false);
        http.setRequestMethod("GET");
        http.connect();
        // 将返回的输入流转换成字符串
        InputStream inputStream = http.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        String str = null;
        while ((str = bufferedReader.readLine()) != null) {
            buffer.append(str);
        }
        bufferedReader.close();
        inputStreamReader.close();
        // 释放资源
        inputStream.close();
        inputStream = null;
        http.disconnect();
        str = buffer.toString();
        int index = str.indexOf("(");
        String jsonString = str.substring(index + 1, str.length() -1);
        return jsonString;
    }

    private boolean WXPay(WebView view, String url){
        //IWXAPI api = WXAPIFactory.createWXAPI(view.getContext(), /*"你的appid"*/"wxb4ba3c02aa476ea1");

        try{
            String content = getStringFromUrl(url);
            if(content != null && content.length() > 0){
                //Log.e("get server pay params:",content);
                JSONObject json = new JSONObject(content);
                if(null != json && !json.has("retcode") ){
                    PayReq req = new PayReq();
                    //req.appId = "wxf8b4f85f3a794e77";  // 测试用appId
                    req.appId			= json.getString("appid");
                    req.partnerId		= json.getString("partnerid");
                    req.prepayId		= json.getString("prepayid");
                    req.nonceStr		= json.getString("noncestr");
                    req.timeStamp		= json.getString("timestamp");
                    req.packageValue	= json.getString("package");
                    req.sign			= json.getString("sign");
                    req.extData			= "app data"; // optional
                    IWXAPI api = WXAPIFactory.createWXAPI(view.getContext(), /*"你的appid"*/req.appId);
                    //api.registerApp();
                    api.sendReq(req);
                    return true;
                }else{
                    LogUtil.d("Junwang", "返回错误"+json.getString("retmsg"));
                    Toast.makeText(this, "返回错误"+json.getString("retmsg"), Toast.LENGTH_SHORT).show();
                }
            }else{
                LogUtil.d("Junwang", "服务器请求错误");
                Toast.makeText(this, "服务器请求错误", Toast.LENGTH_SHORT).show();
            }
        }catch(Exception e){
            LogUtil.d("Junwang", "异常："+e.getMessage());
            Toast.makeText(this, "异常："+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            default:
                break;
        }
    }

    public static void start(Context context, String url) {
        Intent intent = new Intent(context, WebViewNewsActivity.class);
        LogUtil.i("Junwang", "WebViewNewsActivity url="+url);
        intent.putExtra(URL, url);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void start(Context context, String url, String title){
        Intent intent = new Intent(context, WebViewNewsActivity.class);
        LogUtil.i("Junwang", "WebViewNewsActivity url="+url);
        intent.putExtra(URL, url);
        intent.putExtra(TITLE, title);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void start(Context context, String url, String title, String msgId){
        Intent intent = new Intent(context, WebViewNewsActivity.class);
        LogUtil.i("Junwang", "WebViewNewsActivity url="+url);
        intent.putExtra(URL, url);
        intent.putExtra(TITLE, title);
        intent.putExtra(MSGID, msgId);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebViewSetting() {
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        WebSettings webSetting = mWebView.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
//        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(false);
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);
        webSetting.setDatabaseEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        webSetting.setCacheMode(webSetting.LOAD_NO_CACHE);
        webSetting.setAppCachePath(getDir("appCache", Context.MODE_PRIVATE).getPath());
//        webSetting.setDatabasePath(getDir("databases", Context.MODE_PRIVATE).getPath());
//        webSetting.setGeolocationDatabasePath(getDir(/*"geolocation"*/"database", Context.MODE_PRIVATE).getPath());
        webSetting.setGeolocationDatabasePath(getFilesDir().getPath());
        webSetting.setPluginState(WebSettings.PluginState.ON);
        webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSetting.setTextSize(WebSettings.TextSize.NORMAL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSetting.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        setupWebView();
        mWebView.loadUrl(mUrl);
    }

    private void setupWebView() {
        mWebView.addJavascriptInterface(new JsInterfaceLogic(), "app");
    }

    /**
     *  暴露出去给JS调用的Java对象
     */
    class JsInterfaceLogic {
        private String phoneNumber;
        @JavascriptInterface
        public String getUserAccount() {
            String phoneNumber = ChatbotUtils.getPhoneNumber();
            return phoneNumber == null ? "+8613777496301" : phoneNumber;
        }

        public String getPhoneNumber(){
            TelephonyManager phoneManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
//        try{
//            phoneNumber = phoneManager.getLine1Number();
//        }catch(SecurityException e){
//            LogUtil.i("Junwang", "No permission to get phone number");
//        }finally {
//
//        }
            if(mMsgId != null) {
                phoneNumber = SendRcsMsgUtils.getSelfNumber(mMsgId);
            }
            if(phoneNumber == null || phoneNumber.length() == 0){
                phoneNumber = "+8613777496301";
            }
            return phoneNumber;
        }
    }
}
