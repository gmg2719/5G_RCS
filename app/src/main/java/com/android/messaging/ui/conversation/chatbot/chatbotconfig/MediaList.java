package com.android.messaging.ui.conversation.chatbot.chatbotconfig;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class MediaList {
    @JSONField(name = "media-entry")
    private List<MediaEntry> media_entry;
    public void setMediaEntry(List<MediaEntry> media_entry) {
        this.media_entry = media_entry;
    }
    public List<MediaEntry> getMediaEntry() {
        return media_entry;
    }
}
