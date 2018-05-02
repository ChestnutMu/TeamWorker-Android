package cn.chestnut.mvvm.teamworker.module.massage.activity;

import android.databinding.DataBindingUtil;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityTeamNotificationDetailBinding;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.adapter.BaseListViewAdapter;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.model.TeamNotification;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/5/2 20:02:44
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class TeamNotificationDetailActivity extends BaseActivity {

    private ActivityTeamNotificationDetailBinding binding;

    private TeamNotification teamNotification;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("团队公告详情");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_team_notification_detail, viewGroup, true);
        initData();
    }

    @Override
    protected void initData() {
        teamNotification = (TeamNotification) getIntent().getSerializableExtra("teamNotification");
        binding.setTeamNotification(teamNotification);
    }

}
