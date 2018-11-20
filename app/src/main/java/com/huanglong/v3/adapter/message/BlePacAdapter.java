package com.huanglong.v3.adapter.message;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huanglong.v3.R;
import com.huanglong.v3.model.home.BlessingBean;
import com.huanglong.v3.utils.ItemClickListener;
import com.huanglong.v3.utils.MImageOptions;

import org.xutils.x;

import java.util.List;

/**
 * Created by bin on 2018/3/17.
 * 福包adapter
 */

public class BlePacAdapter extends RecyclerView.Adapter<BlePacAdapter.ViewHolder> {


    private List<BlessingBean> blessingBeans;
    private ItemClickListener itemClickListener;

    public void setData(List<BlessingBean> blessingBeans) {
        this.blessingBeans = blessingBeans;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_blessing_packet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(blessingBeans.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (blessingBeans != null) {
            return blessingBeans.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView img_avatar, img_enterprise;
        private TextView tv_nickname, tv_content, tv_type, tv_date;
        private View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            img_avatar = itemView.findViewById(R.id.item_bless_packet_avatar);
            tv_nickname = itemView.findViewById(R.id.item_bless_packet_nickname);
            tv_content = itemView.findViewById(R.id.item_bless_packet_content);
            tv_type = itemView.findViewById(R.id.item_bless_packet_type);
            tv_date = itemView.findViewById(R.id.item_bless_packet_date);
            img_enterprise = itemView.findViewById(R.id.item_bless_packet_enterprise);
        }

        public void setData(final BlessingBean blessingBean, int position) {

            x.image().bind(img_avatar, blessingBean.getHead_image(), MImageOptions.getCircularImageOptions());

            tv_nickname.setText(blessingBean.getNickname());
            tv_content.setText(blessingBean.getContent());
            tv_date.setText(blessingBean.getCreate_time());
            int type = blessingBean.getType();
            if (type == 1) {
                tv_type.setText("个人");
                img_enterprise.setVisibility(View.GONE);
            } else {
                tv_type.setText("企业");
                img_enterprise.setVisibility(View.VISIBLE);
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClickListener.onItemClick(blessingBean, position);
                }
            });

        }
    }


    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
