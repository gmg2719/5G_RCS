package com.android.messaging.ui.conversation.chatbot.vote;

import android.view.View;

public interface VoteObserver {
    void update(View view, boolean status, boolean isRefresh);
    void updateProgressBarBorder(View view);
    void setTotalNumber(int totalNumber);
    int getSelectedItem();
}
