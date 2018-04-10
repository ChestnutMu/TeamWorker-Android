package cn.chestnut.mvvm.teamworker.module.team;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cn.chestnut.mvvm.teamworker.databinding.ItemPermissionBinding;
import cn.chestnut.mvvm.teamworker.databinding.ItemTeamMemberBinding;
import cn.chestnut.mvvm.teamworker.main.adapter.BaseListViewAdapter;
import cn.chestnut.mvvm.teamworker.model.User;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/10 16:25:22
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class PermissionAdapter extends BaseListViewAdapter<User> {

    private ItemPermissionBinding binding;

    private OnAddPermissionLisenter onAddPermissionLisenter;

    private OnDeletePermissionLisenter onDeletePermissionLisenter;

    private List<User> userList;

    private int itemLayoutId;

    private int itemId;

    public PermissionAdapter(int itemLayoutId, int itemId, List<User> objects) {
        super(itemLayoutId, itemId, objects);
        this.itemLayoutId = itemLayoutId;
        this.itemId = itemId;
        this.userList = objects;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), itemLayoutId, parent, false);
        } else {
            binding = DataBindingUtil.getBinding(convertView);
        }
        binding.setVariable(itemId, userList.get(position));

        binding.ivAddPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddPermissionLisenter.onAddPermission(userList.get(position).getUserId(), position);
            }
        });

        binding.ivDeletePermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeletePermissionLisenter.onDeletePermisson(userList.get(position).getUserId(), position);
            }
        });

        return binding.getRoot();
    }

    interface OnAddPermissionLisenter {
        void onAddPermission(String teamUserId, int position);
    }

    public void setOnAddPermissionLisenter(OnAddPermissionLisenter onAddPermissionLisenter) {
        this.onAddPermissionLisenter = onAddPermissionLisenter;
    }

    interface OnDeletePermissionLisenter {
        void onDeletePermisson(String teamUserId, int position);
    }

    public void setOnDeletePermissionLisenter(OnDeletePermissionLisenter onDeletePermissionLisenter) {
        this.onDeletePermissionLisenter = onDeletePermissionLisenter;
    }
}
