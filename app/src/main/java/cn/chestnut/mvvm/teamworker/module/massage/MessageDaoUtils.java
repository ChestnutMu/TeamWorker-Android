package cn.chestnut.mvvm.teamworker.module.massage;

import android.content.Context;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

import cn.chestnut.mvvm.teamworker.db.MessageDao;
import cn.chestnut.mvvm.teamworker.module.massage.bean.Message;
import cn.chestnut.mvvm.teamworker.utils.Log;
import cn.chestnut.mvvm.teamworker.utils.sqlite.DaoManager;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/10 20:49:36
 * Description：ORM操作
 * Email: xiaoting233zhang@126.com
 */
public class MessageDaoUtils {
    private DaoManager mManager;

    public MessageDaoUtils(Context context){
        mManager = DaoManager.getInstance();
        mManager.init(context);
    }

    /**
     * 完成message记录的插入，如果表未创建，先创建Message表
     * @param message
     * @return
     */
    public boolean insertMessage(Message message){
        boolean flag = false;
        flag = mManager.getDaoSession().getMessageDao().insert(message) == -1 ? false : true;
        Log.i( "insert Message :" + flag + "-->" + message.toString());
        return flag;
    }

    /**
     * 插入多条数据，在子线程操作
     * @param messageList
     * @return
     */
    public boolean insertMultMessage(final List<Message> messageList) {
        boolean flag = false;
        try {
            mManager.getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (Message message : messageList) {
                        mManager.getDaoSession().insertOrReplace(message);
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
     * @param message
     * @return
     */
    public boolean updateMessage(Message message){
        boolean flag = false;
        try {
            mManager.getDaoSession().update(message);
            flag = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除单条记录
     * @param message
     * @return
     */
    public boolean deleteMessage(Message message){
        boolean flag = false;
        try {
            //按照id删除
            mManager.getDaoSession().delete(message);
            flag = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除所有记录
     * @return
     */
    public boolean deleteAll(){
        boolean flag = false;
        try {
            //按照id删除
            mManager.getDaoSession().deleteAll(Message.class);
            flag = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 查询所有记录
     * @return
     */
    public List<Message> queryAllMessage(){
        return mManager.getDaoSession().loadAll(Message.class);
    }

    /**
     * 根据主键id查询记录
     * @param key
     * @return
     */
    public Message queryMessageById(String key){
        return mManager.getDaoSession().load(Message.class, key);
    }

    /**
     * 使用native sql进行查询操作
     */
    public List<Message> queryMessageByNativeSql(String sql, String[] conditions){
        return mManager.getDaoSession().queryRaw(Message.class, sql, conditions);
    }

    /**
     * 使用queryBuilder进行查询
     * @return
     */
    public List<Message> queryMessageByQueryBuilder(String id){
        QueryBuilder<Message> queryBuilder = mManager.getDaoSession().queryBuilder(Message.class);
        return queryBuilder.where(MessageDao.Properties.MessageId.eq(id)).list();
    }
}