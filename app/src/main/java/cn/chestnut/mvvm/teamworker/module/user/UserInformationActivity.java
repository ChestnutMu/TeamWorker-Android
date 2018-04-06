package cn.chestnut.mvvm.teamworker.module.user;

import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import cn.chestnut.mvvm.teamworker.model.User;
import cn.chestnut.mvvm.teamworker.module.work.WorkFragment;
import cn.chestnut.mvvm.teamworker.utils.PreferenceUtil;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/6 14:56:48
 * Description：用户资料
 * Email: xiaoting233zhang@126.com
 */

public class UserInformationActivity extends BaseActivity {

    private ActivityUserInformationBinding binding;

    private User user;

    private boolean isMyFriend;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("详情资料");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_user_information, viewGroup, true);
        initData();
        initView();
        addListener();
    }

    protected void initData() {
        user = (User) getIntent().getSerializableExtra("user");
    }

    protected void initView() {
        binding.setVariable(BR.userInformation, user);
        isMyFriend();
        //如果该用户是自己或者是好友，那么显示"发送消息"的按钮，否则显示默认的"添加到通讯录"按钮
        if (isMyFriend || user.getUserId().equals(PreferenceUtil.getInstances(this).getPreferenceString("userId"))) {
            binding.btnSubmit.setText("发送消息");
        }
    }

    protected void addListener() {
        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserInformationActivity.this, RequestFriendActivity.class);
                intent.putExtra("userId", user.getUserId());
                startActivity(intent);
            }
        });
    }

    private void isMyFriend() {
        Map<String, String> param = new HashMap<>(1);
        param.put("userId", getIntent().getStringExtra("userId"));
        RequestManager.getInstance(this).executeRequest(HttpUrls.IS_MY_FRIEND, param, new AppCallBack<ApiResponse<Boolean>>() {
            @Override
            public void next(ApiResponse<Boolean> response) {
                if (response.isSuccess()) {
                    isMyFriend = response.getData();
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
