package com.huanglong.v3.adapter.homepage;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.model.homepage.PerClassBean;
import com.huanglong.v3.utils.ItemClickListener;

import java.util.List;

/**
 * Created by bin on 2018/4/18.
 * 个人主页分类
 */

public class PerClassAdapter extends RecyclerView.Adapter<PerClassAdapter.ViewHolder> {

    private List<PerClassBean> perClassBeans;
    private ItemClickListener itemClickListener;


    public void setData(List<PerClassBean> perClassBeans) {
        this.perClassBeans = perClassBeans;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_per_class, parent, false);
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

        private TextView radioButton;
        private View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            radioButton = itemView.findViewById(R.id.item_personal_page_class);
        }

        public void setData(PerClassBean perClassBean, int position) {
            radioButton.setText(perClassBean.getName());
            boolean selected = perClassBean.isSelected();
            if (selected) {
                radioButton.setTextColor(ContextCompat.getColor(V3Application.getInstance(), R.color.red_FB5638));
                radioButton.setBackgroundResource(R.color.gray_E6);
            } else {
                radioButton.setTextColor(ContextCompat.getColor(V3Application.getInstance(), R.color.gray_33));
                radioButton.setBackgroundResource(R.color.white);
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(perClassBean, position);
                    }
                }
            });

        }
    }

    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

}
