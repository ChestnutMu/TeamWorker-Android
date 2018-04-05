package cn.chestnut.mvvm.teamworker.module.team;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cn.chestnut.mvvm.teamworker.databinding.ItemPhoneDirectoryBinding;
import cn.chestnut.mvvm.teamworker.main.adapter.BaseListViewAdapter;
import cn.chestnut.mvvm.teamworker.model.PhoneDirctoryPerson;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/5 12:01:58
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class PhoneDirectoryAdapter extends BaseListViewAdapter{

    private ItemPhoneDirectoryBinding binding;

    private List<PhoneDirctoryPerson> personList;

    private int itemLayoutId;
    private int itemId;

    public PhoneDirectoryAdapter(int itemLayoutId, int itemId, List<PhoneDirctoryPerson> objects) {
        super(itemLayoutId, itemId, objects);
        this.itemLayoutId = itemLayoutId;
        this.itemId = itemId;
        this.personList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), itemLayoutId, parent, false);
        } else {
            binding = DataBindingUtil.getBinding(convertView);
        }
        binding.setVariable(itemId, personList.get(position));

        binding.tvWord.setText(personList.get(position).getWordHeader());
        binding.tvName.setText(personList.get(position).getName());
        //将相同字母开头的合并在一起
        if (position == 0) {
            //第一个是一定显示的
            binding.tvWord.setVisibility(View.VISIBLE);
        } else {
            //后一个与前一个对比,判断首字母是否相同，相同则隐藏
            if (personList.get(position).getWordHeader().equals(personList.get(position-1).getWordHeader())) {
                binding.tvWord.setVisibility(View.GONE);
            } else {
                binding.tvWord.setVisibility(View.VISIBLE);
            }
        }
        return binding.getRoot();
    }
}
