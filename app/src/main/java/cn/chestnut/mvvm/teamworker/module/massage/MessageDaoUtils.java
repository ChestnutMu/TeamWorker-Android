package cn.chestnut.mvvm.teamworker.module.massage;

import android.content.Context;

import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cn.chestnut.mvvm.teamworker.db.MessageDao;
import cn.chestnut.mvvm.teamworker.module.massage.bean.Message;
import cn.chestnut.mvvm.teamworker.module.massage.bean.MessageUser;
import cn.chestnut.mvvm.teamworker.module.massage.bean.MessageVo;
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

    public MessageDaoUtils(Context context) {
        mManager = DaoManager.getInstance();
        mManager.init(context);
    }

    /**
     * 完成message记录的插入，如果表未创建，先创建Message表
     *
     * @param message
     * @return
     */
    public boolean insertMessage(Message message) {
        boolean flag;
        flag = mManager.getDaoSession().getMessageDao().insert(message) == -1 ? false : true;
        Log.i("insert Message :" + flag + "-->" + message.toString());
        return flag;
    }

    /**
     * 插入多条数据，在子线程操作
     *
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
     * 插入多条数据，在子线程操作
     *
     * @param messageUserList
     * @return
     */
    public boolean insertMultMessageUser(final List<MessageUser> messageUserList) {
        boolean flag = false;
        try {
            mManager.getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (MessageUser messageUser : messageUserList) {
                        mManager.getDaoSession().insertOrReplace(messageUser);
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
     * 插入一条用户信息
     *
     * @param messageUser
     * @return
     */
    public boolean insertMessageUser(MessageUser messageUser) {
        boolean flag;
        flag = mManager.getDaoSession().getMessageUserDao().insert(messageUser) == -1 ? false : true;
        Log.i("insert messageUser :" + flag + "-->" + messageUser.toString());
        return flag;
    }

    /**
     * 修改一条数据
     *
     * @param message
     * @return
     */
    public boolean updateMessage(Message message) {
        boolean flag = false;
        try {
            mManager.getDaoSession().update(message);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 修改一条用户信息
     *
     * @param messageUser
     * @return
     */
    public boolean updateMessageUser(MessageUser messageUser) {
        boolean flag = false;
        try {
            mManager.getDaoSession().update(messageUser);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除单条记录
     *
     * @param message
     * @return
     */
    public boolean deleteMessage(Message message) {
        boolean flag = false;
        try {
            //按照id删除
            mManager.getDaoSession().delete(message);
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
            mManager.getDaoSession().deleteAll(Message.class);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 查询所有记录
     *
     * @return
     */
    public List<Message> queryAllMessage() {
        return mManager.getDaoSession().loadAll(Message.class);
    }

    /**
     * 根据主键id查询记录
     *
     * @param key
     * @return
     */
    public Message queryMessageById(String key) {
        return mManager.getDaoSession().load(Message.class, key);
    }

    /**
     * 使用native sql进行查询操作
     */
    public List<Message> queryMessageByNativeSql(String sql, String[] conditions) {
        return mManager.getDaoSession().queryRaw(Message.class, sql, conditions);
    }

    /**
     * 使用queryBuilder进行查询
     *
     * @return
     */
    public List<Message> queryMessageByQueryBuilder(String id) {
        QueryBuilder<Message> queryBuilder = mManager.getDaoSession().queryBuilder(Message.class);
        return queryBuilder.where(MessageDao.Properties.ReceiverId.eq(id)).list();
    }

    /**
     * 根据userId查询接收到的消息列表
     *
     * @return
     */
    public List<Message> queryTopMessageByUserId(String userId) {
        Query query = mManager.getDaoSession().getMessageDao().queryBuilder().where(
                new WhereCondition.StringCondition(
                        "RECEIVER_ID = '" + userId + "' OR SENDER_ID = '" + userId +
                                "' GROUP BY CHAT_ID")).build();
        return query.list();
    }

    /**
     * 根据userId查询接收到的消息列表
     *
     * @return
     */
    public Message queryTopMessageByChatId(String chatId) {
        Query query = mManager.getDaoSession().getMessageDao().queryBuilder().where(
                new WhereCondition.StringCondition(
                        "CHAT_ID = '" + chatId +
                                "' ORDER BY TIME DESC")).offset(0).limit(1).build();
        return (Message) query.unique();
    }


    /**
     * 根据userId和senderId查询接收到的消息列表
     *
     * @return
     */
    public List<Message> queryMessageByUserIdAndSenderId(String userId, String senderId) {
        Query query = mManager.getDaoSession().getMessageDao().queryBuilder().where(
                new WhereCondition.StringCondition(
                        "RECEIVER_ID = '" + userId +
                                "' AND SENDER_ID = '" + senderId +
                                "' OR RECEIVER_ID = '" + senderId +
                                "' AND SENDER_ID = '" + userId +
                                "' ORDER BY TIME ASC")).build();
        return query.list();
    }

    /**
     * 根据userId查询用户名和头像
     *
     * @return
     */
    public MessageUser queryMessageUserByUserId(String userId) {
        Query query = mManager.getDaoSession().getMessageUserDao().queryBuilder().where(
                new WhereCondition.StringCondition(
                        "USER_ID = '" + userId + "'")).build();
        return (MessageUser) query.unique();
    }

    /**
     * 根据chatId查询MessageList
     *
     * @param chatId
     * @return
     */
    public List<Message> queryMessageByChatId(String chatId) {
        Query query = mManager.getDaoSession().getMessageDao().queryBuilder().where(
                new WhereCondition.StringCondition(
                        "CHAT_ID = '" + chatId + "' ORDER BY TIME ASC")).build();
        return query.list();
    }

    /**
     * 根据chatId和userId查询MessageUser
     *
     * @param chatId
     * @param userId
     * @return
     */
    public List<String> queryMessageUserIdByChatId(String chatId, String userId) {
        Query<Message> query = mManager.getDaoSession().getMessageDao().queryBuilder().where(
                new WhereCondition.StringCondition("CHAT_ID = '" + chatId + "' GROUP BY SENDER_ID ")
        ).build();
        List<Message> messageList = query.list();
        List<String> senderIdList = new ArrayList<>();
        for (Message message : messageList) {
            if (!userId.equals(message.getSenderId()))
                senderIdList.add(message.getSenderId());
        }
        return senderIdList;
    }

    /**
     * 将Message转为MessageUser
     *
     * @param messages
     * @return
     */
    public List<MessageVo> transferMessageVo(List<Message> messages) {
        List<MessageVo> result = new LinkedList<>();
        for (Message message : messages) {
            MessageVo messageVo = new MessageVo();
            messageVo.setMessage(message);
            result.add(messageVo);
        }
        return result;
    }

    /**
     * 根据userId查找用户信息添加到MessageVo
     *
     * @param messages
     * @param userId
     * @return
     */
    public List<MessageVo> transferMessageVoByUserId(List<Message> messages, String userId) {
        List<MessageVo> result = new LinkedList<>();
        for (Message message : messages) {
            MessageVo messageVo = new MessageVo();
            messageVo.setMessage(message);
            messageVo.setMessageUser(queryMessageUserByUserId(userId));
            result.add(messageVo);
        }
        return result;
    }

    /**
     *
     * @param messages
     * @param messageUser
     * @return
     */
    public List<MessageVo> transferMessageVoByMessageUser(List<Message> messages, MessageUser messageUser) {
        List<MessageVo> result = new LinkedList<>();
        for (Message message : messages) {
            MessageVo messageVo = new MessageVo();
            messageVo.setMessage(message);
            messageVo.setMessageUser(messageUser);
            result.add(messageVo);
        }
        return result;
    }
}