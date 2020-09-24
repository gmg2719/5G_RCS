package com.android.messaging.ui.conversation.chatbot.chatbotconfig;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

public class VerificationInfo {
    private boolean verified;
    @JSONField(name = "verified-by")
    private String verified_by;
    private Date expires;
    public void setVerified(boolean verified) {
        this.verified = verified;
    }
    public boolean getVerified() {
        return verified;
    }

    public void setVerifiedBy(String verified_by) {
        this.verified_by = verified_by;
    }
    public String getVerifiedBy() {
        return verified_by;
    }

    public void setExpires(Date expires) {
        this.expires = expires;
    }
    public Date getExpires() {
        return expires;
    }
}
