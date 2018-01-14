package cn.chestnut.mvvm.teamworker.module.massage.adapter;

import android.content.Context;

import java.util.List;

import cn.chestnut.mvvm.teamworker.databinding.ItemMessageBinding;
import cn.chestnut.mvvm.teamworker.main.adapter.BaseRecyclerViewAdapter;
import cn.chestnut.mvvm.teamworker.main.common.MyApplication;
import cn.chestnut.mvvm.teamworker.module.massage.MessageDaoUtils;
import cn.chestnut.mvvm.teamworker.module.massage.bean.MessageUser;
import cn.chestnut.mvvm.teamworker.module.massage.bean.MessageVo;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/29 15:36:20
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class MessageAdapter extends BaseRecyclerViewAdapter<MessageVo, ItemMessageBinding> {

    private MessageDaoUtils messageDaoUtils;

    private OnUpdateMessageLayoutListener onUpdateMessageLayoutListener;

    public MessageAdapter(List<MessageVo> mItems, Context mContext) {
        super(mItems);
        messageDaoUtils = new MessageDaoUtils(mContext);
    }

    @Override
    protected void handleViewHolder(ItemMessageBinding binding, MessageVo obj, int position) {
        if (obj.getMessageUser() == null) {
            //本地获取
            List<MessageUser> messageUserList = messageDaoUtils.queryMessageUserByUserId(obj.getMessage().getSenderId());
            //本地获取没有则从服务器拿
            if (messageUserList.isEmpty()) {
                updateMessageUser(this, obj);
            } else {
                obj.setMessageUser(messageUserList.get(0));
            }
        }
    }


    private void updateMessageUser(MessageAdapter messageAdapter, MessageVo obj) {
        if (onUpdateMessageLayoutListener != null) {
            onUpdateMessageLayoutListener.onUpdate(messageAdapter, obj);
        }
    }

    public MessageDaoUtils getMessageDaoUtils() {
        return messageDaoUtils;
    }

    public void setMessageDaoUtils(MessageDaoUtils messageDaoUtils) {
        this.messageDaoUtils = messageDaoUtils;
    }

    public OnUpdateMessageLayoutListener getOnUpdateMessageLayoutListener() {
        return onUpdateMessageLayoutListener;
    }

    public void setOnUpdateMessageLayoutListener(OnUpdateMessageLayoutListener onUpdateMessageLayoutListener) {
        this.onUpdateMessageLayoutListener = onUpdateMessageLayoutListener;
    }

    public interface OnUpdateMessageLayoutListener {
        void onUpdate(MessageAdapter messageAdapter, MessageVo obj);
    }
}
