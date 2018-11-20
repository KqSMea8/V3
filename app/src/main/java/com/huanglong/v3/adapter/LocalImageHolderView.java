package com.huanglong.v3.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.convenientbanner.holder.Holder;
import com.huanglong.v3.R;
import com.huanglong.v3.utils.MImageOptions;

import org.xutils.x;

/**
 * Created by bin on 2017/10/17.
 */

public class LocalImageHolderView implements Holder<String> {
    private ImageView imageView;
    private TextView tv;


    @Override
    public View createView(Context context) {
        //找到布局填充器
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //找到整个xml布局
        LinearLayout rl = (LinearLayout) inflater.inflate(R.layout.item_home_banner_head_view, null);
        //通过找到xml布局来找控件
        imageView = (ImageView) rl.findViewById(R.id.image);

        return rl;
    }

    @Override
    public void UpdateUI(final Context context, int position, String data) {
        x.image().bind(imageView, data, MImageOptions.getNormalImageOptions());

    }
}

