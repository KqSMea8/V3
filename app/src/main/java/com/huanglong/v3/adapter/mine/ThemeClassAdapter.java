package com.huanglong.v3.adapter.mine;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huanglong.v3.R;
import com.huanglong.v3.model.homepage.PerClassBean;
import com.huanglong.v3.utils.ItemTypeClickListener;

import java.util.List;

/**
 * Created by bin on 2018/3/13.
 * 主题分类的adapter
 */

public class ThemeClassAdapter extends RecyclerView.Adapter<ThemeClassAdapter.ViewHolder> {

    private List<PerClassBean> perClassBeans;

    private ItemTypeClickListener itemTypeClickListener;

    public void setData(List<PerClassBean> perClassBeans) {
        this.perClassBeans = perClassBeans;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all_class, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(perClassBeans.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (perClassBeans != null) {
            return perClassBeans.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_class;
        private View view;
        private ImageView img_modify, img_delete;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tv_class = itemView.findViewById(R.id.item_all_class_tv);
            img_modify = itemView.findViewById(R.id.item_all_class_modify);
            img_delete = itemView.findViewById(R.id.item_all_class_delete);
        }

        public void setData(final PerClassBean perClassBean, final int position) {
            tv_class.setText(perClassBean.getName());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemTypeClickListener != null) {
                        itemTypeClickListener.onItemClick(perClassBean, position, 1);
                    }
                }
            });


            img_modify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemTypeClickListener != null) {
                        itemTypeClickListener.onItemClick(perClassBean, position, 2);
                    }
                }
            });

            img_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemTypeClickListener != null) {
                        itemTypeClickListener.onItemClick(perClassBean, position, 3);
                    }
                }
            });
        }
    }


    public void setOnItemClickListener(ItemTypeClickListener itemTypeClickListener) {
        this.itemTypeClickListener = itemTypeClickListener;
    }
}
