package com.huanglong.v3.adapter.homepage;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huanglong.v3.R;
import com.huanglong.v3.model.homepage.GiftListBean;
import com.huanglong.v3.utils.ItemClickListener;
import com.huanglong.v3.utils.MImageOptions;

import org.xutils.x;

import java.util.List;

/**
 * Created by bin on 2018/6/6.
 * 礼物榜的adapter
 */

public class GiftListAdapter extends RecyclerView.Adapter<GiftListAdapter.ViewHolder> {

    private List<GiftListBean> giftListBeans;
    private ItemClickListener itemClickListener;


    public void setData(List<GiftListBean> giftListBeans) {
        this.giftListBeans = giftListBeans;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gift_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(giftListBeans.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (giftListBeans != null) {
            return giftListBeans.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_sort, tv_nickname, tv_contribution;
        private ImageView img_avatar, img_cover;
        private View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tv_sort = itemView.findViewById(R.id.item_gift_list_sort);
            tv_nickname = itemView.findViewById(R.id.item_gift_list_nickname);
            tv_contribution = itemView.findViewById(R.id.item_gift_list_contribution);
            img_avatar = itemView.findViewById(R.id.item_gift_list_avatar);
            img_cover = itemView.findViewById(R.id.item_gift_list_cover);


        }

        public void setData(GiftListBean giftListBean, int position) {
            tv_sort.setText((position + 1) + "");
            tv_nickname.setText(giftListBean.getNickname());
            x.image().bind(img_avatar, giftListBean.getHead_image(), MImageOptions.getCircularImageOptions());
            x.image().bind(img_cover, giftListBean.getImg_url(), MImageOptions.getNormalImageOptions());


            tv_contribution.setText("贡献 " + giftListBean.getV_coin() + " v3币");

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(giftListBean, position);
                    }
                }
            });
        }
    }


    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
