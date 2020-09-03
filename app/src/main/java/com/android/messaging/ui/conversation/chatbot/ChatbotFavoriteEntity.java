package com.android.messaging.ui.conversation.chatbot;

import com.android.messaging.util.LogUtil;

public class ChatbotFavoriteEntity {
    private String chatbot_fav_sip_uri;
    private String chatbot_fav_name;
    private String chatbot_fav_logo;
    private String chatbot_fav_card_description;
    private String chatbot_fav_image_url;
    private String chatbot_fav_saved_date;
    private String chatbot_fav_channel_id;
    private String chatbot_fav_msg_id;
    private String chatbot_fav_conversation_id;

    public String getChatbot_fav_sip_uri() {
        return chatbot_fav_sip_uri;
    }

    public void setChatbot_fav_sip_uri(String chatbot_fav_sip_uri) {
        this.chatbot_fav_sip_uri = chatbot_fav_sip_uri;
    }

    public String getChatbot_fav_name() {
        return chatbot_fav_name;
    }

    public void setChatbot_fav_name(String chatbot_fav_name) {
        this.chatbot_fav_name = chatbot_fav_name;
    }

    public String getChatbot_fav_logo() {
        return chatbot_fav_logo;
    }

    public void setChatbot_fav_logo(String chatbot_fav_logo) {
        this.chatbot_fav_logo = chatbot_fav_logo;
    }

    public String getChatbot_fav_card_description() {
        return chatbot_fav_card_description;
    }

    public void setChatbot_fav_card_description(String chatbot_fav_card_description) {
        this.chatbot_fav_card_description = chatbot_fav_card_description;
    }

    public String getChatbot_fav_image_url() {
        return chatbot_fav_image_url;
    }

    public void setChatbot_fav_image_url(String chatbot_fav_image_url) {
        this.chatbot_fav_image_url = chatbot_fav_image_url;
    }

    public String getChatbot_fav_saved_date() {
        LogUtil.i("Junwang", "saved date "+chatbot_fav_saved_date);
        return chatbot_fav_saved_date;
    }

    public void setChatbot_fav_saved_date(String chatbot_fav_saved_date) {
        this.chatbot_fav_saved_date = chatbot_fav_saved_date;
    }

    public String getChatbot_fav_channel_id() {
        return chatbot_fav_channel_id;
    }

    public void setChatbot_fav_channel_id(String chatbot_fav_channel_id) {
        this.chatbot_fav_channel_id = chatbot_fav_channel_id;
    }

    public String getChatbot_fav_msg_id() {
        return chatbot_fav_msg_id;
    }

    public void setChatbot_fav_msg_id(String chatbot_fav_msg_id) {
        this.chatbot_fav_msg_id = chatbot_fav_msg_id;
    }

    public String getChatbot_fav_conversation_id() {
        return chatbot_fav_conversation_id;
    }

    public void setChatbot_fav_conversation_id(String chatbot_fav_conversation_id) {
        this.chatbot_fav_conversation_id = chatbot_fav_conversation_id;
    }
}
