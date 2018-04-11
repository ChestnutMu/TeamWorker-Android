package cn.chestnut.mvvm.teamworker.module.team;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityTeamManagementBinding;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.module.approval.ApprovalActivity;
import cn.chestnut.mvvm.teamworker.module.checkattendance.CheckAttendanceActivity;
import cn.chestnut.mvvm.teamworker.module.work.GridViewAdapter;
import cn.chestnut.mvvm.teamworker.module.work.WorkFragment;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/10 15:28:19
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class TeamManagementActivity extends BaseActivity {

    private ActivityTeamManagementBinding binding;

    private GridViewAdapter gridViewAdapter;

    private ArrayList<String> applicationNameList;

    private ArrayList<Integer> applicationDrawableList;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("团队管理");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_team_management, viewGroup, true);
        initData();
        initView();
    }

    @Override
    protected void initData() {
        applicationNameList = new ArrayList<>();
        applicationNameList.add("成员");
        applicationNameList.add("审批");
        applicationNameList.add("考勤");
        applicationNameList.add("权限");

        applicationDrawableList = new ArrayList<>();
        applicationDrawableList.add(R.mipmap.icon_member_manage);
        applicationDrawableList.add(R.mipmap.icon_approval);
        applicationDrawableList.add(R.mipmap.icon_attendance);
        applicationDrawableList.add(R.mipmap.icon_permission);
    }


    /**
     * 初始化界面
     */
    @Override
    protected void initView() {
        gridViewAdapter = new GridViewAdapter(this, applicationNameList, applicationDrawableList);
        binding.gvCommonApps.setAdapter(gridViewAdapter);
    }

    @Override
    protected void addListener() {
        binding.gvCommonApps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (applicationDrawableList.get(position)) {
                    case R.mipmap.icon_member_manage:
                        Intent intent1 = new Intent(TeamManagementActivity.this, TeamMemberActivity.class);
                        intent1.putExtra("teamId", getIntent().getStringExtra("teamId"));
                        startActivity(intent1);
                        break;
                    case R.mipmap.icon_attendance:
                        startActivity(new Intent(TeamManagementActivity.this, CheckAttendanceActivity.class));
                        break;
                    case R.mipmap.icon_approval:
                        startActivity(new Intent(TeamManagementActivity.this, ApprovalActivity.class));
                        break;
                    case R.mipmap.icon_permission:
                        if (getIntent().getIntExtra("type", -1) == WorkFragment.TEAM_OWNER) {
                            Intent intent2 = new Intent(TeamManagementActivity.this, PermissionActivity.class);
                            intent2.putExtra("teamId", getIntent().getStringExtra("teamId"));
                            startActivity(intent2);
                        } else {
                            showToast("只有团队所有者可以修改权限");
                        }
                        break;
                }
            }
        });
    }
}
