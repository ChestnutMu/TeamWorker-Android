package cn.chestnut.mvvm.teamworker.module.massage.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.List;

import cn.chestnut.mvvm.teamworker.databinding.ItemChatPersonalBinding;
import cn.chestnut.mvvm.teamworker.main.adapter.BaseRecyclerViewAdapter;
import cn.chestnut.mvvm.teamworker.module.massage.bean.MessageVo;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/1/16 22:03:58
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class ChatPersonalAdapter extends BaseRecyclerViewAdapter<MessageVo, ItemChatPersonalBinding> {

    private String userId;

    public ChatPersonalAdapter(List<MessageVo> mItems, String userId) {
        super(mItems);
        this.userId = userId;
    }

    @Override
    protected void handleViewHolder(ItemChatPersonalBinding binding, MessageVo obj, int position) {
        if (obj.getMessage().getSenderId().equals(userId)) {
            binding.llRight.setVisibility(View.VISIBLE);
            binding.llLeft.setVisibility(View.GONE);
            binding.tvRightContent.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
        } else {
            binding.llLeft.setVisibility(View.VISIBLE);
            binding.llRight.setVisibility(View.GONE);
            binding.tvRightContent.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
        }

    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).getChatPersonalViewType();
    }
}
