package com.android.messaging.product.entity;

import android.text.TextUtils;

import com.android.messaging.product.utils.ListUtils;
import com.google.gson.Gson;

import java.util.List;

public class H5CardItem {
    public static class ItemType{
        /**
         * 纯文字布局(文章、广告)
         */
        public static final int TEXT_PLAIN = 1;
        /**
         * 居中大图布局(1.单图文章；2.单图广告；3.视频，中间显示播放图标，右侧显示时长)
         */
        public static final int CENTER_SINGLE_PIC = 2;
        /**
         * 右侧小图布局(1.小图新闻；2.视频类型，右下角显示视频时长)
         */
        public static final int RIGHT_PIC_VIDEO = 3;
        /**
         * 三张图片布局(文章、广告)
         */
        public static final int THREE_PICS = 4;

        public static final int ZHIBO = 5;

        public static final int ONE_BUTTON = 6;

        public static final int TWO_BUTTON = 7;

        public static final int THREE_BUTTON = 8;

        public static final int CENTER_SINGLE_VIDEO_NEWS = 9;
    }


    public int card_item_type;
    public String title;
    public int image_count;
    public int video_style;
    public String url;
    public boolean has_image;
    public boolean has_video;
    public int video_duration;
    public String video_path;
    public ImageEntity middle_image;
    public List<ImageEntity> image_list;
    public List<H5CardItem> items_list;

    public static int getViewType(H5CardItem item) {
        if (item.has_video) {
            //如果有视频
            if (item.video_style == 0) {
                //右侧视频
                if (item.middle_image == null || TextUtils.isEmpty(item.middle_image.url)) {
                    return ItemType.TEXT_PLAIN;
                }
                return ItemType.RIGHT_PIC_VIDEO;
            } else if (item.video_style == 2) {
                //居中视频
                return ItemType.CENTER_SINGLE_VIDEO_NEWS;
            }
            return ItemType.CENTER_SINGLE_VIDEO_NEWS;
//            return ZHIBO_NEWS;
        } else {
            if(item.card_item_type == 6){
                if(item.items_list != null){
                    int i = item.items_list.size();
                    if(i == 1){
                        return ItemType.ONE_BUTTON;
                    }else if(i == 2){
                        return ItemType.TWO_BUTTON;
                    }else if(i == 3){
                        return ItemType.THREE_BUTTON;
                    }
                }
            }
            //非视频新闻
            else if (!item.has_image) {
                //纯文字新闻
                return ItemType.TEXT_PLAIN;
            } else {
                if (ListUtils.isEmpty(item.image_list)) {
                    //图片列表为空，则是右侧图片
                    return ItemType.RIGHT_PIC_VIDEO;
                }

                if (item.image_count == 3) {
                    //图片数为3，则为三图
                    return ItemType.THREE_PICS;
                }

                //中间大图，右下角显示图数
                return ItemType.CENTER_SINGLE_PIC;
//                return ZHIBO_NEWS;
            }
        }

        return ItemType.TEXT_PLAIN;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
