package com.huanglong.v3.im.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huanglong.v3.R;
import com.huanglong.v3.im.model.ProfileSummary;
import com.huanglong.v3.im.presenter.FriendshipManagerPresenter;
import com.huanglong.v3.utils.ItemTypeClickListener;
import com.huanglong.v3.utils.MImageOptions;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import org.xutils.common.util.LogUtil;
import org.xutils.x;

import java.util.List;

/**
 * Created by bin on 2018/4/16.
 * 群成员列表的adapter
 */

public class GroupMemberAdapter extends RecyclerView.Adapter<GroupMemberAdapter.ViewHolder> {

    private List<ProfileSummary> members;

    private ItemTypeClickListener itemTypeClickListener;

    public void setData(List<ProfileSummary> members) {
        this.members = members;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_member, parent, false);
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

        private ImageView avatar;
        private TextView tv_name;

        public ViewHolder(View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.item_group_member_avatar);
            tv_name = itemView.findViewById(R.id.item_group_member_name);
        }

        public void setData(ProfileSummary profileSummary, int position) {

            final String identify = profileSummary.getIdentify();
            if (TextUtils.equals("add", identify)) {
                avatar.setImageResource(R.mipmap.icon_group_member_add);
                tv_name.setVisibility(View.GONE);
            } else if (TextUtils.equals("reduce", identify)) {
                avatar.setImageResource(R.mipmap.icon_group_member_reduce);
                tv_name.setVisibility(View.GONE);
            } else {

                FriendshipManagerPresenter.getOneUsersProfile(profileSummary.getIdentify(), new TIMValueCallBack<List<TIMUserProfile>>() {
                    @Override
                    public void onError(int i, String s) {
                        LogUtil.e("group member code:" + i + ",msg:" + s);
                    }

                    @Override
                    public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                        if (timUserProfiles != null && timUserProfiles.size() > 0) {
                            String str_nickname = "";
                            String remark = timUserProfiles.get(0).getRemark();
                            String nickname = timUserProfiles.get(0).getNickName();
                            if (!TextUtils.isEmpty(remark)) {
                                str_nickname = remark;
                            } else if (!TextUtils.isEmpty(nickname)) {
                                str_nickname = nickname;
                            } else {
                                str_nickname = timUserProfiles.get(0).getIdentifier();
                            }
                            tv_name.setText(str_nickname);
                            x.image().bind(avatar, timUserProfiles.get(0).getFaceUrl(), MImageOptions.getCircularImageOptions());
                        }
                    }
                });
            }


            avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemTypeClickListener != null) {
                        int type = 0;
                        if (TextUtils.equals("add", identify)) {
                            type = 1;
                        } else if (TextUtils.equals("reduce", identify)) {
                            type = 2;
                        }
                        itemTypeClickListener.onItemClick(profileSummary, position, type);
                    }
                }
            });


        }
    }

    public void setItemOnClickListener(ItemTypeClickListener itemOnClickListener) {
        this.itemTypeClickListener = itemOnClickListener;
    }

}
