package com.android.messaging.ui.santipopupmenu;

import android.app.Activity;
import android.app.Dialog;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.messaging.R;
import com.android.messaging.util.LogUtil;

import java.util.List;

public class CustomSelectDialog extends Dialog {

    private SelectDialogListener mListener;
    private SelectDialogCancelListener mCancelListener;

    private Activity mActivity;
    private List<String> mName;
    private String mTitle;
    private boolean mUseCustomColor = false;
    private int mFirstItemColor;
    private int mOtherItemColor;
    //add by junwang
    private float mOffsetX;
    private int mButtonWidth;
    private int mButtonHeight;
    private ListView dialogList;
    private View mView;

    /**
     * 设置item监听事件
     */
    public interface SelectDialogListener {
        /**
         * 条目点击事件
         * @param parent                    parent
         * @param view                      view
         * @param position                  索引
         * @param id                        id
         */
        void onItemClick(AdapterView<?> parent, View view, int position, long id);
    }

    /**
     * 取消事件监听接口
     */
    public interface SelectDialogCancelListener {
        /**
         * 取消按钮点击事件
         * @param v                         view
         */
        void onCancelClick(View v);
    }

    /**
     * @param activity          调用弹出菜单的activity
     * @param theme             主题
     * @param listener          菜单项单击事件
     * @param names             菜单项名称
     */
    public CustomSelectDialog(Activity activity, int theme, SelectDialogListener listener,
                              List<String> names) {
        super(activity, theme);
        mActivity = activity;
        mListener = listener;
        this.mName=names;
        setCanceledOnTouchOutside(true);
    }

    /**
     * @param activity          调用弹出菜单的activity
     * @param theme             主题
     * @param listener          菜单项单击事件
     * @param cancelListener    取消事件
     * @param names             菜单项名称
     */
    public CustomSelectDialog(Activity activity, int theme, SelectDialogListener listener,
                              SelectDialogCancelListener cancelListener , List<String> names) {
        super(activity, theme);
        mActivity = activity;
        mListener = listener;
        mCancelListener = cancelListener;
        this.mName=names;
        // 设置是否点击外围不解散
        setCanceledOnTouchOutside(false);
    }

    /**
     * @param activity          调用弹出菜单的activity
     * @param theme             主题
     * @param listener          菜单项单击事件
     * @param names             菜单项名称
     * @param title             菜单标题文字
     *
     */
    public CustomSelectDialog(Activity activity, int theme, SelectDialogListener listener,
                              List<String> names, String title) {
        super(activity, theme);
        mActivity = activity;
        mListener = listener;
        this.mName=names;
        mTitle = title;
        // 设置是否点击外围可解散
        setCanceledOnTouchOutside(true);
    }

    /**
     * @param activity          调用弹出菜单的activity
     * @param theme             主题
     * @param listener          菜单项单击事件
     * @param cancelListener    取消点击事件
     * @param names             名称
     * @param title             标题
     */
    public CustomSelectDialog(Activity activity, int theme, SelectDialogListener listener,
                              SelectDialogCancelListener cancelListener, List<String> names,
                              String title) {
        super(activity, theme);
        mActivity = activity;
        mListener = listener;
        mCancelListener = cancelListener;
        this.mName=names;
        mTitle = title;
        // 设置是否点击外围可解散
        setCanceledOnTouchOutside(true);
    }

