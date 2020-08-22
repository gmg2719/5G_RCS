package com.android.messaging.ui.conversation.chatbot;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.messaging.R;
import com.android.messaging.util.LogUtil;

import java.util.List;

public class VoteListAdapter extends ArrayAdapter {
    int resourceId;
    List voteList;

    public VoteListAdapter(@NonNull Context context, int resource, List voteList) {
        super(context, resource);
        this.resourceId = resourceId;
        this.voteList = voteList;
    }

    @Override
    public int getCount() {
        return voteList.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // 加个判断，以免ListView每次滚动时都要重新加载布局，以提高运行效率
        View view;
        ViewHolder viewHolder;
        if (convertView==null){

            // 避免ListView每次滚动时都要重新加载布局，以提高运行效率
            view= LayoutInflater.from(getContext()).inflate(R.layout.vote_item, parent,false);

            // 避免每次调用getView()时都要重新获取控件实例
            viewHolder=new ViewHolder();
            viewHolder.voteText=view.findViewById(R.id.vote_item_text);

            // 将ViewHolder存储在View中（即将控件的实例存储在其中）
            view.setTag(viewHolder);
        } else{
            view=convertView;
            viewHolder=(ViewHolder) view.getTag();
        }

        // 获取控件实例，并调用set...方法使其显示出来
        viewHolder.voteText.setText((String)voteList.get(position));
        LogUtil.i("Junwang", "vote text="+(String)voteList.get(position));
        return view;
    }

    // 定义一个内部类，用于对控件的实例进行缓存
    class ViewHolder{
        TextView voteText;
        ImageView selectImage;
    }
}
