package cn.chestnut.mvvm.teamworker.module.checkattendance;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.chestnut.mvvm.teamworker.BR;
import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivitySelectMemberBinding;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.adapter.BaseListViewAdapter;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.model.User;
import cn.chestnut.mvvm.teamworker.module.team.TeamMemberAdapter;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/10 14:57:54
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class SelectMemberActivity extends BaseActivity {

    private ActivitySelectMemberBinding binding;

    private List<User> userList;

    private BaseListViewAdapter adapter;

    private String teamId;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("选择团队成员");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_select_member, viewGroup, true);
        initView();
        initData();
        addListener();
    }

    @Override
    protected void initView() {
        userList = new ArrayList<>();
        adapter = new BaseListViewAdapter(R.layout.item_select_member, BR.user, userList);
        binding.lvTeamMember.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        teamId = getIntent().getStringExtra("teamId");
        getTeamers();
    }

    @Override
    protected void addListener() {
        binding.lvTeamMember.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SelectMemberActivity.this, CheckAttendanceActivity.class);
                intent.putExtra("teamId", teamId);
                intent.putExtra("teamUserId", userList.get(position).getUserId());
                intent.putExtra("teamUserName", userList.get(position).getNickname());
                intent.putExtra("teamUserAvatar", userList.get(position).getAvatar());
                startActivity(intent);
            }
        });

    }

    private void getTeamers() {
        Map<String, String> param = new HashMap<>();
        param.put("teamId", teamId);
        RequestManager.getInstance(this).executeRequest(HttpUrls.GET_TEAMERS, param, new AppCallBack<ApiResponse<List<User>>>() {
            @Override
            public void next(ApiResponse<List<User>> response) {
                if (response.isSuccess()) {
                    if (response.isSuccess()) {
                        userList.addAll(response.getData());
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void error(Throwable error) {

            }

            @Override
            public void complete() {

            }
        });
    }

}
