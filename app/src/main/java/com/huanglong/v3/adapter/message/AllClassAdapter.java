package com.huanglong.v3.adapter.message;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huanglong.v3.R;
import com.huanglong.v3.model.home.JobClassBean;
import com.huanglong.v3.utils.ItemClickListener;

import java.util.List;

/**
 * Created by bin on 2018/3/13.
 * 全部分类的adapter
 */

public class AllClassAdapter extends RecyclerView.Adapter<AllClassAdapter.ViewHolder> {

    private List<JobClassBean> jobClassBeans;

    private ItemClickListener itemClickListener;

    public void setData(List<JobClassBean> jobClassBeans) {
        this.jobClassBeans = jobClassBeans;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all_class, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(jobClassBeans.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (jobClassBeans != null) {
            return jobClassBeans.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_class;
        private View view;
        private ImageView iv_edit, iv_del;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tv_class = itemView.findViewById(R.id.item_all_class_tv);

            iv_edit = itemView.findViewById(R.id.item_all_class_modify);
            iv_del = itemView.findViewById(R.id.item_all_class_delete);

        }

        public void setData(final JobClassBean jobClassBean, final int position) {
            tv_class.setText(jobClassBean.getName());
            iv_edit.setVisibility(View.GONE);
            iv_del.setVisibility(View.GONE);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(jobClassBean, position);
                    }
                }
            });
        }
    }


    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
