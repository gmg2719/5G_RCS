package com.android.messaging.product.entity;

import android.text.TextUtils;

import com.android.messaging.product.utils.ListUtils;
import com.google.gson.Gson;

import java.util.List;

public class News {
    /**
     * 纯文字布局(文章、广告)
     */
    public static final int TEXT_NEWS = 100;
    /**
     * 居中大图布局(1.单图文章；2.单图广告；3.视频，中间显示播放图标，右侧显示时长)
     */
    public static final int CENTER_SINGLE_PIC_NEWS = 200;
    /**
     * 右侧小图布局(1.小图新闻；2.视频类型，右下角显示视频时长)
     */
    public static final int RIGHT_PIC_VIDEO_NEWS = 300;
    /**
     * 三张图片布局(文章、广告)
     */
    public static final int THREE_PICS_NEWS = 400;

    public static final int ZHIBO_NEWS = 500;

    public static final int ONE_BUTTON = 600;

    public static final int TWO_BUTTON = 700;

    public static final int THREE_BUTTON = 800;

    public static final int CENTER_SINGLE_VIDEO_NEWS = 900;

    private int article_type;
//    public String tag;
    private String title;
//    public int hot;
//    public String source;
//    public int comment_count;
//    public String article_url;
    private int image_count;
    private int video_style;
//    public String item_id;
//    public UserEntity user_info;
//    public long behot_time;
    private String url;
    private boolean has_image;
    private boolean has_video;
    private int video_duration;
    private String video_path;
    private ImageEntity middle_image;
    private List<ImageEntity> image_list;
    private List<BusinessActionEntity> bae_list;
    private List<News> news_list;

    public int getArticle_type() {
        return article_type;
    }

    public int getImage_count() {
        return image_count;
    }

    public String getTitle() {
        return title;
    }

    public int getVideo_style() {
        return video_style;
    }

    public String getUrl() {
        return url;
    }

    public boolean isHas_image() {
        return has_image;
    }

    public boolean isHas_video() {
        return has_video;
    }

    public int getVideo_duration() {
        return video_duration;
    }

    public String getVideo_path() {
        return video_path;
    }

    public ImageEntity getMiddle_image() {
        return middle_image;
    }

    public List<ImageEntity> getImage_list() {
        return image_list;
    }

    public List<BusinessActionEntity> getBae_list() {
        return bae_list;
    }

    public List<News> getNews_list() {
        return news_list;
    }

    public static int getViewType(News news) {
        if (news.has_video) {
            //如果有视频
            if (news.video_style == 0) {
                //右侧视频
                if (news.middle_image == null || TextUtils.isEmpty(news.middle_image.url)) {
                    return TEXT_NEWS;
                }
                return RIGHT_PIC_VIDEO_NEWS;
            } else if (news.video_style == 2) {
                //居中视频
                return CENTER_SINGLE_VIDEO_NEWS;
            }
//            return CENTER_SINGLE_VIDEO_NEWS;
            return ZHIBO_NEWS;
        } else {
            if(news.article_type == 600){
                if(news.getBae_list() != null){
                    int i = news.getBae_list().size();
                    if(i == 1){
                        return ONE_BUTTON;
                    }else if(i == 2){
                        return TWO_BUTTON;
                    }else if(i == 3){
                        return THREE_BUTTON;
                    }
                }
            }
            //非视频新闻
            else if (!news.has_image) {
                //纯文字新闻
                return TEXT_NEWS;
            } else {
                if (ListUtils.isEmpty(news.image_list)) {
                    //图片列表为空，则是右侧图片
                    return RIGHT_PIC_VIDEO_NEWS;
                }

                if (news.image_count == 3) {
                    //图片数为3，则为三图
                    return THREE_PICS_NEWS;
                }

                //中间大图，右下角显示图数
                return CENTER_SINGLE_PIC_NEWS;
//                return ZHIBO_NEWS;
            }
        }

        return TEXT_NEWS;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
