package com.huanglong.v3.adapter.circle;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.huanglong.v3.R;
import com.huanglong.v3.utils.ItemLongClickListener;
import com.huanglong.v3.utils.ItemTypeClickListener;
import com.huanglong.v3.utils.MImageOptions;

import org.xutils.x;

import java.util.List;

/**
 * Created by bin on 2018/3/12.
 * 发布友圈 选择图片的adapter
 */

public class RelCirImgAdapter extends RecyclerView.Adapter<RelCirImgAdapter.ViewHolder> {

    private List<String> relImageBeans;

    private ItemTypeClickListener itemClickListener;
    private ItemLongClickListener itemLongClickListener;


    public boolean isDelete = false;

    /**
     * 设置数据
     *
     * @param relImageBeans
     */
    public void setData(List<String> relImageBeans) {
        this.relImageBeans = relImageBeans;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rel_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(relImageBeans.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (relImageBeans != null) {
            return relImageBeans.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView img, img_delete;

        public ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.item_rel_img);
            img_delete = itemView.findViewById(R.id.item_rel_delete);
        }

        public void setData(final String relImageBean, final int position) {
            String imgUrl = relImageBean;
            if (TextUtils.equals("add", imgUrl)) {
                img.setImageResource(R.mipmap.icon_add_picture);
                img_delete.setVisibility(View.GONE);
            } else {
                x.image().bind(img, imgUrl, MImageOptions.getNormalImageOptions());
                if (isDelete) {
                    img_delete.setVisibility(View.VISIBLE);
                } else {
                    img_delete.setVisibility(View.GONE);
                }
            }

            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(relImageBean, position, 1);
                    }
                }
            });

            img.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (itemLongClickListener != null) {
                        itemLongClickListener.onItemClick(relImageBean, position);
                    }
                    return true;
                }
            });

            img_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(relImageBean, position, 2);
                    }
                }
            });

        }
    }


    public void setItemClickListener(ItemTypeClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setItemLongClickListener(ItemLongClickListener itemLongClickListener) {
        this.itemLongClickListener = itemLongClickListener;
    }
}
