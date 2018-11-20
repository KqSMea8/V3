package com.huanglong.v3.utils;

import java.text.DecimalFormat;
import java.util.Random;

/**
 * Created by bin on 2018/1/22.
 * 转化工具类
 */

public class TransformationUtils {

    /**
     * double保留后面两个小数点
     *
     * @param num
     * @return
     */
    public static String doubleDecimal(double num) {
        DecimalFormat df = new DecimalFormat("#0.00");
        return df.format(num);
    }


    /**
     * 获取某个范围内的随机数
     *
     * @param min
     * @param max
     * @return
     */
    public static int getRandom(int min, int max) {
        Random random = new Random();
        int s = random.nextInt(max) % (max - min + 1) + min;
        return s;
    }

}
