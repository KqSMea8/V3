package com.huanglong.v3.adapter.message;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huanglong.v3.R;
import com.huanglong.v3.model.YelInfoBean;
import com.huanglong.v3.utils.ItemClickListener;
import com.huanglong.v3.utils.MImageOptions;

import org.xutils.x;

import java.util.List;

/**
 * Created by bin on 2018/5/16.
 * 黄页信息列表的adapter
 */

public class YelInfoAdapter extends RecyclerView.Adapter<YelInfoAdapter.ViewHolder> {

    private List<YelInfoBean> yelInfoBeans;
    private ItemClickListener itemClickListener;


    public void setData(List<YelInfoBean> yelInfoBeans) {
        this.yelInfoBeans = yelInfoBeans;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_yel_info_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(yelInfoBeans.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (yelInfoBeans != null) {
            return yelInfoBeans.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView img_cover;
        private TextView tv_title, tv_content, tv_time, tv_short_name;
        private View view;


        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            img_cover = itemView.findViewById(R.id.item_yel_info_img);
            tv_title = itemView.findViewById(R.id.item_yel_info_title);
            tv_content = itemView.findViewById(R.id.item_yel_info_content);
            tv_time = itemView.findViewById(R.id.item_yel_info_time);
            tv_short_name = itemView.findViewById(R.id.item_yel_info_short_name);

        }

        public void setData(YelInfoBean yelInfoBean, int position) {
            x.image().bind(img_cover, yelInfoBean.getCover_img(), MImageOptions.getNormalImageOptions());
            tv_title.setText(yelInfoBean.getTitle());
            tv_content.setText(yelInfoBean.getContent());
            tv_time.setText(yelInfoBean.getCreate_time());
            tv_short_name.setText(yelInfoBean.getShort_name());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(yelInfoBean, position);
                    }
                }
            });

        }
    }

    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

}
