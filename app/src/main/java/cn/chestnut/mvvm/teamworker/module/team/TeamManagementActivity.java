package cn.chestnut.mvvm.teamworker.module.team;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityTeamManagementBinding;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.module.approval.AskForWorkOffActivity;
import cn.chestnut.mvvm.teamworker.module.checkattendance.CheckAttendanceActivity;
import cn.chestnut.mvvm.teamworker.module.work.WorkFragment;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/10 15:28:19
 * Description：团队管理
 * Email: xiaoting233zhang@126.com
 */

public class TeamManagementActivity extends BaseActivity {

    private ActivityTeamManagementBinding binding;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("团队管理");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_team_management, viewGroup, true);
        addListener();
    }

    @Override
    protected void addListener() {
        //团队成员管理
        binding.llTeamMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(TeamManagementActivity.this, TeamMemberActivity.class);
                intent1.putExtra("teamId", getIntent().getStringExtra("teamId"));
                intent1.putExtra("roleType", getIntent().getIntExtra("type", -1));
                startActivity(intent1);
            }
        });

        //查看成员的工作汇报
        binding.llWorkoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TeamManagementActivity.this, AskForWorkOffActivity.class));
            }
        });

        //查看成员的考勤情况
        binding.llAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TeamManagementActivity.this, CheckAttendanceActivity.class));
            }
        });

        //审批成员的报销申请
        binding.llDecrusement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //团队成员权限管理
        binding.llPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getIntent().getIntExtra("type", -1) == WorkFragment.TEAM_OWNER) {
                    Intent intent2 = new Intent(TeamManagementActivity.this, PermissionActivity.class);
                    intent2.putExtra("teamId", getIntent().getStringExtra("teamId"));
                    startActivity(intent2);
                } else {
                    showToast("只有团队所有者可以修改权限");
                }
            }
        });

        //审批成员的请假申请
        binding.llWorkoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TeamManagementActivity.this, AskForWorkOffActivity.class));
            }
        });

        //审批成员的物品领用申请
        binding.llUseGood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //审批成员的用章申请
        binding.llUseSeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
