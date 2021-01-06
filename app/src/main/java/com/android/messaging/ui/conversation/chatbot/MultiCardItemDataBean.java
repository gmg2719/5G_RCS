package com.android.messaging.ui.conversation.chatbot;

import com.android.messaging.ui.chatbotservice.CardContent;

import java.io.Serializable;

public class MultiCardItemDataBean implements Serializable {
    private String mediaType;
    private String mediaUr;
    private String buttonText;
    private String buttonAction;
    private String title;
    private String extraData1;
    private CardContent cardContent;

    public MultiCardItemDataBean(String title, String mediaType, String mediaUr, String buttonText, String buttonAction, String extraData1, CardContent cardContent) {
        this.mediaType = mediaType;
        this.mediaUr = mediaUr;
        this.buttonText = buttonText;
        this.buttonAction = buttonAction;
        this.title = title;
        this.extraData1 = extraData1;
        this.cardContent = cardContent;
    }

    public String getMediaType() {
        return mediaType;
    }

    public String getMediaUr() {
        return mediaUr;
    }

    public String getButtonText() {
        return buttonText;
    }

    public String getButtonAction() {
        return buttonAction;
    }

    public String getTitle() {
        return title;
    }

    public String getExtraData1() {
        return extraData1;
    }

    public CardContent getCardContent() {
        return cardContent;
    }
}
