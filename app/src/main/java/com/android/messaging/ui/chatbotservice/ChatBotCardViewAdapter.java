package com.android.messaging.ui.chatbotservice;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.android.messaging.BugleApplication;
import com.android.messaging.R;
import com.android.messaging.product.ui.WebViewNewsActivity;
import com.android.messaging.product.utils.GlideUtils;

import java.util.List;

public class ChatBotCardViewAdapter extends RecyclerView.Adapter {
    private List<ChatBotDataBean> lists;
    private Context context;

    public ChatBotCardViewAdapter(List<ChatBotDataBean> lists, Context context) {
        this.lists = lists;
        this.context = context;
    }

    class MyHolder extends RecyclerView.ViewHolder{
        private ImageView iv;
        private Button bt1;
        private Button bt2;
        private Button bt3;
        public MyHolder(View itemView) {
            super(itemView);
            iv = (ImageView) itemView.findViewById(R.id.image);
            bt1 = (Button) itemView.findViewById(R.id.action_button1);
            bt2 = (Button) itemView.findViewById(R.id.action_button2);
            bt3 = (Button) itemView.findViewById(R.id.action_button3);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyHolder holder =new MyHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chatbot_cardview,parent,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        LogUtil.d("TAG", "onBindViewHolder: "+lists.get(position).getAutor());
//        ((MyHolder)holder).tv1.setText(lists.get(position).getAutor());
//        ((MyHolder)holder).tv2.setText(lists.get(position).getContent());
        GlideUtils.load(context, lists.get(position).getMediaUr(), ((MyHolder)holder).iv);
        ((MyHolder)holder).bt1.setText(lists.get(position).getButtonText());
        ((MyHolder)holder).bt1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                WebViewNewsActivity.start(BugleApplication.getContext(),lists.get(position).getButtonAction());
            }
        });

    }

    @Override
    public int getItemCount() {
        return lists.size();
    }
}
