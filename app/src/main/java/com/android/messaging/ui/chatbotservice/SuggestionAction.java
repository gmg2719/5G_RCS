package com.android.messaging.ui.chatbotservice;

public class SuggestionAction {
    public UrlAction urlAction;
    public DialerAction dialerAction;
    public MapAction mapAction;
    public String displayText;
    public PostBack postback;


    public class UrlAction{
        public class OpenUrl{
            public String url;
        }
        public OpenUrl openUrl;
    }


    public class DialerAction{
        public class DialPhoneNumber{
            public String phoneNumber;
        }
        public DialPhoneNumber dialPhoneNumber;
    }

    public class MapAction{
        public class ShowLocation{
            public class Location{
                public double latitude;
                public double longitude;
                public String label;
            }
            public Location location;
            public String fallbackUrl;
        }
        public ShowLocation showLocation;
    }

    public class CalendarAction{
        class CreateCalendarEvent{
            String startTime;
            String endTime;
            String title;
            String description;
        }
        ChatBotMessage.CalendarAction.CreateCalendarEvent createCalendarEvent;
    }

    public class ComposeAction{
        class ComposeTextMessage{
            String phoneNumber;
            String text;
        }
        ChatBotMessage.ComposeAction.ComposeTextMessage composeTextMessage;
    }

    public class SettingsAction{
        String displayText;
        String postback;
    }

    public class PostBack{
        public String data;

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }
}
