package com.huanglong.v3.adapter.homepage;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huanglong.v3.R;
import com.huanglong.v3.model.homepage.GiftUserBean;
import com.huanglong.v3.utils.ItemClickListener;
import com.huanglong.v3.utils.MImageOptions;

import org.xutils.x;

import java.util.List;

/**
 * Created by bin on 2018/4/9.
 * 礼物榜头像的Adapter
 */

public class GiftAvatarAdapter extends RecyclerView.Adapter<GiftAvatarAdapter.ViewHolder> {

    private List<GiftUserBean> giftUserBeans;
    private ItemClickListener itemClickListener;

    public void setData(List<GiftUserBean> giftUserBeans) {
        this.giftUserBeans = giftUserBeans;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gift_avatar, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(giftUserBeans.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (giftUserBeans != null) {
            if (giftUserBeans.size() > 5) {
                return 5;
            } else {
                return giftUserBeans.size();
            }
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
            img_avatar = itemView.findViewById(R.id.item_gift_avatar);
            tv_nickname = itemView.findViewById(R.id.item_gift_nickname);

        }

        public void setData(GiftUserBean giftUserBean, int position) {
            x.image().bind(img_avatar, giftUserBean.getHead_image(), MImageOptions.getCircularImageOptions());
            tv_nickname.setText(giftUserBean.getNickname());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(giftUserBean, position);
                    }
                }
            });

        }
    }


    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
