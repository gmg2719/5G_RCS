package com.android.messaging.ui.conversation.chatbot.chatbotconfig;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class OrgDetails {
    @JSONField(name = "org-name")
    private List<OrgName> org_name;
    @JSONField(name = "comm-addr")
    private CommAddr comm_addr;
    @JSONField(name = "media-list")
    private MediaList media_list;
    @JSONField(name = "org-description")
    private String org_description;
    public void setOrgName(List<OrgName> org_name) {
        this.org_name = org_name;
    }
    public List<OrgName> getOrgName() {
        return org_name;
    }

    public void setCommAddr(CommAddr comm_addr) {
        this.comm_addr = comm_addr;
    }
    public CommAddr getCommAddr() {
        return comm_addr;
    }

    public void setMediaList(MediaList media_list) {
        this.media_list = media_list;
    }
    public MediaList getMediaList() {
        return media_list;
    }

    public void setOrgDescription(String org_description) {
        this.org_description = org_description;
    }
    public String getOrgDescription() {
        return org_description;
    }
}
