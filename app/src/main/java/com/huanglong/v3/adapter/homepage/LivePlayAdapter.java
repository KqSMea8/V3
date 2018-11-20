package com.huanglong.v3.adapter.homepage;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huanglong.v3.R;
import com.huanglong.v3.model.homepage.LivePlayBean;
import com.huanglong.v3.utils.ItemClickListener;
import com.huanglong.v3.utils.MImageOptions;

import org.xutils.x;

import java.util.List;

/**
 * Created by bin on 2018/3/21.
 * 直播的adapter
 */

public class LivePlayAdapter extends RecyclerView.Adapter<LivePlayAdapter.ViewHolder> {

    private List<LivePlayBean> livePlayBeans;

    private ItemClickListener itemClickListener;


    public void setData(List<LivePlayBean> livePlayBeans) {
        this.livePlayBeans = livePlayBeans;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_live_play, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(livePlayBeans.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (livePlayBeans != null) {
            return livePlayBeans.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView img;
        private TextView tv_number, tv_title, tv_content, tv_status;
        private View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            img = itemView.findViewById(R.id.item_live_play_img);
            tv_number = itemView.findViewById(R.id.item_live_play_number);
            tv_title = itemView.findViewById(R.id.item_live_play_title);
            tv_content = itemView.findViewById(R.id.item_live_play_content);
            tv_status = itemView.findViewById(R.id.item_live_play_status);

        }

        public void setData(final LivePlayBean livePlayBean, final int position) {
            x.image().bind(img, livePlayBean.getCover_image(), MImageOptions.getNormalImageOptions());
            tv_title.setText(livePlayBean.getTitle());
            tv_number.setText(livePlayBean.getCanyu_count() + "人观看");

            int status = livePlayBean.getStatus();
            switch (status) {
                case 0:
                    tv_status.setText("直播预告");
                    break;
                case 1:
                    tv_status.setText("直播中");
                    break;
                case 3:
                    tv_status.setText("回放");
                    break;
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(livePlayBean, position);
                    }
                }
            });

        }
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

}
