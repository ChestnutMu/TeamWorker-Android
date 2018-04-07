package cn.chestnut.mvvm.teamworker.model;

import cn.chestnut.mvvm.teamworker.BR;
import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.main.adapter.BindingItem;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/6 19:29:22
 * Description：好友请求
 * Email: xiaoting233zhang@126.com
 */
public class NewFriendRequest extends BindingItem{

    //好友请求Id
    private String requestId;

    //好友请求接收者Id
    private String recipientId;

    //好友请求者Id
    private String requesterId;

    //好友请求者头像
    private String requesterAvatar;

    //好友请求者账号
    private String requesterAccount;

    //好友请求者昵称
    private String requesterNickname;

    //验证消息
    private String authenticationMessage;

    //好友请求时间
    private long time;

    //已发送
    private boolean send;

    //已接受好友请求
    private boolean accepted;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getRequesterAvatar() {
        return requesterAvatar;
    }

    public void setRequesterAvatar(String requesterAvatar) {
        this.requesterAvatar = requesterAvatar;
    }

    public String getRequesterAccount() {
        return requesterAccount;
    }

    public void setRequesterAccount(String requesterAccount) {
        this.requesterAccount = requesterAccount;
    }

    public String getRequesterNickname() {
        return requesterNickname;
    }

    public void setRequesterNickname(String requesterNickname) {
        this.requesterNickname = requesterNickname;
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

    public boolean getSend() {
        return send;
    }

    public void setSend(boolean send) {
        this.send = send;
    }

    public boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
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
