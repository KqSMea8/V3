package com.huanglong.v3.adapter.homepage;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.model.homepage.GiftBean;
import com.huanglong.v3.utils.ItemClickListener;

import java.util.List;

/**
 * Created by bin on 2018/5/11.
 * 礼物的adapter
 */

public class GiftAdapter extends RecyclerView.Adapter<GiftAdapter.ViewHolder> {

    private List<GiftBean> giftBeans;
    private ItemClickListener itemClickListener;


    public void setData(List<GiftBean> giftBeans) {
        this.giftBeans = giftBeans;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gift_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(giftBeans.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (giftBeans != null) {
            return giftBeans.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView img_icon;
        private TextView tv_coin;
        private LinearLayout lin;

        private View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            img_icon = itemView.findViewById(R.id.item_gift_icon);
            tv_coin = itemView.findViewById(R.id.item_gift_coin);
            lin = itemView.findViewById(R.id.item_gift_lin);

        }

        public void setData(GiftBean giftBean, int position) {

//            x.image().bind(img_icon, giftBean.getImg_url(), MImageOptions.getNormalImageOptions());

            Glide.with(V3Application.getInstance()).load(giftBean.getImg_url())
                    .placeholder(R.mipmap.icon_default)
                    .error(R.mipmap.icon_default)
                    .into(img_icon);

            tv_coin.setText(giftBean.getV_coin() + "");


            boolean selected = giftBean.isSelected();
            if (selected) {
                lin.setBackgroundResource(R.drawable.box_red_gift);
            } else {
                lin.setBackgroundColor(ContextCompat.getColor(V3Application.getInstance(), R.color.white));
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(giftBean, position);
                    }
                }
            });
        }
    }


    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
