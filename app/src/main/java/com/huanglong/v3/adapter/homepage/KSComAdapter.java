package com.huanglong.v3.adapter.homepage;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huanglong.v3.R;
import com.huanglong.v3.model.homepage.KSComBean;
import com.huanglong.v3.utils.MImageOptions;

import org.xutils.x;

import java.util.List;

/**
 * Created by bin on 2018/4/10.
 * k歌评论的adapter
 */

public class KSComAdapter extends RecyclerView.Adapter<KSComAdapter.ViewHolder> {

    private List<KSComBean> ksComBeans;

    public void setData(List<KSComBean> ksComBeans) {
        this.ksComBeans = ksComBeans;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_k_s_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(ksComBeans.get(position));
    }

    @Override
    public int getItemCount() {
        if (ksComBeans != null) {
            return ksComBeans.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView img_avatar;
        private TextView tv_nickname, tv_content, tv_time;

        public ViewHolder(View itemView) {
            super(itemView);

            img_avatar = itemView.findViewById(R.id.item_k_s_com_avatar);
            tv_nickname = itemView.findViewById(R.id.item_k_s_com_nickname);
            tv_content = itemView.findViewById(R.id.item_k_s_com_content);
            tv_time = itemView.findViewById(R.id.item_k_s_com_time);

        }

        public void setData(KSComBean ksComBean) {
            x.image().bind(img_avatar, ksComBean.getHead_image(), MImageOptions.getCircularImageOptions());
            tv_nickname.setText(ksComBean.getNickname());
            tv_content.setText(ksComBean.getContent());
            tv_time.setText(ksComBean.getCreate_time());

        }
    }
}
