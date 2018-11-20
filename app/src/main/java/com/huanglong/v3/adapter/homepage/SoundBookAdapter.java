package com.huanglong.v3.adapter.homepage;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huanglong.v3.R;
import com.huanglong.v3.model.homepage.SoundBookBean;
import com.huanglong.v3.utils.ItemTypeClickListener;
import com.huanglong.v3.utils.MImageOptions;
import com.huanglong.v3.utils.UserInfoUtils;

import org.xutils.x;

import java.util.List;

/**
 * Created by bin on 2018/4/5.
 * 有声书的adapter
 */

public class SoundBookAdapter extends RecyclerView.Adapter<SoundBookAdapter.ViewHolder> {

    private List<SoundBookBean> soundBookBean;
    private ItemTypeClickListener itemClickListener;

    public void setData(List<SoundBookBean> soundBookBean) {
        this.soundBookBean = soundBookBean;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sound_book, parent, false);
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

        private ImageView img_icon, img_new, img_delete;
        private TextView tv_title, tv_content, tv_play, tv_drama;
        private View view;


        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            img_icon = itemView.findViewById(R.id.item_sound_book_img);
            img_new = itemView.findViewById(R.id.item_sound_book_new);
            tv_title = itemView.findViewById(R.id.item_sound_book_title);
            tv_content = itemView.findViewById(R.id.item_sound_book_content);
            tv_play = itemView.findViewById(R.id.item_sound_book_play);
            tv_drama = itemView.findViewById(R.id.item_sound_book_drama);
            img_delete = itemView.findViewById(R.id.item_sound_book_delete);

        }

        public void setData(final SoundBookBean soundBookBean, final int position) {
            x.image().bind(img_icon, soundBookBean.getCover_img(), MImageOptions.getNormalImageOptions());
            tv_title.setText(soundBookBean.getTitle());
            tv_content.setText(soundBookBean.getIntroduce());
            tv_play.setText(soundBookBean.getPlay_count() + "");
            tv_drama.setText(soundBookBean.getCollect_count() + "集");


            String member_id = soundBookBean.getMember_id();
            if (TextUtils.equals(member_id, UserInfoUtils.getUid())) {
                img_delete.setVisibility(View.VISIBLE);
            } else {
                img_delete.setVisibility(View.GONE);
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(soundBookBean, position, 1);
                    }
                }
            });


            img_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(soundBookBean, position, 2);
                    }
                }
            });

        }
    }


    public void setOnItemClickListener(ItemTypeClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

}
