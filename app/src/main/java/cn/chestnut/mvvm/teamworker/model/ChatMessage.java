package cn.chestnut.mvvm.teamworker.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

import java.io.Serializable;

import org.greenrobot.greendao.annotation.Generated;

import cn.chestnut.mvvm.teamworker.BR;
import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.main.adapter.BindingItem;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/11 09:15:28
 * Description：
 * Email: xiaoting233zhang@126.com
 */

@Entity
public class ChatMessage extends BindingItem implements Serializable {

    private static final long serialVersionUID = 2666712439300977506L;

    @Id(autoincrement = true)
    private Long id;

    @Unique
    private String chatMessageId;

    /*聊天室id*/
    private String chatId;

    /*接收该消息的用户id*/
    private String userId;

    /*发该消息的用户id*/
    private String senderId;

    /*发送的消息*/
    private String message;

    /*发送时间*/
    private long sendTime;

    /*是否发送消息成功*/
    private boolean done = true;

    private String nickname;

    private String avatar;

    @Generated(hash = 1140521755)
    public ChatMessage(Long id, String chatMessageId, String chatId, String userId,
            String senderId, String message, long sendTime, boolean done, String nickname,
            String avatar) {
        this.id = id;
        this.chatMessageId = chatMessageId;
        this.chatId = chatId;
        this.userId = userId;
        this.senderId = senderId;
        this.message = message;
        this.sendTime = sendTime;
        this.done = done;
        this.nickname = nickname;
        this.avatar = avatar;
    }

    @Generated(hash = 2271208)
    public ChatMessage() {
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChatMessageId() {
        return chatMessageId;
    }

    public void setChatMessageId(String chatMessageId) {
        this.chatMessageId = chatMessageId;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public boolean getDone() {
        return this.done;
    }

    @Override
    public int getViewType() {
        return R.layout.item_chat;
    }

    @Override
    public int getViewVariableId() {
        return BR.message;
    }
}
