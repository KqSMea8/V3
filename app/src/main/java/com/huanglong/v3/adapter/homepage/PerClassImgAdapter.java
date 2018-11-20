package com.huanglong.v3.adapter.homepage;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.activities.imagepreview.ImagePreviewActivity;
import com.huanglong.v3.model.homepage.PerClassImgBean;
import com.huanglong.v3.utils.MImageOptions;
import com.huanglong.v3.utils.UserInfoUtils;

import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bin on 2018/4/18.
 * 个人主页分类图片
 */

public class PerClassImgAdapter extends RecyclerView.Adapter<PerClassImgAdapter.ViewHolder> {

    private List<PerClassImgBean> perClassImgBeans;

    public void setData(List<PerClassImgBean> perClassImgBeans) {
        this.perClassImgBeans = perClassImgBeans;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_per_class_img, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(perClassImgBeans.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (perClassImgBeans != null) {
            return perClassImgBeans.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView img;

        public ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.item_personal_page_class_img);

        }

        public void setData(PerClassImgBean perClassImgBean, int position) {
            x.image().bind(img, perClassImgBean.getImgurl(), MImageOptions.getNormalImageOptions());
            String member_id = perClassImgBean.getMember_id();
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    List<String> imgs = new ArrayList<>();
                    for (PerClassImgBean perClassImgBean : perClassImgBeans) {
                        imgs.add(perClassImgBean.getImgurl());
                    }
                    Intent intent = new Intent();
                    intent.setClass(V3Application.getInstance(), ImagePreviewActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putStringArrayListExtra("imgs", (ArrayList<String>) imgs);
                    intent.putExtra("index", position);
                    if (TextUtils.equals(member_id, UserInfoUtils.getUid())) {
                        intent.putExtra("isDelete", true);
                    } else {
                        intent.putExtra("isDelete", false);
                    }
                    V3Application.getInstance().startActivity(intent);
                    imgs.clear();
                    imgs = null;
                }
            });
        }
    }
}
