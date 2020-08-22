package com.android.messaging.ui.chatbotservice;

import java.util.List;

public class PublicAccountListenerAdapter implements PublicAccountListener
{
    public void onComplainFail(int paramInt1, int paramInt2) {}

    public void onComplainSucceed(int paramInt) {}

    public void onGetDetailFail(int paramInt1, int paramInt2) {}

    public void onGetDetailSucceed(int paramInt, PublicAccountDetailBean paramPublicAccountDetailBean) {}

    public void onGetHistoryMessagesFail(int paramInt1, int paramInt2) {}

    public void onGetHistoryMessagesSucceed(int paramInt, List<PublicAccountMessageBean> paramList) {}

    public void onGetMenusFail(int paramInt1, int paramInt2) {}

    public void onGetMenusSucceed(int paramInt, List<PublicAccountMenuBean> paramList) {}

    public void onGetRecommendedPublicAccountsFail(int paramInt1, int paramInt2) {}

    public void onGetRecommendedPublicAccountsSucceed(int paramInt, List<PublicAccountBean> paramList) {}

    public void onGetUserSubscriptionFail(int paramInt1, int paramInt2) {}

    public void onGetUserSubscriptionSucceed(int paramInt, List<PublicAccountBean> paramList) {}

    public void onSearchPublicAccountsFail(int paramInt1, int paramInt2) {}

    public void onSearchPublicAccountsSucceed(int paramInt, List<PublicAccountBean> paramList) {}

    public void onSetAcceptStatusFail(int paramInt1, int paramInt2) {}

    public void onSetAcceptStatusSucceed(int paramInt) {}

    public void onSubscribeFail(int paramInt1, int paramInt2) {}

    public void onSubscribeSucceed(int paramInt) {}

    public void onUnsubscribeFail(int paramInt1, int paramInt2) {}

    public void onUnsubscribeSucceed(int paramInt) {}
}
