package com.huanglong.v3.adapter.homepage;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huanglong.v3.R;
import com.huanglong.v3.model.homepage.AlbumBean;
import com.huanglong.v3.utils.ItemClickListener;

import java.util.List;

/**
 * Created by bin on 2018/4/8.
 * 专辑列表的adapter
 */

public class AlbumListAdapter extends RecyclerView.Adapter<AlbumListAdapter.ViewHolder> {

    private List<AlbumBean> soundBookBean;
    private ItemClickListener itemClickListener;


    public void setData(List<AlbumBean> soundBookBean) {
        this.soundBookBean = soundBookBean;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(soundBookBean.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (soundBookBean != null) {
            return soundBookBean.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        private TextView tv_index, tv_title, tv_play_number, tv_comment_number, tv_date;
        private View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tv_index = itemView.findViewById(R.id.item_album_index);
            tv_title = itemView.findViewById(R.id.item_album_title);
            tv_play_number = itemView.findViewById(R.id.item_album_play_number);
            tv_comment_number = itemView.findViewById(R.id.item_album_comment_number);
            tv_date = itemView.findViewById(R.id.item_album_date);

        }

        public void setData(final AlbumBean albumBean, final int position) {
            tv_index.setText((position + 1) + "");
            tv_title.setText(albumBean.getTitle());
            tv_play_number.setText(albumBean.getPlay_count() + "");
            tv_comment_number.setText(albumBean.getComment_count() + "");
            tv_date.setText(albumBean.getCreate_time());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(albumBean, position);
                    }
                }
            });

        }
    }


    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
