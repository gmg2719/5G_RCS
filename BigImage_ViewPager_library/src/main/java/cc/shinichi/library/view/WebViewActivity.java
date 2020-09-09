package cc.shinichi.library.view;

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
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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

import java.util.HashMap;
import java.util.Map;

import cc.shinichi.library.R;

public class WebViewActivity extends Activity implements View.OnClickListener{
    public static final String URL = "url";
    public static final String TITLE= "title";

    private ImageView mIVBack;
    private WebView mWebView;
    private TextView mTVTitle;
    private String mUrl;
    private String mTitle;
    private ProgressBar mPbLoading;
//    private boolean mUpdatedText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_activity);
        mTitle = getIntent().getStringExtra(TITLE);
        initView();
        mUrl = getIntent().getStringExtra(URL);
        if(mUrl != null){
            initWebViewSetting();
        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        }
        setStatusBar();
    }

    protected boolean useThemestatusBarColor = true;//是否使用特殊的标题栏背景颜色，android5.0以上可以设置状态栏背景色，如果不使用则使用透明色值
    protected boolean useStatusBarColor = true;//是否使用状态栏文字和图标为暗色，如果状态栏采用了白色系，则需要使状态栏和图标为暗色，android6.0以上可以设置
    protected void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0及以上
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            //根据上面设置是否对状态栏单独设置颜色
            if (useThemestatusBarColor) {
                getWindow().setStatusBarColor(Color.parseColor("#FFFFFF")/*getResources().getColor(R.color.action_bar_background_color)*/);
            } else {
                getWindow().setStatusBarColor(Color.TRANSPARENT);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4到5.0
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && useStatusBarColor) {//android6.0以后可以对状态栏文字颜色和图标进行修改
            getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
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
                        Toast.makeText(WebViewActivity.this, "没有找到应用可以打开，请到应用市场下载！", Toast.LENGTH_LONG);
                    }
                });
                mWebView.stopLoading();
            }
            startActivity(intent);
        }
        catch (Exception e)
        {
            Log.e("Junwang", e.getMessage());
        }
    }

    private void initView(){
        StatusBarUtil.setStatusBarColor(this, /*R.color.color_BDBDBD*/Color.parseColor("#BDBDBD"));
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
                Log.d("Junwang", "loadUrl url="+url);
                if(url.startsWith("weixin://wap/pay?") || url.startsWith("https://wx.tenpay.com")){
                    return true;
                }
                try{
                    if(url.startsWith("http://") || url.startsWith("https://") || url.startsWith("rtsp://")){
                        Log.d("Junwang", "loadUrl url1="+url);
                        if(true)
                            mWebView.loadUrl(url);
                        //return true;
                        return false;
                    }else {
                        startThirdpartyApp(url);
                        return true;
                    }
                }catch(Exception e){
                    return true;
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("Junwang", "shouldOverrideUrlLoading loadUrl url.");
                if(url == null){
                    return true;
                }
                Log.d("Junwang", "loadUrl url="+url);

                Log.d("Junwang", "isIntercepted = ");
                if(true) {
                    if (url.startsWith("weixin://wap/pay?") || url.startsWith("http://weixin/wap/pay") ) {
                        try {
                            startActivity(new Intent("android.intent.action.VIEW", Uri.parse(url)));
                            return true;
                        } catch (Exception e) {
                            Log.i("Junwang", "weixin pay exception "+e.toString());
                        }
                    } else {
                        Map<String, String> extraHeaders = new HashMap<String, String>();
                        extraHeaders.put("Referer", "http://testxhs.supermms.cn");
                        if (url.startsWith("https://mclient.alipay.com") || url.startsWith("https://mclient.alipay.com")/*url.startsWith("alipays:") || url.startsWith("alipay")*/) {
                            mWebView.loadUrl(url);
                            return false;
                        } else {
                            view.loadUrl(url, extraHeaders);
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
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                mPbLoading.setProgress(newProgress);
            }
        });
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.iv_back){
            finish();
        }
    }

    public static void start(Context context, String url) {
        Intent intent = new Intent(context, WebViewActivity.class);
        Log.i("Junwang", "WebViewNewsActivity url="+url);
        intent.putExtra(URL, url);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void start(Context context, String url, String title){
        Intent intent = new Intent(context, WebViewActivity.class);
        Log.i("Junwang", "WebViewNewsActivity url="+url);
        intent.putExtra(URL, url);
        intent.putExtra(TITLE, title);
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
        @JavascriptInterface
        public String getUserAccount() {
            return "+8613777496301";
        }
    }

    public class SantiWebChromeClient extends WebChromeClient {
        //    private View mCustomView;
//    private CustomViewCallback mCustomViewCallback;
        private Context mContext;
        private Activity mActivity;

        public SantiWebChromeClient(Context mContext, Activity activity) {
            this.mContext = mContext;
            this.mActivity = activity;
        }

        @Nullable
        @Override
        public Bitmap getDefaultVideoPoster() {
            return Bitmap.createBitmap(new int[]{Color.TRANSPARENT}, 1, 1, Bitmap.Config.ARGB_8888);
        }

        @Nullable
        @Override
        public View getVideoLoadingProgressView() {
            return super.getVideoLoadingProgressView();
        }
    }
}
