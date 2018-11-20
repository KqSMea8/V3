package com.huanglong.v3.adapter.mine;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huanglong.v3.R;
import com.huanglong.v3.model.mine.FollowBean;
import com.huanglong.v3.utils.ItemClickListener;
import com.huanglong.v3.utils.MImageOptions;

import org.xutils.x;

import java.util.List;

/**
 * Created by bin on 2018/6/8.
 * 关注人的列表
 */

public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.ViewHolder> {

    private List<FollowBean> followBeans;

    private ItemClickListener itemClickListener;


    public void setData(List<FollowBean> followBeans) {
        this.followBeans = followBeans;
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_follow, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(followBeans.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (followBeans != null) {
            return followBeans.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView img_avatar;
        private TextView tv_nickname;
        private View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            img_avatar = itemView.findViewById(R.id.item_follow_avatar);
            tv_nickname = itemView.findViewById(R.id.item_follow_nickname);

        }

        public void setData(FollowBean followBean, int position) {
            x.image().bind(img_avatar, followBean.getHead_image(), MImageOptions.getCircularImageOptions());
            tv_nickname.setText(followBean.getNickname());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(followBean, position);
                    }
                }
            });

        }
    }

    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

}
