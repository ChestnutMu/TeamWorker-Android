package cn.chestnut.mvvm.teamworker.main.common;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.support.multidex.MultiDex;

import java.util.LinkedList;
import java.util.List;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/10 20:49:36
 * Description：Application类
 * Email: xiaoting233zhang@126.com
 */

public class MyApplication
        extends android.app.Application {

    public static String cachePath = "/sdcard/oa/";//sd路径


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onCreate() {
        super.onCreate();
        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public Vibrator mVibrator;
    private static MyApplication instance;

    public static MyApplication getInstance() {
        return instance;
    }

    public MyApplication() {
        instance = this;
    }

    /**
     * 提交在活动列表中
     */
    private List<Activity> activityList = new LinkedList<Activity>();

    /**
     * 添加界面
     *
     * @param activity
     */
    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    public void removeActivity(BaseActivity activity) {
        activityList.remove(activity);
    }

    /**
     * 获取最上次的activity
     *
     * @return
     */
    public Activity getTopBaseActivity() {
        int size = activityList.size();
        if (size > 0) {
            return activityList.get(size - 1);
        }
        return null;
    }

    /**
     * 退出程序
     */
    public void exit() {
        Activity ac = getTopBaseActivity();
        for (Activity activity : activityList) {
            try {
                activity.finish();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        System.exit(0);
        onTerminate();
    }

}
