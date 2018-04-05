package cn.chestnut.mvvm.teamworker.module.team;

import android.view.View;

import java.util.List;

import cn.chestnut.mvvm.teamworker.databinding.ItemBuildTeamBinding;
import cn.chestnut.mvvm.teamworker.main.adapter.BaseRecyclerViewAdapter;
import cn.chestnut.mvvm.teamworker.model.PhoneDirectoryPerson;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/5 13:42:21
 * Description:
 * Email: xiaoting233zhang@126.com
 */

public class BuildTeamAdapter extends BaseRecyclerViewAdapter<PhoneDirectoryPerson, ItemBuildTeamBinding> {

    public BuildTeamAdapter(List<PhoneDirectoryPerson> mItems) {
        super(mItems);
    }

    @Override
    protected void handleViewHolder(ItemBuildTeamBinding binding, PhoneDirectoryPerson obj, final int position) {
        binding.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItems.remove(position);
                BuildTeamAdapter.this.notifyItemRemoved(position);
                BuildTeamAdapter.this.notifyDataSetChanged();
            }
        });
    }

}
