package com.android.messaging.datamodel.data;

import com.android.messaging.datamodel.mygsonconverter.GsonConverterFactory;
import com.android.messaging.util.LogUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class MessageJsonData {
    public static final String TAG = "MessageJsonData";
    private static Gson mGson = new Gson();

    class JsonData {
        private String title;
        private String text;
        private String picUrl;
        private String responseUrl;

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public void setResponseUrl(String responseUrl){
            this.responseUrl = responseUrl;
        }

        public String getResponseUrl(){
            return responseUrl;
        }

        public void setPicUrl(String picUrl){
            this.picUrl = picUrl;
        }

        public String getPicUrl(){
            return picUrl;
        }
    }

    public interface JsonDataRetrofitService{
//        @GET("top250")
//        Observable<ResponseBody> getTopMovie(@Query("start") int start, @Query("count") int count);
//
        @GET("{id}")
        Observable<JsonData> getMessageJson(@Path("id") String path);
    }

    public static final String BASE_URL = "http://newadmin.supermms.cn/api/RCSTest/";
    public void fetchJsonFromUrl(String url){
        LogUtil.i(TAG, "fetchJsonFromUrl start");
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).
                addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
//        JsonReader.setLenient(true);

        MessageJsonData.JsonDataRetrofitService service = retrofit.create(MessageJsonData.JsonDataRetrofitService.class);
        service.getMessageJson("test1").subscribe(new Consumer<JsonData>() {
            @Override
            public void accept(JsonData jsonData) throws Exception {
                LogUtil.i(TAG, "get json successfully");
                if(jsonData == null){
                    return;
                }
                String s = jsonData.text;
//                MessagingContentProvider.notifyPartsChanged();
            }
        });
    }

    /**
     * 将json转换成新闻集合
     *
     * @param json
     * @return
     */
    public static List<JsonData> convertToJsonData(String json) {
        return mGson.fromJson(json, new TypeToken<List<JsonData>>() {
        }.getType());
    }
}
