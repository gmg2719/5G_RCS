package com.android.messaging.ui.conversation.chatbot.chatbotconfig;

import com.alibaba.fastjson.annotation.JSONField;

public class Pcc {
    @JSONField(name="pcc-type")
    private String pcc_type;
    @JSONField(name = "org-details")
    private OrgDetails org_details;
    public void setPccType(String pcc_type) {
        this.pcc_type = pcc_type;
    }
    public String getPccType() {
        return pcc_type;
    }

    public void setOrgDetails(OrgDetails org_details) {
        this.org_details = org_details;
    }
    public OrgDetails getOrg_details() {
        return org_details;
    }
}
