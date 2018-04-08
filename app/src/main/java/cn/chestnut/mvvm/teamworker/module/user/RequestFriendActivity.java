package cn.chestnut.mvvm.teamworker.module.user;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityVerifyApplicationBinding;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.model.User;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/6 16:11:39
 * Description：添加好友，验证申请
 * Email: xiaoting233zhang@126.com
 */

public class RequestFriendActivity extends BaseActivity {

    private ActivityVerifyApplicationBinding binding;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("验证申请");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_verify_application, viewGroup, true);
        addListener();
    }

    protected void addListener() {
        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestFriend();
            }
        });

        //点击软键盘上的回车键进行搜索操作
        binding.etApplicationContent.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    requestFriend();
                    return true;
                }
                return false;
            }
        });

        //点击输入框外则隐藏软键盘
        binding.llParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

    }

    private void requestFriend() {
        Map<String, String> params = new HashMap<>(1);
        params.put("userId", getIntent().getStringExtra("userId"));
        params.put("authenticationMessage", binding.etApplicationContent.getText().toString());
        RequestManager.getInstance(this).executeRequest(HttpUrls.SEND_FRIEND_REQUEST, params, new AppCallBack<ApiResponse<User>>() {
            @Override
            public void next(ApiResponse<User> response) {
                showToast(response.getMessage());
                finish();
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
