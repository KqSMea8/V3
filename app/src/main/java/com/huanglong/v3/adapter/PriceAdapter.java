package com.huanglong.v3.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huanglong.v3.R;
import com.huanglong.v3.utils.ItemClickListener;

/**
 * Created by bin on 2018/6/13.
 * 价格的adapter
 */

public class PriceAdapter extends RecyclerView.Adapter<PriceAdapter.ViewHolder> {

    private String[] price;
    private ItemClickListener itemClickListener;


    public void setData(String[] price) {
        this.price = price;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_price_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(price[position], position);
    }

    @Override
    public int getItemCount() {
        if (price != null) {
            return price.length;
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_price;
        private View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tv_price = itemView.findViewById(R.id.item_price_tv);

        }

        public void setData(String s, int position) {
            tv_price.setText(s);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(s, position);
                    }
                }
            });
        }
    }


    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

}
