package com.android.messaging.datamodel.data;

import java.util.ArrayList;

public class ButtonMenu {
    String menu_name;
    int action_type;
    String action_url;
    int action_native_function;
    ArrayList<BusnMenuItem> menu_items;

    public String getMenu_name() {
        return menu_name;
    }

    public void setMenu_name(String menu_name) {
        this.menu_name = menu_name;
    }

    public int getAction_type() {
        return action_type;
    }

    public void setAction_type(int action_type) {
        this.action_type = action_type;
    }

    public String getAction_url() {
        return action_url;
    }

    public void setAction_url(String action_url) {
        this.action_url = action_url;
    }

    public ArrayList<BusnMenuItem> getMenu_items() {
        return menu_items;
    }

    public void setMenu_items(ArrayList<BusnMenuItem> menu_items) {
        this.menu_items = menu_items;
    }

    public ArrayList<BusnMenuItem> getMenuList(){
        return menu_items;
    }

    public int getAction_native_function() {
        return action_native_function;
    }

    public void setAction_native_function(int action_native_function) {
        this.action_native_function = action_native_function;
    }

    public class BusnMenuItem{
        String item_name;
        String action_url;
        int action_type;
        int action_native_function;

        public BusnMenuItem(String item_name, String action_url, int action_type, int action_native_function) {
            this.item_name = item_name;
            this.action_url = action_url;
            this.action_type = action_type;
            this.action_native_function = action_native_function;
        }

        public String getItem_name() {
            return item_name;
        }

        public String getAction_url() {
            return action_url;
        }

        public int getAction_type() {
            return action_type;
        }

        public int getAction_native_function() {
            return action_native_function;
        }

        public void setItem_name(String item_name) {
            this.item_name = item_name;
        }

        public void setAction_url(String action_url) {
            this.action_url = action_url;
        }

        public void setAction_type(int action_type) {
            this.action_type = action_type;
        }

        public void setAction_native_function(int action_native_function) {
            this.action_native_function = action_native_function;
        }
    }
}
