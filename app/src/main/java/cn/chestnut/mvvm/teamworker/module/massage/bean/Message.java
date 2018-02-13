package cn.chestnut.mvvm.teamworker.module.massage.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;

import java.io.Serializable;

import cn.chestnut.mvvm.teamworker.BR;
import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.main.adapter.BindingItem;
import cn.chestnut.mvvm.teamworker.utils.FormatDateUtil;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/29 14:43:32
 * Description：
 * Email: xiaoting233zhang@126.com
 */

@Entity
public class Message implements Serializable{

    @Id(autoincrement = true)
    private Long id;

    @Unique
    private String messageId;

    private String chatId;

    private String chatName;

    @NotNull
    private String senderId;
    
    private String receiverId;

    private String title;

    @NotNull
    private String content;

    @NotNull
    private Long time;


    private static final long serialVersionUID = 42L;

    @Generated(hash = 876155601)
    public Message(Long id, String messageId, String chatId, String chatName,
            @NotNull String senderId, String receiverId, String title,
            @NotNull String content, @NotNull Long time) {
        this.id = id;
        this.messageId = messageId;
        this.chatId = chatId;
        this.chatName = chatName;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.title = title;
        this.content = content;
        this.time = time;
    }

    @Generated(hash = 637306882)
    public Message() {
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getMessageId() {
        return this.messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderId() {
        return this.senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return this.receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getTime() {
        return this.time;
    }

    public void setTime(Long time) {
        this.time = time;
    }


    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
