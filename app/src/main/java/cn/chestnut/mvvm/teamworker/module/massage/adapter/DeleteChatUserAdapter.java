package cn.chestnut.mvvm.teamworker.module.massage.adapter;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ItemDeleteChatUserBinding;
import cn.chestnut.mvvm.teamworker.main.adapter.BaseListViewAdapter;
import cn.chestnut.mvvm.teamworker.model.UserInfo;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/7 19:29:19
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class DeleteChatUserAdapter extends BaseListViewAdapter<UserInfo> {
    private ItemDeleteChatUserBinding binding;

    private int itemLayoutId;

    private int itemId;

    //选择中的
    private List<UserInfo> chooseUserIdSet;

    public DeleteChatUserAdapter(int itemLayoutId, int itemId, List<UserInfo> objects) {
        super(itemLayoutId, itemId, objects);
        this.itemLayoutId = itemLayoutId;
        this.itemId = itemId;
        this.chooseUserIdSet = new ArrayList<>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), itemLayoutId, parent, false);
        } else {
            binding = DataBindingUtil.getBinding(convertView);
        }
        binding.setVariable(itemId, objects.get(position));

        //判断是否选择
        if (chooseUserIdSet.contains(objects.get(position))) {
            binding.ivShowChoose.setImageResource(R.mipmap.icon_select);
        } else {
            binding.ivShowChoose.setImageResource(R.mipmap.icon_unselected);
        }

        return binding.getRoot();
    }

    public List<UserInfo> getChooseUserIdSet() {
        return chooseUserIdSet;
    }

    public void setChooseUserIdSet(List<UserInfo> chooseUserIdSet) {
        this.chooseUserIdSet = chooseUserIdSet;
    }
}
