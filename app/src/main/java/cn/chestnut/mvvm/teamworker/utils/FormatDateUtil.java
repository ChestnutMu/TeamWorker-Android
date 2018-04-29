package cn.chestnut.mvvm.teamworker.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.chestnut.mvvm.teamworker.main.common.MyApplication;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/10 20:49:36
 * Description：时间工具类
 * Email: xiaoting233zhang@126.com
 */

public class FormatDateUtil {
    /**
     * 设置每个阶段时间
     */
    private static final int seconds_of_1minute = 60;

    private static final int seconds_of_30minutes = 30 * 60;

    private static final int seconds_of_1hour = 60 * 60;

    private static final int seconds_of_1day = 24 * 60 * 60;

    private static final int seconds_of_2day = 24 * 2 * 60 * 60;

    private static final int seconds_of_15days = seconds_of_1day * 15;

    private static final int seconds_of_30days = seconds_of_1day * 30;

    private static final int seconds_of_6months = seconds_of_30days * 6;

    private static final int seconds_of_1year = seconds_of_30days * 12;

    private static final long millis_of_one_day = seconds_of_1day * 1000;

    //时分
    private static SimpleDateFormat hsFormat = new SimpleDateFormat("HH:mm");

    //详细时间格式
    private static SimpleDateFormat yMdmsFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //生日时间格式
    private static SimpleDateFormat yMdFormat = new SimpleDateFormat("yyyy年MM月dd日");

    /**
     * 格式化时间
     *
     * @param mTime
     * @return
     */
    public static String getTimeRange(long mTime) {
        long currentTime = MyApplication.currentServerTimeMillis();
        long between = (currentTime - mTime) / 1000;
        int elapsedTime = (int) (between);
        if (elapsedTime < seconds_of_1minute) {

            return "刚刚";
        }
        if (elapsedTime < seconds_of_30minutes) {

            return elapsedTime / seconds_of_1minute + "分钟前";
        }
        if (elapsedTime < seconds_of_1hour) {

            return "半小时前";
        }
        if (elapsedTime < seconds_of_1day) {

            return elapsedTime / seconds_of_1hour + "小时前";
        }
        if (elapsedTime < seconds_of_15days) {

            return elapsedTime / seconds_of_1day + "天前";
        }
        if (elapsedTime < seconds_of_30days) {

            return "半个月前";
        }
        if (elapsedTime < seconds_of_6months) {

            return elapsedTime / seconds_of_30days + "月前";
        }
        if (elapsedTime < seconds_of_1year) {

            return "半年前";
        }
        if (elapsedTime >= seconds_of_1year) {

            return elapsedTime / seconds_of_1year + "年前";
        }
        return "";
    }

    /**
     * 转换long为时间格式
     *
     * @param time Millisecond
     * @return Every yyyy-MM-dd HH:mm:ss
     */
    public static String timeParseNormal(long time) {
        try {
            return yMdFormat.format(time);
        } catch (Exception e) {
            e.printStackTrace();
            return "1970-01-01";
        }
    }

    /**
     * 转换long为详细时间格式
     *
     * @param time Millisecond
     * @return Every yyyy-MM-dd HH:mm:ss
     */
    public static String timeParseDetail(long time) {
        try {
            return yMdmsFormat.format(time);
        } catch (Exception e) {
            e.printStackTrace();
            return "1970-01-01 00:00:00";
        }
    }

    public static String getMessageTime(long currentTime, long mTime) {
        SimpleDateFormat yFormat = new SimpleDateFormat("yyyy");
        SimpleDateFormat mDFormat = new SimpleDateFormat("MM月dd日");

        if (isSameDay(currentTime, mTime)) {
            return hsFormat.format(mTime);
        } else if (isYesterday(mTime)) {
            return "昨天";
        } else if (yFormat.format(currentTime).equals(yFormat.format(mTime))) {
            return mDFormat.format(mTime);
        } else return timeParseNormal(mTime);
    }

    public static boolean isSameDay(long date1, long Date2) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date1);
        int year1 = calendar.get(Calendar.YEAR);
        int day1 = calendar.get(Calendar.DAY_OF_YEAR);

        calendar.setTimeInMillis(Date2);
        int year2 = calendar.get(Calendar.YEAR);
        int day2 = calendar.get(Calendar.DAY_OF_YEAR);

        if ((year1 == year2) && (day1 == day2)) {
            return true;
        }
        return false;
    }

    /**
     * 获取昨天时间的最小值
     *
     * @return
     */
    public static long getYesterdayMinTimeMillis() {
        Calendar mCalendar = Calendar.getInstance();
        long currTime = System.currentTimeMillis();
        mCalendar.setTime(new Date(currTime));

        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);

        mCalendar.set(year, month, day, 0, 0, 0);
        long minToday = mCalendar.getTimeInMillis() - millis_of_one_day;

        return minToday;
    }

    /**
     * 获取昨天时间的最大值
     *
     * @return
     */
    public static long getYesterdayMaxTimeMillis() {
        Calendar mCalendar = Calendar.getInstance();
        long currTime = System.currentTimeMillis();
        mCalendar.setTime(new Date(currTime));

        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);

        mCalendar.set(year, month, day, 23, 59, 59);
        long minToday = mCalendar.getTimeInMillis() - millis_of_one_day;

        return minToday;
    }

    /**
     * 获取今天时间的最小值，即00:00
     *
     * @return
     */
    public static long getThisMonthFirstDayMinTimeMillsis() {
        Calendar mCalendar = Calendar.getInstance();
        long currTime = System.currentTimeMillis();
        mCalendar.setTime(new Date(currTime));

        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);

        mCalendar.set(year, month, day, 0, 0, 0);
        long minToday = mCalendar.getTimeInMillis();

        return minToday;
    }

    /**
     * 获取今天时间的最小值，即00:00
     *
     * @param calendar
     * @return
     */
    public static long getTodayMinTimeMillis(com.haibin.calendarview.Calendar calendar) {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(calendar.getYear(), calendar.getMonth() - 1, calendar.getDay(), 0, 0, 0);
        return mCalendar.getTimeInMillis();
    }

    /**
     * 获取今天时间的最大值，即23:59:59
     *
     * @param calendar
     * @return
     */
    public static long getTodayMaxTimeMillis(com.haibin.calendarview.Calendar calendar) {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(calendar.getYear(), calendar.getMonth() - 1, calendar.getDay(), 23, 59, 59);
        return mCalendar.getTimeInMillis();
    }

    /**
     * 判断某个时间是否是昨天
     *
     * @param time
     * @return
     */
    public static boolean isYesterday(long time) {
        if (time >= getYesterdayMinTimeMillis() &&
                time <= getYesterdayMaxTimeMillis()) {
            return true;
        } else {
            return false;
        }
    }
}
