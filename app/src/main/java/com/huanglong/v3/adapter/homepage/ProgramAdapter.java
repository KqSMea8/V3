package com.huanglong.v3.adapter.homepage;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huanglong.v3.R;
import com.huanglong.v3.model.homepage.ProgramBean;
import com.huanglong.v3.utils.ItemClickListener;
import com.huanglong.v3.utils.MImageOptions;
import com.huanglong.v3.utils.TransformationUtils;

import org.xutils.x;

import java.util.List;

/**
 * Created by bin on 2018/10/9.
 * 小程序的adapter
 */

public class ProgramAdapter extends RecyclerView.Adapter<ProgramAdapter.ViewHolder> {


    private List<ProgramBean> programAll;
    private ItemClickListener itemClickListener;


    public void setData(List<ProgramBean> programAll) {
        this.programAll = programAll;
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_program, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(programAll.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (programAll != null) {
            return programAll.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private ImageView iv_avatar;
        private TextView tv_name, tv_address, tv_distance;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            iv_avatar = itemView.findViewById(R.id.item_program_avatar);
            tv_name = itemView.findViewById(R.id.item_program_name);
            tv_address = itemView.findViewById(R.id.item_program_address);
            tv_distance = itemView.findViewById(R.id.item_program_distance);


        }

        public void setData(ProgramBean programBean, int position) {

            x.image().bind(iv_avatar, programBean.getLogourl(), MImageOptions.getCircularImageOptions());
            tv_name.setText(programBean.getName());
            tv_address.setText(programBean.getAddress());

            double distance = programBean.getDistance();
            if (distance > 1000) {
                tv_distance.setText(TransformationUtils.doubleDecimal(distance / 1000) + "km");
            } else {
                tv_distance.setText(distance + "m");
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener == null) return;
                    itemClickListener.onItemClick(programBean, position);
                }
            });


        }
    }


    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
