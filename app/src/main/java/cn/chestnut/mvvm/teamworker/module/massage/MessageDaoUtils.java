package cn.chestnut.mvvm.teamworker.module.massage;

import org.greenrobot.greendao.async.AsyncOperation;
import org.greenrobot.greendao.async.AsyncOperationListener;
import org.greenrobot.greendao.async.AsyncSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cn.chestnut.mvvm.teamworker.db.MessageDao;
import cn.chestnut.mvvm.teamworker.db.UserDao;
import cn.chestnut.mvvm.teamworker.model.Message;
import cn.chestnut.mvvm.teamworker.model.MessageUser;
import cn.chestnut.mvvm.teamworker.model.MessageVo;
import cn.chestnut.mvvm.teamworker.model.User;
import cn.chestnut.mvvm.teamworker.model.UserInfo;
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

    public MessageDaoUtils() {
    }

    /**
     * 完成message记录的插入，如果表未创建，先创建Message表
     *
     * @param message
     * @return
     */
    public boolean insertMessage(Message message) {
        boolean flag;
        flag = DaoManager.getDaoSession().getMessageDao().insert(message) == -1 ? false : true;
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
            DaoManager.getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (Message message : messageList) {
                        DaoManager.getDaoSession().insertOrReplace(message);
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
            DaoManager.getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (MessageUser messageUser : messageUserList) {
                        DaoManager.getDaoSession().insertOrReplace(messageUser);
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
        flag = DaoManager.getDaoSession().getMessageUserDao().insert(messageUser) == -1 ? false : true;
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
            DaoManager.getDaoSession().update(message);
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
            DaoManager.getDaoSession().update(messageUser);
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
            DaoManager.getDaoSession().delete(message);
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
            DaoManager.getDaoSession().deleteAll(Message.class);
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
        return DaoManager.getDaoSession().loadAll(Message.class);
    }

    /**
     * 根据主键id查询记录
     *
     * @param key
     * @return
     */
    public Message queryMessageById(String key) {
        return DaoManager.getDaoSession().load(Message.class, key);
    }

    /**
     * 使用native sql进行查询操作
     */
    public List<Message> queryMessageByNativeSql(String sql, String[] conditions) {
        return DaoManager.getDaoSession().queryRaw(Message.class, sql, conditions);
    }

    /**
     * 使用queryBuilder进行查询
     *
     * @return
     */
    public List<Message> queryMessageByQueryBuilder(String id) {
        QueryBuilder<Message> queryBuilder = DaoManager.getDaoSession().queryBuilder(Message.class);
        return queryBuilder.where(MessageDao.Properties.ReceiverId.eq(id)).list();
    }

    /**
     * 根据userId查询接收到的消息列表
     *
     * @return
     */
    public List<Message> queryTopMessageByUserId(String userId) {
        Query query = DaoManager.getDaoSession().getMessageDao().queryBuilder().where(
                new WhereCondition.StringCondition(
                        "RECEIVER_ID = '" + userId + "' OR SENDER_ID = '" + userId +
                                "' GROUP BY CHAT_ID")).build();
        return query.list();
    }

    /**
     * 根据chatId查询接收到的消息列表
     *
     * @return
     */
    public Message queryTopMessageByChatId(String chatId) {
        Query query = DaoManager.getDaoSession().getMessageDao().queryBuilder().where(
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
        Query query = DaoManager.getDaoSession().getMessageDao().queryBuilder().where(
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
        Query query = DaoManager.getDaoSession().getMessageUserDao().queryBuilder().where(
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
        Query query = DaoManager.getDaoSession().getMessageDao().queryBuilder().where(
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
        Query<Message> query = DaoManager.getDaoSession().getMessageDao().queryBuilder().where(
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


    /**
     * 根据userId查询用户信息
     *
     * @return
     */
    public User queryUserByUserId(String userId) {
        Query query = DaoManager.getDaoSession().getUserDao().queryBuilder().where(
                new WhereCondition.StringCondition(
                        "USER_ID = '" + userId + "'")).build();
        return (User) query.unique();
    }

    public static void updateChatMessageUserInfo(AsyncSession asyncSession, final String userId, final String nickname, final String avatar) {
        asyncSession.runInTx(new Runnable() {
            @Override
            public void run() {
                Database db = DaoManager.getDaoSession().getDatabase();
                db.execSQL("update CHAT_MESSAGE set NICKNAME = ? and AVATAR = ? where SENDER_ID = ? ", new String[]{nickname, avatar, userId});
            }
        });
    }

    public static void updateChatMessageUserInfoNickname(AsyncSession asyncSession, final String userId, final String nickname) {
        asyncSession.runInTx(new Runnable() {
            @Override
            public void run() {
                Database db = DaoManager.getDaoSession().getDatabase();
                db.execSQL("update CHAT_MESSAGE set NICKNAME = ? where SENDER_ID = ? ", new String[]{nickname, userId});
//                db.beginTransaction();
//                db.close();
            }
        });
    }

    public static void updateChatMessageUserInfoAvatar(AsyncSession asyncSession, final String userId, final String avatar) {
        asyncSession.runInTx(new Runnable() {
            @Override
            public void run() {
                Database db = DaoManager.getDaoSession().getDatabase();
                db.execSQL("update CHAT_MESSAGE set AVATAR = ? where SENDER_ID = ? ", new String[]{avatar, userId});
            }
        });
    }

    public static void checkUserInfoOrUpdateToLocal(final AsyncSession asyncSession, final User user) {
        asyncSession.setListenerMainThread(new AsyncOperationListener() {

            @Override
            public void onAsyncOperationCompleted(AsyncOperation operation) {
                if (operation.isFailed()) {
                    Log.d("checkUserInfoOrUpdateToLocal 获取数据异常");
                    //从服务器创建并保存到本地
                    return;
                }
                Log.d("operation.getType()= " + operation.getType());
                if (operation.getType() == AsyncOperation.OperationType.QueryUnique) {
                    Object obj = operation.getResult();
                    Log.d("获取数据 obj = " + obj);
                    if (null == obj) {
                        //保存到本地
                        UserInfo userInfo = new UserInfo();
                        userInfo.setUserId(user.getUserId());
                        userInfo.setNickname(user.getNickname());
                        userInfo.setAvatar(user.getAvatar());
                        asyncSession.insertOrReplace(userInfo);
                        asyncSession.insertOrReplace(user);
                    } else {
                        User oldUser = (User) obj;
                        if (oldUser.getNickname() != null && !oldUser.getNickname().equals(user.getNickname())) {
                            if (oldUser.getAvatar() != null && !oldUser.getAvatar().equals(user.getAvatar())) {
                                //两个都不一样
                                MessageDaoUtils.updateChatMessageUserInfo(asyncSession, user.getUserId(), user.getNickname(), user.getAvatar());
                                UserInfo userInfo = new UserInfo();
                                userInfo.setUserId(user.getUserId());
                                userInfo.setNickname(user.getNickname());
                                userInfo.setAvatar(user.getAvatar());
                                asyncSession.insertOrReplace(userInfo);
                                asyncSession.insertOrReplace(user);
                            } else {
                                //nickname不一样
                                MessageDaoUtils.updateChatMessageUserInfoNickname(asyncSession, user.getUserId(), user.getNickname());
                                UserInfo userInfo = new UserInfo();
                                userInfo.setUserId(user.getUserId());
                                userInfo.setNickname(user.getNickname());
                                asyncSession.insertOrReplace(userInfo);
                                asyncSession.insertOrReplace(user);
                            }
                        } else {
                            if (oldUser.getAvatar() != null && !oldUser.getAvatar().equals(user.getAvatar())) {
                                //avatar不一样
                                MessageDaoUtils.updateChatMessageUserInfoAvatar(asyncSession, user.getUserId(), user.getAvatar());
                                UserInfo userInfo = new UserInfo();
                                userInfo.setUserId(user.getUserId());
                                userInfo.setAvatar(user.getAvatar());
                                asyncSession.insertOrReplace(userInfo);
                                asyncSession.insertOrReplace(user);
                            }
                        }
                    }
                }
            }
        });
        asyncSession.queryUnique(QueryBuilder.internalCreate(DaoManager.getDaoSession().getDao(User.class))
                .where(UserDao.Properties.UserId.eq(user.getUserId()))
                .build());
    }
}