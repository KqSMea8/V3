package com.huanglong.v3.utils;

import com.huanglong.v3.V3Application;

/**
 * Created by bin on 2018/1/5.
 * 字符串工具类
 */

public class StringUtils {

    /**
     * 获取资源String
     *
     * @param strId
     * @return
     */
    public static String getResourcesString(int strId) {
        return V3Application.getInstance().getResources().getString(strId);
    }

}
