package com.huanglong.v3.adapter.homepage;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huanglong.v3.R;
import com.huanglong.v3.smallvideo.play.TCVideoInfo;
import com.huanglong.v3.utils.ItemTypeClickListener;
import com.huanglong.v3.utils.MImageOptions;
import com.huanglong.v3.utils.UserInfoUtils;

import org.xutils.x;

import java.util.List;

/**
 * Created by bin on 2018/3/20.
 * 视频推荐的adapter
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    private List<TCVideoInfo> videoBeans;
    private ItemTypeClickListener itemClickListener;

    public boolean isShowDelete = true;


    public void setData(List<TCVideoInfo> videoBeans) {
        this.videoBeans = videoBeans;
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_recommend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(videoBeans.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (videoBeans != null) {
            return videoBeans.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView img_bg, img_avatar, img_delete;
        private TextView tv_location, tv_name;
        private View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            img_bg = itemView.findViewById(R.id.item_video_rec_img);
            img_avatar = itemView.findViewById(R.id.item_video_rec_avatar);
            tv_location = itemView.findViewById(R.id.item_video_rec_location);
            tv_name = itemView.findViewById(R.id.item_video_rec_name);
            img_delete = itemView.findViewById(R.id.item_video_rec_delete);

        }

        public void setData(TCVideoInfo videoBean, int position) {
            x.image().bind(img_bg, videoBean.getCover_img(), MImageOptions.getNormalImageOptions());
            x.image().bind(img_avatar, videoBean.getHead_image(), MImageOptions.getCircularImageOptions());
            tv_location.setText(videoBean.getContent());
            tv_name.setText(videoBean.getNickname());

            if (isShowDelete) {
                String member_id = videoBean.getMember_id();
                if (TextUtils.equals(member_id, UserInfoUtils.getUid())) {
                    img_delete.setVisibility(View.VISIBLE);
                } else {
                    img_delete.setVisibility(View.GONE);
                }
            } else {
                img_delete.setVisibility(View.GONE);
            }


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(videoBean, position, 1);
                    }
                }
            });

            img_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(videoBean, position, 2);
                    }
                }
            });

        }
    }

    public void setOnItemClickListener(ItemTypeClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

}
