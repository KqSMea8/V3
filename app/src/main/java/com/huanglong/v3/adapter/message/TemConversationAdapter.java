package com.huanglong.v3.adapter.message;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huanglong.v3.R;
import com.huanglong.v3.model.homepage.TemConBean;
import com.huanglong.v3.utils.ItemClickListener;
import com.huanglong.v3.utils.MImageOptions;

import org.xutils.x;

import java.util.List;

/**
 * Created by bin on 2018/7/20.
 * 临时会话的adapter
 */

public class TemConversationAdapter extends RecyclerView.Adapter<TemConversationAdapter.ViewHolder> {

    private List<TemConBean> temConBeans;

    private ItemClickListener itemClickListener;


    public void setData(List<TemConBean> temConBeans) {
        this.temConBeans = temConBeans;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tem_con_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(temConBeans.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (temConBeans != null) {
            return temConBeans.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_nickname, tv_content;
        private ImageView img_avatar;
        private View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            img_avatar = itemView.findViewById(R.id.item_tem_con_avatar);
            tv_nickname = itemView.findViewById(R.id.item_tem_con_nickname);
            tv_content = itemView.findViewById(R.id.item_tem_con_content);


        }

        public void setData(TemConBean temConBean, int position) {
            x.image().bind(img_avatar, temConBean.getHead_image(), MImageOptions.getCircularImageOptions());
            tv_nickname.setText(temConBean.getNickname());
            tv_content.setText(temConBean.getContent());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(temConBean, position);
                    }
                }
            });
        }
    }


    public void setOnClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
