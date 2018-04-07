package cn.chestnut.mvvm.teamworker.module.mine;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cn.chestnut.mvvm.teamworker.databinding.ItemMyFriendBinding;
import cn.chestnut.mvvm.teamworker.main.adapter.BaseListViewAdapter;
import cn.chestnut.mvvm.teamworker.model.MyFriend;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/7 19:29:19
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class MyFriendAdapter extends BaseListViewAdapter {
    private ItemMyFriendBinding binding;

    private List<MyFriend> myFriendList;

    private int itemLayoutId;

    private int itemId;

    public MyFriendAdapter(int itemLayoutId, int itemId, List<MyFriend> objects) {
        super(itemLayoutId, itemId, objects);
        this.itemLayoutId = itemLayoutId;
        this.itemId = itemId;
        this.myFriendList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), itemLayoutId, parent, false);
        } else {
            binding = DataBindingUtil.getBinding(convertView);
        }
        binding.setVariable(itemId, myFriendList.get(position));

        binding.tvWord.setText(myFriendList.get(position).getWordHeader());
        binding.tvNickname.setText(myFriendList.get(position).getNickname());
        //将相同字母开头的合并在一起
        if (position == 0) {
            //第一个是一定显示的
            binding.tvWord.setVisibility(View.VISIBLE);
        } else {
            //后一个与前一个对比,判断首字母是否相同，相同则隐藏
            if (myFriendList.get(position).getWordHeader().equals(myFriendList.get(position - 1).getWordHeader())) {
                binding.tvWord.setVisibility(View.GONE);
            } else {
                binding.tvWord.setVisibility(View.VISIBLE);
            }
        }
        return binding.getRoot();
    }
}
