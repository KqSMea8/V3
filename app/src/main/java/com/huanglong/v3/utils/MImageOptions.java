package com.huanglong.v3.utils;

import android.widget.ImageView;

import com.huanglong.v3.R;

import org.xutils.image.ImageOptions;

/**
 * Created by bin on 2018/1/15.
 * 图像处理
 */

public class MImageOptions {


    private static ImageOptions normalImageOptions;
    private static ImageOptions circularImageOptions;
    private static ImageOptions circularAvatarImageOptions;
    private static ImageOptions normalImageNotCardOptions;

    /**
     * 正常图片 Option
     *
     * @return
     */
    public static ImageOptions getNormalImageOptions() {
        if (normalImageOptions != null) {
            return normalImageOptions;
        }
        normalImageOptions = new ImageOptions.Builder()
                //.setSize(DensityUtil.dip2px(120), DensityUtil.dip2px(120))
                .setRadius(6)
                // 如果ImageView的大小不是定义为wrap_content, 不要crop.
                //.setCrop(true) // 很多时候设置了合适的scaleType也不需要它.
                // 加载中或错误图片的ScaleType
                .setPlaceholderScaleType(ImageView.ScaleType.CENTER_CROP)
                //.setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.mipmap.icon_default)
                .setFailureDrawableId(R.mipmap.icon_default)
                .setUseMemCache(true)
                .build();
        return normalImageOptions;
    }

    /**
     * 正常图片 Option
     *
     * @return
     */
    public static ImageOptions getNormalImageNotCropOptions() {
        if (normalImageNotCardOptions != null) {
            return normalImageNotCardOptions;
        }
        normalImageNotCardOptions = new ImageOptions.Builder()
                //.setSize(DensityUtil.dip2px(120), DensityUtil.dip2px(120))
//                .setRadius(6)
                // 如果ImageView的大小不是定义为wrap_content, 不要crop.
                .setCrop(false) // 很多时候设置了合适的scaleType也不需要它.
                // 加载中或错误图片的ScaleType
//                .setPlaceholderScaleType(ImageView.ScaleType.FIT_XY)
                .setImageScaleType(ImageView.ScaleType.FIT_XY)
                .setLoadingDrawableId(R.mipmap.icon_default)
                .setFailureDrawableId(R.mipmap.icon_default)
                .setUseMemCache(true)
                .build();
        return normalImageNotCardOptions;
    }

    /**
     * 用户头像显示 Option
     *
     * @return
     */
    public static ImageOptions getCircularImageOptions() {
        if (circularImageOptions != null) {
            return circularImageOptions;
        }
        circularImageOptions = new ImageOptions.Builder()
                //.setSize(DensityUtil.dip2px(120), DensityUtil.dip2px(120))
                //.setRadius(DensityUtil.dip2px(5))
                // 如果ImageView的大小不是定义为wrap_content, 不要crop.
                //.setCrop(true) // 很多时候设置了合适的scaleType也不需要它.
                // 加载中或错误图片的ScaleType
                .setCircular(true)
                .setPlaceholderScaleType(ImageView.ScaleType.CENTER_CROP)
                //.setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.drawable.head_other)
                .setFailureDrawableId(R.drawable.head_other)
                .setUseMemCache(true)
                .build();
        return circularImageOptions;
    }

    /**
     * 群头像显示
     *
     * @return
     */
    public static ImageOptions getGroupAvatarImageOptions() {
        if (circularAvatarImageOptions != null) {
            return circularAvatarImageOptions;
        }
        circularAvatarImageOptions = new ImageOptions.Builder()
                //.setSize(DensityUtil.dip2px(120), DensityUtil.dip2px(120))
                //.setRadius(DensityUtil.dip2px(5))
                // 如果ImageView的大小不是定义为wrap_content, 不要crop.
                //.setCrop(true) // 很多时候设置了合适的scaleType也不需要它.
                // 加载中或错误图片的ScaleType
                .setCircular(true)
                .setPlaceholderScaleType(ImageView.ScaleType.CENTER_CROP)
                //.setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.mipmap.head_group)
                .setFailureDrawableId(R.mipmap.head_group)
                .setUseMemCache(true)
                .build();
        return circularAvatarImageOptions;
    }


}
