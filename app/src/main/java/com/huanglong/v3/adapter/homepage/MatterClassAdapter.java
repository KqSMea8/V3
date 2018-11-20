package com.huanglong.v3.adapter.homepage;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.huanglong.v3.R;
import com.huanglong.v3.V3Application;
import com.huanglong.v3.model.homepage.MatterClassBean;
import com.yhy.gvp.adapter.GVPAdapter;

import java.util.List;

/**
 * Created by bin on 2018/4/25.
 * 素材歌曲分类 adapter
 */

public class MatterClassAdapter extends GVPAdapter<MatterClassBean> {


    public MatterClassAdapter(int layoutId, List<MatterClassBean> dataList) {
        super(layoutId, dataList);
    }


    @Override
    public void bind(View item, int position, MatterClassBean data) {
        ImageView ivImg = (ImageView) item.findViewById(R.id.item_matter_class_img);
        TextView tvName = (TextView) item.findViewById(R.id.item_matter_class_name);

        Glide.with(V3Application.getInstance()).load(data.getImg_url()).into(ivImg);
//        x.image().bind(ivImg, data.getImg_url(), MImageOptions.getNormalImageOptions());
        tvName.setText(data.getName());
    }
}
