package cn.chestnut.mvvm.teamworker.module.massage.adapter;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ItemChooseUserBinding;
import cn.chestnut.mvvm.teamworker.databinding.ItemMyFriendBinding;
import cn.chestnut.mvvm.teamworker.main.adapter.BaseListViewAdapter;
import cn.chestnut.mvvm.teamworker.model.User;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/7 19:29:19
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class ChatUserAdapter extends BaseListViewAdapter<User> {
    private ItemChooseUserBinding binding;

    private int itemLayoutId;

    private int itemId;

    //选择中的
    private Set<String> chooseUserIdSet = new HashSet<>();
    //默认已经选择了的
    private Set<String> chosenUserIdSet = new HashSet<>();

    public ChatUserAdapter(int itemLayoutId, int itemId, List<User> objects) {
        super(itemLayoutId, itemId, objects);
        this.itemLayoutId = itemLayoutId;
        this.itemId = itemId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), itemLayoutId, parent, false);
        } else {
            binding = DataBindingUtil.getBinding(convertView);
        }
        binding.setVariable(itemId, objects.get(position));

        binding.tvWord.setText(objects.get(position).getWordHeader());
        binding.tvNickname.setText(objects.get(position).getNickname());
        //将相同字母开头的合并在一起
        if (position == 0) {
            //第一个是一定显示的
            binding.tvWord.setVisibility(View.VISIBLE);
        } else {
            //后一个与前一个对比,判断首字母是否相同，相同则隐藏
            if (objects.get(position).getWordHeader().equals(objects.get(position - 1).getWordHeader())) {
                binding.tvWord.setVisibility(View.GONE);
            } else {
                binding.tvWord.setVisibility(View.VISIBLE);
            }
        }

        //判断是否选择
        if (chosenUserIdSet.contains(objects.get(position).getUserId())) {
            binding.ivShowChoose.setImageResource(R.mipmap.icon_has_selected);
        } else {
            if (chooseUserIdSet.contains(objects.get(position).getUserId())) {
                binding.ivShowChoose.setImageResource(R.mipmap.icon_select);
            } else {
                binding.ivShowChoose.setImageResource(R.mipmap.icon_unselected);
            }
        }

        return binding.getRoot();
    }

    public Set<String> getChooseUserIdSet() {
        return chooseUserIdSet;
    }

    public void setChooseUserIdSet(Set<String> chooseUserIdSet) {
        this.chooseUserIdSet = chooseUserIdSet;
    }

    public Set<String> getChosenUserIdSet() {
        return chosenUserIdSet;
    }

    public void setChosenUserIdSet(Set<String> chosenUserIdSet) {
        this.chosenUserIdSet = chosenUserIdSet;
    }
}
