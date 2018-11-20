package com.huanglong.v3.utils;

import android.widget.Toast;

import com.huanglong.v3.V3Application;

/**
 * Created by bin on 2018/1/5.
 * toast 工具类
 */

public class ToastUtils {

    /**
     * 弹出提示框
     *
     * @param msg
     */
    public static void showToast(String msg) {
        Toast.makeText(V3Application.getInstance(), msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 弹出提示框
     *
     * @param msgId
     */
    public static void showToast(int msgId) {
        Toast.makeText(V3Application.getInstance(), StringUtils.getResourcesString(msgId), Toast.LENGTH_SHORT).show();
    }
}
