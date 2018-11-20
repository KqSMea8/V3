package com.huanglong.v3.activities.imagepreview;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.huanglong.v3.R;
import com.huanglong.v3.utils.ItemClickListener;

import java.util.List;

/**
 * Created by bin on 2018/4/13.
 * 图片预览的adapter
 */

public class ImagePreviewAdapter extends PagerAdapter {

    private List<String> imgs;
    private Context context;
    private ItemClickListener itemClickListener;


    public ImagePreviewAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<String> imgs) {
        this.imgs = imgs;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (imgs != null) {
            return imgs.size();
        }
        return 0;
    }

    @Override
    public int getItemPosition(Object object) {
        if (imgs.size() > 0) {
            return POSITION_NONE;
        }
        return super.getItemPosition(object);
    }


    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    //展示的view
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        PhotoView photoView = new PhotoView(context);
        // 开启图片缩放功能
        photoView.enable();
        photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        photoView.setMaxScale(2.5f);
        photoView.setBackgroundColor(ContextCompat.getColor(context, R.color.black));
        Glide.with(context).load(imgs.get(position)).into(photoView);

        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(imgs.get(position), position);
                }
            }
        });
        //添加到容器
        container.addView(photoView);
        //返回显示的view
        return photoView;

    }

    //销毁view
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //从容器中移除view
        container.removeView((View) object);
    }


    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


}
