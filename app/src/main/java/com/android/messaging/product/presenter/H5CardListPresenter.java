package com.android.messaging.product.presenter;

import com.android.messaging.datamodel.data.MessagePartData;
import com.android.messaging.product.api.H5CardApi;
import com.android.messaging.product.base.BaseCardPresenter;
import com.android.messaging.product.entity.H5CardItem;
import com.android.messaging.product.presenter.view.INewsView;
import com.android.messaging.util.LogUtil;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import io.reactivex.functions.Consumer;
import retrofit2.Response;

//import java.util.Observable;

public class H5CardListPresenter extends BaseCardPresenter<INewsView> {

    public H5CardListPresenter(INewsView view) {
        super(view);
    }

    public static void getCardFromServerSync(String url, final String messageId){
        if(url == null){
            return;
        }
        new H5CardListPresenter(null).getCardSync(url, messageId);
    }

    public static void getCardFromServer(String url, final String messageId){
        if(url == null){
            return;
        }
//        new H5CardListPresenter(null).getCard(url, messageId);
    }

    public void getCardSync(String url, final String messageId){
        if(url == null){
            return;
        }
        try {
            Response<JsonElement> response=mApiService.getCardCoverSync(url).execute();
            if((response == null) || (response.body() == null)){
                LogUtil.i("Junwang", "cardJson is null.");
            }
            String JsonString = response.body().toString();
            LogUtil.i("Junwang", "JsonString is "+JsonString);
            H5CardItem temp = new GsonBuilder().setLenient().create().fromJson(JsonString, H5CardItem.class);
            if(temp != null){
                LogUtil.i("Junwang", "Json can be parsed to H5 card.");
                MessagePartData.updatePartJson(JsonString, messageId);
            }else{
                LogUtil.i("Junwang", "Json can't be parsed to H5 card.");
            }
        }catch (Exception e){
            LogUtil.i("Junwang", "Catch Exception when getJson from Server." + e.toString());
        }
    }

    public void getH5Card(String url, final String messageId){
        if(url == null){
            return;
        }
        mApiService.getH5Card(url).subscribe(new Consumer<H5CardApi>() {
            @Override
            public void accept(H5CardApi h5CardApi) throws Exception {
                if(h5CardApi.getRet_code() != 0){
                    LogUtil.i("Junwang", "getH5getH5Card error code="+h5CardApi.getRet_code());
                }
                for(H5CardItem item : h5CardApi.getH5_card()){

                }
            }
        });
    }


//    public void getCard(String url, final String messageId){
//        if(url == null){
//            return;
//        }
//        addSubscription(mApiService.getCardCover(url), new Subscriber<JsonElement>() {
//            @Override
//            public void onCompleted() {
//
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                LogUtil.i("junwang", "error "+e.getLocalizedMessage());
//                KLog.e(e.getLocalizedMessage());
//                mView.onError();
//            }
//
//            @Override
//            public void onNext(JsonElement cardJson) {
//                if(cardJson == null){
//                    LogUtil.i("Junwang", "cardJson is null.");
//                }
//                try {
//                    H5CardItem temp = new Gson().fromJson(cardJson, H5CardItem.class);
//                    if (temp != null) {
//                        LogUtil.i("Junwang", "Json can be parsed to card.");
//                        LogUtil.i("Junwang", "title=" + temp.title + ", type=" + temp.card_item_type + ", url=" + temp.url
//                                + ", image_count=" + temp.image_count + ", has_image=" + temp.has_image + ", has_video=" + temp.has_video);
//                        for (ImageEntity ie : temp.image_list) {
//                            LogUtil.i("Junwang", "image url=" + ie.url + ", width=" + ie.width + ", height=" + ie.height);
//                        }
//                        String temp_string = cardJson.toString();
//                        LogUtil.i("Junwang", "json string=" + temp_string);
//                        MessagePartData.updatePartJson(cardJson.toString(), messageId);
////                    setNewsJson(newsJson);
////                    MessagePartData.updatePartJson(newsJson, messageId);
//                    } else {
//                        LogUtil.i("Junwang", "Json can't be parsed to card.");
//                    }
//                }catch (Exception e){
//                    LogUtil.i("Junwang", "NewsListPresenter parse json exception " + e.toString());
//                }
//            }
//        });
//    }
}
