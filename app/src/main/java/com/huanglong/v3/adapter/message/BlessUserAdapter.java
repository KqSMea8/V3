package com.huanglong.v3.adapter.message;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huanglong.v3.R;
import com.huanglong.v3.model.home.BlessUserBean;
import com.huanglong.v3.utils.MImageOptions;

import org.xutils.x;

import java.util.List;

/**
 * Created by bin on 2018/3/18.
 * 福包中奖人的adapter
 */

public class BlessUserAdapter extends RecyclerView.Adapter<BlessUserAdapter.ViewHolder> {


    private List<BlessUserBean> blessUserBeans;

    public void setData(List<BlessUserBean> blessUserBeans) {
        this.blessUserBeans = blessUserBeans;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_blessing_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(blessUserBeans.get(position));
    }

    @Override
    public int getItemCount() {
        if (blessUserBeans != null) {
            return blessUserBeans.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView img_avatar;
        private TextView tv_name, tv_date, tv_money;


        public ViewHolder(View itemView) {
            super(itemView);

            img_avatar = itemView.findViewById(R.id.item_bless_user_avatar);
            tv_name = itemView.findViewById(R.id.item_bless_user_name);
            tv_date = itemView.findViewById(R.id.item_bless_user_date);
            tv_money = itemView.findViewById(R.id.item_bless_user_money);
        }

        public void setData(BlessUserBean blessUserBean) {

            x.image().bind(img_avatar, blessUserBean.getHead_image(), MImageOptions.getCircularImageOptions());
            tv_name.setText(blessUserBean.getNickname());
            tv_date.setText(blessUserBean.getCreate_time());
            tv_money.setText(blessUserBean.getPrice() + "元");
        }
    }
}
