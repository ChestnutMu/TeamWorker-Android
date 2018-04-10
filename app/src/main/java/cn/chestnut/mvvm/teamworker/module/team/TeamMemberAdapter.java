package cn.chestnut.mvvm.teamworker.module.team;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

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

public class TeamMemberAdapter extends BaseListViewAdapter<User> {

    private ItemTeamMemberBinding binding;

    private OnDeleteMemberLisenter onDeleteMemberLisenter;

    private List<User> userList;

    private int itemLayoutId;

    private int itemId;

    public TeamMemberAdapter(int itemLayoutId, int itemId, List<User> objects) {
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

        binding.ivDeleteMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeleteMemberLisenter.onDeleteMember(userList.get(position).getUserId(), position);
            }
        });

        return binding.getRoot();
    }

    interface OnDeleteMemberLisenter {
        void onDeleteMember(String teamUserId, int position);
    }

    public void setOnDeleteMemberLisenter(OnDeleteMemberLisenter onDeleteMemberLisenter) {
        this.onDeleteMemberLisenter = onDeleteMemberLisenter;
    }
}
