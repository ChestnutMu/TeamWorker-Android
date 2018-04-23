package cn.chestnut.mvvm.teamworker.utils.sqlite;

import org.greenrobot.greendao.query.QueryBuilder;

import cn.chestnut.mvvm.teamworker.db.DaoMaster;
import cn.chestnut.mvvm.teamworker.db.DaoSession;
import cn.chestnut.mvvm.teamworker.main.activity.LoginActivity;
import cn.chestnut.mvvm.teamworker.main.common.MyApplication;
import cn.chestnut.mvvm.teamworker.utils.Log;
import cn.chestnut.mvvm.teamworker.utils.PreferenceUtil;
import cn.chestnut.mvvm.teamworker.utils.StringUtil;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/10 20:49:36
 * Description：创建数据库、创建数据库表、包含增删改查的操作以及数据库的升级
 * Email: xiaoting233zhang@126.com
 */

public class DaoManager {
    private static final String DB_NAME = "teamworker2018";

    private volatile static DaoMaster sDaoMaster;
    private static DaoMaster.DevOpenHelper sHelper;
    private static DaoSession sDaoSession;

    /**
     * 判断是否有存在数据库，如果没有则创建
     *
     * @return
     */
    private synchronized static DaoMaster getDaoMaster() {
        Log.d("getDaoMaster 获取DaoMaster DaoMaster是否null" + (sDaoMaster == null));
        if (sDaoMaster == null) {
            String userId = PreferenceUtil.getInstances(MyApplication.getInstance()).getPreferenceString("userId");
            Log.d("getDaoMaster userId  = " + userId);
            if (StringUtil.isEmpty(userId)) return null;
            Log.d("getDaoMaster 初始化DevOpenHelper DevOpenHelper是否null" + (sHelper == null));
            sHelper = new DaoMaster.DevOpenHelper(MyApplication.getInstance(), DB_NAME + userId, null);
            sDaoMaster = new DaoMaster(sHelper.getWritableDatabase());
        }
        return sDaoMaster;
    }

    /**
     * 完成对数据库的添加、删除、修改、查询操作，仅仅是一个接口
     *
     * @return
     */
    public static DaoSession getDaoSession() {
        Log.d("getDaoSession 获取DaoSession DaoSession是否null" + (sDaoSession == null));
        if (sDaoSession == null) {
            if (sDaoMaster == null) {
                sDaoMaster = getDaoMaster();
            }
            sDaoSession = sDaoMaster.newSession();
        }
        return sDaoSession;
    }

    /**
     * 打开输出日志，默认关闭
     */
    public static void setDebug() {
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
    }

    /**
     * 关闭所有的操作，数据库开启后，使用完毕要关闭
     */
    public static void closeConnection() {
        closeHelper();
        closeDaoSession();
        sDaoMaster = null;
    }

    private static void closeHelper() {
        if (sHelper != null) {
            sHelper.close();
            sHelper = null;
        }
    }

    private static void closeDaoSession() {
        if (sDaoSession != null) {
            sDaoSession.clear();
            sDaoSession = null;
        }
    }
}