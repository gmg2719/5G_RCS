package com.android.messaging.ui.conversation.chatbot;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.messaging.R;
import com.android.messaging.ui.conversation.RoundedCornerCenterCrop;
import com.android.messaging.util.LogUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class FavoriteCardItemViewAdapter extends FavoriteBaseAdapter<FavoriteCardItemViewAdapter.MyHolder> {
    private List<ChatbotFavoriteEntity> lists;
    private Context context;

    public FavoriteCardItemViewAdapter(Context context) {
        super(context);
        this.context = context;
    }

    public void notifyDataSetChanged(List<ChatbotFavoriteEntity> dataList) {
        this.lists = dataList;
        super.notifyDataSetChanged();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        private ImageView title_image;
        private TextView tv_title;
        private ImageView iv;
        private TextView tv_description;
//        private TextView tvTitle;
        private TextView tv_date;
        private View view;
        public MyHolder(View itemView) {
            super(itemView);
            title_image = (ImageView)itemView.findViewById(R.id.title_image);
            tv_title = (TextView)itemView.findViewById(R.id.tv_title);
            iv = (ImageView) itemView.findViewById(R.id.fav_card_image);
            tv_description = (TextView) itemView.findViewById(R.id.fav_card_description);
//            tvTitle = (TextView)itemView.findViewById(R.id.tv_title);
            tv_date = (TextView)itemView.findViewById(R.id.fav_card_date);
            view = itemView;
        }
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyHolder holder =new MyHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite_card, parent,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        LogUtil.i("Junwang","FavoriteCardItemViewAdapter onBindViewHolder");

        ((MyHolder)holder).tv_title.setText(lists.get(position).getChatbot_fav_name());
        if(lists.get(position).getChatbot_fav_logo() != null) {
            ((MyHolder) holder).title_image.setImageURI(Uri.parse(lists.get(position).getChatbot_fav_logo()));
        }else{
            ((MyHolder) holder).title_image.setImageResource(R.drawable.icon_news);
        }
        ((MyHolder)holder).tv_description.setText(lists.get(position).getChatbot_fav_card_description());
        ((MyHolder)holder).tv_date.setText(lists.get(position).getChatbot_fav_saved_date());
        String imgUrl = lists.get(position).getChatbot_fav_image_url();
        if(imgUrl != null) {
            RequestOptions options = new RequestOptions().error(R.drawable.msg_bubble_error).bitmapTransform(new RoundedCornerCenterCrop(15));//图片圆角为30
            Glide.with(context).load(imgUrl)
                    .apply(options)
                    .into(((MyHolder) holder).iv);
//            Glide.with(context).load(imgUrl)
//                    .centerCrop()
//                    .into(((MyHolder) holder).iv);
//            ((MyHolder) holder).view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
////                WebViewNewsActivity.start(BugleApplication.getContext(),lists.get(position).getButtonAction());
//                }
//            });
        }

    }

    @Override
    public int getItemCount() {
        return lists.size();
    }
}
