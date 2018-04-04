package cn.chestnut.mvvm.teamworker.module.team;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityBuildTeamBinding;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/3 22:20:12
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class BuildTeamActivity extends BaseActivity {

    ActivityBuildTeamBinding binding;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("创建团队");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_build_team, viewGroup, true);
    }

    private void addListener(){
        binding.llAddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent());
            }
        });
    }

}
