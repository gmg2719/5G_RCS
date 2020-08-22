package com.android.messaging.datamodel.microfountain.sms;

public class RcsContant {
    public class RcsTypes {
        public static final String MIME_RCS_PRE = "rcs/";
        public static final String MIME_TEXT = MIME_RCS_PRE + "text/plain";
        public static final String MIME_VCARD = MIME_RCS_PRE + "text/vcard";
        public static final String MIME_X_VCARD = MIME_RCS_PRE + "text/x-vcard";
        public static final String MIME_BINARY = MIME_RCS_PRE + "application/octet-stream";
        public static final String MIME_GEO_PUSH = MIME_RCS_PRE + "application/vnd.gsma.rcspushlocation+xml";
        public static final String MIME_SDP = MIME_RCS_PRE + "application/sdp";
        public static final String MIME_FT_HTTP_XML = MIME_RCS_PRE + "application/vnd.gsma.rcs-ft-http+xml";
        public static final String MIME_BOT_MESSAGE = MIME_RCS_PRE + "application/vnd.gsma.botmessage.v1.0+json";
        public static final String MIME_BOT_SUGGESTION = MIME_RCS_PRE + "application/vnd.gsma.botsuggestion.v1.0+json";
        public static final String MIME_BOT_SUGGESTION_RESPONSE = MIME_RCS_PRE + "application/vnd.gsma.botsuggestion.response.v1.0+json";
        public static final String MIME_BOT_SHARED_CLIENT_DATA = MIME_RCS_PRE + "application/vnd.gsma.botsharedclientdata.v1.0+json";
        public static final String MIME_RCS_SPAM_REPORT = MIME_RCS_PRE + "application/vnd.gsma.rcsspam-report+xml";
        public static final String MIME_PUBLIC_ACCOUNT_XML = MIME_RCS_PRE + "application/xml";
        public static final String MIME_IMDN_XML = MIME_RCS_PRE + "message/imdn+xml";
        public static final String MIME_RCS_REVOKE_XML = MIME_RCS_PRE + "application/vnd.gsma.rcsrevoke+xml";
        public static final String MIME_VEMOTICON = MIME_RCS_PRE + "application/vemoticon+xml";
        public static final String MIME_CLOUD_FILE = MIME_RCS_PRE + "application/cloudfile+xml";
        public static final String MIME_CMREDBAG = MIME_RCS_PRE + "application/cmredbag+xml";
        public static final String MIME_CHREDBAG = MIME_RCS_PRE + "application/chredbag+xml";
        public static final String MIME_CARD = MIME_RCS_PRE + "application/card+xml";
        public static final String MIME_COMMONTEMPLATE = MIME_RCS_PRE + "application/commontemplate+xml";
        public static final String MIME_EIM = MIME_RCS_PRE + "application/eim+xml";
        public static final String MIME_EIMID = MIME_RCS_PRE + "application/eimid+xml";
        public static final String MIME_OMA_PUSH = MIME_RCS_PRE + "application/vnd.oma.push";
        public static final String MIME_PDF = MIME_RCS_PRE + "application/pdf";
        public static final String MIME_IMAGE = MIME_RCS_PRE + "image/";
        public static final String MIME_AUDIO = MIME_RCS_PRE + "audio/";
        public static final String MIME_VIDEO = MIME_RCS_PRE + "video/";
        public static final String MIME_MIXED = MIME_RCS_PRE + "multipart/mixed";
        public static final String MIME_UNKNOWN = MIME_RCS_PRE + "unknown";
    }

    public static final int CARD_TYPE_NORMAL_SINGLE_CARD = 0;
    public static final int CARD_TYPE_ACTIVITY_SUB = 1;
    public static final int CARD_TYPE_VOTE = 2;
    public static final int CARD_TYPE_VIDEO_NEWS = 3;
    public static final int CARD_TYPE_PRODUCT_RECOMMEND = 4;
    public static final int CARD_TYPE_SUB_ACTIVITY_START = 5;
    public static final int CARD_TYPE_PRODUCT_ORDER = 6;


    public static enum CardType{
        NORMAL_SINGLE_CARD(0),
        ACTIVITY_SUB(1),
        VOTE(2),
        VIDEO_NEWS(3),
        PRODUCT_RECOMMEND(4),
        SUB_ACTIVITY_START(5),
        PRODUCT_ORDER(6);

        private final int value;
        //构造方法必须是private或者默认
        /*private*/ CardType(int value) {
            this.value = value;
        }

        public static CardType valueOf(int value) {
            switch (value) {
                case 1:
                    return CardType.ACTIVITY_SUB;
                case 2:
                    return CardType.VOTE;
                case 3:
                    return CardType.VIDEO_NEWS;
                case 4:
                    return CardType.PRODUCT_RECOMMEND;
                case 5:
                    return CardType.SUB_ACTIVITY_START;
                case 6:
                    return CardType.PRODUCT_ORDER;
                default:
                    return CardType.NORMAL_SINGLE_CARD;
            }
        }
    }
}
