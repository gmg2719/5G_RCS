package com.android.messaging.ui.conversation;

public class CardMsgTemplate {
    public static final int RECHARGE_NOTIFICATION                   = 1;
    public static final int VERICATION_CODE_NOTIFICATION            = 2;
    public static final int BUY_TICKETS_SUCCESSFULLY_NOTIFICATION   = 3;

    //原生action类型
    static class NativeActionType{
        public static final int PHONE_CALL              = 1;
        public static final int SEND_MSG                = 2;
        public static final int TAKE_PICTURE            = 3;
        public static final int TAKE_VIDEO              = 4;
        public static final int COPY                    = 5;
        public static final int OPEN_LOCATION           = 6;
        public static final int CALENDAR                = 7;
        public static final int READ_CONTACT            = 8;
    }
    //菜单项， 按钮action类型
    static class ActionType{
        public static final int JUMP_TO_WEBURL             = 1;
        public static final int CALL_NATIVE_FUNCTION       = 2;
        public static final int JUMP_TO_APP                = 3;
        public static final int JUMP_TO_QUICK_APP          = 4;
    }
}
