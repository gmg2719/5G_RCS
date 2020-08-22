/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.messaging.ui.conversation;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.android.messaging.R;
import com.android.messaging.datamodel.data.ConversationMessageData;
import com.android.messaging.datamodel.data.MessagePartData;
import com.android.messaging.product.entity.News;
import com.android.messaging.ui.AsyncImageView.AsyncImageViewDelayLoader;
import com.android.messaging.ui.CursorRecyclerAdapter;
import com.android.messaging.ui.conversation.ConversationMessageView.ConversationMessageViewHost;
import com.android.messaging.util.Assert;
import com.android.messaging.util.LogUtil;
import com.google.gson.Gson;

/**
 * Provides an interface to expose Conversation Message Cursor data to a UI widget like a
 * RecyclerView.
 */
public class ConversationMessageAdapter extends
    CursorRecyclerAdapter<ConversationMessageAdapter.ConversationMessageViewHolder> {

    private final ConversationMessageViewHost mHost;
    private final AsyncImageViewDelayLoader mImageViewDelayLoader;
    private final View.OnClickListener mViewClickListener;
    private final View.OnLongClickListener mViewLongClickListener;
    private boolean mOneOnOne;
    private String mSelectedMessageId;
    //add by junwang
    private static final int TYPE_WEBVIEW = 1;
    private static final int TYPE_OTHER = 2;
    private static final int TYPE_H5_CONTENT = 3;
//    private String mConverstationText;

    public ConversationMessageAdapter(final Context context, final Cursor cursor,
        final ConversationMessageViewHost host,
        final AsyncImageViewDelayLoader imageViewDelayLoader,
        final View.OnClickListener viewClickListener,
        final View.OnLongClickListener longClickListener) {
        super(context, cursor, 0);
        mHost = host;
        mViewClickListener = viewClickListener;
        mViewLongClickListener = longClickListener;
        mImageViewDelayLoader = imageViewDelayLoader;
        setHasStableIds(true);
        //add by junwang
//        mConverstationText = null;
    }

    @Override
    public void bindViewHolder(final ConversationMessageViewHolder holder,
            final Context context, final Cursor cursor) {

        Assert.isTrue(holder.mView instanceof ConversationMessageView);
        final ConversationMessageView conversationMessageView =
                (ConversationMessageView) holder.mView;
        //add by junwang
        holder.mView.requestFocus();
        ConversationMessageData cmd = new ConversationMessageData();
        cmd.bind(cursor);
        String text = cmd.getText();
        LogUtil.i("MMM", "bindViewHolder, text="+text);
        if((text != null) && (text.contains("https://")
                || text.contains("http://")
                || text.contains("rtsp://")
                || text.startsWith("{\"message\":"))){
//            mConverstationText = text;
            conversationMessageView.setTag(text);
        }else{
//            mConverstationText = null;
        }
        conversationMessageView.bind(cursor, mOneOnOne, mSelectedMessageId);
//        if(conversationMessageView.mMessageWebView != null &&
//                conversationMessageView.mMessageWebView.getVisibility() == View.VISIBLE){
//            ((ConversationMessageView) holder.mView).setTag(conversationMessageView.getConversationText());
//        }
    }

    @Override
    public ConversationMessageViewHolder createViewHolder(final Context context,
            final ViewGroup parent, final int viewType) {
        LogUtil.i("MMM", "createViewHolder");
        final LayoutInflater layoutInflater = LayoutInflater.from(context);
        final ConversationMessageView conversationMessageView = (ConversationMessageView)
                layoutInflater.inflate(R.layout.conversation_message_view, null);
        conversationMessageView.setHost(mHost);
        conversationMessageView.setImageViewDelayLoader(mImageViewDelayLoader);
        //add by junwang
//        if(viewType == TYPE_WEBVIEW){
////            conversationMessageView.setTag(mConverstationText);
////            LogUtil.w("Junwang", "createViewHolder setTag="+mConverstationText);
//        }
        return new ConversationMessageViewHolder(conversationMessageView,
                            mViewClickListener, mViewLongClickListener);
    }

    //add by junwang
    @Override
    public int getItemViewType(int position) {
        LogUtil.i("MMM", "getItemViewType, position="+position);
        Cursor c = (Cursor)getItem(position);
        ConversationMessageData cmd = new ConversationMessageData();

        cmd.bind(c);
        String text = cmd.getText();
        String jsonString = cmd.getJson();

        if((jsonString != null) && !jsonString.equals("a")){
            try {
                News temp = new Gson().fromJson(jsonString, News.class);
                if (temp != null) {
                    return News.getViewType(temp);
                }
            }catch (Exception e){
                LogUtil.i("Junwang", "Gson parse jsonString="+jsonString+" exception");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        MessagePartData.updatePartJson("a", cmd.getMessageId());
                    }
                }).start();

            }
        }else if((text != null) && (text.contains("https://")
                || text.contains("http://")
                || text.contains("rtsp://")
                || text.startsWith("{\"message\":"))){
            //mConverstationText = text;
            return TYPE_WEBVIEW;
        }else{
            //mConverstationText = null;
        }
        return TYPE_OTHER;
    }



    //    @Override
//    public View getView( int position, View convertView, ViewGroup parent) {
//        View v;
//        if (convertView == null) {
//            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.conversation_message_view, null);
//        } else {
//            v = convertView;
//        }
//        Cursor c = (Cursor)getItem(position);
//        ConversationMessageData cmd = new ConversationMessageData();
//        cmd.bind(c);
//        if(cmd.getText().startsWith("https://")
//                || cmd.getText().startsWith("http://")
//                || cmd.getText().startsWith("rtsp://")){
//            v.setTag(cmd.getText());
//        }
//        //bindView(v, mContext, mCursor);
//        return v;
//    }

    public void setSelectedMessage(final String messageId) {
        mSelectedMessageId = messageId;
        notifyDataSetChanged();
    }

    public void setOneOnOne(final boolean oneOnOne, final boolean invalidate) {
        if (mOneOnOne != oneOnOne) {
            mOneOnOne = oneOnOne;
            if (invalidate) {
                notifyDataSetChanged();
            }
        }
    }

    /**
    * ViewHolder that holds a ConversationMessageView.
    */
    public static class ConversationMessageViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        //add by junwang
        private WebView mWebView;

        /**
         * @param viewClickListener a View.OnClickListener that should define the interaction when
         *        an item in the RecyclerView is clicked.
         */
        public ConversationMessageViewHolder(final View itemView,
                final View.OnClickListener viewClickListener,
                final View.OnLongClickListener viewLongClickListener) {
            super(itemView);
            mView = itemView;

            mView.setOnClickListener(viewClickListener);
            mView.setOnLongClickListener(viewLongClickListener);
//            mView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
//                @Override
//                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                    LogUtil.i("Junwang", "message adapter onScrollChange");
//                }
//            });
//            (ConversationMessageView)mView.setTag();
            //add by junwang
//            mWebView = itemView.findViewById(R.id.message_webview);
        }
    }
}
