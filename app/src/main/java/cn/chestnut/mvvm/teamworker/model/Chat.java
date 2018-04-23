package cn.chestnut.mvvm.teamworker.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.io.Serializable;

import org.greenrobot.greendao.annotation.Generated;

import cn.chestnut.mvvm.teamworker.BR;
import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.main.adapter.BindingItem;
import cn.chestnut.mvvm.teamworker.utils.FormatDateUtil;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/11 09:21:28
 * Description：
 * Email: xiaoting233zhang@126.com
 */

@Entity
public class Chat extends BindingItem implements Serializable {

    private static final long serialVersionUID = 1544780274098331286L;

    @Id(autoincrement = true)
    private Long id;

    @Unique
    private String chatId;

    /*聊天室的用户列表 HashSet的json串 */
    private String userList;

    /*双人聊天室的管理员*/
    private String adminId;

    /*双人聊天室的用户*/
    private String userId;

    /*聊天室名字*/
    private String chatName;

    /*聊天室图标*/
    private String chatPic;

    /*聊天室类型 ChatConstants*/
    private Integer chatType;

    /*创建时间*/
    private long createTime;

    /*更新时间*/
    private long updateTime;

    private String lastMessage;

    @Generated(hash = 335641571)
    public Chat(Long id, String chatId, String userList, String adminId, String userId,
            String chatName, String chatPic, Integer chatType, long createTime,
            long updateTime, String lastMessage) {
        this.id = id;
        this.chatId = chatId;
        this.userList = userList;
        this.adminId = adminId;
        this.userId = userId;
        this.chatName = chatName;
        this.chatPic = chatPic;
        this.chatType = chatType;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.lastMessage = lastMessage;
    }

    @Generated(hash = 519536279)
    public Chat() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getUserList() {
        return userList;
    }

    public void setUserList(String userList) {
        this.userList = userList;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public String getChatPic() {
        return chatPic;
    }

    public void setChatPic(String chatPic) {
        this.chatPic = chatPic;
    }

    public Integer getChatType() {
        return chatType;
    }

    public void setChatType(Integer chatType) {
        this.chatType = chatType;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String showTime() {
        return FormatDateUtil.getMessageTime(System.currentTimeMillis(), getUpdateTime());
    }

    @Override
    public int getViewType() {
        return R.layout.item_message;
    }

    @Override
    public int getViewVariableId() {
        return BR.chat;
    }
}
