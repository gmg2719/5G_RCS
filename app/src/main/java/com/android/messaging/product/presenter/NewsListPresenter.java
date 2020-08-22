package com.android.messaging.product.presenter;

import com.android.messaging.datamodel.data.MessagePartData;
import com.android.messaging.product.base.BasePresenter;
import com.android.messaging.product.entity.ImageEntity;
import com.android.messaging.product.entity.News;
import com.android.messaging.product.presenter.view.INewsView;
import com.android.messaging.util.LogUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.socks.library.KLog;

import retrofit2.Response;
import rx.Subscriber;

public class NewsListPresenter extends BasePresenter<INewsView> {

    private long lastTime;

    public static String newsJson;

    public void setNewsJson(String news){
        newsJson = news;
    }

    public static String getNewsJson(){
        return newsJson;
    }

    public NewsListPresenter(INewsView view) {
        super(view);
    }

    public static void getNewsFromServerSync(String url, final String messageId){
        if(url == null){
            return;
        }
        new NewsListPresenter(null).getNewsSync(url, messageId);
    }

    public static void getNewsFromServer(String url, final String messageId){
        if(url == null){
            return;
        }
        new NewsListPresenter(null).getNews(url, messageId);
    }

    public void getNewsSync(String url, final String messageId){
        if(url == null){
            return;
        }
        try {
            Response<JsonElement> response=mApiService.getNewsCoverSync(url).execute();
            if((response == null) || (response.body() == null)){
                LogUtil.i("Junwang", "newsJson is null.");
                setNewsJson(null);
            }
            String JsonString = response.body().toString();
            LogUtil.i("Junwang", "JsonString is "+JsonString);
//            News temp = new Gson().fromJson(JsonString, News.class);
            News temp = new GsonBuilder().setLenient().create().fromJson(JsonString, News.class);
            if(temp != null){
                LogUtil.i("Junwang", "Json can be parsed to News.");
//                LogUtil.i("Junwang", "title="+temp.title+", type="+temp.article_type+", url="+temp.url
//                        +", image_count="+temp.image_count+", has_image="+temp.has_image+", has_video="+temp.has_video);
//                for(ImageEntity ie: temp.image_list){
//                    LogUtil.i("Junwang", "image url="+ie.url+", width="+ie.width+", height="+ie.height);
//                }
                MessagePartData.updatePartJson(JsonString, messageId);
            }else{
                LogUtil.i("Junwang", "Json can't be parsed to News.");
                setNewsJson(null);
            }
        }catch (Exception e){
            LogUtil.i("Junwang", "Catch Exception when getJson from Server." + e.toString());
        }
//        mApiService.getNewsCoverSync(url).enqueue(new Callback<JsonElement>() {
//            @Override
//            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
//                if((response == null) || (response.body() == null)){
//                    LogUtil.i("Junwang", "newsJson is null.");
//                    setNewsJson(null);
//                }
//                News temp = new Gson().fromJson(response.body().toString(), News.class);
//                if(temp != null){
//                    LogUtil.i("Junwang", "Json can be parsed to News.");
//                    LogUtil.i("Junwang", "title="+temp.title+", type="+temp.article_type+", url="+temp.url
//                            +", image_count="+temp.image_count+", has_image="+temp.has_image+", has_video="+temp.has_video);
//                    for(ImageEntity ie: temp.image_list){
//                        LogUtil.i("Junwang", "image url="+ie.url+", width="+ie.width+", height="+ie.height);
//                    }
//                    String temp_string = response.body().toString();
//                    LogUtil.i("Junwang", "json string="+temp_string);
//                    MessagePartData.updatePartJson(response.body().toString(), messageId);
//                }else{
//                    LogUtil.i("Junwang", "Json can't be parsed to News.");
//                    setNewsJson(null);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<JsonElement> call, Throwable t) {
//
//            }
//        });
    }


    public void getNews(String url, final String messageId){
        if(url == null){
            return;
        }
        addSubscription(mApiService.getNewsCover(/*"http://172.16.31.11:1995/image"*/url), new Subscriber<JsonElement>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                LogUtil.i("junwang", "error "+e.getLocalizedMessage());
                KLog.e(e.getLocalizedMessage());
                mView.onError();
            }

            @Override
            public void onNext(JsonElement newsJson) {
//                lastTime = System.currentTimeMillis() / 1000;
//                PreUtils.putLong(channelCode,lastTime);//保存刷新的时间戳
//
//                List<NewsData> data = response.data;
//                List<News> newsList = new ArrayList<>();
//                if (!ListUtils.isEmpty(data)){
//                    for (NewsData newsData : data) {
//                        News news = new Gson().fromJson(newsData.content, News.class);
//                        newsList.add(news);
//                    }
//                }
//                KLog.e(newsList);
//                News news = new Gson().fromJson(newsJson, News.class);
                //保存到database
//                NewsRecordHelper.save(mChannelCode, mGson.toJson(newList));
//                mView.onGetNewsListSuccess(response.newsdata,response.tips.display_info);
                if(newsJson == null){
                    LogUtil.i("Junwang", "newsJson is null.");
                    setNewsJson(null);
                }
                try {
                    News temp = new Gson().fromJson(newsJson, News.class);
                    if (temp != null) {
                        LogUtil.i("Junwang", "Json can be parsed to News.");
                        for (ImageEntity ie : temp.getImage_list()) {
                            LogUtil.i("Junwang", "image url=" + ie.url + ", width=" + ie.width + ", height=" + ie.height);
                        }
                        String temp_string = newsJson.toString();
                        LogUtil.i("Junwang", "json string=" + temp_string);
                        MessagePartData.updatePartJson(newsJson.toString(), messageId);
//                    setNewsJson(newsJson);
//                    MessagePartData.updatePartJson(newsJson, messageId);
                    } else {
                        LogUtil.i("Junwang", "Json can't be parsed to News.");
                        setNewsJson(null);
                    }
                }catch (Exception e){
                    LogUtil.i("Junwang", "NewsListPresenter parse json exception " + e.toString());
                }
            }
        });
    }
}
