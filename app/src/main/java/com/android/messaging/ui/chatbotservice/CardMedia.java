package com.android.messaging.ui.chatbotservice;

import android.net.Uri;

import java.util.List;

public class CardMedia {
    String type;

    class Properties{
        class MediaUrl{
            String type;
            Uri format;
        }
        String mediaContentType;
        class MediaFileSize{
            String title;
            int type;
            int minimum;
        }
        MediaUrl thumbnailUrl;
        String thumbnailContentType;
        MediaFileSize thumbnailFileSize;
        class ContentDescription{
            String title;
            String description;
            String type;
            int minLength;
            int maxLength;
        }
        class CardStyle{
            String title;
            String type;
            Uri format;
        }

        List<String> titleFontStyle;
        List<String> descriptionFontStyle;
    }
}
