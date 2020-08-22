package com.android.messaging.ui.chatbotservice;

public class ChatbotMenuEntity{
    public class ChatbotMenu {
        ChatbotMenuItem[] entries;

        public ChatbotMenuItem[] getEntries() {
            return entries;
        }
    }
    ChatbotMenu menu;

    public ChatbotMenu getMenu() {
        return menu;
    }
}

