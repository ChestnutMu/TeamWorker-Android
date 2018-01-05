package cn.chestnut.mvvm.teamworker.module.massage.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;

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
public class Message extends BindingItem {

    @Id(autoincrement = true)
    private Long id;

    @Unique
    private String messageId;

    @NotNull
    private String senderId;

    @NotNull
    private String receiverId;

    @NotNull
    private String title;

    @NotNull
    private String content;

    @NotNull
    private Long time;

    private String senderName;

    @Generated(hash = 1089162060)
    public Message(Long id, String messageId, @NotNull String senderId,
            @NotNull String receiverId, @NotNull String title,
            @NotNull String content, @NotNull Long time, String senderName) {
        this.id = id;
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.title = title;
        this.content = content;
        this.time = time;
        this.senderName = senderName;
    }

    @Generated(hash = 637306882)
    public Message() {
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String showTime() {
        return FormatDateUtil.getTimeRange(System.currentTimeMillis(), time);
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

    @Override
    public int getViewType() {
        return R.layout.item_message;
    }

    @Override
    public int getViewVariableId() {
        return BR.message;
    }
}
