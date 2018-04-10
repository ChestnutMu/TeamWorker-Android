package cn.chestnut.mvvm.teamworker.module.user;

import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.List;

import cn.chestnut.mvvm.teamworker.model.Message;
import cn.chestnut.mvvm.teamworker.model.NewFriendRequest;
import cn.chestnut.mvvm.teamworker.utils.Log;
import cn.chestnut.mvvm.teamworker.utils.sqlite.DaoManager;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/10 20:49:36
 * Description：ORM操作
 * Email: xiaoting233zhang@126.com
 */
public class NewFriendRequestDaoUtils {
    private DaoManager mManager;

    public NewFriendRequestDaoUtils() {
        mManager = DaoManager.getInstance();
    }

    /**
     * 完成message记录的插入，如果表未创建，先创建Message表
     *
     * @param newFriendRequest
     * @return
     */
    public boolean insertNewFriendRequest(NewFriendRequest newFriendRequest) {
        boolean flag;
        flag = mManager.getDaoSession().getNewFriendRequestDao().insert(newFriendRequest) == -1 ? false : true;
        Log.i("insert Message :" + flag + "-->" + newFriendRequest.toString());
        return flag;
    }

    /**
     * 插入多条数据，在子线程操作
     *
     * @param newFriendRequestList
     * @return
     */
    public boolean insertMultNewFriendRequest(final List<NewFriendRequest> newFriendRequestList) {
        boolean flag = false;
        try {
            mManager.getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (NewFriendRequest newFriendRequest : newFriendRequestList) {
                        mManager.getDaoSession().insertOrReplace(newFriendRequest);
                    }
                }
            });
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 修改一条数据
     *
     * @param newFriendRequest
     * @return
     */
    public boolean updateNewFriendRequest(NewFriendRequest newFriendRequest) {
        boolean flag = false;
        try {
            mManager.getDaoSession().update(newFriendRequest);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除单条记录
     *
     * @param newFriendRequest
     * @return
     */
    public boolean deleteNewFriendRequest(NewFriendRequest newFriendRequest) {
        boolean flag = false;
        try {
            //按照id删除
            mManager.getDaoSession().delete(newFriendRequest);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除所有记录
     *
     * @return
     */
    public boolean deleteAll() {
        boolean flag = false;
        try {
            //按照id删除
            mManager.getDaoSession().deleteAll(NewFriendRequest.class);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 根据newFriendRequestId查询记录
     *
     * @param requestId
     * @return
     */
    public NewFriendRequest queryNewFriendRequestById(String requestId) {
        Query<NewFriendRequest> query = mManager.getDaoSession().getNewFriendRequestDao().queryBuilder().where(
                new WhereCondition.StringCondition("NEW_FRIEND_REQUEST_ID = '" + requestId + "")
        ).build();
        List<NewFriendRequest> requestList = query.list();
        if (requestList.size() > 0) {
            return requestList.get(0);
        } else {
            return null;
        }
    }

    /**
     * 使用native sql进行查询操作
     */
    public List<NewFriendRequest> queryNewFriendRequestByNativeSql(String sql, String[] conditions) {
        return mManager.getDaoSession().queryRaw(NewFriendRequest.class, sql, conditions);
    }

    /**
     * 根据userId查询记录
     *
     * @return
     */
    public List<NewFriendRequest> queryNewFriendRequestByUserId(String userId) {
        Query<NewFriendRequest> query = mManager.getDaoSession().getNewFriendRequestDao().queryBuilder().where(
                new WhereCondition.StringCondition("RECIPIENT_ID = '" + userId + "' ORDER BY TIME DESC")
        ).build();
        List<NewFriendRequest> requestList = query.list();
        return requestList;
    }
}