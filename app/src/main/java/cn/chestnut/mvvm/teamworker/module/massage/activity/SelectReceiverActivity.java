package cn.chestnut.mvvm.teamworker.module.massage.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivitySelectReceiverBinding;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.module.massage.adapter.DepartmentAdapter;
import cn.chestnut.mvvm.teamworker.module.massage.adapter.UserAdapter;
import cn.chestnut.mvvm.teamworker.module.massage.bean.Department;
import cn.chestnut.mvvm.teamworker.module.massage.bean.User;
import cn.chestnut.mvvm.teamworker.service.DataManager;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/1/2 21:50:34
 * Description：选择通知接受者
 * Email: xiaoting233zhang@126.com
 */

public class SelectReceiverActivity extends BaseActivity {

    ActivitySelectReceiverBinding binding;
    private UserAdapter userAdapter;
    private ArrayList<User> userList = new ArrayList<>();
    private String departmentId;
    private int pageNum = 1;
    private int pageSize = 15;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("选择接收者");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_select_receiver,viewGroup,true);
        initData();
        initView();
        addListener();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        departmentId = getIntent().getStringExtra("departmentId");
        getUserByDepartment(departmentId,pageNum, pageSize);
    }

    private void initView() {
        userAdapter = new UserAdapter(userList);
        binding.swipeTarget.setAdapter(userAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.swipeTarget.setLayoutManager(manager);

    }

    private void addListener() {
        binding.swipeToLoadLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (userList != null) {
                    userList.clear();
                    pageNum = 1;
                    getUserByDepartment(departmentId,pageNum, pageSize);
                }
                binding.swipeToLoadLayout.setRefreshing(false);
            }
        });
        binding.swipeToLoadLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                if (userList != null) {
                    pageNum++;
                    getUserByDepartment(departmentId,pageNum, pageSize);
                }
                binding.swipeToLoadLayout.setLoadingMore(false);
            }
        });
        userAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("user",userList.get(position));
                setResult(Activity.RESULT_OK,intent);
                finish();
            }
        });
    }


    /**
     * 获取所有部门
     */
    private void getUserByDepartment(String departmentId,int pageNum, int pageSize) {
        Map<String, Object> params = new HashMap<>();
        params.put("departmentId", departmentId);
        params.put("pageNum", pageNum);
        params.put("pageSize", pageSize);
        DataManager.getInstance(this).executeRequest(HttpUrls.GET_USER_BY_DEPARTMENT, params, new AppCallBack<ApiResponse<List<User>>>() {

            @Override
            public void next(ApiResponse<List<User>> response) {
                if (response.isSuccess()) {
                    userList.addAll(response.getData());
                    userAdapter.notifyDataSetChanged();
                } else {
                    showToast(response.getMessage());
                }
            }

            @Override
            public void error(Throwable error) {

            }

            @Override
            public void complete() {

            }

            @Override
            public void before() {
            }
        });

    }
}
