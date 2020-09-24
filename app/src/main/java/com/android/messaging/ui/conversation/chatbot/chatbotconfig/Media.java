package com.android.messaging.ui.conversation.chatbot.chatbotconfig;

import com.alibaba.fastjson.annotation.JSONField;

public class Media {
    @JSONField(name = "media-url")
    private String media_url;
    public void setMediaUrl(String media_url) {
        this.media_url = media_url;
    }
    public String getMediaUrl() {
        return media_url;
    }

}
