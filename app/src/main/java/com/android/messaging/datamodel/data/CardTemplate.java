package com.android.messaging.datamodel.data;

import java.util.ArrayList;

public class CardTemplate {
    private int card_msg_template_no;
    private String card_msg_title;
    private String regular_expression;
    private String regular_expression_key[];
    private ArrayList<ActionButton> action_button;

    public static final int RECHARGE_NOTIFICATION                   = 1;
    public static final int VERICATION_CODE_NOTIFICATION            = 2;
    public static final int BUY_TICKETS_SUCCESSFULLY_NOTIFICATION   = 3;

    //原生action类型
    public static class NativeActionType{
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

    public static class ActionButton{
        private String button_name;
        private int button_action_type;
        private String button_action_url;
        private int[] button_action_native_function;

        public String getButton_name() {
            return button_name;
        }

        public void setButton_name(String button_name) {
            this.button_name = button_name;
        }

        public int getButton_action_type() {
            return button_action_type;
        }

        public void setButton_action_type(int button_action_type) {
            this.button_action_type = button_action_type;
        }

        public String getButton_action_url() {
            return button_action_url;
        }

        public void setButton_action_url(String button_action_url) {
            this.button_action_url = button_action_url;
        }

        public int[] getButton_action_native_function() {
            return button_action_native_function;
        }

        public void setButton_action_native_function(int[] button_action_native_function) {
            this.button_action_native_function = button_action_native_function;
        }
    }

    public int getCard_msg_template_no() {
        return card_msg_template_no;
    }

    public void setCard_msg_template_no(int card_msg_template_no) {
        this.card_msg_template_no = card_msg_template_no;
    }

    public String getRegular_expression() {
        return regular_expression;
    }

    public void setRegular_expression(String regular_expression) {
        this.regular_expression = regular_expression;
    }

    public String getCard_msg_title() {
        return card_msg_title;
    }

    public void setCard_msg_title(String card_msg_title) {
        this.card_msg_title = card_msg_title;
    }

    public String[] getRegular_expression_key() {
        return regular_expression_key;
    }

    public void setRegular_expression_key(String[] regular_expression_key) {
        this.regular_expression_key = regular_expression_key;
    }

    public ArrayList<ActionButton> getAction_button() {
        return action_button;
    }

    public void setAction_button(ArrayList<ActionButton> action_button) {
        this.action_button = action_button;
    }
}
