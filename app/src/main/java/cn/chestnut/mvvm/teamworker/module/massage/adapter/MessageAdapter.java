package cn.chestnut.mvvm.teamworker.module.massage.adapter;

import android.content.Context;

import java.util.List;

import cn.chestnut.mvvm.teamworker.databinding.ItemMessageBinding;
import cn.chestnut.mvvm.teamworker.main.adapter.BaseRecyclerViewAdapter;
import cn.chestnut.mvvm.teamworker.model.Chat;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/29 15:36:20
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class MessageAdapter extends BaseRecyclerViewAdapter<Chat, ItemMessageBinding> {

    private Context mContext;

    public MessageAdapter(List<Chat> mItems) {
        super(mItems);
    }

    @Override
    protected void handleViewHolder(ItemMessageBinding binding, Chat obj, int position) {

    }
}
