package com.huanglong.v3.adapter.homepage;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.huanglong.v3.R;
import com.huanglong.v3.model.homepage.MatterSongBean;
import com.huanglong.v3.utils.ItemTypeClickListener;
import com.huanglong.v3.utils.MImageOptions;

import org.xutils.x;

import java.util.List;

/**
 * Created by bin on 2018/4/25.
 * 素材歌曲的adapter
 */

public class MatterSongAdapter extends RecyclerView.Adapter<MatterSongAdapter.ViewHolder> {

    private List<MatterSongBean> matterSongBeans;
    private ItemTypeClickListener itemTypeClickListener;


    public void setData(List<MatterSongBean> matterSongBeans) {
        this.matterSongBeans = matterSongBeans;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_matter_song, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(matterSongBeans.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (matterSongBeans != null) {
            return matterSongBeans.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView img;
        private TextView tv_name, tv_album, tv_duration;
        private Button btn_camera;

        private View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            img = itemView.findViewById(R.id.item_matter_song_img);
            tv_name = itemView.findViewById(R.id.item_matter_song_name);
            tv_album = itemView.findViewById(R.id.item_matter_song_album);
            tv_duration = itemView.findViewById(R.id.item_matter_song_duration);
            btn_camera = itemView.findViewById(R.id.item_matter_song_camera);

        }

        public void setData(MatterSongBean matterSongBean, int position) {

            x.image().bind(img, matterSongBean.getImg_url(), MImageOptions.getNormalImageOptions());
            tv_name.setText(matterSongBean.getName());
            tv_album.setText(matterSongBean.getIntroduce());
            tv_duration.setText(matterSongBean.getDuration());

            boolean camera = matterSongBean.isCamera();
            if (camera) {
                btn_camera.setVisibility(View.VISIBLE);
            } else {
                btn_camera.setVisibility(View.GONE);
            }


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemTypeClickListener != null) {
                        itemTypeClickListener.onItemClick(matterSongBean, position, 1);
                    }
                }
            });

            btn_camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemTypeClickListener != null) {
                        itemTypeClickListener.onItemClick(matterSongBean, position, 2);
                    }
                }
            });


        }
    }

    public void setOnItemClickListener(ItemTypeClickListener itemTypeClickListener) {
        this.itemTypeClickListener = itemTypeClickListener;
    }

}
