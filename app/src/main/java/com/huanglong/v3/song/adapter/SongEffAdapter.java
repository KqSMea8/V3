package com.huanglong.v3.song.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.huanglong.v3.R;
import com.huanglong.v3.song.model.SongEffBean;
import com.huanglong.v3.utils.ItemTypeClickListener;
import com.huanglong.v3.utils.MImageOptions;

import org.xutils.x;

import java.util.List;

/**
 * Created by bin on 2018/5/4.
 * k歌素材的adapter
 */

public class SongEffAdapter extends RecyclerView.Adapter<SongEffAdapter.ViewHolder> {

    private List<SongEffBean> songEffBeans;


    private ItemTypeClickListener itemTypeClickListener;

    public void setData(List<SongEffBean> songEffBeans) {
        this.songEffBeans = songEffBeans;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_eff_song, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(songEffBeans.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (songEffBeans != null) {
            return songEffBeans.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView img_cover;
        private TextView tv_name, tv_album, tv_duration;
        private Button btn_k_song;
        private View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            img_cover = itemView.findViewById(R.id.item_eff_cover);
            tv_name = itemView.findViewById(R.id.item_eff_name);
            tv_album = itemView.findViewById(R.id.item_eff_album);
            tv_duration = itemView.findViewById(R.id.item_eff_duration);
            btn_k_song = itemView.findViewById(R.id.item_eff_k_song);

        }

        public void setData(SongEffBean songEffBean, int position) {
            x.image().bind(img_cover, songEffBean.getImg_url(), MImageOptions.getNormalImageOptions());
            tv_name.setText(songEffBean.getName());
            tv_album.setText(songEffBean.getIntroduce());
            tv_duration.setText(songEffBean.getDuration());

            boolean isRecord = songEffBean.getIsRecord();
            if (isRecord) {
                btn_k_song.setVisibility(View.VISIBLE);
            } else {
                btn_k_song.setVisibility(View.GONE);
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemTypeClickListener != null) {
                        itemTypeClickListener.onItemClick(songEffBean, position, 1);
                    }
                }
            });

            btn_k_song.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemTypeClickListener != null) {
                        itemTypeClickListener.onItemClick(songEffBean, position, 2);
                    }
                }
            });

        }
    }


    public void setOnItemClickListener(ItemTypeClickListener itemClickListener) {
        this.itemTypeClickListener = itemClickListener;
    }
}
