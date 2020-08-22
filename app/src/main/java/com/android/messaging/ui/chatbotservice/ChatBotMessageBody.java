package com.android.messaging.ui.chatbotservice;

import java.net.URI;

public class ChatBotMessageBody {
    URI address;
    String[] destinationAddress;
    String senderAddress;
    String imFormat;
    String contentType;
    String contentEncoding;
    String conversationID;
    String contributionID;
    String inReplyToContributionID;
    String[] reportRequest;
    boolean shortMessageSupported;
    boolean storeSupported;
    boolean fallbackSupported;
    ServiceCapability[] serviceCapability;
    String trafficType;
    String bodyText;
    String smBodyText;
    String fallbackContentType;
    String fallbackContentEncoding;
    String rcsBodyText;
    String clientCorrelator;
    String resourceURL;
    String messageId;
    DeliveryInfo[] deliveryInfoList;


}
