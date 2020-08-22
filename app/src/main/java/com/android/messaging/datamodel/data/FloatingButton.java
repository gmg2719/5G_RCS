package com.android.messaging.datamodel.data;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.messaging.R;
import com.android.messaging.util.LogUtil;
import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.PermissionListener;
import com.yhao.floatwindow.Screen;
import com.yhao.floatwindow.ViewStateListener;

public class FloatingButton {

    public static Button popupFloatingWindow(Context context, String url, float widthRatio, float heightRatio,
                                             float startXRatio, float startYRatio, int moveType, String btText,
                                             ViewStateListener vsl, PermissionListener pl) {
        Button bt = new Button(context);
        bt.setText(btText);

        if (FloatWindow.get(btText) == null) {
            FloatWindow
                    .with(context)
                    .setView(bt)
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
                    .setDesktopShow(true)                        //桌面显示
                    .setViewStateListener(/*mViewStateListener*/null)    //监听悬浮控件状态改变
                    .setPermissionListener(/*mPermissionListener*/null)  //监听权限申请结果
                    .setMoveType(moveType)
                    .setTag(btText)
                    .build();
        }
        if (!FloatWindow.get(btText).isShowing()) {
            FloatWindow.get(btText).show();
        }
        return bt;
    }

    public static Button popupFloatingWindow(Context context, String url, int width, int height,
                                             int startX, int startY, int moveType, String btText,
                                             ViewStateListener vsl, PermissionListener pl) {
        Button bt = new Button(context);
        if(null != btText) {
            bt.setText(btText);
        }else{
            bt.setText("百度地图");
        }

        LogUtil.i("Junwang", "width="+bt.getWidth()+", measuredWidth="+bt.getMeasuredWidth());

        if (FloatWindow.get(btText) == null) {
            FloatWindow
                    .with(context)
                    .setView(bt)
//                    .setMoveStyle(3000, new TimeInterpolator() {
//                        @Override
//                        public float getInterpolation(float input) {
//                            return (float)(Math.cos((input + 1) * Math.PI) / 2.0f) + 0.5f;
//                        }
//                    })
                    .setWidth(/*width*/ViewGroup.LayoutParams.WRAP_CONTENT)                               //设置控件宽高
                    .setHeight(height)
                    .setX(startX)                                   //设置控件初始位置
                    .setY(startY)
                    .setDesktopShow(true)                        //桌面显示
                    .setViewStateListener(/*mViewStateListener*/null)    //监听悬浮控件状态改变
                    .setPermissionListener(/*mPermissionListener*/null)  //监听权限申请结果
                    .setTag(btText)
                    .build();
            bt.setBackgroundResource(R.drawable.button_circle);
//            bt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
//            bt.setTextColor(Color.BLUE);
            bt.setTextColor(Color.parseColor("#344AE3"));
            bt.getPaint().setFakeBoldText(true);
            bt.setPadding(1,1,1,1);
//            ViewGroup.LayoutParams lp =  new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        }
        if (!FloatWindow.get(btText).isShowing()) {
            FloatWindow.get(btText).show();
        }
        return bt;
    }

    public static void hideButton(String tag) {
        if (FloatWindow.get(tag) != null) {
            FloatWindow.get(tag).hide();
            FloatWindow.destroy();
        }
    }
}
