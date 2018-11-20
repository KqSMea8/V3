package com.huanglong.v3.adapter;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.model.homepage.LiveClassBean;
import com.huanglong.v3.utils.ItemClickListener;

import java.util.List;

/**
 * Created by bin on 2018/5/16.
 * 分类的adapter
 */

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ViewHolder> {

    private List<LiveClassBean> liveClassBeans;
    private ItemClickListener itemClickListener;

    public void setData(List<LiveClassBean> liveClassBeans) {
        this.liveClassBeans = liveClassBeans;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(liveClassBeans.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (liveClassBeans != null) {
            return liveClassBeans.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_name;
        private View line, view;


        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tv_name = itemView.findViewById(R.id.item_class_name);
            line = itemView.findViewById(R.id.item_class_line);
        }

        public void setData(LiveClassBean liveClassBean, int position) {
            tv_name.setText(liveClassBean.getName());

            boolean selected = liveClassBean.isSelected();
            if (selected) {
                line.setVisibility(View.VISIBLE);
                tv_name.setTextColor(ContextCompat.getColor(V3Application.getInstance(), R.color.colorAccent));
            } else {
                line.setVisibility(View.INVISIBLE);
                tv_name.setTextColor(ContextCompat.getColor(V3Application.getInstance(), R.color.gray_33));
            }


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(liveClassBean, position);
                    }
                }
            });

        }
    }

    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

}
