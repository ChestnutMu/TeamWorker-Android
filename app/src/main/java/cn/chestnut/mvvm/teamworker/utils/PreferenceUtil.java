package cn.chestnut.mvvm.teamworker.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/10 20:49:36
 * Description：Preference工具类
 * Email: xiaoting233zhang@126.com
 */


public class PreferenceUtil {
    private static PreferenceUtil instance;
    public static String TOKEN;

    private Context mContext;

    private PreferenceUtil(Context mContext) {
        this.mContext = mContext;
    }

    public static PreferenceUtil getInstances(Context context) {
        if (instance == null) {
            instance = new PreferenceUtil(context);
        }
        return instance;
    }


    /**
     * 存储的偏好名称
     */
    public static final String userinfo = "userinfo_pref";

    /**
     * 提交boolean类型的值
     *
     * @param key
     * @param value
     */
    public void savePreferenceBoolean(String key, boolean value) {
        SharedPreferences preferences = mContext.getSharedPreferences(userinfo,
                MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * 提交boolean类型的值
     *
     * @param key
     * @param value
     */
    public void savePreferenceBooleanBySecond(String key, boolean value, long second) {
        savePreferenceBoolean(key, value, second * 1000L);
    }

    /**
     * 提交boolean类型的值
     *
     * @param key
     * @param value
     */
    public void savePreferenceBoolean(String key, boolean value, long time) {
        SharedPreferences preferences = mContext.getSharedPreferences(userinfo,
                MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key + "_time", (System.currentTimeMillis() + time) + "_" + value);
        editor.commit();
    }

    /**
     * 获取boolean类型的值
     *
     * @param key
     * @return
     */
    public boolean getPreferenceBooleanHaveTime(String key) {
        SharedPreferences preferences = mContext.getSharedPreferences(userinfo,
                MODE_PRIVATE);
        String value = preferences.getString(key + "_time", "");
        if (value.equals("")) {//默认
            return false;
        } else {
            String[] array = value.split("_");
            long time = Long.parseLong(array[0]);
            if (System.currentTimeMillis() > time) {
                return false;
            } else {
                return Boolean.parseBoolean(array[1]);
            }
        }
    }

    /**
     * 获取boolean类型的值
     *
     * @param key
     * @return
     */
    public boolean getPreferenceBoolean(String key) {
        SharedPreferences preferences = mContext.getSharedPreferences(userinfo,
                MODE_PRIVATE);
        return preferences.getBoolean(key, false);
    }

    /**
     * 提交字符串
     *
     * @param key
     * @param value
     */
    public void savePreferenceString(String key, String value) {
        SharedPreferences preferences = mContext.getSharedPreferences(userinfo,
                MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * 获取String类型的值
     *
     * @param key
     * @return
     */
    public String getPreferenceString(String key) {
        SharedPreferences preferences = mContext.getSharedPreferences(userinfo,
                MODE_PRIVATE);
        return preferences.getString(key, "");
    }

    /**
     * 提交long类型的值
     *
     * @param key
     * @param value
     */
    public void savePreferenceLong(String key, long value) {
        SharedPreferences preferences = mContext.getSharedPreferences(userinfo,
                MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    /**
     * 获取long类型的值
     *
     * @param key
     * @return
     */
    public long getPreferenceLong(String key) {
        SharedPreferences preferences = mContext.getSharedPreferences(userinfo,
                MODE_PRIVATE);
        return preferences.getLong(key, 0);
    }

    /**
     * 删除某一个提交的偏好值
     *
     * @param key
     */
    public void deleteKey(String key) {
        try {
            SharedPreferences preferences = mContext.getSharedPreferences(userinfo,
                    MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove(key);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
