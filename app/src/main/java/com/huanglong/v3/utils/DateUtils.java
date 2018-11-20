package com.huanglong.v3.utils;

import android.text.TextUtils;

import org.xutils.common.util.LogUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by bin on 2018/1/29.
 * 时间工具类
 */

public class DateUtils {

    public static final DateFormat FORMATER_DATE = new SimpleDateFormat(
            "yyyy-MM-dd");

    private static final SimpleDateFormat time = new SimpleDateFormat("HH:mm");

    private static final DateFormat FORMATER_DATE_POINT = new SimpleDateFormat(
            "yyyy.MM.dd");

    private static final DateFormat FORMATER_MINUTE = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm");

    private static final DateFormat FORMATER_M_S = new SimpleDateFormat(
            "mm:ss");

    private static final DateFormat FORMATER_MINUTE_POINT = new SimpleDateFormat(
            "yyyy.MM.dd HH:mm");

    private static final SimpleDateFormat DATE_TIME = new SimpleDateFormat("MM/dd HH:mm");

    private static final DateFormat FORMATER_DAY = new SimpleDateFormat(
            "yyyyMMdd");

    private static final DateFormat YEAR = new SimpleDateFormat("yyyy");

    private static Calendar calendar = Calendar.getInstance();


    /**
     * 解析日期
     *
     * @param time
     * @return
     * @throws ParseException
     */
    public static Date parseTime(String time) throws ParseException {
        Date date = FORMATER_MINUTE_POINT.parse(time);
        return date;
    }

    /**
     * 解析日期
     *
     * @param time
     * @return
     * @throws ParseException
     */
    public static Date parseDate(String time) throws ParseException {
        Date date = FORMATER_DATE_POINT.parse(time);
        return date;
    }

    /**
     * 截取年数
     *
     * @param date
     * @return
     */
    public static int dataFormatIntYear(Date date) {
        String dateStr = YEAR.format(date);
        return Integer.valueOf(dateStr);
    }

    /**
     * Date转为字符串，默认格式
     *
     * @param date
     * @return
     */
    public static String formatTime(Date date) {
        if (date == null) {
            return "";
        }
        return FORMATER_DATE.format(date);
    }

    /**
     * Date转为字符串，默认格式 2018.03.01
     *
     * @param date
     * @return
     */
    public static String formatDatePoint(Date date) {
        if (date == null) {
            return "";
        }
        return FORMATER_DATE_POINT.format(date);
    }

    /**
     * 接口返回日期处理 '/Data(1234567889)'
     *
     * @param date
     * @return
     */
    public static String dealDateStr(String date) {
        if (TextUtils.isEmpty(date)) {
            return "";
        }
        if (date.contains("/")) {
            date = date.replaceAll("/", "");
        }
        if (date.contains("Date")) {
            date = date.replace("Date", "");
        }

        if (date.contains("(")) {
            date = date.replace("(", "");
        }

        if (date.contains(")")) {
            date = date.replace(")", "");
        }
        long lt = new Long(date);
        Date date1 = new Date(lt);
        return FORMATER_DATE.format(date1);
    }

    /**
     * 获取当前日期
     *
     * @return
     */
    public static String getCurrentDate() {
        return FORMATER_DATE_POINT.format(calendar.getTime());
    }

    /**
     * 获取当前日期
     *
     * @return
     */
    public static String getCurrentDateNoPoint() {
        return FORMATER_DAY.format(calendar.getTime());
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static String getCurrentTime() {
        return FORMATER_MINUTE.format(calendar.getTime());
    }

    /**
     * 获取前n天日期、后n天日期
     *
     * @param distanceDay 前几天 如获取前7天日期则传-7即可；如果后7天则传7
     * @return
     */
    public static String getOldDate(int distanceDay) {
        Date beginDate = new Date();
        Calendar date = Calendar.getInstance();
        date.setTime(beginDate);
        date.set(Calendar.DATE, date.get(Calendar.DATE) + distanceDay);
        Date endDate = null;
        try {
            endDate = FORMATER_DATE_POINT.parse(FORMATER_DATE_POINT.format(date.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return FORMATER_DATE_POINT.format(endDate);
    }

    /**
     * 友好显示时间 几天前、几小时前等
     *
     * @param date
     * @return
     */
    public static String friendlyTime(String date) {
        if (TextUtils.isEmpty(date)) {
            return "";
        }

        try {
            // 获取当前时间与date相差的毫秒数
            Date d = parseTime(date);
            Date now = new Date();

            int nowYear = dataFormatIntYear(now);
            int serYear = dataFormatIntYear(d);

            int countYear = nowYear - serYear;
            if (countYear > 0) {
                return FORMATER_MINUTE_POINT.format(d);
            }

            Calendar cal = Calendar.getInstance();
            cal.setTime(now);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            Date today = cal.getTime();

            long diff = d.getTime() - today.getTime();
            if (diff > 0) {
                // 今天
                return time.format(d);
            } else {
                // 今天以前的时间
                cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) - 1);
                Date yesterday = cal.getTime();
                diff = d.getTime() - yesterday.getTime();
                if (diff > 0) {
                    // 昨天
                    return "昨天 " + time.format(d);
                } else {
                    cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) - 1);
                    Date qian = cal.getTime();
                    diff = d.getTime() - qian.getTime();
                    if (diff > 0) {
                        return "前天 " + time.format(d);
                    } else {
                        return DATE_TIME.format(d);
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.e(e.toString());
            return date;
        }
    }


    /**
     * 判断两个时间相差是否大于5分钟
     */
    public static boolean calendarFiveMinutes(String date1, String date2) {
        long fiveMinutes = 1000 * 60 * 5;
        try {
            Date d1 = parseTime(date1);
            Date d2 = parseTime(date2);
            return Math.abs(d1.getTime() - d2.getTime()) > fiveMinutes;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Android 音乐播放器应用里，读出的音乐时长为 long 类型以毫秒数为单位，例如：将 234736 转化为分钟和秒应为 03:55 （包含四舍五入）
     *
     * @param duration 音乐时长
     * @return
     */
    public static String timeParse(int duration) {
        String time = "";
        long minute = duration / 60000;
        long seconds = duration % 60000;
        long second = Math.round((float) seconds / 1000);
        if (minute < 10) {
            time += "0";
        }
        time += minute + ":";
        if (second < 10) {
            time += "0";
        }
        time += second;
        return time;
    }

    /**
     * Android 音乐播放器应用里，读出的音乐时长为 long 类型以毫秒数为单位，例如：将 234736 转化为分钟和秒应为 03:55 （包含四舍五入）
     *
     * @param time 音乐时长
     * @return
     */
    public static long reverseTimeParse(String time) {
        if (TextUtils.isEmpty(time)) {
            return 0;
        }
        int duration = 0;
        int index1 = time.indexOf(":");
        int index2 = time.indexOf(":", index1 + 1);
        int hh = Integer.parseInt(time.substring(0, index1));
        int mi = Integer.parseInt(time.substring(index1 + 1, index2));
        int ss = Integer.parseInt(time.substring(index2 + 1));
        duration = hh * 60 * 60 + mi * 60 + ss;
        LogUtil.e("formatTurnSecond: 时间== " + duration);
        return duration * 1000;
    }


}
