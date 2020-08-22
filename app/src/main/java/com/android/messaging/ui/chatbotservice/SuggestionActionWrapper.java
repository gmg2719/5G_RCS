package com.android.messaging.ui.chatbotservice;

public class SuggestionActionWrapper {
    public SuggestionAction action;
    public Reply reply;

    public class Reply{
        public String displayText;
        public SuggestionAction.PostBack postback;

        public String getDisplayText() {
            return displayText;
        }

        public SuggestionAction.PostBack getPostback() {
            return postback;
        }
    }

    public SuggestionAction getAction() {
        return action;
    }

    public Reply getReply() {
        return reply;
    }
}
