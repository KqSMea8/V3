package com.huanglong.v3.adapter.message;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huanglong.v3.R;
import com.huanglong.v3.model.home.YellowBean;
import com.huanglong.v3.utils.ItemTypeClickListener;
import com.huanglong.v3.utils.MImageOptions;
import com.huanglong.v3.utils.TransformationUtils;

import org.xutils.x;

import java.util.List;

/**
 * Created by bin on 2018/3/18.
 * 黄页的adapter
 */

public class YellowAdapter extends RecyclerView.Adapter<YellowAdapter.ViewHolder> {

    private List<YellowBean> yellowBeans;
    private ItemTypeClickListener itemTypeClickListener;

    public void setData(List<YellowBean> yellowBeans) {
        this.yellowBeans = yellowBeans;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_yellow_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(yellowBeans.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (yellowBeans != null) {
            return yellowBeans.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView img, img_call;
        private TextView tv_title, tv_service_area, tv_distance;
        private View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            img = itemView.findViewById(R.id.item_yellow_img);
            img_call = itemView.findViewById(R.id.item_yellow_call);
            tv_title = itemView.findViewById(R.id.item_yellow_title);
            tv_service_area = itemView.findViewById(R.id.item_yellow_service_area);
            tv_distance = itemView.findViewById(R.id.item_yellow_service_distance);
        }


        public void setData(final YellowBean yellowBean, final int position) {
            x.image().bind(img, yellowBean.getHead_image(), MImageOptions.getNormalImageOptions());
            tv_title.setText(yellowBean.getShort_name());
            tv_service_area.setText("主营业务:" + yellowBean.getMain_scope());
            double distance = yellowBean.getDistance();
            if (distance > 1000) {
                tv_distance.setText(TransformationUtils.doubleDecimal(distance / 1000) + "km");
            } else {
                tv_distance.setText(distance + "m");
            }


            img_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemTypeClickListener != null) {
                        itemTypeClickListener.onItemClick(yellowBean, position, 1);
                    }
                }
            });


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemTypeClickListener != null) {
                        itemTypeClickListener.onItemClick(yellowBean, position, 2);
                    }
                }
            });
        }
    }


    public void setOnItemClickListener(ItemTypeClickListener itemClickListener) {
        this.itemTypeClickListener = itemClickListener;
    }
}
