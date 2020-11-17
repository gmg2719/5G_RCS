package com.android.messaging.ui.conversation.chatbot.chatbotconfig;

public class BotInfo {
    private Pcc pcc;
    private String version;
    private String provider;
    private String colour;
    public void setPcc(Pcc pcc) {
        this.pcc = pcc;
    }
    public Pcc getPcc() {
        return pcc;
    }

    public void setVersion(String version) {
        this.version = version;
    }
    public String getVersion() {
        return version;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
    public String getProvider() {
        return provider;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }
    public String getColour() {
        return colour;
    }
}
