package com.android.messaging.ui.conversation.chatbot.chatbotconfig;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class CommAddr {
    @JSONField(name = "uri-entry")
    private List<UriEntry> uri_entry;
    public void setUriEntry(List<UriEntry> uri_entry) {
        this.uri_entry = uri_entry;
    }
    public List<UriEntry> getUriEntry() {
        return uri_entry;
    }
}
