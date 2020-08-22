package com.android.messaging.util;

import com.microfountain.rcs.aidl.service.login.SubscriptionStatus;
import com.microfountain.rcs.rcskit.service.RcsSubscriptionInfo;
import com.microfountain.rcs.rcskit.service.RcsSubscriptionManager;

import java.util.List;

import static com.microfountain.rcs.rcskit.service.RcsSubscriptionManager.getActiveSubscriptionInfoList;


public class RCSUtil {
    public static boolean isSubscriptionRcsEnabled(int subscriptionId) {
        List<RcsSubscriptionInfo> subscriptionInfoList =
                getActiveSubscriptionInfoList();
        for (RcsSubscriptionInfo subscriptionInfo : subscriptionInfoList) {
            if (subscriptionInfo.subscriptionId == subscriptionId) {
                SubscriptionStatus subscriptionStatus =
                        subscriptionInfo.getSubscriptionStatus();
                switch (subscriptionStatus) {
                    case DEFAULT:
                    case DESTROYED:
                        return false;
                    case CONFIGURED:
                    case CONNECTING:
                    case CONNECTED:
                    case DISCONNECTED:
                        return true;
                }
            }
        }
        return false;
    }

    public static boolean isSubscriptionRcsRegistered(int subscriptionId) {
        List<RcsSubscriptionInfo> subscriptionInfoList =
                getActiveSubscriptionInfoList();
        for (RcsSubscriptionInfo subscriptionInfo : subscriptionInfoList) {
            if (subscriptionInfo.subscriptionId == subscriptionId) {
                SubscriptionStatus subscriptionStatus =
                        subscriptionInfo.getSubscriptionStatus();
                switch (subscriptionStatus) {
                    case DEFAULT:
                    case DESTROYED:
                    case CONFIGURED:
                    case CONNECTING:
                    case DISCONNECTED:
                        return false;
                    case CONNECTED:
                        return true;
                }
            }
        }
        return false;
    }

    public static int getEnabledSubscriptionId() {
        List<RcsSubscriptionInfo> subscriptionInfoList =
                RcsSubscriptionManager.getActiveSubscriptionInfoList();
        for (RcsSubscriptionInfo subscriptionInfo : subscriptionInfoList) {
            SubscriptionStatus subscriptionStatus =
                    subscriptionInfo.getSubscriptionStatus();
            switch (subscriptionStatus) {
                case DEFAULT:
                case DESTROYED:
                    continue;
                case CONFIGURED:
                case CONNECTING:
                case CONNECTED:
                case DISCONNECTED: {
                    return subscriptionInfo.subscriptionId;
                }
            }
        }
        return -1;
    }

    public static RcsSubscriptionInfo getEnabledSubscriptionInfo() {
        List<RcsSubscriptionInfo> subscriptionInfoList =
                RcsSubscriptionManager.getActiveSubscriptionInfoList();
        for (RcsSubscriptionInfo subscriptionInfo : subscriptionInfoList) {
            SubscriptionStatus subscriptionStatus =
                    subscriptionInfo.getSubscriptionStatus();
            switch (subscriptionStatus) {
                case DEFAULT:
                case DESTROYED:
                    continue;
                case CONFIGURED:
                case CONNECTING:
                case CONNECTED:
                case DISCONNECTED: {
                    return subscriptionInfo;
                }
            }
        }
        return null;
    }

}


