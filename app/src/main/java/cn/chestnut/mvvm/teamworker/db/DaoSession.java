package cn.chestnut.mvvm.teamworker.db;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import cn.chestnut.mvvm.teamworker.model.Message;
import cn.chestnut.mvvm.teamworker.model.User;
import cn.chestnut.mvvm.teamworker.model.NewFriendRequest;
import cn.chestnut.mvvm.teamworker.model.MessageUser;
import cn.chestnut.mvvm.teamworker.model.ChatMessage;
import cn.chestnut.mvvm.teamworker.model.Chat;

import cn.chestnut.mvvm.teamworker.db.MessageDao;
import cn.chestnut.mvvm.teamworker.db.UserDao;
import cn.chestnut.mvvm.teamworker.db.NewFriendRequestDao;
import cn.chestnut.mvvm.teamworker.db.MessageUserDao;
import cn.chestnut.mvvm.teamworker.db.ChatMessageDao;
import cn.chestnut.mvvm.teamworker.db.ChatDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig messageDaoConfig;
    private final DaoConfig userDaoConfig;
    private final DaoConfig newFriendRequestDaoConfig;
    private final DaoConfig messageUserDaoConfig;
    private final DaoConfig chatMessageDaoConfig;
    private final DaoConfig chatDaoConfig;

    private final MessageDao messageDao;
    private final UserDao userDao;
    private final NewFriendRequestDao newFriendRequestDao;
    private final MessageUserDao messageUserDao;
    private final ChatMessageDao chatMessageDao;
    private final ChatDao chatDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        messageDaoConfig = daoConfigMap.get(MessageDao.class).clone();
        messageDaoConfig.initIdentityScope(type);

        userDaoConfig = daoConfigMap.get(UserDao.class).clone();
        userDaoConfig.initIdentityScope(type);

        newFriendRequestDaoConfig = daoConfigMap.get(NewFriendRequestDao.class).clone();
        newFriendRequestDaoConfig.initIdentityScope(type);

        messageUserDaoConfig = daoConfigMap.get(MessageUserDao.class).clone();
        messageUserDaoConfig.initIdentityScope(type);

        chatMessageDaoConfig = daoConfigMap.get(ChatMessageDao.class).clone();
        chatMessageDaoConfig.initIdentityScope(type);

        chatDaoConfig = daoConfigMap.get(ChatDao.class).clone();
        chatDaoConfig.initIdentityScope(type);

        messageDao = new MessageDao(messageDaoConfig, this);
        userDao = new UserDao(userDaoConfig, this);
        newFriendRequestDao = new NewFriendRequestDao(newFriendRequestDaoConfig, this);
        messageUserDao = new MessageUserDao(messageUserDaoConfig, this);
        chatMessageDao = new ChatMessageDao(chatMessageDaoConfig, this);
        chatDao = new ChatDao(chatDaoConfig, this);

        registerDao(Message.class, messageDao);
        registerDao(User.class, userDao);
        registerDao(NewFriendRequest.class, newFriendRequestDao);
        registerDao(MessageUser.class, messageUserDao);
        registerDao(ChatMessage.class, chatMessageDao);
        registerDao(Chat.class, chatDao);
    }
    
    public void clear() {
        messageDaoConfig.clearIdentityScope();
        userDaoConfig.clearIdentityScope();
        newFriendRequestDaoConfig.clearIdentityScope();
        messageUserDaoConfig.clearIdentityScope();
        chatMessageDaoConfig.clearIdentityScope();
        chatDaoConfig.clearIdentityScope();
    }

    public MessageDao getMessageDao() {
        return messageDao;
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public NewFriendRequestDao getNewFriendRequestDao() {
        return newFriendRequestDao;
    }

    public MessageUserDao getMessageUserDao() {
        return messageUserDao;
    }

    public ChatMessageDao getChatMessageDao() {
        return chatMessageDao;
    }

    public ChatDao getChatDao() {
        return chatDao;
    }

}
