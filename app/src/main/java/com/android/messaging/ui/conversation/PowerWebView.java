package com.android.messaging.ui.conversation;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.webkit.WebView;

import com.android.messaging.util.LogUtil;

public class PowerWebView extends WebView {
//    private boolean mIsTextInput = false;
    private boolean mIsIframeToButtom = false;
    float mDownY;
    public PowerWebView(Context context) {
        super(context);
    }

    public PowerWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PowerWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PowerWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setIframeState(boolean state){
        mIsIframeToButtom = state;
    }

    public boolean getIframeState(){
        LogUtil.i("Junwang", "aaaaaaaaaaaaaaaamIsIframeToButtom="+mIsIframeToButtom);
        return mIsIframeToButtom;
    }

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent event) {
//        int action = event.getActionMasked();
//        float webcontent = this.getContentHeight() * this.getScaleY();
//        if(action == MotionEvent.ACTION_DOWN){
//            mDownY = event.getY();
//            getParent().requestDisallowInterceptTouchEvent(true);
////            if(is) {
////                ((LinearLayout) getParent()).setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
////            }else{
////                ((LinearLayout) getParent()).setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
////            }
//            return false;
//        }else if(action == MotionEvent.ACTION_MOVE){
//            float webnow = getHeight() + getScrollY();
//            if((!canScrollVertically(1) && event.getY()<mDownY) /*&& Math.abs(webnow - webcontent) < 1*/){
//                getParent().requestDisallowInterceptTouchEvent(false);
//                return false;
//            }else if(!canScrollVertically(-1) && (event.getY()>mDownY) && getScrollY() == 0){
//                getParent().requestDisallowInterceptTouchEvent(false);
//                return false;
//            }else{
//                return false;
//            }
//            //return true;
//        }else if(event.getAction() == MotionEvent.ACTION_UP){
//            //return true;
//            //requestDisallowInterceptTouchEvent(true);
//        }
//        return false;
//        //return super.onInterceptTouchEvent(ev);
//    }

//    @Override
//    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
//        mIsTextInput = true;
//        return super.onCreateInputConnection(outAttrs);
//    }


    @Override
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {

//        if(/*mIsTextInput*/true){
//            scrollTo(0, 0);
//            return super.requestFocus(direction, previouslcusedRect);
//        }
//        return true;
        LogUtil.i("Junwang", "scroll to 0, 0");
        scrollTo(0, 0);
        return super.requestFocus(direction, previouslyFocusedRect);
    }

//    @Override
//    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
//        LogUtil.i("Junwang", "deltaX="+deltaX+", deltaY="+deltaY+", scrollX="+scrollX+", scrollY="+scrollY
//            +", maxOverScrollX="+maxOverScrollX+",maxOverScrollY="+maxOverScrollY);
//        boolean canScrollUp = canScrollVertically(1);
//        boolean canScrollDown = canScrollVertically(-1);
//
//        int offset = computeVerticalScrollOffset();
//        int i = getScrollIndicators();
//        int r = computeVerticalScrollRange();
//        int e = computeVerticalScrollExtent();
//        int range = r - e;
//        LogUtil.i("Junwang", "canScrollUp="+canScrollUp+", canScrollDown="
//                +canScrollDown+", i="+i+", ScrollOffset="+offset+", range="+range);
//        LogUtil.i("Junwang", "ScrollRange="+r+", ScrollExtent="+e);
//        if(deltaY<0){
//
//        }
//        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
//    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        LogUtil.i("Junwang", "scrollX="+scrollX+", scrollY="+scrollY+", clampedX="+clampedX+", clampedY="+clampedY);
        if(scrollY == 0){
            setIframeState(true);
        }
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    }

//    @Override
//    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
//        LogUtil.i("Junwang", "onScrollChanged enter");
//        boolean canScrollUp = canScrollVertically(1);
//        boolean canScrollDown = canScrollVertically(-1);
//
//        int offset = computeVerticalScrollOffset();
//        int i = getScrollIndicators();
//        int r = computeVerticalScrollRange();
//        int e = computeVerticalScrollExtent();
//        int range = r - e;
//        LogUtil.i("Junwang", "canScrollUp="+canScrollUp+", canScrollDown="
//                +canScrollDown+", i="+i+", ScrollOffset="+offset+", range="+range);
//        LogUtil.i("Junwang", "ScrollRange="+r+", ScrollExtent="+e);
//        super.onScrollChanged(l, t, oldl, oldt);
//    }

    //    @Override
//    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
//        if(!canGoForward()){
//            ((LinearLayout)getParent()).setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
//            return true;
//        }
//        return super.requestFocus(direction, previouslyFocusedRect);
//    }


    //    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        requestDisallowInterceptTouchEvent(true);
//        if(event.getAction() == MotionEvent.ACTION_UP){
//            this.getParent().requestDisallowInterceptTouchEvent(false);
//        }else{
//            this.getParent().requestDisallowInterceptTouchEvent(true);
//        }
//        return super.onTouchEvent(event);
//    }
//
//    @Override
//    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
//        // webview的高度
//        float webcontent = this.getContentHeight() * this.getScaleY();
//        // 当前webview的高度
//        this.getParent().requestDisallowInterceptTouchEvent(true);
//
//        float webnow = getHeight() + getScrollY();
//        if (!canScrollVertically(1) &&
//                t>oldt && (Math.abs(webcontent - webnow) < 1)) {
//            //处于底端
//            this.getParent().requestDisallowInterceptTouchEvent(false);
//        } else if (!canScrollVertically(-1)
//                && (t <= oldt) &&getScrollY() == 0) {
//            //处于顶端
//            this.getParent().requestDisallowInterceptTouchEvent(false);
//        }
//        super.onScrollChanged(l, t, oldl, oldt);
//    }


}
