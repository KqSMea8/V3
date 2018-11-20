package com.huanglong.v3.adapter.circle;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huanglong.v3.R;
import com.huanglong.v3.model.circle.PraiseBean;

import java.util.List;

/**
 * Created by bin on 2018/7/4.
 * 赞列表的adapter
 */

public class ZanListAdapter extends RecyclerView.Adapter<ZanListAdapter.ViewHolder> {

    private List<PraiseBean> upvote_list;

    public void setData(List<PraiseBean> upvote_list) {
        this.upvote_list = upvote_list;
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_zan_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(upvote_list.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (upvote_list != null) {
            return upvote_list.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_zan;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_zan = itemView.findViewById(R.id.item_space_zan);

        }

        public void setData(PraiseBean praiseBean, int position) {
            if (position == upvote_list.size() - 1) {
                tv_zan.setText(praiseBean.getNickname());
            } else {
                tv_zan.setText(praiseBean.getNickname() + ",");
            }
        }
    }
}
