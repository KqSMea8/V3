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
 * Created by bin on 2018/5/8.
 * 其他作品的adapter
 */

public class OtherZuoPinAdapter extends RecyclerView.Adapter<OtherZuoPinAdapter.ViewHolder> {


    private List<KSFBean> kDetBeans;
    private ItemClickListener itemClickListener;


    public void setData(List<KSFBean> kDetBeans) {
        this.kDetBeans = kDetBeans;
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_other_zuopin, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(kDetBeans.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (kDetBeans != null) {
            return kDetBeans.size();
        }

        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        private ImageView img_cover;
        private TextView tv_title, tv_date;
        private View view;


        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            img_cover = itemView.findViewById(R.id.item_other_zuopin_cover);
            tv_title = itemView.findViewById(R.id.item_other_zuopin_title);
            tv_date = itemView.findViewById(R.id.item_other_zuopin_date);


        }

        public void setData(KSFBean kDetBean, int position) {

            x.image().bind(img_cover, kDetBean.getCover_img(), MImageOptions.getNormalImageOptions());
            tv_title.setText(kDetBean.getContent());
            tv_date.setText(kDetBean.getCreate_time());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(kDetBean, position);
                    }
                }
            });
        }
    }


    public void setItemOnClickListener(ItemClickListener itemOnClickListener) {
        this.itemClickListener = itemOnClickListener;
    }
}
