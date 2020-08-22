package com.android.messaging.ui.conversation.santilayout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.messaging.R;
import com.android.messaging.product.entity.News;
import com.android.messaging.product.utils.GlideUtils;
import com.android.messaging.util.LogUtil;

import java.util.List;

public class ExpandListViewAdapter extends BaseAdapter {
    private List<News> mList;
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    public ExpandListViewAdapter(Context context, List<News> mList) {
        this.mList = mList;
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @Override
    public int getCount() {
        if(mList == null){
            return 0;
        }
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        if(mList == null){
            return null;
        }
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = mLayoutInflater.inflate(R.layout.expand_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.expand_title = (TextView)convertView.findViewById(R.id.tv_title);
            viewHolder.expand_imageView = (ImageView)convertView.findViewById(R.id.expand_iv_img);
            viewHolder.expand_ll_duration = (LinearLayout)convertView.findViewById(R.id.expand_ll_duration);
            viewHolder.expand_iv_play = (ImageView)convertView.findViewById(R.id.expand_iv_play);
            viewHolder.expand_tv_duration = (TextView)convertView.findViewById(R.id.expand_tv_duration);
            convertView.setTag(viewHolder);
//            LogUtil.i("ExpandLayout", "position1="+position+", image_url="+mList.get(position).middle_image.url);
//            GlideUtils.load(mContext, mList.get(position).middle_image.url, viewHolder.expand_imageView);//右侧图片或视频的图片使用middle_image
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
            LogUtil.i("ExpandLayout", "position2="+position);
        }
        viewHolder.expand_title.setText(mList.get(position).getTitle());
        LogUtil.i("ExpandLayout", "title="+mList.get(position).getTitle());
        GlideUtils.load(mContext, mList.get(position).getMiddle_image().url, viewHolder.expand_imageView);//右侧图片或视频的图片使用middle_image
        return convertView;
    }

    private class ViewHolder {
        public TextView expand_title;
        public ImageView expand_imageView;
        public LinearLayout expand_ll_duration;
        public ImageView expand_iv_play;
        public TextView expand_tv_duration;
    }

}
