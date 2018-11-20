package com.huanglong.v3.adapter.mine;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huanglong.v3.R;
import com.huanglong.v3.model.mine.AddBooBean;
import com.huanglong.v3.utils.ItemClickListener;
import com.huanglong.v3.utils.MImageOptions;

import org.xutils.x;

import java.util.List;

/**
 * Created by bin on 2018/4/26.
 * 我的通讯录的adapter
 */

public class AddBooAdapter extends RecyclerView.Adapter<AddBooAdapter.ViewHolder> {

    private List<AddBooBean> addBooBeans;
    private ItemClickListener itemClickListener;

    public boolean isShowRadio = false;


    public void setData(List<AddBooBean> addBooBeans) {
        this.addBooBeans = addBooBeans;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_contacts, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(addBooBeans.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (addBooBeans != null) {
            return addBooBeans.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private ImageView img_avatar, img_radio, img_enterprise;
        private TextView tv_name;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            img_avatar = itemView.findViewById(R.id.item_my_contacts_avatar);
            tv_name = itemView.findViewById(R.id.item_my_contacts_name);
            img_radio = itemView.findViewById(R.id.item_my_contacts_radio);
            img_enterprise = itemView.findViewById(R.id.item_contacts_enterprise);

        }

        public void setData(AddBooBean addBooBean, int position) {
            x.image().bind(img_avatar, addBooBean.getHead_image(), MImageOptions.getCircularImageOptions());
            tv_name.setText(addBooBean.getNickname());

            if (isShowRadio) {
                img_radio.setVisibility(View.VISIBLE);
                boolean selected = addBooBean.isSelected();
                if (selected) {
                    img_radio.setImageResource(R.mipmap.icon_radio_press);
                } else {
                    img_radio.setImageResource(R.mipmap.icon_radio_normal);
                }
            } else {
                img_radio.setVisibility(View.GONE);
            }


            int type = addBooBean.getType();
            if (type == 2) {
                img_enterprise.setVisibility(View.VISIBLE);
            } else {
                img_enterprise.setVisibility(View.INVISIBLE);
            }


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(addBooBean, position);
                    }
                }
            });
        }

    }

    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
