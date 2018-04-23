package cn.chestnut.mvvm.teamworker.module.team;

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
import cn.chestnut.mvvm.teamworker.databinding.ActivityPermissionBinding;
import cn.chestnut.mvvm.teamworker.databinding.ActivityTeamMemberBinding;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.model.User;
import cn.chestnut.mvvm.teamworker.module.user.UserInformationActivity;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/10 14:57:54
 * Description：管理团队权限
 * Email: xiaoting233zhang@126.com
 */

public class PermissionActivity extends BaseActivity {

    private ActivityPermissionBinding binding;

    private List<User> userList;

    private PermissionAdapter adapter;

    private String teamId;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("修改管理权限");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_permission, viewGroup, true);
        initView();
        initData();
        addListener();
    }

    @Override
    protected void initView() {
        userList = new ArrayList<>();
        adapter = new PermissionAdapter(R.layout.item_permission, BR.user, userList);
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
                UserInformationActivity.startActivity(PermissionActivity.this, userList.get(position).getUserId(), false, userList.get(position));
            }
        });

        adapter.setOnAddPermissionLisenter(new PermissionAdapter.OnAddPermissionLisenter() {
            @Override
            public void onAddPermission(String teamUserId, int position) {
                modifyPermission(teamUserId, "1");
            }
        });

        adapter.setOnDeletePermissionLisenter(new PermissionAdapter.OnDeletePermissionLisenter() {
            @Override
            public void onDeletePermisson(String teamUserId, int position) {
                modifyPermission(teamUserId, "0");
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

    private void modifyPermission(String teamUserId, String type) {
        Map<String, String> params = new HashMap<>();
        params.put("teamId", teamId);
        params.put("teamUserId", teamUserId);
        params.put("type", type);//团队人员类型 0普通人 1管理员
        showProgressDialog(this);
        RequestManager.getInstance(this).executeRequest(HttpUrls.AUTH_TEAM_USER, params, new AppCallBack<ApiResponse<Object>>() {
            @Override
            public void next(ApiResponse<Object> response) {
                if (response.isSuccess()) {
                    showToast("修改团队成员权限成功");
                } else {
                    showToast("修改团队成员权限失败");
                }
            }

            @Override
            public void error(Throwable error) {
                hideProgressDialog();
            }

            @Override
            public void complete() {
                hideProgressDialog();
            }
        });
    }

}
