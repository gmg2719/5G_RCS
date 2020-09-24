package com.android.messaging.ui.conversation.chatbot.chatbotconfig;

import com.alibaba.fastjson.annotation.JSONField;

public class UriEntry {
    @JSONField(name = "addr-uri-type")
    private String addr_uri_type;
    @JSONField(name = "addr-uri")
    private String addr_uri;
    private String label;
    public void setAddrUriType(String addr_uri_type) {
        this.addr_uri_type = addr_uri_type;
    }
    public String getAddrUriType() {
        return addr_uri_type;
    }

    public void setAddrUri(String addr_uri) {
        this.addr_uri = addr_uri;
    }
    public String getAddrUri() {
        return addr_uri;
    }

    public void setLabel(String label) {
        this.label = label;
    }
    public String getLabel() {
        return label;
    }
}
