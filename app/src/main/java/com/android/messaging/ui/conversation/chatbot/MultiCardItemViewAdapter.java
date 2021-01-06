package com.android.messaging.ui.conversation.chatbot;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.messaging.BugleApplication;
import com.android.messaging.R;
import com.android.messaging.product.ui.WebViewNewsActivity;
import com.bumptech.glide.Glide;

import java.util.List;

public class MultiCardItemViewAdapter extends RecyclerView.Adapter {
    private List<MultiCardItemDataBean> lists;
    private Context context;
    private int resource;
    private Activity activity;

    public MultiCardItemViewAdapter(List<MultiCardItemDataBean> lists, Context context, int resource, Activity activity) {
        this.lists = lists;
        this.context = context;
        this.resource = resource;
        this.activity = activity;
    }

    class MyHolder extends RecyclerView.ViewHolder{
        private ImageView iv;
        private TextView tvDescription;
        private TextView tvTitle;
        private TextView dateTime;
        public MyHolder(View itemView) {
            super(itemView);
            iv = (ImageView) itemView.findViewById(R.id.image);
            tvDescription = (TextView) itemView.findViewById(R.id.product_description);
            tvTitle = (TextView)itemView.findViewById(R.id.tv_title);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyHolder holder =new MyHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chatbot_multicard_itemview,parent,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        LogUtil.d("TAG", "onBindViewHolder: "+lists.get(position).getAutor());
//        ((MyHolder)holder).tv1.setText(lists.get(position).getAutor());
//        ((MyHolder)holder).tv2.setText(lists.get(position).getContent());
//        GlideUtils.load(context, lists.get(position).getMediaUr(), ((MyHolder)holder).iv);
//        ((MyHolder)holder).tvDescription.setText(lists.get(position).getButtonText());
        ((MyHolder)holder).tvDescription.setText(lists.get(position).getTitle());
        Glide.with(context).load(lists.get(position).getMediaUr())
                .centerCrop()
                .into(((MyHolder)holder).iv);
        ((MyHolder)holder).iv.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                WebViewNewsActivity.start(BugleApplication.getContext(),lists.get(position).getCardContent().getExtraData1()/*getButtonAction()*/);
            }
        });
//        ConversationMessageView.loadTitleAndSuggestion(context, resource, true,  lists.get(position).getCardContent(), activity);

    }

    @Override
    public int getItemCount() {
        return lists.size();
    }
}
