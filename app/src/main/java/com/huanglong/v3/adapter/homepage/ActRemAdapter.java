package com.huanglong.v3.adapter.homepage;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huanglong.v3.R;
import com.huanglong.v3.model.homepage.ActivityBean;
import com.huanglong.v3.utils.ItemClickListener;
import com.huanglong.v3.utils.MImageOptions;

import org.xutils.x;

import java.util.List;


/**
 * Created by bin on 2018/3/20.
 * 推荐活动的adapter
 */

public class ActRemAdapter extends RecyclerView.Adapter<ActRemAdapter.ViewHolder> {

    private List<ActivityBean> activityBeans;
    private ItemClickListener itemClickListener;

    public void setData(List<ActivityBean> activityBeans) {
        this.activityBeans = activityBeans;
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity_recommend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(activityBeans.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (activityBeans != null) {
            if (activityBeans.size() > 3) {
                return 3;
            } else {
                return activityBeans.size();
            }
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView img;
        private TextView tv_play_num, tv_title;
        private View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            img = itemView.findViewById(R.id.item_activity_rec_img);
            tv_play_num = itemView.findViewById(R.id.item_activity_rec_play_number);
            tv_title = itemView.findViewById(R.id.item_activity_rec_title);
        }

        public void setData(ActivityBean activityBean, int position) {

            x.image().bind(img, activityBean.getImg_url(), MImageOptions.getNormalImageOptions());

            tv_play_num.setText(activityBean.getEnroll_count() + "人报名");
            tv_title.setText(activityBean.getTitle());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(activityBean, position);
                    }
                }
            });

        }
    }


    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

}
