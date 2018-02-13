package cn.chestnut.mvvm.teamworker.module.massage.bean;

import cn.chestnut.mvvm.teamworker.BR;
import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.main.adapter.BindingItem;
import cn.chestnut.mvvm.teamworker.utils.FormatDateUtil;
import cn.chestnut.mvvm.teamworker.utils.StringUtil;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/29 14:43:32
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class MessageVo extends BindingItem {

    private Message message;

    private MessageUser messageUser;

    public String showSenderName() {
        if (!StringUtil.isEmpty(message.getChatName()))
            return message.getChatName();
        if (messageUser == null) {
            return "";
        } else {
            return messageUser.getNickname();
        }
    }

    public String showTime() {
        return FormatDateUtil.getMessageTime(System.currentTimeMillis(), message.getTime());
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public MessageUser getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(MessageUser messageUser) {
        this.messageUser = messageUser;
    }

    public int getChatPersonalViewType() {
        return R.layout.item_chat;
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
