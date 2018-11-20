package com.huanglong.v3.voice.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huanglong.v3.R;
import com.huanglong.v3.utils.ItemTypeClickListener;
import com.huanglong.v3.utils.MImageOptions;
import com.huanglong.v3.voice.entity.MatterVocBean;

import org.xutils.x;

import java.util.List;

/**
 * Created by bin on 2018/5/2.
 * 音效选择素材adapter
 */

public class MatterVocAdapter extends RecyclerView.Adapter<MatterVocAdapter.ViewHolder> {


    private List<MatterVocBean> matterVocBeans;
    private ItemTypeClickListener itemTypeClickListener;


    public void setData(List<MatterVocBean> matterVocBeans) {
        this.matterVocBeans = matterVocBeans;
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_matter_voc, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(matterVocBeans.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (matterVocBeans != null) {
            return matterVocBeans.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView img_cover;
        private TextView tv_title, tv_duration, tv_number, tv_use;
        private View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            img_cover = itemView.findViewById(R.id.item_matter_voc_img);
            tv_title = itemView.findViewById(R.id.item_matter_voc_title);
            tv_duration = itemView.findViewById(R.id.item_matter_voc_duration);
            tv_number = itemView.findViewById(R.id.item_matter_voc_number);
            tv_use = itemView.findViewById(R.id.item_matter_voc_use);

        }

        public void setData(MatterVocBean matterVocBean, int position) {

            x.image().bind(img_cover, matterVocBean.getImg_url(), MImageOptions.getNormalImageOptions());
            tv_title.setText(matterVocBean.getIntroduce());
            tv_duration.setText(matterVocBean.getDuration());
            tv_number.setText("下载次数:" + matterVocBean.getClick_count());


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemTypeClickListener != null) {
                        itemTypeClickListener.onItemClick(matterVocBean, position, 1);
                    }
                }
            });


            tv_use.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemTypeClickListener != null) {
                        itemTypeClickListener.onItemClick(matterVocBean, position, 2);
                    }
                }
            });
        }
    }

    public void setOnItemClickListener(ItemTypeClickListener itemClickListener) {
        this.itemTypeClickListener = itemClickListener;
    }
}
