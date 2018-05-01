package cn.chestnut.mvvm.teamworker.module.team;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityViewTeamInformationBinding;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.model.Team;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/29 21:49:29
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class ViewTeamInformationActivity extends BaseActivity {

    private ActivityViewTeamInformationBinding binding;

    private Team team;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("团队资料");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_view_team_information, viewGroup, true);
        initView();
    }

    @Override
    protected void initView() {
        team = (Team) getIntent().getSerializableExtra("team");
        binding.setTeam(team);
    }
}
