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
import cn.chestnut.mvvm.teamworker.module.approval.PurchaseListActivity;
import cn.chestnut.mvvm.teamworker.module.approval.ReimbursementListActivity;
import cn.chestnut.mvvm.teamworker.module.approval.UseGoodListActivity;
import cn.chestnut.mvvm.teamworker.module.approval.WorkOffListActivity;
import cn.chestnut.mvvm.teamworker.module.checkattendance.CheckAttendanceActivity;
import cn.chestnut.mvvm.teamworker.module.checkattendance.PunchClockActivity;
import cn.chestnut.mvvm.teamworker.module.checkattendance.SelectMemberActivity;
import cn.chestnut.mvvm.teamworker.module.report.DayReportListActivity;
import cn.chestnut.mvvm.teamworker.module.report.WorkReportActivity;
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

    private String teamId;

    private int roleType;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("团队管理");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_team_management, viewGroup, true);
        initData();
        addListener();
    }

    @Override
    protected void initData() {
        teamId = getIntent().getStringExtra("teamId");
        roleType = getIntent().getIntExtra("roleType", -1);
    }

    @Override
    protected void addListener() {
        //团队成员管理
        binding.llTeamMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeamManagementActivity.this, TeamMemberActivity.class);
                intent.putExtra("teamId", teamId);
                intent.putExtra("roleType", roleType);
                startActivity(intent);
            }
        });

        //查看成员的工作汇报
        binding.llWorkReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeamManagementActivity.this, WorkReportActivity.class);
                intent.putExtra("teamId", teamId);
                startActivity(intent);
            }
        });

        //查看成员的考勤情况
        binding.llAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeamManagementActivity.this, SelectMemberActivity.class);
                intent.putExtra("teamId", teamId);
                startActivity(intent);
            }
        });

        //审批成员的报销申请
        binding.llReimbursement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeamManagementActivity.this, ReimbursementListActivity.class);
                intent.putExtra("teamId", teamId);
                intent.putExtra("reimbursementType", WorkFragment.TEAM_DATA_TYPE);
                startActivity(intent);
            }
        });

        //团队成员权限管理
        binding.llPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (roleType == WorkFragment.TEAM_OWNER) {
                    Intent intent = new Intent(TeamManagementActivity.this, PermissionActivity.class);
                    intent.putExtra("teamId", teamId);
                    startActivity(intent);
                } else {
                    showToast("只有团队所有者可以修改权限");
                }
            }
        });

        //审批成员的请假申请
        binding.llWorkoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeamManagementActivity.this, WorkOffListActivity.class);
                intent.putExtra("teamId", teamId);
                intent.putExtra("workOffType", WorkFragment.TEAM_DATA_TYPE);
                startActivity(intent);
            }
        });

        //审批成员的物品领用申请
        binding.llUseGood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeamManagementActivity.this, UseGoodListActivity.class);
                intent.putExtra("teamId", teamId);
                intent.putExtra("useGoodType", WorkFragment.TEAM_DATA_TYPE);
                startActivity(intent);
            }
        });

        //审批成员的采购申请
        binding.llPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeamManagementActivity.this, PurchaseListActivity.class);
                intent.putExtra("teamId", teamId);
                intent.putExtra("purchaseType", WorkFragment.TEAM_DATA_TYPE);
                startActivity(intent);
            }
        });
    }
}
