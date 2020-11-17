package com.android.messaging.ui.conversation.chatbot.chatbotconfig;

import com.alibaba.fastjson.annotation.JSONField;

public class BotVerification {
    @JSONField(name = "verification-info")
    private VerificationInfo verification_info;

    public void setVerificationInfo(VerificationInfo verification_info) {
        this.verification_info = verification_info;
    }
    public VerificationInfo getVerificationInfo() {
        return verification_info;
    }
}
