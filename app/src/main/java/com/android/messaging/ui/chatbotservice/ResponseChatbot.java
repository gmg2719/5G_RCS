package com.android.messaging.ui.chatbotservice;

public class ResponseChatbot {
    public class ResponseChatbotMsg{
        public class Reply{
            public String displayText;
            public SuggestionAction.PostBack postback;

            public String getDisplayText() {
                return displayText;
            }

            public void setDisplayText(String displayText) {
                this.displayText = displayText;
            }

            public SuggestionAction.PostBack getPostback() {
                return postback;
            }

            public void setPostback(SuggestionAction.PostBack postback) {
                this.postback = postback;
            }
        }
        public Reply reply;
    }
    public ResponseChatbotMsg response;
}
