package com.android.messaging.ui.conversation.chatbot.chatbotconfig;

import com.alibaba.fastjson.annotation.JSONField;

public class MediaEntry {
    @JSONField(name = "media-content")
    private String media_content;
    private Media media;
    private String label;
    public void setMediaContent(String media_content) {
        this.media_content = media_content;
    }
    public String getMediaContent() {
        return media_content;
    }

    public void setMedia(Media media) {
        this.media = media;
    }
    public Media getMedia() {
        return media;
    }

    public void setLabel(String label) {
        this.label = label;
    }
    public String getLabel() {
        return label;
    }
}
