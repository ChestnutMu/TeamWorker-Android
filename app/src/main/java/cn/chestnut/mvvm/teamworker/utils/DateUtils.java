package cn.chestnut.mvvm.teamworker.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/10 20:49:36
 * Description：时间工具类
 * Email: xiaoting233zhang@126.com
 */

public class DateUtils {
    public final static String dateFormat = "yyyy-MM-dd";
    public final static String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";

    /**
     * 日期转换成字符串
     *
     * @param date
     * @return str
     */
    public static String dateToStr(Date date, String formatStr) {
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        String str = format.format(date);
        return str;
    }

    public static String longToStr(long milliseconds, String formatStr) {
        if (milliseconds <= 0) {
            return "";
        }
        Date date = new Date(milliseconds);
        return dateToStr(date, formatStr);

    }

    public static String longToStr(Long milliseconds, String formatStr) {
        if (milliseconds == null) {
            return "";
        }
        if (milliseconds <= 0) {
            return "";
        }
        Date date = new Date(milliseconds.longValue());
        return dateToStr(date, formatStr);

    }

    /**
     * 字符串转换成日期
     *
     * @param str 例如：2012-09-10
     * @return date
     */
    public static Date strToDate(String str, String formatStr) {
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 获取当前时间
     *
     * @param pattern 时间格式，如:"yyyy-MM-dd HH:mm:ss"
     */
    public static String getTime(String pattern) {
        Date nowDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);//日期格式
        String time = dateFormat.format(nowDate);
        return time;
    }

    /**
     * 输入时间，输出需要的时间格式
     *
     * @param time
     * @param pattern
     * @return
     */
    public static String stringFormat(String time, String pattern, String out) {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        Date date = null;
        try {
            date = df.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(out);//日期格式
        return dateFormat.format(date);
    }

    /**
     * 将毫秒转换成标准时间格式
     *
     * @param time
     * @param pattern
     * @return
     */
    public static String getDateFormat(String time, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        long lt = new Long(time);
        Date date = new Date(lt);
        String d = format.format(date);
        return d;
    }

    /**
     * 日期比较
     *
     * @param date_1
     * @param date_2
     * @param pattern 日期格式
     *                返回0：相等；
     *                返回-1：date_1<date_2;
     *                返回1:date_1>date_2
     */
    public static int comparetDate(String date_1, String date_2, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        Date date_01 = null;
        Date date_02 = null;
        try {
            date_01 = dateFormat.parse(date_1);
            date_02 = dateFormat.parse(date_2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date_01.compareTo(date_02);
    }

    /**
     * 格式化时间（输出类似于 刚刚, 4分钟前, 一小时前, 昨天这样的时间）
     *
     * @param time    需要格式化的时间 如"2016-09-14 19:01:45"
     * @param pattern 输入参数time的时间格式 如:"yyyy-MM-dd HH:mm:ss"
     *                如果为空则默认使用"yyyy-MM-dd HH:mm:ss"格式
     * @return time为null，或者时间格式不匹配，输出空字符""
     */

    public static String formatDisplayTime(String time, String currentTime, String pattern) {
        String display = "";
        int tMin = 60 * 1000;
        int tHour = 60 * tMin;
        int tDay = 24 * tHour;
        Date today;
        if (time != null) {
            try {
                Date tDate = new SimpleDateFormat(pattern).parse(time);
                if (StringUtil.isStringNotNull(currentTime)) {
                    today = new SimpleDateFormat(pattern).parse(currentTime);
                } else {
                    today = new Date();
                }
                SimpleDateFormat thisYearDf = new SimpleDateFormat("yyyy");
                SimpleDateFormat todayDf = new SimpleDateFormat("yyyy-MM-dd");
                Date thisYear = new Date(thisYearDf.parse(thisYearDf.format(today)).getTime());
                Date yesterday = new Date(todayDf.parse(todayDf.format(today)).getTime());
                Date beforeYes = new Date(yesterday.getTime() - tDay);
                if (tDate != null) {
                    SimpleDateFormat halfDf = new SimpleDateFormat("MM月dd日");
                    long dTime = today.getTime() - tDate.getTime();
                    if (tDate.before(thisYear)) {
                        display = new SimpleDateFormat("yyyy年MM月dd日").format(tDate);
                    } else {
                        if (dTime < tMin) {
                            display = "刚刚";
                        } else if (dTime < tHour) {
                            display = (int) Math.ceil(dTime / tMin) + "分钟前";
                        } else if (dTime < tDay && tDate.after(yesterday)) {
                            display = (int) Math.ceil(dTime / tHour) + "小时前";
                        } else if (tDate.after(beforeYes) && tDate.before(yesterday)) {
                            display = "昨天" + new SimpleDateFormat("HH:mm").format(tDate);
                        } else {
                            display = halfDf.format(tDate);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return display;
    }

}
