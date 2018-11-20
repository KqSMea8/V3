package com.huanglong.v3.adapter.homepage;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huanglong.v3.R;
import com.huanglong.v3.model.homepage.KSFBean;
import com.huanglong.v3.utils.ItemTypeClickListener;
import com.huanglong.v3.utils.MImageOptions;
import com.huanglong.v3.utils.TransformationUtils;
import com.huanglong.v3.utils.UserInfoUtils;

import org.xutils.x;

import java.util.List;

/**
 * Created by bin on 2018/4/7.
 * K歌关注的列表
 */

public class KSFAdapter extends RecyclerView.Adapter<KSFAdapter.ViewHolder> {

    private List<KSFBean> ksfBeans;

    private ItemTypeClickListener itemClickListener;

    public void setData(List<KSFBean> ksfBeans) {
        this.ksfBeans = ksfBeans;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_k_song_follow, parent, false);
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

        private ImageView img_avatar, img_cover, img_delete;
        private TextView tv_nickname, tv_time, tv_cover_title, tv_cover_number, tv_cover_dis, tv_content;
        private TextView tv_gift_num, tv_comment_num, tv_distance, tv_share_num;
        private View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            img_avatar = itemView.findViewById(R.id.item_k_song_avatar);
            img_cover = itemView.findViewById(R.id.item_k_song_cover);
            tv_nickname = itemView.findViewById(R.id.item_k_song_nickname);
            tv_time = itemView.findViewById(R.id.item_k_song_time);
            tv_cover_title = itemView.findViewById(R.id.item_k_song_cover_title);
            tv_cover_number = itemView.findViewById(R.id.item_k_song_cover_number);
            tv_cover_dis = itemView.findViewById(R.id.item_k_song_cover_dis);
            tv_content = itemView.findViewById(R.id.item_k_song_content);
            tv_gift_num = itemView.findViewById(R.id.item_k_song_gift_number);
            tv_comment_num = itemView.findViewById(R.id.item_k_song_comment_number);
            img_delete = itemView.findViewById(R.id.item_k_song_delete);
            tv_distance = itemView.findViewById(R.id.item_k_song_distance);
            tv_share_num = itemView.findViewById(R.id.item_k_song_share_number);

        }

        public void setData(final KSFBean ksfBean, final int position) {

            x.image().bind(img_avatar, ksfBean.getHead_image(), MImageOptions.getCircularImageOptions());
            x.image().bind(img_cover, ksfBean.getCover_img(), MImageOptions.getNormalImageOptions());
            tv_nickname.setText(ksfBean.getNickname());
            tv_time.setText(ksfBean.getCreate_time());
            tv_cover_title.setText(ksfBean.getTitle());
            tv_cover_number.setText(ksfBean.getClick_count() + "收听  " + ksfBean.getComment_count() + "人评论");
            tv_cover_dis.setText("好友擂主" + ksfBean.getPoints() + "分");
            tv_content.setText(ksfBean.getContent());
            tv_gift_num.setText(ksfBean.getGift_count() + "");
            tv_comment_num.setText(ksfBean.getComment_count() + "");
            tv_share_num.setText(ksfBean.getShare_count() + "");

            double distance = ksfBean.getDistance();
            if (distance > 1000) {
                String str_distance = TransformationUtils.doubleDecimal(distance / 1000);
                tv_distance.setText(str_distance + "km");
            } else {
                tv_distance.setText(ksfBean.getDistance() + "m");
            }


            String member_id = ksfBean.getMember_id();
            if (TextUtils.equals(member_id, UserInfoUtils.getUid())) {
                img_delete.setVisibility(View.VISIBLE);
            } else {
                img_delete.setVisibility(View.GONE);
            }


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(ksfBean, position, 1);
                    }
                }
            });

            img_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(ksfBean, position, 2);
                    }
                }
            });

            img_avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(ksfBean, position, 3);
                    }
                }
            });


        }
    }

    public void setOnItemClickListener(ItemTypeClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


}
