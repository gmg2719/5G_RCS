package com.android.messaging.ui.chatbotservice;

public class CardContent {
    ChatbotContentMedia media;
    String title;
    String description;
    int cardType;
    String extraData1;
    ChatbotExtraData[] extraData;

    SuggestionActionWrapper[] suggestions;

    public ChatbotContentMedia getMedia() {
        return media;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public SuggestionActionWrapper[] getSuggestionActionWrapper() {
        return suggestions;
    }

    public int getCardType() {
        return cardType;
    }

    public String getExtraData1() {
        return extraData1;
    }

    public ChatbotExtraData[] getExtraData() {
        return extraData;
    }
}
