package cn.chestnut.mvvm.teamworker.module.massage.adapter;

import java.util.List;

import cn.chestnut.mvvm.teamworker.databinding.ItemChosenDeleteUserBinding;
import cn.chestnut.mvvm.teamworker.main.adapter.BaseRecyclerViewAdapter;
import cn.chestnut.mvvm.teamworker.model.UserInfo;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/1/16 22:03:58
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class ChosenDeleteUserAdapter extends BaseRecyclerViewAdapter<UserInfo, ItemChosenDeleteUserBinding> {

    public ChosenDeleteUserAdapter(List<UserInfo> mItems) {
        super(mItems);
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).getOtherViewType();
    }
}
