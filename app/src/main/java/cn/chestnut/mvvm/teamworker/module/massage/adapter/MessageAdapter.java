package cn.chestnut.mvvm.teamworker.module.massage.adapter;

import java.util.List;

import cn.chestnut.mvvm.teamworker.databinding.ItemMessageBinding;
import cn.chestnut.mvvm.teamworker.main.adapter.BaseRecyclerViewAdapter;
import cn.chestnut.mvvm.teamworker.module.massage.bean.Message;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/29 15:36:20
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class MessageAdapter extends BaseRecyclerViewAdapter<Message, ItemMessageBinding> {

    public MessageAdapter(List<Message> mItems) {
        super(mItems);
    }
}
