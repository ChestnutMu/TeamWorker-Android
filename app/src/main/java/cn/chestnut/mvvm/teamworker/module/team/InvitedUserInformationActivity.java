package cn.chestnut.mvvm.teamworker.module.team;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import cn.chestnut.mvvm.teamworker.BR;
import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityUserInformationBinding;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.model.UserFriend;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/6 14:56:48
 * Description：查看用户资料，并拉入团队中
 * Email: xiaoting233zhang@126.com
 */

public class InvitedUserInformationActivity extends BaseActivity {

    private ActivityUserInformationBinding binding;

    private UserFriend userFriend;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("详情资料");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_user_information, viewGroup, true);
        initData();
        initView();
    }

    protected void initData() {
        getUserDetail();
    }

    protected void initView() {
        binding.btnSubmit.setText("拉入团队中");
        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    private void getUserDetail() {
        Map<String, String> param = new HashMap<>(1);
        param.put("friendId", getIntent().getStringExtra("userId"));
        RequestManager.getInstance(this).executeRequest(HttpUrls.GET_USER_DETAIL, param, new AppCallBack<ApiResponse<UserFriend>>() {
            @Override
            public void next(ApiResponse<UserFriend> response) {
                if (response.isSuccess()) {
                    userFriend = response.getData();
                    binding.setVariable(BR.userInformation, userFriend.getUser());
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
