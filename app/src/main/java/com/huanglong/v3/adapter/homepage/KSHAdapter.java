package com.huanglong.v3.adapter.homepage;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huanglong.v3.R;
import com.huanglong.v3.model.homepage.KSFBean;
import com.huanglong.v3.utils.ItemClickListener;
import com.huanglong.v3.utils.MImageOptions;

import org.xutils.x;

import java.util.List;

/**
 * Created by bin on 2018/4/7.
 * K歌热门的列表
 */

public class KSHAdapter extends RecyclerView.Adapter<KSHAdapter.ViewHolder> {

    private List<KSFBean> ksfBeans;

    private ItemClickListener itemClickListener;


    public void setData(List<KSFBean> ksfBeans) {
        this.ksfBeans = ksfBeans;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_k_song_hot, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(ksfBeans.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (ksfBeans != null) {
            return ksfBeans.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView img_avatar, img_cover;
        private TextView tv_cover_title, tv_cover_number, tv_nickname;
        private View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            img_avatar = itemView.findViewById(R.id.item_k_song_h_avatar);
            img_cover = itemView.findViewById(R.id.item_k_song_h_cover);
            tv_cover_title = itemView.findViewById(R.id.item_k_song_h_title);
            tv_cover_number = itemView.findViewById(R.id.item_k_song_h_number);
            tv_nickname = itemView.findViewById(R.id.item_k_song_h_nickname);

        }

        public void setData(KSFBean ksfBean, int position) {

            x.image().bind(img_avatar, ksfBean.getHead_image(), MImageOptions.getCircularImageOptions());
            x.image().bind(img_cover, ksfBean.getCover_img(), MImageOptions.getNormalImageOptions());
            tv_nickname.setText(ksfBean.getNickname());
            tv_cover_title.setText(ksfBean.getContent());
            tv_cover_number.setText(ksfBean.getClick_count() + "");

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(ksfBean, position);
                    }
                }
            });


//            img_avatar.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent();
//                    intent.setClass(V3Application.getInstance(), PersonalPageActivity.class);
//                    intent.putExtra("uid", ksfBean.getMember_id());
//                    V3Application.getInstance().startActivity(intent);
//                }
//            });

        }
    }


    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
