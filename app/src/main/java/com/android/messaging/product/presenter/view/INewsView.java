package com.android.messaging.product.presenter.view;

import com.android.messaging.product.entity.News;

public interface INewsView {

    void onGetNewsListSuccess(News news, String tipInfo);

    void  onError();
}
