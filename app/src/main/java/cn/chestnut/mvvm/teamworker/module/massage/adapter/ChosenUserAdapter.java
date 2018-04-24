package cn.chestnut.mvvm.teamworker.module.massage.adapter;

import java.util.List;

import cn.chestnut.mvvm.teamworker.databinding.ItemChosenUserBinding;
import cn.chestnut.mvvm.teamworker.main.adapter.BaseRecyclerViewAdapter;
import cn.chestnut.mvvm.teamworker.model.User;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/1/16 22:03:58
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class ChosenUserAdapter extends BaseRecyclerViewAdapter<User, ItemChosenUserBinding> {

    public ChosenUserAdapter(List<User> mItems) {
        super(mItems);
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).getChooseUserViewType();
    }
}
