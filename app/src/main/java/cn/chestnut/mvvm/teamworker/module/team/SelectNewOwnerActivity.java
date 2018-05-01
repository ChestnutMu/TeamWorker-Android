package cn.chestnut.mvvm.teamworker.module.team;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.android.databinding.library.baseAdapters.BR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivitySelectNewOwnerBinding;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.adapter.BaseListViewAdapter;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.model.User;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/30 13:36:12
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class SelectNewOwnerActivity extends BaseActivity {

    private ActivitySelectNewOwnerBinding binding;

    private BaseListViewAdapter adapter;

    private String teamId;

    private List<User> userList;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("选择新团队所有者");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_select_new_owner, viewGroup, true);
        initView();
        initData();
        addListener();
    }

    @Override
    protected void initView() {
        userList = new ArrayList<>();
        adapter = new BaseListViewAdapter(R.layout.item_select_new_owner, BR.user, userList);
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
                showGiveUpOwnerDialog(userList.get(position).getUserId());
            }
        });
    }

    private void showGiveUpOwnerDialog(final String teamUserId) {
        new AlertDialog.Builder(this)
                .setTitle("转让团队")
                .setMessage("确定要将该团队转让给该用户吗？")
                .setPositiveButton("确定转让", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        giveUpTeamOwner(teamUserId);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    private void giveUpTeamOwner(String teamUserId) {
        Map<String, String> param = new ArrayMap<>(2);
        param.put("teamId", teamId);
        param.put("teamUserId", teamUserId);
        showProgressDialog(this);
        RequestManager.getInstance(this).executeRequest(HttpUrls.GIVE_UP_TEAM_OWNER, param, new AppCallBack<ApiResponse<Object>>() {
            @Override
            public void next(ApiResponse<Object> response) {
                if (response.isSuccess()) {
                    showToast("转让团队所有者成功");
                    setResult(RESULT_OK);
                } else {
                    showToast(response.getMessage());
                }
                finish();
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

    private void getTeamers() {
        Map<String, String> param = new ArrayMap<>(1);
        param.put("teamId", teamId);
        showProgressDialog(this);
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
                hideProgressDialog();
            }

            @Override
            public void complete() {
                hideProgressDialog();
            }
        });
    }


}
