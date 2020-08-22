package com.android.messaging.ui.conversation.chatbot;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class VoteListView extends ListView {
    public VoteListView(Context context) {
        super(context);
    }

    public VoteListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int newHeightSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, newHeightSpec);
    }
}