    /**
     * 类似于Activity的onCreate函数，可以在这个方法中进行Dialog的一些初始化操作
     * 包括调用setContentView方法
     * @param savedInstanceState                savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtil.i("CustomSelectDialog", "onCreate");
        super.onCreate(savedInstanceState);
        mView = getLayoutInflater().inflate(R.layout.view_dialog_select, null);
//        this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setContentView(mView, params);
        initViews();
    }

    /**
     * 当对话框启动的时候被调用
     */
    @Override
    protected void onStart() {
        LogUtil.i("CustomSelectDialog", "onStart");
        super.onStart();
        //设置弹窗的位置时在底部
        Window window = getWindow();
        // 设置显示动画
        if (window != null) {
            window.setWindowAnimations(R.style.BottomDialogAnimationStyle);
//            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.setDimAmount(0f);
            window.setGravity(Gravity.BOTTOM);
            WindowManager.LayoutParams wl = window.getAttributes();
            //设置相对于屏幕中心点的偏移量
//            wl.x = 0;
//            wl.y = 0;
            wl.x = (int)(mActivity.getWindowManager().getDefaultDisplay().getWidth()*mOffsetX);
            LogUtil.i("CustomSelectDialog", "wl.x="+wl.x+", width="+mActivity.getWindowManager().getDefaultDisplay().getWidth());
            mView.measure(mButtonWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
            wl.y =  mButtonHeight-16;
//                    - mView.getMeasuredHeight()*mName.size()/2 - getNavigationBarHeight(mActivity);
            LogUtil.i("CustomSelectDialog", "mButtonHeight="+mButtonHeight+", wl.y="+wl.y+", view_height="
                    +mView.getMeasuredHeight()+", window_height="+mActivity.getWindowManager().getDefaultDisplay().getHeight());
            // 以下这两句是为了保证按钮可以水平满屏
//            wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
            wl.width = mButtonWidth;
//            wl.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            // 设置显示位置
            onWindowAttributesChanged(wl);
        }
    }



    //add by junwang start
    public void setPopupMenuLayoutParams(float x, int width, int height){
        mOffsetX = x;
        mButtonHeight = height;
        mButtonWidth = width;
    }
    //add by junwang end

    /**
     * 当对话框停止的时候被调用
     */
    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void show() {
        if (mActivity!=null && mActivity.isFinishing() ){
            return;
        }
        if (isShowing()){
            return;
        }
        super.show();
    }

//    @Override
//    public boolean onTouchEvent(@NonNull MotionEvent event) {
//        LogUtil.i("Junwang", "CustomSelectDialog onTouchEvent RawX="+event.getRawX()+
//                ", x="+event.getX()+", RawY="+event.getRawY()+", y="+event.getY());
//
//        if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
//            LogUtil.i("Junwang", "CustomSelectDialog ACTION_OUTSIDE");
//            dismiss();
//            return true;
//        }
//
//        // Delegate everything else to Activity.
//        return super.onTouchEvent(event);
////        return true;
//    }



    private void initViews() {
        DialogAdapter dialogAdapter = new DialogAdapter(mName);
        /*ListView*/ dialogList= findViewById(R.id.dialog_list);
        Button mMBtnCancel = findViewById(R.id.mBtn_Cancel);
        TextView mTvTitle = findViewById(R.id.mTv_Title);

        dialogList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListener.onItemClick(parent, view, position, id);
                dismiss();
            }
        });
        dialogList.setAdapter(dialogAdapter);
        mMBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCancelListener != null){
                    mCancelListener.onCancelClick(v);
                }
                dismiss();
            }
        });

        if(!TextUtils.isEmpty(mTitle)){
            mTvTitle.setVisibility(View.VISIBLE);
            mTvTitle.setText(mTitle);
        }else{
            mTvTitle.setVisibility(View.GONE);
        }
    }

    private class DialogAdapter extends BaseAdapter {

        private List<String> mStrings;
        private ViewHolder viewholder;
        private LayoutInflater layoutInflater;

        DialogAdapter(List<String> strings) {
            this.mStrings = strings;
            this.layoutInflater = mActivity.getLayoutInflater();
        }

        @Override
        public int getCount() {
            return mStrings.size();
        }

        @Override
        public Object getItem(int position) {
            return mStrings.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                viewholder=new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.view_dialog_item, null);
                viewholder.tvDialog= convertView.findViewById(R.id.dialog_item_bt);
                convertView.setTag(viewholder);
            }else{
                viewholder=(ViewHolder) convertView.getTag();
            }
            viewholder.tvDialog.setText(mStrings.get(position));
            viewholder.tvDialog.setTextSize(14);
            if (!mUseCustomColor) {
                mFirstItemColor = mActivity.getResources().getColor(R.color.grayText);
                mOtherItemColor = mActivity.getResources().getColor(R.color.grayText);
            }
            if (1 == mStrings.size()) {
                viewholder.tvDialog.setTextColor(mFirstItemColor);
                viewholder.tvDialog.setBackgroundResource(R.drawable.shape_dialog_item_bg_only);
            } else if (position == 0) {
                viewholder.tvDialog.setTextColor(mFirstItemColor);
                viewholder.tvDialog.setBackgroundResource(R.drawable.select_dialog_item_bg_top);
            } else if (position == mStrings.size() - 1) {
                viewholder.tvDialog.setTextColor(mOtherItemColor);
                viewholder.tvDialog.setBackgroundResource(R.drawable.select_dialog_item_bg_buttom);
            } else {
                viewholder.tvDialog.setTextColor(mOtherItemColor);
                viewholder.tvDialog.setBackgroundResource(R.drawable.select_dialog_item_bg_center);
            }
            return convertView;
        }
    }


    public static class ViewHolder {
        TextView tvDialog;
    }


    /**
     * 设置列表项的文本颜色
     */
    public void setItemColor(int firstItemColor, int otherItemColor) {
        mFirstItemColor = firstItemColor;
        mOtherItemColor = otherItemColor;
        mUseCustomColor = true;
    }

    /**
     * 判断是否有NavigationBar
     *
     * @param activity
     * @return
     */
    public static boolean checkHasNavigationBar(Activity activity) {
        WindowManager windowManager = activity.getWindowManager();
        Display d = windowManager.getDefaultDisplay();

        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            d.getRealMetrics(realDisplayMetrics);
        }

        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);

        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
    }

    /**
     * 获得NavigationBar的高度
     */
    public static int getNavigationBarHeight(Activity activity) {
        int result = 0;
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0 && checkHasNavigationBar(activity)) {
            result = resources.getDimensionPixelSize(resourceId);
        }
        return result;
    }

}
