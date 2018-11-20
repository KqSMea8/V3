package com.huanglong.v3.utils;

import android.graphics.BitmapFactory;

/**
 * Created by bin on 2018/6/7.
 * 图片工具类
 */

public class PictureUtil {


    /**
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {

        int height = options.outHeight;
        int width = options.outWidth;
        if (height < width) {
            int manger = height;
            height = width;
            width = manger;
        }
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
            // if(height>4000|| width>3000){
            // inSampleSize+=2;
            // }else{
            // inSampleSize ++;
            // }
        }
        if (inSampleSize > 2 && inSampleSize % 2 == 1) {
            inSampleSize++;
        }

        return inSampleSize;
    }

}
