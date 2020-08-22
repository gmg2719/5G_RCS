package com.android.messaging.ui.chatbotservice;

import java.util.List;

public interface PublicAccountListener {

    void onComplainFail(int paramInt1, int paramInt2);

    void onComplainSucceed(int paramInt);

    void onGetDetailFail(int paramInt1, int paramInt2);

    void onGetDetailSucceed(int paramInt, PublicAccountDetailBean paramPublicAccountDetailBean);

    void onGetHistoryMessagesFail(int paramInt1, int paramInt2);

    void onGetHistoryMessagesSucceed(int paramInt, List<PublicAccountMessageBean> paramList);

    void onGetMenusFail(int paramInt1, int paramInt2);

    void onGetMenusSucceed(int paramInt, List<PublicAccountMenuBean> paramList);

    void onGetRecommendedPublicAccountsFail(int paramInt1, int paramInt2);

    void onGetRecommendedPublicAccountsSucceed(int paramInt, List<PublicAccountBean> paramList);

    void onGetUserSubscriptionFail(int paramInt1, int paramInt2);

    void onGetUserSubscriptionSucceed(int paramInt, List<PublicAccountBean> paramList);

    void onSearchPublicAccountsFail(int paramInt1, int paramInt2);

    void onSearchPublicAccountsSucceed(int paramInt, List<PublicAccountBean> paramList);

    void onSetAcceptStatusFail(int paramInt1, int paramInt2);

    void onSetAcceptStatusSucceed(int paramInt);

    void onSubscribeFail(int paramInt1, int paramInt2);

    void onSubscribeSucceed(int paramInt);

    void onUnsubscribeFail(int paramInt1, int paramInt2);

    void onUnsubscribeSucceed(int paramInt);
}
