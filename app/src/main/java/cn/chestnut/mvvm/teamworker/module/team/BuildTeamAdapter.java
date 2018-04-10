package cn.chestnut.mvvm.teamworker.module.team;

import android.view.View;

import java.util.List;

import cn.chestnut.mvvm.teamworker.databinding.ItemBuildTeamBinding;
import cn.chestnut.mvvm.teamworker.main.adapter.BaseRecyclerViewAdapter;
import cn.chestnut.mvvm.teamworker.model.User;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/5 13:42:21
 * Description:
 * Email: xiaoting233zhang@126.com
 */

public class BuildTeamAdapter extends BaseRecyclerViewAdapter<User, ItemBuildTeamBinding> {

    public static int BUILD_TEAM_VIEW_TYPE = 7;

    private RemoveMemberListener removeMemberListener;

    public BuildTeamAdapter(List<User> mItems) {
        super(mItems);
    }

    @Override
    protected void handleViewHolder(ItemBuildTeamBinding binding, User obj, final int position) {
        binding.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeMemberListener.removeMember(position);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).getOtherViewType(BUILD_TEAM_VIEW_TYPE);
    }

    interface RemoveMemberListener {
        void removeMember(int position);
    }

    public void setRemoveMemberListener(RemoveMemberListener removeMemberListener) {
        this.removeMemberListener = removeMemberListener;
    }
}
