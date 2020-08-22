package com.android.messaging.ui.conversation.chatbot;

import java.io.Serializable;

public class MultiCardItemDataBean implements Serializable {
    private String mediaType;
    private String mediaUr;
    private String buttonText;
    private String buttonAction;
    private String title;
    private String extraData1;

    public MultiCardItemDataBean(String title, String mediaType, String mediaUr, String buttonText, String buttonAction, String extraData1) {
        this.mediaType = mediaType;
        this.mediaUr = mediaUr;
        this.buttonText = buttonText;
        this.buttonAction = buttonAction;
        this.title = title;
        this.extraData1 = extraData1;
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
}
