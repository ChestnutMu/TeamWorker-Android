package cn.chestnut.mvvm.teamworker.module.work;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cn.chestnut.mvvm.teamworker.databinding.ItemMyTeamBinding;
import cn.chestnut.mvvm.teamworker.main.adapter.BaseListViewAdapter;
import cn.chestnut.mvvm.teamworker.model.Team;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/8 12:48:46
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class WorkMyTeamAdapter extends BaseListViewAdapter{
    private ItemMyTeamBinding binding;

    private List<Team> teamList;

    private int itemLayoutId;

    private int itemId;

    public WorkMyTeamAdapter(int itemLayoutId, int itemId, List<Team> objects) {
        super(itemLayoutId, itemId, objects);
        this.itemLayoutId = itemLayoutId;
        this.itemId = itemId;
        this.teamList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), itemLayoutId, parent, false);
        } else {
            binding = DataBindingUtil.getBinding(convertView);
        }

        return binding.getRoot();
    }
}
