package cn.chestnut.mvvm.teamworker.main.common;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.multidex.MultiDex;

import com.socks.library.KLog;

import cn.chestnut.mvvm.teamworker.BuildConfig;
import cn.chestnut.mvvm.teamworker.core.TeamWorkerMessageHandler;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/10 20:49:36
 * Description：Application类
 * Email: xiaoting233zhang@126.com
 */

public class MyApplication extends android.app.Application {

    private static TeamWorkerMessageHandler mHandler;


    public static TeamWorkerMessageHandler getTeamWorkerMessageHandler() {
        if (mHandler == null) {
            mHandler = TeamWorkerMessageHandler.getInstance();
        }
        return mHandler;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        KLog.init(BuildConfig.DEBUG);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onCreate() {
        super.onCreate();
    }

    private static MyApplication instance;

    public static MyApplication getInstance() {
        return instance;
    }

    public MyApplication() {
        instance = this;
    }

}
