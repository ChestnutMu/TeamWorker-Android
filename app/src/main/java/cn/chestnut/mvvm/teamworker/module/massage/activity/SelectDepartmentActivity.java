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
import cn.chestnut.mvvm.teamworker.module.massage.bean.Department;
import cn.chestnut.mvvm.teamworker.module.massage.bean.User;
import cn.chestnut.mvvm.teamworker.http.RequestManager;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/1/2 21:50:34
 * Description：选择通知接受者
 * Email: xiaoting233zhang@126.com
 */

public class SelectDepartmentActivity extends BaseActivity {

    ActivitySelectReceiverBinding binding;
    private DepartmentAdapter departmentAdapter;
    private ArrayList<Department> departmentList = new ArrayList<>();
    private int pageNum = 1;
    private int pageSize = 15;

    private final int request_code_select_person = 1001;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("选择部门");

    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_select_receiver, viewGroup, true);
        initData();
        initView();
        addListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == request_code_select_person && resultCode == Activity.RESULT_OK) {
            User user = (User) data.getExtras().getSerializable("user");
            if (user != null) {
                Intent intent = new Intent();
                intent.putExtra("user", user);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }
    }

    /**
     * 初始化数据
     */
    private void initData() {
        getAllDepartments(pageNum, pageSize);
    }

    private void initView() {
        departmentAdapter = new DepartmentAdapter(departmentList);
        binding.swipeTarget.setAdapter(departmentAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.swipeTarget.setLayoutManager(manager);

    }

    private void addListener() {
        binding.swipeToLoadLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (departmentList != null) {
                    departmentList.clear();
                    pageNum = 1;
                    getAllDepartments(pageNum, pageSize);
                }
                binding.swipeToLoadLayout.setRefreshing(false);
            }
        });
        binding.swipeToLoadLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                if (departmentList != null) {
                    pageNum++;
                    getAllDepartments(pageNum, pageSize);
                }
                binding.swipeToLoadLayout.setLoadingMore(false);
            }
        });
        departmentAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SelectDepartmentActivity.this, SelectReceiverActivity.class);
                intent.putExtra("departmentId", departmentList.get(position).getDepartmentId());
                startActivityForResult(intent, request_code_select_person);
            }
        });
    }


    /**
     * 获取所有部门
     */
    private void getAllDepartments(int pageNum, int pageSize) {
        Map<String, Object> params = new HashMap<>();
        params.put("pageNum", pageNum);
        params.put("pageSize", pageSize);
        showProgressDialog(this);
        RequestManager.getInstance(this).executeRequest(HttpUrls.GET_ALL_DEPARTMENTS, params, new AppCallBack<ApiResponse<List<Department>>>() {

            @Override
            public void next(ApiResponse<List<Department>> response) {
                if (response.isSuccess()) {
                    departmentList.addAll(response.getData());
                    departmentAdapter.notifyDataSetChanged();
                } else {
                    showToast(response.getMessage());
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
