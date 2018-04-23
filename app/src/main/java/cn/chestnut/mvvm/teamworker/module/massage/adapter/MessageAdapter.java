package cn.chestnut.mvvm.teamworker.module.massage.adapter;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import cn.chestnut.mvvm.teamworker.Constant;
import cn.chestnut.mvvm.teamworker.databinding.ItemMessageBinding;
import cn.chestnut.mvvm.teamworker.main.adapter.BaseRecyclerViewAdapter;
import cn.chestnut.mvvm.teamworker.main.common.MyApplication;
import cn.chestnut.mvvm.teamworker.model.Chat;
import cn.chestnut.mvvm.teamworker.utils.PreferenceUtil;
import cn.chestnut.mvvm.teamworker.utils.StringUtil;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/29 15:36:20
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class MessageAdapter extends BaseRecyclerViewAdapter<Chat, ItemMessageBinding> {

    private Gson gson;

    private OnUpdateChatLayoutListener layoutListener;

    public MessageAdapter(List<Chat> mItems) {
        super(mItems);
        gson = new Gson();
    }

    @Override
    protected void handleViewHolder(ItemMessageBinding binding, Chat chat, int position) {
        if (chat.getChatType().equals(Constant.ChatType.TYPE_CHAT_DOUBLE)) {
            if (StringUtil.isEmpty(chat.getUserId())) {
                List<String> userList = gson.fromJson(chat.getUserList(), new TypeToken<List<String>>() {
                }.getType());
                userList.remove(PreferenceUtil.getInstances(MyApplication.getInstance()).getPreferenceString("userId"));
                String senderId = userList.get(0);
                if (layoutListener != null) {
                    layoutListener.onUpdate(chat, senderId, position);
                }
            }
        }
    }

    public void setLayoutListener(OnUpdateChatLayoutListener layoutListener) {
        this.layoutListener = layoutListener;
    }

    public interface OnUpdateChatLayoutListener {
        void onUpdate(Chat chat, String senderId, int position);
    }
}
