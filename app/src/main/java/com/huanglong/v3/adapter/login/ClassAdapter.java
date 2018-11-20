package com.huanglong.v3.adapter.login;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.model.login.ClassBean;
import com.huanglong.v3.utils.ItemClickListener;
import com.huanglong.v3.utils.MImageOptions;

import org.xutils.x;

import java.util.List;

/**
 * Created by bin on 2018/4/23.
 * 分类的adapter
 */

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ViewHolder> {

    public static int SELECTED = 0;
    public static int SINGLE_SELECTED = 1;


    private List<ClassBean> classBeans;
    private ItemClickListener itemClickListener;

    private int type = 0;//0.注册二级分类列表
    public boolean isIcon = true;//是否显示图标
    private int showType;//1.竖向分类列表 2.横向分类列表

    public ClassAdapter(int showType) {
        this.showType = showType;
    }


    public void setData(List<ClassBean> classBeans) {
        this.classBeans = classBeans;
        notifyDataSetChanged();
    }

    /**
     * 选择类型
     *
     * @param type
     */
    public void selType(int type) {
        this.type = type;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (showType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ent_class, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ent_class_2, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(classBeans.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (classBeans != null) {
            return classBeans.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView img, img_sel;
        private TextView tv_name;
        private View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            img = itemView.findViewById(R.id.item_ent_class_img);
            tv_name = itemView.findViewById(R.id.item_ent_class_name);
            img_sel = itemView.findViewById(R.id.item_ent_class_sel);
        }

        public void setData(ClassBean classBean, int position) {
            tv_name.setText(classBean.getName());

            if (isIcon) {
                img.setVisibility(View.VISIBLE);
                x.image().bind(img, classBean.getApp_icon(), MImageOptions.getNormalImageOptions());
            } else {
                img.setVisibility(View.GONE);
                boolean selected = classBean.isSelected();
                if (selected) {
                    tv_name.setTextColor(ContextCompat.getColor(V3Application.getInstance(), R.color.red_FB5638));
                } else {
                    tv_name.setTextColor(ContextCompat.getColor(V3Application.getInstance(), R.color.gray_33));
                }
            }

            if (type == SINGLE_SELECTED) {
                img_sel.setVisibility(View.VISIBLE);
                boolean selected = classBean.isSelected();
                if (selected) {
                    img_sel.setImageResource(R.mipmap.icon_radio_press);
                } else {
                    img_sel.setImageResource(R.mipmap.icon_radio_normal);
                }
            } else {
                img_sel.setVisibility(View.GONE);
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(classBean, position);
                    }
                }
            });

        }
    }

    public void setItemOnClickListener(ItemClickListener itemOnClickListener) {
        this.itemClickListener = itemOnClickListener;
    }

}
