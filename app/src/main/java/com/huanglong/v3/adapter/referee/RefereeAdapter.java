package com.huanglong.v3.adapter.referee;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.huanglong.v3.R;

import org.xutils.x;

/**
 * Created by bin on 2018/1/20.
 * 推荐的adapter
 */

public class RefereeAdapter extends RecyclerView.Adapter<RefereeAdapter.ViewHolder> {


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_referee, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        x.image().bind(holder.img, "http://imgsrc.baidu.com/imgad/pic/item/4a36acaf2edda3cc6348c23e0be93901213f927b.jpg");

    }

    @Override
    public int getItemCount() {
        return 20;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView img;

        public ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.item_referee_img);


        }
    }
}
