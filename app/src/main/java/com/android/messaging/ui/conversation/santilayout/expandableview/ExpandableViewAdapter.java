package com.android.messaging.ui.conversation.santilayout.expandableview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.messaging.R;
import com.android.messaging.product.entity.News;
import com.android.messaging.product.utils.GlideUtils;

import java.util.List;

public class ExpandableViewAdapter extends RecyclerView.Adapter<ExpandableViewAdapter.ViewHolder> {
    private List<News> mList;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private int mLayoutId;
    public ExpandableViewAdapter() {
        super();
    }

    public ExpandableViewAdapter(Context context, List<News> list) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.expand_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.expand_title.setText(mList.get(position).getTitle());
        GlideUtils.load(mContext, mList.get(position).getMiddle_image().url, holder.expand_imageView);//右侧图片或视频的图片使用middle_image
    }

    @Override
    public int getItemCount() {
        if(mList == null){
            return 0;
        }
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements ExpandableViewHoldersUtil.Expandable {
        public TextView expand_title;
        public ImageView expand_imageView;
        public LinearLayout expand_ll_duration;
        public ImageView expand_iv_play;
        public TextView expand_tv_duration;
        public LinearLayout expand_list_layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

//            keepOne = ExpandableViewHoldersUtil.getInstance().getKeepOneHolder();

            expand_list_layout = (LinearLayout)itemView.findViewById(R.id.expand_list_layout);
            expand_title = (TextView)itemView.findViewById(R.id.tv_title);
            expand_imageView = (ImageView)itemView.findViewById(R.id.expand_iv_img);
            expand_ll_duration = (LinearLayout)itemView.findViewById(R.id.expand_ll_duration);
            expand_iv_play = (ImageView)itemView.findViewById(R.id.expand_iv_play);
            expand_tv_duration = (TextView)itemView.findViewById(R.id.expand_tv_duration);

            expand_list_layout.setVisibility(View.GONE);
            expand_list_layout.setAlpha(0);
        }

        @Override
        public View getExpandView() {
            return expand_list_layout;
        }

        @Override
        public void doCustomAnim(boolean isOpen) {
//            if (isOpen) {
//                ExpandableViewHoldersUtil.getInstance().rotateExpandIcon(arrowImage, 180, 0);
//            } else {
//                ExpandableViewHoldersUtil.getInstance().rotateExpandIcon(arrowImage, 0, 180);
//            }
        }
    }
}
