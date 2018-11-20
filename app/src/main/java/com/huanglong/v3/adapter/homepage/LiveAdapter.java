package com.huanglong.v3.adapter.homepage;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huanglong.v3.R;
import com.huanglong.v3.model.homepage.LiveBean;
import com.huanglong.v3.utils.ItemClickListener;
import com.huanglong.v3.utils.MImageOptions;

import org.xutils.x;

import java.util.List;

/**
 * Created by bin on 2018/3/20.
 * 推荐直播的adapter
 */

public class LiveAdapter extends RecyclerView.Adapter<LiveAdapter.ViewHolder> {


    private List<LiveBean> liveBeans;
    private ItemClickListener itemClickListener;
//    private boolean isShowDelete = true;


    public void setData(List<LiveBean> liveBeans) {
        this.liveBeans = liveBeans;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_live_recommend, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(liveBeans.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (liveBeans != null) {
            if (liveBeans.size() > 2) {
                return 2;
            } else {
                return liveBeans.size();
            }
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView img_bg, img_avatar;
        private TextView tv_name, tv_number, tv_title, tv_status;
        private View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            img_bg = itemView.findViewById(R.id.item_live_rec_img);
            img_avatar = itemView.findViewById(R.id.item_live_rec_avatar);
            tv_name = itemView.findViewById(R.id.item_live_rec_name);
            tv_number = itemView.findViewById(R.id.item_live_rec_number);
            tv_title = itemView.findViewById(R.id.item_live_rec_title);
            tv_status = itemView.findViewById(R.id.item_live_rec_status);

        }

        public void setData(LiveBean liveBean, int position) {
            x.image().bind(img_bg, liveBean.getCover_image(), MImageOptions.getNormalImageOptions());
            x.image().bind(img_avatar, liveBean.getHead_image(), MImageOptions.getCircularImageOptions());
            tv_name.setText(liveBean.getNickname());
            tv_number.setText(liveBean.getCanyu_count() + "");
            tv_title.setText(liveBean.getTitle());

            int status = liveBean.getStatus();
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
                        itemClickListener.onItemClick(liveBean, position);
                    }
                }
            });

        }
    }

    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
