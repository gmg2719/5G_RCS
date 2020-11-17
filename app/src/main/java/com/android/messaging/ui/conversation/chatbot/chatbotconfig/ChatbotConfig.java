package com.android.messaging.ui.conversation.chatbot.chatbotconfig;

import com.alibaba.fastjson.annotation.JSONField;

public class ChatbotConfig {
    private BotInfo botinfo;
    @JSONField(name = "bot-verification")
    private BotVerification bot_verification;
    public void setBotinfo(BotInfo botinfo) {
        this.botinfo = botinfo;
    }
    public BotInfo getBotinfo() {
        return botinfo;
    }

    public void setBotVerification(BotVerification bot_verification) {
        this.bot_verification = bot_verification;
    }
    public BotVerification getBotVerification() {
        return bot_verification;
    }
}
