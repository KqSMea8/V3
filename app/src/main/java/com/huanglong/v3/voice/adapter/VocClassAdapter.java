package com.huanglong.v3.voice.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huanglong.v3.R;
import com.huanglong.v3.utils.ItemClickListener;
import com.huanglong.v3.utils.MImageOptions;
import com.huanglong.v3.voice.entity.ChapterBean;

import org.xutils.x;

import java.util.List;

/**
 * Created by bin on 2018/5/3.
 * 音频分类的adapter
 */

public class VocClassAdapter extends RecyclerView.Adapter<VocClassAdapter.ViewHolder> {

    private List<ChapterBean> chapterBean;

    private ItemClickListener itemClickListener;

    public void setData(List<ChapterBean> chapterBean) {
        this.chapterBean = chapterBean;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_voc_class, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(chapterBean.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (chapterBean != null) {
            return chapterBean.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_name, tv_class;
        private ImageView img_cover;
        private View view;


        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tv_name = itemView.findViewById(R.id.item_voc_class_name);
            img_cover = itemView.findViewById(R.id.item_voc_class_cover);
            tv_class = itemView.findViewById(R.id.item_voc_class_class);
        }

        public void setData(ChapterBean chapterBean, int position) {
            tv_name.setText(chapterBean.getTitle());
            x.image().bind(img_cover, chapterBean.getCover_img(), MImageOptions.getNormalImageOptions());
            tv_class.setText("所属列表:" + chapterBean.getName());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(chapterBean, position);
                    }
                }
            });
        }
    }


    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
