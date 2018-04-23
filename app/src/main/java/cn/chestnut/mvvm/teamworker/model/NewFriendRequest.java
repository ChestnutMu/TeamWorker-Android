package cn.chestnut.mvvm.teamworker.model;

import android.util.SparseArray;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import cn.chestnut.mvvm.teamworker.BR;
import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.main.adapter.BindingItem;

import org.greenrobot.greendao.annotation.Generated;

import java.io.Serializable;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/6 19:29:22
 * Description：好友请求
 * Email: xiaoting233zhang@126.com
 */
@Entity
public class NewFriendRequest extends BindingItem implements Serializable {

    private static final long serialVersionUID = 42L;

    @Id(autoincrement = true)
    private Long id;

    //好友请求Id
    private String newFriendRequestId;

    //好友请求者Id
    private String requesterId;

    //好友请求接收者Id
    private String recipientId;

    //验证消息
    private String authenticationMessage;

    //好友请求时间
    private long time;

    //好友请求者昵称
    private String requesterNickname;

    //好友请求者账号
    private String requesterTelephone;

    //好友请求者头像
    private String requesterAvatar;

    //已接受好友请求
    private boolean accepted;

    //已发送成功
    private boolean send;

    @Generated(hash = 1056898298)
    public NewFriendRequest(Long id, String newFriendRequestId, String requesterId,
                            String recipientId, String authenticationMessage, long time,
                            String requesterNickname, String requesterTelephone,
                            String requesterAvatar, boolean accepted, boolean send) {
        this.id = id;
        this.newFriendRequestId = newFriendRequestId;
        this.requesterId = requesterId;
        this.recipientId = recipientId;
        this.authenticationMessage = authenticationMessage;
        this.time = time;
        this.requesterNickname = requesterNickname;
        this.requesterTelephone = requesterTelephone;
        this.requesterAvatar = requesterAvatar;
        this.accepted = accepted;
        this.send = send;
    }

    @Generated(hash = 2136299112)
    public NewFriendRequest() {
    }

    public String getNewFriendRequestId() {
        return newFriendRequestId;
    }

    public void setNewFriendRequestId(String newFriendRequestId) {
        this.newFriendRequestId = newFriendRequestId;
    }

    public String getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(String requesterId) {
        this.requesterId = requesterId;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public String getAuthenticationMessage() {
        return authenticationMessage;
    }

    public void setAuthenticationMessage(String authenticationMessage) {
        this.authenticationMessage = authenticationMessage;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getRequesterNickname() {
        return requesterNickname;
    }

    public void setRequesterNickname(String requesterNickname) {
        this.requesterNickname = requesterNickname;
    }

    public String getRequesterTelephone() {
        return requesterTelephone;
    }

    public void setRequesterTelephone(String requesterTelephone) {
        this.requesterTelephone = requesterTelephone;
    }

    public String getRequesterAvatar() {
        return requesterAvatar;
    }

    public void setRequesterAvatar(String requesterAvatar) {
        this.requesterAvatar = requesterAvatar;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public boolean isSend() {
        return send;
    }

    public void setSend(boolean send) {
        this.send = send;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean getAccepted() {
        return this.accepted;
    }

    public boolean getSend() {
        return this.send;
    }

    @Override
    public int getViewType() {
        return R.layout.item_new_friend;
    }

    @Override
    public int getViewVariableId() {
        return BR.request;
    }
}
