package com.android.messaging.ui.santiwebview;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.EditText;

import com.android.messaging.ui.conversation.ConversationActivity;
import com.android.messaging.util.LogUtil;

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

    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

        builder.setTitle("对话框")
                .setMessage(message)
                .setPositiveButton("确定", null);

        // 不需要绑定按键事件
        // 屏蔽keycode等于84之类的按键
        builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                LogUtil.v("onJsAlert", "keyCode==" + keyCode + "event="+ event);
                return true;
            }
        });
        // 禁止响应按back键的事件
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
        result.confirm();// 因为没有绑定事件，需要强行confirm,否则页面会变黑显示不了内容。
        return true;
        // return super.onJsAlert(view, url, message, result);
    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("对话框")
                .setMessage(message)
                .setPositiveButton("确定",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        result.confirm();
                    }
                })
                .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        result.cancel();
                    }
                });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                result.cancel();
            }
        });
        return true;
    }

    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

        builder.setTitle("对话框").setMessage(message);

        final EditText et = new EditText(view.getContext());
        et.setSingleLine();
        et.setText(defaultValue);
        builder.setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm(et.getText().toString());
                    }

                })
                .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        result.cancel();
                    }
                });
        return true;
    }

    private boolean isSystemLocationEnable() {
        if(mContext != null) {
            LocationManager manager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            boolean gpsLocationEnable = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean networkLocationEnable = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            return gpsLocationEnable || networkLocationEnable;
        }
        return false;
    }

    @Override
    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
//        if(isSystemLocationEnable()) {
//
//            onPermissionRequest(new GeoPermissionRequest(origin, callback));
//            callback.invoke(origin, true, false);
//        }else{
//            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//            if(mContext != null) {
//                mContext.startActivity(intent);
//            }
//
//            onPermissionRequest(new GeoPermissionRequest(origin, callback));
//            callback.invoke(origin, true, false);
//        }
        callback.invoke(origin, true, false);
    }

    @Override
    public void onPermissionRequest(PermissionRequest request) {
//        if (Build.VERSION.SDK_INT >= 23) {
//            int checkPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
//            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
//                ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//                return;
//            }
//        }
    }



    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        String[] mTemp = fileChooserParams.getAcceptTypes();
        if("image/*".equals(mTemp[0])){
            ConversationActivity.handleShowFileChooser(filePathCallback, true);
        }else if("video/*".equals(mTemp[0])){
            ConversationActivity.handleShowFileChooser(filePathCallback, false);
        }
        return true;
    }



    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
        ConversationActivity.handleOpenFileChooser(uploadMsg, true);
    }
    public void openFileChooser(ValueCallback<Uri> uploadMsg,String acceptType) {
        if("image/*".equals(acceptType)){
            ConversationActivity.handleOpenFileChooser(uploadMsg, true);
        }else if("video/*".equals(acceptType)){
            ConversationActivity.handleOpenFileChooser(uploadMsg, false);
        }
    }
    public void openFileChooser(ValueCallback<Uri> uploadMsg,String acceptType, String capture) {
        if("image/*".equals(acceptType)){
            ConversationActivity.handleOpenFileChooser(uploadMsg, true);
        }else if("video/*".equals(acceptType)){
            ConversationActivity.handleOpenFileChooser(uploadMsg, false);
        }
    }

    //add by junwang start
    //work around our wonky API by wrapping a geo permission prompt inside a regular permissionRequest.
    private static class GeoPermissionRequest extends PermissionRequest{
        private String mOrigin;
        private GeolocationPermissions.Callback mCallback;
        private static final String RESOURCE_GEO = "RESOURCE_GEO";

        public GeoPermissionRequest(String origin, GeolocationPermissions.Callback callback){
            mOrigin = origin;
            mCallback = callback;
        }

        public Uri getOrigin(){
            return Uri.parse(mOrigin);
        }

        @Override
        public String[] getResources() {
            return new String[]{this.RESOURCE_GEO};
        }

        public void grant(String[] resources){
            assert resources.length == 1;
            assert this.RESOURCE_GEO.equals(resources[0]);
            mCallback.invoke(mOrigin, true, false);
        }

        public void deny(){
            mCallback.invoke(mOrigin, false, false);
        }
    }
}
