package com.huanglong.v3.adapter.mine;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huanglong.v3.R;
import com.huanglong.v3.model.mine.MGiftBean;
import com.huanglong.v3.utils.ItemClickListener;
import com.huanglong.v3.utils.MImageOptions;

import org.xutils.x;

import java.util.List;

/**
 * Created by bin on 2018/7/11.
 * 我的礼物列表适配器
 */

public class MGiftAdapter extends RecyclerView.Adapter<MGiftAdapter.ViewHolder> {


    private List<MGiftBean> mGiftBeans;
    private ItemClickListener itemClickListener;


    public void setData(List<MGiftBean> mGiftBeans) {
        this.mGiftBeans = mGiftBeans;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_gift, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(mGiftBeans.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (mGiftBeans != null) {
            return mGiftBeans.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView img_avatar, img_cover;
        private TextView tv_nickname, tv_time;
        private View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            img_avatar = itemView.findViewById(R.id.item_my_gift_avatar);
            img_cover = itemView.findViewById(R.id.item_my_gift_cover);
            tv_nickname = itemView.findViewById(R.id.item_my_gift_nickname);
            tv_time = itemView.findViewById(R.id.item_my_gift_time);


        }

        public void setData(MGiftBean mGiftBean, int position) {
            x.image().bind(img_avatar, mGiftBean.getHead_image(), MImageOptions.getCircularImageOptions());
            x.image().bind(img_cover, mGiftBean.getImg_url(), MImageOptions.getNormalImageOptions());

            tv_nickname.setText(mGiftBean.getNickname());
            tv_time.setText(mGiftBean.getCreate_time());


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(mGiftBean, position);
                    }
                }
            });

        }
    }


    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
