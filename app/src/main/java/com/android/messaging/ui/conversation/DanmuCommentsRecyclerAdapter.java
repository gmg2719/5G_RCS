package com.android.messaging.ui.conversation;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.messaging.R;

import java.util.List;

public class DanmuCommentsRecyclerAdapter extends RecyclerView.Adapter<DanmuCommentsRecyclerAdapter.ViewHolder> {
    private List<DanmuComments> danmuComments;
    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public TextView content;

        public ViewHolder(View itemView){
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.comments_name);
            content = (TextView)itemView.findViewById(R.id.comments_content);
        }
    }

    public DanmuCommentsRecyclerAdapter(List<DanmuComments> comments){
        danmuComments = comments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.danmu_comments_item, viewGroup,false);
        View view= View.inflate(viewGroup.getContext(), R.layout.danmu_comments_item, null);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        DanmuComments dc = danmuComments.get(i);
        viewHolder.name.setText(dc.getName());
        viewHolder.content.setText(dc.getContent());
    }

//    @Override
//    public int getItemCount() {
//        return danmuComments.size();
//    }


    @Override
    public int getItemCount() {
//        if (danmuComments!=null){
//            if (danmuComments.size()<=30)
//                return danmuComments.size();
//            else
//                return danmuComments.size()*20;//return Integer.MAX_VALUE;
//        }else {
//            return 0;
//        }
        if(danmuComments != null){
            return danmuComments.size();
        }
        else{
            return 0;
        }
    }
}
