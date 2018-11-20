package com.huanglong.v3.adapter.homepage;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huanglong.v3.R;
import com.huanglong.v3.model.homepage.VideoCommBean;
import com.huanglong.v3.utils.ItemTypeClickListener;
import com.huanglong.v3.utils.MImageOptions;

import org.xutils.x;

import java.util.List;

/**
 * Created by bin on 2018/4/27.
 * 视频的评论 adapter
 */

public class VideoCommAdapter extends RecyclerView.Adapter<VideoCommAdapter.ViewHolder> {

    private List<VideoCommBean> commBeans;

    private ItemTypeClickListener itemTypeClickListener;


    public void setData(List<VideoCommBean> commBeans) {
        this.commBeans = commBeans;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(commBeans.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (commBeans != null) {
            return commBeans.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView img_avatar, img_praise;
        private TextView tv_nickname, tv_content, tv_time, tv_praise;
        private LinearLayout praise_lin;


        public ViewHolder(View itemView) {
            super(itemView);
            img_avatar = itemView.findViewById(R.id.item_video_comm_avatar);
            img_praise = itemView.findViewById(R.id.item_video_comm_praise_icon);
            tv_nickname = itemView.findViewById(R.id.item_video_comm_nickname);
            tv_content = itemView.findViewById(R.id.item_video_comm_content);
            tv_time = itemView.findViewById(R.id.item_video_comm_time);
            tv_praise = itemView.findViewById(R.id.item_video_comm_praise_count);
            praise_lin = itemView.findViewById(R.id.item_video_comm_praise);
        }

        public void setData(VideoCommBean videoCommBean, int position) {
            x.image().bind(img_avatar, videoCommBean.getHead_image(), MImageOptions.getCircularImageOptions());
            tv_nickname.setText(videoCommBean.getNickname());
            tv_content.setText(videoCommBean.getContent());
            tv_time.setText(videoCommBean.getCreate_time());
            tv_praise.setText(videoCommBean.getUpvote_count() + "");


            int is_zan = videoCommBean.getIs_zan();
            if (is_zan == 0) {
                img_praise.setImageResource(R.mipmap.icon_praise);
            } else {
                img_praise.setImageResource(R.mipmap.icon_red_praise);
            }


            praise_lin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemTypeClickListener != null) {
                        itemTypeClickListener.onItemClick(videoCommBean, position, 1);
                    }
                }
            });
        }
    }


    public void setOnItemClickListener(ItemTypeClickListener itemClickListener) {
        this.itemTypeClickListener = itemClickListener;
    }
}
