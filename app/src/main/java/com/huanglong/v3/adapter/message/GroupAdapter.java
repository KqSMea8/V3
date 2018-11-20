package com.huanglong.v3.adapter.message;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huanglong.v3.R;
import com.huanglong.v3.im.model.ProfileSummary;
import com.huanglong.v3.im.view.CircleImageView;
import com.huanglong.v3.model.contacts.GroupInfoBean;
import com.huanglong.v3.utils.ItemClickListener;
import com.huanglong.v3.utils.MImageOptions;

import org.xutils.x;

import java.util.List;

/**
 * Created by bin on 2018/4/2.
 * 群列表的adapter
 */

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {


    private List<GroupInfoBean> timGroupBaseInfos;
    private ItemClickListener itemClickListener;
    private int type = 0;//1.显示选择


    public void setData(List<GroupInfoBean> timGroupBaseInfos, int type) {
        this.timGroupBaseInfos = timGroupBaseInfos;
        this.type = type;
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(timGroupBaseInfos.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (timGroupBaseInfos != null) {
            return timGroupBaseInfos.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView avatar;
        private ImageView iv_radio;
        private TextView tv_group_name;
        private View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            avatar = itemView.findViewById(R.id.item_group_avatar);
            tv_group_name = itemView.findViewById(R.id.item_group_name);
            iv_radio = itemView.findViewById(R.id.item_contacts_radio);
        }

        public void setData(GroupInfoBean timGroupBaseInfo, int position) {
            x.image().bind(avatar, timGroupBaseInfo.getGroup_avatar(), MImageOptions.getCircularImageOptions());
            tv_group_name.setText(timGroupBaseInfo.getGroup_name());

            if (type == 1) {
                iv_radio.setVisibility(View.VISIBLE);
                boolean selected = timGroupBaseInfo.isSelected();
                if (selected) {
                    iv_radio.setImageResource(R.mipmap.icon_radio_press);
                } else {
                    iv_radio.setImageResource(R.mipmap.icon_radio_normal);
                }

            } else {
                iv_radio.setVisibility(View.GONE);
            }


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(timGroupBaseInfo, position);
                    }
                }
            });
        }
    }

    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

}
