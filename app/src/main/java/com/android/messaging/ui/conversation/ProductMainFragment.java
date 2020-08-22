package com.android.messaging.ui.conversation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.messaging.R;
import com.android.messaging.product.utils.GlideUtils;
import com.yc.cn.ycbannerlib.banner.BannerView;
import com.yc.cn.ycbannerlib.banner.adapter.AbsDynamicPagerAdapter;
import com.yc.cn.ycbannerlib.banner.hintview.TextHintView;

import java.util.ArrayList;

public class ProductMainFragment extends Fragment {

    private ScrollView mScrollView;
    private TextView mTvGoodsTitle;
    private TextView mTvNewPrice;
    private TextView mTvOldPrice;
    private LinearLayout mLlActivity;
    private LinearLayout mLlCurrentGoods;
    private TextView mTvCurrentGoods;
    private TextView mIvEnsure;
    private LinearLayout mLlComment;
    private TextView mTvCommentCount;
    private TextView mTvGoodComment;
    private ImageView mIvCommentRight;
    private LinearLayout mLlEmptyComment;
    private LinearLayout mLlRecommend;
    private TextView mTvBottomView;
    private BannerView mBanner;
    private ArrayList<String> imgs=null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getContentView(), container , false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private int getContentView() {
        return R.layout.product_main_fragment;
    }


    private void initView(View view) {
        mScrollView = view.findViewById(R.id.scrollView);
        mTvGoodsTitle = view.findViewById(R.id.tv_goods_title);
        mTvNewPrice = view.findViewById(R.id.tv_min_price);
        mTvOldPrice = view.findViewById(R.id.tv_max_price);
        mLlCurrentGoods = view.findViewById(R.id.ll_current_goods);
        mTvCurrentGoods = view.findViewById(R.id.tv_current_goods);
        mIvEnsure = view.findViewById(R.id.iv_ensure);
        mLlComment = view.findViewById(R.id.ll_comment);
        mTvCommentCount = view.findViewById(R.id.tv_comment_count);
        mTvGoodComment = view.findViewById(R.id.tv_good_comment);
        mIvCommentRight = view.findViewById(R.id.iv_comment_right);
        mLlEmptyComment = view.findViewById(R.id.ll_empty_comment);
        mLlRecommend = view.findViewById(R.id.ll_recommend);
        mTvBottomView = view.findViewById(R.id.tv_bottom_view);
        initBanner(view);
    }

    private void initBanner(View view) {
        mBanner = view.findViewById(R.id.banner);
        //设置轮播时间
        mBanner.setPlayDelay(2000);
        imgs = new ArrayList<>();
        imgs.add("http://p6-tt.byteimg.com/img/pgc-image/RfEcDnxIDNhSD5~tplv-tt-cs0:300:196.webp");
        imgs.add("http://p6-tt.byteimg.com/img/pgc-image/RfEcDoUGAwo6DM~tplv-tt-cs0:300:196.webp");
        imgs.add("http://p1-tt.byteimg.com/img/pgc-image/RfEcDohGjy4cdO~tplv-tt-cs0:300:196.webp");
        //设置轮播图适配器，必须
        mBanner.setAdapter(new ImageNormalAdapter());
        //设置位置
        mBanner.setHintGravity(1);
        //设置指示器样式
        mBanner.setHintMode(BannerView.HintMode.TEXT_HINT);
        mBanner.setHintView(new TextHintView(getContext()));
        //判断轮播是否进行
        boolean playing = mBanner.isPlaying();
        //轮播图点击事件
        mBanner.setOnBannerClickListener(new BannerView.OnBannerClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(getContext(),position+"被点击呢",Toast.LENGTH_SHORT).show();
            }
        });
        //轮播图滑动事件
        mBanner.setOnPageListener(new BannerView.OnPageListener() {
            @Override
            public void onPageChange(int position) {

            }
        });
    }

    private class ImageNormalAdapter extends AbsDynamicPagerAdapter {

        @Override
        public View getView(ViewGroup container, int position) {
            ImageView view = new ImageView(container.getContext());
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
//            view.setImageURI(Uri.parse(imgs.get(position)));
            GlideUtils.load(getContext(), imgs.get(position), view);
            return view;
        }

        @Override
        public int getCount() {
//            return imgs.length;
            return imgs.size();
        }
    }

//    private class ImageNormalAdapter extends AbsStaticPagerAdapter {
//
//        @Override
//        public View getView(ViewGroup container, int position) {
//            ImageView view = new ImageView(container.getContext());
//            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.MATCH_PARENT));
////            view.setImageResource(imgs[position]);
//            view.setImageURI(Uri.parse(imgs.get(position)));
//            return view;
//        }
//
//        @Override
//        public int getCount() {
////            return imgs.length;
//            return imgs.size();
//        }
//    }


    public void changBottomView(boolean isDetail) {
        if(isDetail){
            mTvBottomView.setText("下拉回到商品详情");
        }else {
            mTvBottomView.setText("继续上拉，查看图文详情");
        }
    }
}
