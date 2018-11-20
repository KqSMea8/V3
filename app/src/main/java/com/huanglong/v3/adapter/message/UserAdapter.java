package com.huanglong.v3.adapter.message;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huanglong.v3.R;
import com.huanglong.v3.model.contacts.UserInfoBean;
import com.huanglong.v3.utils.ItemClickListener;
import com.huanglong.v3.utils.MImageOptions;

import org.xutils.x;

import java.util.List;

/**
 * Created by bin on 2018/3/11.
 * 用户列表的adapter
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {


    private List<UserInfoBean> userInfoBeans;

    private ItemClickListener itemClickListener;

    /**
     * 添加数据
     *
     * @param userInfoBeans
     */
    public void setData(List<UserInfoBean> userInfoBeans) {
        this.userInfoBeans = userInfoBeans;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_info, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(userInfoBeans.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (userInfoBeans != null) {
            return userInfoBeans.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView img_avatar;
        private TextView tv_nickname;
        private View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            img_avatar = itemView.findViewById(R.id.item_user_info_avatar);
            tv_nickname = itemView.findViewById(R.id.item_user_info_nickname);

        }

        public void setData(final UserInfoBean userInfoBean, final int position) {

            x.image().bind(img_avatar, userInfoBean.getHead_image(), MImageOptions.getCircularImageOptions());
            tv_nickname.setText(userInfoBean.getNickname());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClickListener.onItemClick(userInfoBean, position);
                }
            });

        }
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


}
