package com.huanglong.v3.adapter.mine;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huanglong.v3.R;
import com.huanglong.v3.model.mine.FlexibleBean;
import com.huanglong.v3.utils.ItemClickListener;
import com.huanglong.v3.utils.MImageOptions;

import org.xutils.x;

import java.util.List;

/**
 * Created by bin on 2018/4/16.
 * 活动的适配器
 */

public class FlexibleAdapter extends RecyclerView.Adapter<FlexibleAdapter.ViewHolder> {

    private List<FlexibleBean> flexibleBeans;

    private ItemClickListener itemClickListener;


    public void setData(List<FlexibleBean> flexibleBeans) {
        this.flexibleBeans = flexibleBeans;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_flexible, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(flexibleBeans.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (flexibleBeans != null) {
            return flexibleBeans.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView img_cover;
        private TextView tv_content, tv_time, tv_works;
        private View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            img_cover = itemView.findViewById(R.id.item_flexible_img);
            tv_content = itemView.findViewById(R.id.item_flexible_content);
            tv_time = itemView.findViewById(R.id.item_flexible_time);
            tv_works = itemView.findViewById(R.id.item_flexible_works);


        }

        public void setData(FlexibleBean flexibleBean, int position) {
            x.image().bind(img_cover, flexibleBean.getImg_url(), MImageOptions.getNormalImageOptions());
            tv_content.setText(flexibleBean.getTitle());
            tv_time.setText(flexibleBean.getDistance_day());
            tv_works.setText("参赛作品:" + flexibleBean.getZuopin_count());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(flexibleBean, position);
                    }
                }
            });

        }
    }


    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
