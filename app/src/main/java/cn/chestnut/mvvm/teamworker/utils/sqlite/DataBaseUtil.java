package cn.chestnut.mvvm.teamworker.utils.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/10 20:49:36
 * Description：SQLite工具类
 * Email: xiaoting233zhang@126.com
 */

public class DataBaseUtil {
    public static DataBaseHelper dataBaseHelper;

    public DataBaseUtil(Context context) {
        getDataBaseHelper(context);
    }

    public DataBaseHelper getDataBaseHelper(Context context) {
        if (dataBaseHelper == null) {
            dataBaseHelper = DataBaseHelper.getDataBaseHelper(context);
        }
        return dataBaseHelper;
    }
//---------------------------------------示例——————————————————————————————————————————————————
    /**
     * 将外部邮箱分类数据插入数据库
     *
     * @param outEmialBeanList
     */
//    public void inSertEmialInfoIntoDataBase(List<OutEmialBean> outEmialBeanList) {
//        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
//        db.beginTransaction();//手动设置开始事务
//        for (int i = 0; i < outEmialBeanList.size(); i++) {
//            db.execSQL("insert into emial(userid,usercount,password,emialname) values(?,?,?,?)", new Object[]{outEmialBeanList.get(i).getUserId(), outEmialBeanList.get(i).getUserCount()
//                    , outEmialBeanList.get(i).getPassword(), outEmialBeanList.get(i).getEmialName()});
//        }
//        db.setTransactionSuccessful();//设置事务处理成功，不设置会自动回滚不提交
//        db.endTransaction();//处理完成
//        db.close();
//    }



    /**
     * 获取绑定的邮箱数据
     *
     * @return
     */
//    public List<OutEmialBean> readEmialDataBase(String userId) {
//        List<OutEmialBean> outEmialBeanList = new ArrayList<>();
//        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
//        String sql = "select userid,usercount,password,emialname from emial where userId='" + userId + "'";
//        Cursor cursor = db.rawQuery(sql, null);
//        while (cursor.moveToNext()) {
//            int userid = cursor.getInt(0);
//            String usercount = cursor.getString(1);
//            String password = cursor.getString(2);
//            String emialname = cursor.getString(3);
//            OutEmialBean outEmialBean = new OutEmialBean();
//            outEmialBean.setUserId(userid);
//            outEmialBean.setUserCount(usercount);
//            outEmialBean.setPassword(password);
//            outEmialBean.setEmialName(emialname);
//            outEmialBeanList.add(outEmialBean);
//        }
//        return outEmialBeanList;
//    }

    /**
     * 更新邮箱账号
     */
    public void updateEmialDataBase(String userCount, String password, String userId, String emialName) {
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        db.beginTransaction();//手动设置开始事务
        db.execSQL("update emial set usercount='" + userCount + "',password='" + password + "'" +
                " where userid='" + userId + "' and emialname='" + emialName + "'");
        db.setTransactionSuccessful();//设置事务处理成功，不设置会自动回滚不提交
        db.endTransaction();//处理完成
        db.close();
    }


    /**
     * 删除数据库表数据
     */
    public void deleteDatabaseData() {
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        db.delete("emial", null, null);
        db.delete("emiallist", null, null);
    }

}
