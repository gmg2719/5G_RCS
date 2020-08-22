package com.android.messaging.ui.chatbotservice;

import java.util.List;

public class ChatBotMessage {
    GeneralPurposeCard generalPurposeCard;

    class GeneralPurposeCard{
        CardLayout layout;
        CardContent content;
    }

    class CardLayout{
        String cardOrientation;
        String imageAlignment;
        List<String> titleFontStyle;
        List<String> descriptionFontStyle;
        String style;
    }

    class CardContent{
        CardMedia media;
        String title;
        String description;
        CardReply reply;
    }

    class CardReply{
        String displayText;
        PostBack postback;
    }

    class PostBack{
        String data;
    }

    class CardMedia{
        String mediaUrl;
        String mediaContentType;
        long mediaFileSize;
        String thumbnailUrl;
        String thumbnailContentType;
        long thumbnailFileSize;
        String height;
        String contentDescription;
    }

    class UrlAction{
        OpenUrl openUrl;
    }

    class OpenUrl{
        String url;
        String application;
        String viewMode;
        String displayText;
        PostBack postback;
    }

    class DialerAction{
        DialPhoneNumber dialPhoneNumber;
        String displayText;
        PostBack postback;
    }

    class DialPhoneNumber{
        String phoneNumber;
    }

    enum CardOrientation{
        HORIZONTAL, VERTICAL
    };

    enum ImageAlignment{
        LEFT, RIGHT
    };

    class FontStyle{
        String type;
        int minItems;
        int maxItems;
        boolean additionalItems;
        class Items{
            String type;
            List<String> enums;
        }
    }

    class MapAction{
        class ShowLocation{
            class Location{
                double latitude;
                double longitude;
                String label;
            }
            Location location;
            String fallbackUrl;
        }
        ShowLocation showLocation;
        String displayText;
    }

    class CalendarAction{
        class CreateCalendarEvent{
            String startTime;
            String endTime;
            String title;
            String description;
        }
        CreateCalendarEvent createCalendarEvent;
        String displayText;
    }

    class ComposeAction{
        class ComposeTextMessage{
            String phoneNumber;
            String text;
        }
        ComposeTextMessage composeTextMessage;
        String displayText;
        String postback;
    }

    class SettingsAction{
        String displayText;
        String postback;
    }
}
