package cn.chestnut.mvvm.teamworker.module.user;

import java.util.List;

import cn.chestnut.mvvm.teamworker.databinding.ItemNewFriendBinding;
import cn.chestnut.mvvm.teamworker.main.adapter.BaseRecyclerViewAdapter;
import cn.chestnut.mvvm.teamworker.model.NewFriendRequest;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/6 21:36:51
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class NewFriendAdapter extends BaseRecyclerViewAdapter<NewFriendRequest,ItemNewFriendBinding> {

    public NewFriendAdapter(List<NewFriendRequest> mItems) {
        super(mItems);
    }
}
