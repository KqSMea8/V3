package com.huanglong.v3.adapter.message;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huanglong.v3.R;
import com.huanglong.v3.model.contacts.MGroupMemberBean;
import com.huanglong.v3.utils.ItemClickListener;
import com.huanglong.v3.utils.MImageOptions;

import org.xutils.x;

import java.util.List;

/**
 * Created by bin on 2018/6/6.
 * 群成员列表的adapter
 */

public class GroupMemberListAdapter extends RecyclerView.Adapter<GroupMemberListAdapter.ViewHolder> {

    private List<MGroupMemberBean> members;
    private String overId;
    private ItemClickListener itemClickListener;

    public void setData(List<MGroupMemberBean> members, String overId) {
        this.members = members;
        this.overId = overId;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_member_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(members.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (members != null) {
            return members.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView img_avatar, img_radio;
        private TextView tv_nickname;
        private View view;


        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            img_avatar = itemView.findViewById(R.id.item_group_member_avatar);
            img_radio = itemView.findViewById(R.id.item_group_member_radio);
            tv_nickname = itemView.findViewById(R.id.item_group_member_nickname);

        }

        public void setData(MGroupMemberBean profileSummary, int position) {
            x.image().bind(img_avatar, profileSummary.getFaceUrl(), MImageOptions.getCircularImageOptions());
            tv_nickname.setText(profileSummary.getNickname());

            if (TextUtils.equals(profileSummary.getId(), overId)) {
                img_radio.setVisibility(View.GONE);
            } else {
                img_radio.setVisibility(View.VISIBLE);
                boolean selected = profileSummary.isSelected();
                if (selected) {
                    img_radio.setImageResource(R.mipmap.icon_radio_press);
                } else {
                    img_radio.setImageResource(R.mipmap.icon_radio_normal);
                }
            }


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(profileSummary, position);
                    }
                }
            });

        }
    }

    public void setItemOnClickListener(ItemClickListener itemOnClickListener) {
        this.itemClickListener = itemOnClickListener;
    }

}
