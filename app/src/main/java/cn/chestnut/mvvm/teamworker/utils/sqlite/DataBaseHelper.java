package cn.chestnut.mvvm.teamworker.utils.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/10 20:49:36
 * Description：SQLite操作
 * Email: xiaoting233zhang@126.com
 */

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String TABLE_NAME = "table.db";
    private static final int DATABASE_VERSION = 1;
    private static DataBaseHelper instance;//单例

    public DataBaseHelper(Context context) {
        super(context, TABLE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {// 当数据库被首次创建时执行该方法，一般将创建表等初始化操作在该方法中执行。
        db.execSQL("CREATE TABLE IF NOT EXISTS emial (id INTEGER PRIMARY KEY AUTOINCREMENT,userid INTEGER,usercount VARCHAR(20),password VARCHAR(20),emialname VARCHAR(20))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {//当打开数据库时传入的版本号与当前的版本号不同时会调用该方法。

    }

    /**
     * 获取单例dataBaseHelper
     *
     * @param context
     * @return
     */
    public static synchronized DataBaseHelper getDataBaseHelper(Context context) {
        if (instance == null) {
            synchronized (DataBaseHelper.class) {
                if (instance == null)
                    instance = new DataBaseHelper(context);
            }
        }
        return instance;
    }

    /**
     * 关闭数据库
     */
    public static void closeDb() {
        if (instance != null)
            instance.close();
        instance = null;
    }
}
