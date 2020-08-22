package com.android.messaging.product.base;

import com.android.messaging.product.api.ApiRetrofit;
import com.android.messaging.product.api.ApiService;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

//import io.reactivex.android.schedulers.AndroidSchedulers;
//import rx.subscriptions.CompositeSubscription;
//import io.reactivex.android.schedulers.AndroidSchedulers;
//import rx.Observable;
//import rx.Subscriber;
//import rx.schedulers.Schedulers;
//import rx.subscriptions.CompositeSubscription;


public abstract class BasePresenter<V> {
    protected ApiService mApiService = ApiRetrofit.getInstance().getApiService();
    protected V mView;
    private CompositeSubscription mCompositeSubscription;

    public BasePresenter(V view) {
        attachView(view);
    }

    public void attachView(V view) {
        mView = view;
    }

    public void detachView() {
        mView = null;
        onUnsubscribe();
    }


    public void addSubscription(Observable observable, Subscriber subscriber) {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(observable
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber));
    }

    //RXjava取消注册，以避免内存泄露
    public void onUnsubscribe() {
        if (mCompositeSubscription != null && mCompositeSubscription.hasSubscriptions()) {
            mCompositeSubscription.unsubscribe();
        }
    }
}
