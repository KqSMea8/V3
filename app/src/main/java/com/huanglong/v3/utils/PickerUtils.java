package com.huanglong.v3.utils;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;

import java.text.ParseException;
import java.util.Calendar;
import java.util.List;

/**
 * Created by bin on 2018/1/20.
 * 滚动选择器
 */

public class PickerUtils {

    private static OptionsPickerView pvOptions;

    private static List<String> listDate;


    private static TimePickerView timePickerView;

    /**
     * 初始化设置 OptionPicker
     *
     * @param context
     * @param title
     */
    public static void initOptionPicker(Context context, String title, OptionsPickerView.OnOptionsSelectListener listener) {

        pvOptions = new OptionsPickerView.Builder(context, listener).setTitleText(title)
                .setContentTextSize(20)//设置滚轮文字大小
                .setDividerColor(Color.TRANSPARENT)//设置分割线的颜色
                .setSelectOptions(0, 1)//默认选中项
                .setBgColor(Color.WHITE)
                .setTitleBgColor(Color.WHITE)
                .setTitleColor(Color.BLACK)
                .setCancelColor(Color.BLUE)
                .setSubmitColor(Color.RED)
                .setTextColorCenter(Color.BLACK)
                .setBackgroundId(0x66000000) //设置外部遮罩颜色
                .build();

    }


//    public static void setOptionPickerTitle(String title) {
//        if (pvOptions != null) {
////            pvOptions.
//        }
//    }


    /**
     * 初始化设置 OptionPicker
     *
     * @param context
     * @param title
     */
    public static void initTimePickerView(Context context, String title, TimePickerView.OnTimeSelectListener listener) {

        timePickerView = new TimePickerView.Builder(context, listener)
                .setType(new boolean[]{true, true, true, false, false, false})// 默认全部显示
//                .setCancelText("取消")//取消按钮文字
//                .setSubmitText("qu")//确认按钮文字
                .setContentSize(18)//滚轮文字大小
                .setTitleSize(20)//标题文字大小
                .setTitleText(title)//标题文字
                .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(false)//是否循环滚动
                .setTitleColor(Color.BLACK)//标题文字颜色
                .setSubmitColor(Color.BLUE)//确定按钮文字颜色
                .setCancelColor(Color.RED)//取消按钮文字颜色
                .setBgColor(Color.WHITE)//滚轮背景颜色 Night mode
//                .setRangDate(startDate,endDate)//起始终止年月日设定
                .setLabel("年", "月", "日", "时", "分", "秒")//默认设置为年月日时分秒
                .build();
    }

    /**
     * 展示OptionPicker
     *
     * @param listDate
     */
    public static void showOptionPicker(String key, List<String> listDate) {
        PickerUtils.listDate = listDate;
        pvOptions.setPicker(listDate);
        int position = 0;
        if (!TextUtils.isEmpty(key)) {
            for (int i = 0; i < listDate.size(); i++) {
                String s = listDate.get(i);
                if (TextUtils.equals(key, s)) {
                    position = i;
                    break;
                }
            }
        }
        pvOptions.setSelectOptions(position);
        pvOptions.show();
    }

    /**
     * 定位
     *
     * @param position
     */
    public static void setSelectOptions(int position) {
        pvOptions.setSelectOptions(position);
    }

    /**
     * 展示OptionPicker
     */
    public static void showTimePicker(String date) {
        if (timePickerView != null) {
            if (!TextUtils.isEmpty(date)) {
                if (date.contains("-")) {
                    date = date.replaceAll("-", ".");
                }
                Calendar calendar = Calendar.getInstance();
                try {
                    calendar.setTime(DateUtils.parseDate(date));
                    timePickerView.setDate(calendar);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
            timePickerView.show();
        }
    }

    public static void setDateTimePicker(String date) {
        if (timePickerView != null) {
            if (TextUtils.isEmpty(date)) {
                return;
            }
            Calendar calendar = Calendar.getInstance();
            try {
                calendar.setTime(DateUtils.parseDate(date));
                timePickerView.setDate(calendar);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 获取OptionPicker
     */
    public static OptionsPickerView getOptionPicker() {
        return pvOptions;
    }


}
