package com.android.messaging.ui.conversation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.android.messaging.datamodel.data.CardTemplate;
import com.android.messaging.product.ui.WebViewNewsActivity;

import java.util.List;
import java.util.Map;

import static com.android.messaging.ui.conversation.ConversationMessageView.SDK_PAY_FLAG;

public class NativeFunctionUtil {
    public static final String URL = "url";
    public static final String TITLE = "title";

    public static void callNativeFunction(int functionNo, Activity activity, String copyText, View targetView, String phoneNumber){
        switch (functionNo){
            case CardTemplate.NativeActionType.PHONE_CALL:
                callNumber(activity, targetView, phoneNumber);
                break;
            case CardTemplate.NativeActionType.SEND_MSG:
                sendSMS(activity, phoneNumber);
                break;
            case CardTemplate.NativeActionType.TAKE_PICTURE:
                takePicture(activity);
                break;
            case CardTemplate.NativeActionType.TAKE_VIDEO:
                takeVideo(activity);
                break;
            case CardTemplate.NativeActionType.COPY:
                copyText(activity, copyText);
                break;
            case CardTemplate.NativeActionType.OPEN_LOCATION:
                break;
            case CardTemplate.NativeActionType.CALENDAR:
                break;
            case CardTemplate.NativeActionType.READ_CONTACT:
                break;
        }
    }

    public static void openLocation(String addrName, double latitude, double longtitude, Activity activity){
        Bundle bundle = new Bundle();
        bundle.putString("Addr", addrName);
        bundle.putDouble("Latitude", latitude);
        bundle.putDouble("Longtitude", longtitude);

        Intent intent = new Intent(activity, BaiduMapTestActivity.class);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    public static void loadUrl(Context context, String url) {
        Intent intent = new Intent(context, WebViewNewsActivity.class);
        intent.putExtra(URL, url);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void loadUrl(Context context, String url, String title) {
        Intent intent = new Intent(context, WebViewNewsActivity.class);
        intent.putExtra(URL, url);
        intent.putExtra(TITLE, title);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void copyText(Context context, String text){
        if((text == null) || text.length() == 0){
            Toast.makeText(context, "复制内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        ClipboardManager cm = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        assert cm != null;
        cm.setPrimaryClip(ClipData.newPlainText("copy", text));
        Toast.makeText(context, "已复制", Toast.LENGTH_SHORT).show();
    }

    public static void callNumber(Activity activity, View targetView, String phoneNumber){
//        Point centerPoint;
//        if (targetView != null) {
//            final int screenLocation[] = new int[2];
//            targetView.getLocationOnScreen(screenLocation);
//            final int centerX = screenLocation[0] + targetView.getWidth() / 2;
//            final int centerY = screenLocation[1] + targetView.getHeight() / 2;
//            centerPoint = new Point(centerX, centerY);
//        } else {
//            // In the overflow menu, just use the center of the screen.
//            final Display display = activity.getWindowManager().getDefaultDisplay();
//            centerPoint = new Point(display.getWidth() / 2, display.getHeight() / 2);
//        }
//        UIIntents.get().launchPhoneCallActivity(activity, phoneNumber, centerPoint);
        Uri uri = Uri.parse("tel:"+phoneNumber);
        Intent intent = new Intent(Intent.ACTION_DIAL,uri);
        activity.startActivity(intent);
    }

    public static void sendSMS(Activity activity, String number){
        Uri uri = Uri.parse("smsto:"+number);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", "");
        activity.startActivity(intent);
    }

    /**
     * 启动第三方apk
     *
     * 如果已经启动apk，则直接将apk从后台调到前台运行（类似home键之后再点击apk图标启动），如果未启动apk，则重新启动
     */
    public static void launchAPK(Context context, String packageName) {
        Intent intent = getAppOpenIntentByPackageName(context, packageName);
        context.startActivity(intent);
    }

    public static Intent getAppOpenIntentByPackageName(Context context, String packageName) {
        String mainAct = null;
        PackageManager pkgMag = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_NEW_TASK);

        @SuppressLint("WrongConstant") List<ResolveInfo> list = pkgMag.queryIntentActivities(intent,
                PackageManager.GET_ACTIVITIES);
        for (int i = 0; i < list.size(); i++) {
            ResolveInfo info = list.get(i);
            if (info.activityInfo.packageName.equals(packageName)) {
                mainAct = info.activityInfo.name;
                break;
            }
        }
        if (TextUtils.isEmpty(mainAct)) {
            return null;
        }
        intent.setComponent(new ComponentName(packageName, mainAct));
        return intent;
    }

    /**
     * @param orderInfo 接口返回的订单信息
     */
    public static void callAlipay(final Activity activity, final String orderInfo, Handler handler) {

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(activity);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                if(handler != null) {
                    Message msg = new Message();
                    msg.what = SDK_PAY_FLAG;
                    msg.obj = result;
                    handler.sendMessage(msg);
                }
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    public static void takePicture(Activity activity){
        // 打开拍照程序
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        activity.startActivityForResult(intent, 1);
    }

    public static void takeVideo(Activity activity){
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        activity.startActivityForResult(intent, 1);
    }
}
