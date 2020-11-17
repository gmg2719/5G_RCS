package com.android.messaging.ui.conversation.chatbot;

public class ChatbotEntity {
    private String domain;
    private String sip_uri;
    private String expiry_time;
    private String etag;
    private String json;
    private String name;
    private String sms;
    private String menu;

    public String getDomain() {
        return domain;
    }

    public String getSip_uri() {
        return sip_uri;
    }

    public String getExpiry_time() {
        return expiry_time;
    }

    public String getEtag() {
        return etag;
    }

    public String getJson() {
        return json;
    }

    public String getName() {
        return name;
    }

    public String getSms() {
        return sms;
    }

    public String getMenu() {
        return menu;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setSip_uri(String sip_uri) {
        this.sip_uri = sip_uri;
    }

    public void setExpiry_time(String expiry_time) {
        this.expiry_time = expiry_time;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }
}
