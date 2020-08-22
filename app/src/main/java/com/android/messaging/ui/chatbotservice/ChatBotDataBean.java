package com.android.messaging.ui.chatbotservice;

public class ChatBotDataBean {
    private int mediaType;
    private String mediaUr;
    private String buttonText;
    private String buttonAction;

    public ChatBotDataBean(int mediaType, String mediaUr, String buttonText, String buttonAction) {
        this.mediaType = mediaType;
        this.mediaUr = mediaUr;
        this.buttonText = buttonText;
        this.buttonAction = buttonAction;
    }

    public int getMediaType() {
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
}
