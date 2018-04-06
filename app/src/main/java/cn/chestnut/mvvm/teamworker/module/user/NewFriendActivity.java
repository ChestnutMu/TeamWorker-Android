package cn.chestnut.mvvm.teamworker.module.user;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityNewFriendBinding;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.model.NewFriendRequest;
import cn.chestnut.mvvm.teamworker.utils.PreferenceUtil;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/6 17:21:14
 * Description：新朋友
 * Email: xiaoting233zhang@126.com
 */

public class NewFriendActivity extends BaseActivity {

    private ActivityNewFriendBinding binding;

    private List<NewFriendRequest> requestList;

    private NewFriendAdapter adapter;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("新朋友");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_new_friend, viewGroup, true);
        initData();
        initView();
        addListener();
    }

    protected void initData() {
        getNotSendRequestByUserId();
    }

    protected void initView() {
        adapter = new NewFriendAdapter(requestList);
        binding.recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        binding.recyclerView.setLayoutManager(manager);
    }

    protected void addListener() {

    }

    private void getNotSendRequestByUserId() {
        String userId = PreferenceUtil.getInstances(this).getPreferenceString("userId");
        Map param = new HashMap<String, String>(1);
        param.put("userId", userId);
        RequestManager.getInstance(this).executeRequest(HttpUrls.GET_REQUEST_BY_USERID, param, new AppCallBack<ApiResponse<List<NewFriendRequest>>>() {
            @Override
            public void next(ApiResponse<List<NewFriendRequest>> response) {
                if (response.isSuccess()) {
                    requestList.addAll(response.getData());
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
