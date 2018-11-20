package com.huanglong.v3.adapter.circle;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.huanglong.v3.R;
import com.huanglong.v3.model.circle.CircleImgBean;
import com.huanglong.v3.utils.ItemClickListener;
import com.huanglong.v3.utils.MImageOptions;

import org.xutils.x;

import java.util.List;

/**
 * Created by bin on 2018/3/13.
 * 友圈显示图片
 */

public class CircleImgAdapter extends RecyclerView.Adapter<CircleImgAdapter.ViewHolder> {


    private List<CircleImgBean> imgs;
    private ItemClickListener itemClickListener;


    public void setData(List<CircleImgBean> imgs) {
        this.imgs = imgs;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_circle_img, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(imgs.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (imgs != null) {
            return imgs.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView img;
        private View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            img = itemView.findViewById(R.id.item_circle_img);

        }

        public void setData(final CircleImgBean circleImgBean, final int position) {
            x.image().bind(img, circleImgBean.getImageurl(), MImageOptions.getNormalImageOptions());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(circleImgBean, position);
                    }
                }
            });

        }
    }


    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
