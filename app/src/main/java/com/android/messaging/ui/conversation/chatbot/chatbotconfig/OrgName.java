package com.android.messaging.ui.conversation.chatbot.chatbotconfig;

import com.alibaba.fastjson.annotation.JSONField;

public class OrgName {
    @JSONField(name = "display-name")
    private String display_name;
    @JSONField(name = "org-name-type")
    private String org_name_type;
    public void setDisplayName(String display_name) {
        this.display_name = display_name;
    }
    public String getDisplayName() {
        return display_name;
    }

    public void setOrgNameType(String org_name_type) {
        this.org_name_type = org_name_type;
    }
    public String getOrgNameType() {
        return org_name_type;
    }
}
