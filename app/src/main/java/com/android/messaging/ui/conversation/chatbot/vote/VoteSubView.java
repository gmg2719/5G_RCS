package com.android.messaging.ui.conversation.chatbot.vote;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.messaging.R;
import com.android.messaging.util.LogUtil;

import java.text.NumberFormat;

/**
 * 投票的子 view
 */
public class VoteSubView extends LinearLayout implements VoteObserver {

    private ProgressBar progressBar;

    private TextView contentView;

    private TextView numberView;

    private ImageView selectedImage;

    private int mTotalNumber = 1;

    private int mCurrentNumber = 1;

    private AnimatorSet animatorSet;


    public static boolean HasVoted = false;

    public VoteSubView(Context context) {
        this(context, null);
    }

    public VoteSubView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VoteSubView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(getContext(), R.layout.vote_sub_view, this);
        initView();
//        if(false/*has Voted*/){
//            setChildrenView(false);
//        }
        setChildrenView(false);
        initAnimation();
    }

    private void initView() {
        progressBar = findViewById(R.id.progress_view);
        contentView = findViewById(R.id.name_text_view);
        numberView = findViewById(R.id.number_text_view);
        selectedImage = findViewById(R.id.selected_image);
//        numberView.setAlpha(0.0f);
    }

    public void setContent(String content) {
        contentView.setText(content);
    }

    public void setNumber(int number) {
        mCurrentNumber = number;
        numberView.setText(number+"");
    }

    private void initAnimation() {
        animatorSet = new AnimatorSet();
//        Animator[] arrayAnimator = new Animator[2];
//        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(contentView, "x", 30);
//        arrayAnimator[0] = objectAnimator;
//        objectAnimator = ObjectAnimator.ofFloat(numberView, "alpha", 0.0f);
//        arrayAnimator[1] = objectAnimator;
//        animatorSet.playTogether(arrayAnimator);
//        animatorSet.setDuration(VoteView.mAnimationRate);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(numberView, "alpha", 0.0f, 1.0f);
        animatorSet.play(alpha);//.before(animator);
        animatorSet.setDuration(VoteView.mAnimationRate);
    }

    @Override
    public void setSelected(boolean selected) {
        LogUtil.i("Junwang", "setSelected enter selected="+selected);
        super.setSelected(selected);
        setChildViewStatus(selected);
        if (selected) {
            start();
        } else {
            cancel();
        }
    }

    public void start() {
        post(new Runnable() {
            @Override
            public void run() {
                animatorSet.start();
            }
        });
    }

    public void cancel() {
        post(new Runnable() {
            @Override
            public void run() {
                animatorSet.cancel();
            }
        });
    }

    public void updateChildViewStatus(boolean isSelected){
        LogUtil.i("Junwang", "setChildViewStatus enter isSelected"+isSelected);
        progressBar.post(new Runnable() {
            @Override
            public void run() {
                progressBarAnimation(progressBar, mCurrentNumber, mTotalNumber);
            }
        });
        if (isSelected) {
            contentView.setTextColor(Color.parseColor("#FFFFFF"));
        } else {
            contentView.setTextColor(Color.parseColor("#8D9799"));
        }
    }

    public void setChildViewStatus(boolean isSelected) {
        LogUtil.i("Junwang", "setChildViewStatus enter isSelected"+isSelected);
        if (isSelected) {
            progressBar.post(new Runnable() {
                @Override
                public void run() {
                    progressBarAnimation(progressBar, mCurrentNumber, mTotalNumber);
                }
            });
//            numberView.setVisibility(VISIBLE);
//            numberView.setAlpha(0.0f);
//            selectedImage.setVisibility(View.VISIBLE);
            contentView.setTextColor(Color.parseColor("#4F7BFF"));
        } else {
//            selectedImage.setVisibility(View.GONE);
            contentView.setTextColor(Color.parseColor("#8D9799"));
//            progressBar.setProgress(0);
//            numberView.setVisibility(GONE);
//            contentView.setTextColor(Color.parseColor("#8D9799"));
//            contentView.setCompoundDrawables(null, null, null, null);
//            contentView.animate().translationX(0).setDuration(VoteView.mAnimationRate).start();

        }
    }

    @Override
    public void update(View view, boolean status, boolean isRefresh) {
        LogUtil.i("Junwang", "update enter status = "+status);
        selectedImage.setVisibility(View.GONE);
        changeChildrenViewStatus(((int) view.getTag()) == getCurrentIndex());
        if (((int) view.getTag()) == getCurrentIndex()) {
            Log.e("update", "当前被点选的是:" + getCurrentIndex());
            if (status) {
                VoteView.selectedItemPosition = getCurrentIndex();
                if(!isRefresh) {
                    mCurrentNumber += 1;
                }
//                mTotalNumber += 1;
                numberView.setText(mCurrentNumber+"");
            }
        }
        if(!isRefresh) {
            mTotalNumber += 1;
        }
        super.setSelected(status);
        if(((int) view.getTag()) == getCurrentIndex()){
            updateChildViewStatus(true);
//            setSelected(true);
        }else {
            updateChildViewStatus(false);
//            setSelected(false);
        }
        start();
    }

    public void setVotedStatus(boolean status){
        changeChildrenViewStatus(status);
        if (status) {
            mCurrentNumber += 1;
            mTotalNumber += 1;
            numberView.setText(mCurrentNumber+"");
            progressBar.setProgress(mCurrentNumber*100/mTotalNumber);
//            progressBar.post(new Runnable() {
//                @Override
//                public void run() {
//                    progressBarAnimation(progressBar, mCurrentNumber, mTotalNumber);
//                }
//            });
            numberView.setVisibility(VISIBLE);
            numberView.setAlpha(0.0f);
        }else {
            progressBar.setProgress(0);
            numberView.setVisibility(GONE);
            contentView.setTextColor(Color.parseColor("#8D9799"));
            contentView.setCompoundDrawables(null, null, null, null);
            contentView.animate().translationX(0).setDuration(VoteView.mAnimationRate).start();

        }
    }


    @Override
    public void setTotalNumber(int totalNumber) {
        LogUtil.i("Junwang", "totalNumber="+totalNumber);
        mTotalNumber = totalNumber;
        if(mTotalNumber == 0){
            progressBar.setProgress(0);
        }else {
            progressBar.setProgress(mCurrentNumber / mTotalNumber);
        }
    }

    private void progressBarAnimation(final ProgressBar progressBar, int current, int total) {
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(3);
        float result = ((float) current / (float) total) * 100;
        Log.e("progressBarAnimation", "result" + Math.ceil(result));
        ValueAnimator animator = ValueAnimator.ofInt(0, (int) Math.ceil(result)).setDuration(VoteView.mAnimationRate);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                progressBar.setProgress((int) valueAnimator.getAnimatedValue());
            }
        });
        animator.start();
    }

    private int getCurrentIndex() {
        return (int) getTag();
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    @Override
    public void updateProgressBarBorder(View view){
        LogUtil.i("Junwang", "updateProgressBarBorder enter");
        boolean status = ((int) view.getTag()) == getCurrentIndex();
        LogUtil.i("Junwang", "updateProgressBarBorder status="+status);
        //选中文字颜色
        contentView.setTextColor(Color.parseColor(status ? /*"#00C0EB"*/"#4F7BFF" : "#8D9799"));
        selectedImage.setVisibility(status ? View.VISIBLE : View.GONE);
        if(status){
            super.setSelected(true);
        }
        //带勾选框
//        Drawable right = getResources().getDrawable(status ? R.drawable.vote_selected : R.drawable.vote_empty);
//        right.setBounds(0, 0, right.getIntrinsicWidth(), right.getIntrinsicHeight());
//        contentView.setCompoundDrawables(null, null, right, null);
        if(mTotalNumber == 0){
            progressBar.setProgress(0);
        }else {
            progressBar.setProgress(mCurrentNumber / mTotalNumber);
        }
        progressBar.setBackgroundResource(status ? R.drawable.select_bg : R.drawable.unselect_bg);
        setBackgroundResource(status ? R.drawable.select_bg : R.drawable.unselect_bg);
        requestFocus();
        //进度条颜色设置
//        progressBar.setProgressDrawable(getResources().getDrawable(status ? R.drawable.select_progress_view_bg : R.drawable.unselect_progress_view_bg));
//        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
//        params.setMargins(0, 0, 0, 0);
//        setLayoutParams(params);
//        setBackgroundResource(status ? R.drawable.select_bg : R.drawable.unselect_bg);
//        params.setMargins(0, 16, 0, 16);
//        setLayoutParams(params);
//        requestFocus();
    }

    private void changeChildrenViewStatus(boolean status) {
        LogUtil.i("Junwang", "changeChildrenViewStatus enter");
        //选中文字颜色
        contentView.setTextColor(Color.parseColor(status ? /*"#00C0EB"*/"#FFFFFF" : "#8D9799"));
        //数字颜色
        numberView.setTextColor(Color.parseColor(/*status ? "#00C0EB" :*/ "#8D9799"));
        //带勾选框
//        Drawable right = getResources().getDrawable(status ? R.drawable.vote_selected : R.drawable.vote_empty);
//        right.setBounds(0, 0, right.getIntrinsicWidth(), right.getIntrinsicHeight());
//        contentView.setCompoundDrawables(null, null, right, null);
        //进度条颜色设置
        progressBar.setProgressDrawable(getResources().getDrawable(status ? R.drawable.select_progress_view_bg : R.drawable.unselect_progress_view_bg));

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
//        params.setMargins(0, 0, 0, 0);
//        setLayoutParams(params);
        setBackgroundResource(status ? R.drawable.select_bg : R.drawable.unselect_bg);
//        params.setMargins(0, 16, 0, 16);
//        setLayoutParams(params);
    }

    public void setChildrenView(boolean status){
        LogUtil.i("Junwang", "setChildrenView enter");
//        contentView.setGravity(Gravity.LEFT);
//        contentView.requestLayout();
//        numberView.setAlpha(0.0f);
        //选中文字颜色
        contentView.setTextColor(Color.parseColor(status ? /*"#00C0EB"*/"#4F7BFF" : "#8D9799"));
        //数字颜色
//        numberView.setTextColor(Color.parseColor(/*status ? "#00C0EB"*/"#8D9799"));
//        progressBar.setProgress(mCurrentNumber/mTotalNumber);
        //进度条颜色设置
        progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.unselect_progress_view_bg));
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
//        params.setMargins(0, 0, 0, 0);
//        setLayoutParams(params);
        setBackgroundResource(R.drawable.unselect_bg);
//        params.setMargins(0, 16, 0, 16);
//        setLayoutParams(params);
//        invalidate();
    }

    @Override
    public int getSelectedItem() {
        return 0;
    }
}
