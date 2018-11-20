package com.huanglong.v3.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;

import com.huanglong.v3.R;
import com.zyyoona7.lib.EasyPopup;

/**
 * Created by bin on 2018/3/6.
 * PopupWindow 工具类
 */

public class PopupUtils {

    /**
     * 初始化设置pop
     *
     * @param context
     * @param layout
     * @param bgView
     * @return
     */
    public static EasyPopup initPopup(Context context, int layout, ViewGroup bgView) {
        EasyPopup mCirclePop = new EasyPopup(context)
                .setContentView(layout)
                .setAnimationStyle(R.style.QQPopAnim)
                .setFocusAndOutsideEnable(true)
                .setDimView(bgView)
                .createPopup();
        return mCirclePop;
    }

    /**
     * 初始化设置pop
     *
     * @param context
     * @param layout
     * @param bgView
     * @return
     */
    public static EasyPopup initMatchPopup(Context context, int layout, ViewGroup bgView) {
        EasyPopup mCirclePop = new EasyPopup(context)
                .setContentView(layout).setWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setAnimationStyle(R.style.QQPopAnim)
                .setFocusAndOutsideEnable(true)
                .setDimView(bgView)
                .createPopup();
        return mCirclePop;
    }


    /**
     * 初始化设置pop
     *
     * @param context
     * @param layout
     * @param bgView
     * @return
     */
    public static EasyPopup initRightPopup(Context context, int layout, ViewGroup bgView) {
        EasyPopup mCirclePop = new EasyPopup(context)
                .setContentView(layout)
                //是否允许点击PopupWindow之外的地方消失
                .setFocusAndOutsideEnable(true)
                //允许背景变暗
                .setBackgroundDimEnable(true)
                .setDimValue(0f)
                .setAnimationStyle(R.style.CirclePopAnim)
                .setFocusAndOutsideEnable(true)
                .setDimView(bgView)
                .createPopup();
        return mCirclePop;
    }


    /**
     * 初始化设置pop
     *
     * @param context
     * @param layout
     * @param bgView
     * @return
     */
    public static EasyPopup initBottomPopup(Context context, int layout, ViewGroup bgView) {
        EasyPopup mCirclePop = new EasyPopup(context).setContentView(layout)
                //是否允许点击PopupWindow之外的地方消失
                .setFocusAndOutsideEnable(true)
                //允许背景变暗
                .setBackgroundDimEnable(true)
                //变暗的透明度(0-1)，0为完全透明
                //变暗的背景颜色
                .setDimColor(Color.BLACK)
                //指定任意 ViewGroup 背景变暗
                .setDimView(bgView)
                //变暗的透明度(0-1)，0为完全透明
                .setDimValue(0)
                .setAnimationStyle(R.style.BottomPopAnim).
                        setWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .createPopup();
        return mCirclePop;
    }

}
