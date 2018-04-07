package cn.chestnut.mvvm.teamworker.module.user;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;
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
import cn.chestnut.mvvm.teamworker.model.User;
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
        initView();
        initData();
        addListener();
    }

    protected void initData() {
        getNewFriendRequest();
    }

    protected void initView() {
        requestList = new ArrayList<>();
        adapter = new NewFriendAdapter(this, requestList);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.setAdapter(adapter);
    }

    protected void addListener() {
        adapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(NewFriendActivity.this, UserInformationActivity.class);
                intent.putExtra("userId", requestList.get(position).getRequesterId());
                startActivity(intent);
            }
        });
        adapter.setAcceptFriendRequestListener(new NewFriendAdapter.AcceptFriendRequestListener() {
            @Override
            public void acceptFriendRequest(String userId,String requestId) {
                addUserRelation(userId,requestId);
            }
        });
    }

    private void getNewFriendRequest() {
        String userId = PreferenceUtil.getInstances(this).getPreferenceString("userId");
        Map param = new HashMap<String, String>(1);
        param.put("userId", userId);
        RequestManager.getInstance(this).executeRequest(HttpUrls.GET_REQUEST_BY_USERID, param, new AppCallBack<ApiResponse<List<NewFriendRequest>>>() {
            @Override
            public void next(ApiResponse<List<NewFriendRequest>> response) {
                if (response.isSuccess()) {
                    requestList.addAll(response.getData());
                    adapter.notifyDataSetChanged();
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

    private void addUserRelation(final String userId,String requestId) {
        Map params = new HashMap<String, String>(1);
        params.put("userId", userId);
        params.put("requestId",requestId);
        RequestManager.getInstance(this).executeRequest(HttpUrls.ADD_USER_RELATION, params, new AppCallBack<ApiResponse<User>>() {
            @Override
            public void next(ApiResponse<User> response) {
                showToast(response.getMessage());
            }

            @Override
            public void error(Throwable error) {

            }

            @Override
            public void complete() {

            }
        });
        if (requestList.size() > 0) {
            requestList.clear();
        }
        getNewFriendRequest();
    }
}
