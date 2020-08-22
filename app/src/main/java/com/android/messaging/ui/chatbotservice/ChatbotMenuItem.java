package com.android.messaging.ui.chatbotservice;

public class ChatbotMenuItem {
    public class CBMenu{
        SuggestionActionWrapper[] entries;
        String displayText;
        public SuggestionActionWrapper.Reply reply;

        public SuggestionActionWrapper[] getEntries() {
            return entries;
        }

        public String getDisplayText() {
            return displayText;
        }

        public SuggestionActionWrapper.Reply getReply() {
            return reply;
        }
    }
    CBMenu menu;

    public CBMenu getMenu() {
        return menu;
    }
}
