package cn.chestnut.mvvm.teamworker.main.activity;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityRegisterBinding;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.model.User;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.utils.StringUtil;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/1/10 11:18:57
 * Description：注册
 * Email: xiaoting233zhang@126.com
 */

public class RegisterActivity extends BaseActivity {

    private ActivityRegisterBinding binding;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("注册");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register);
        addListener();
    }

    private void addListener() {
        binding.tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = binding.etAccount.getText().toString();
                String password = binding.etPassword.getText().toString();
                String confirmPassword = binding.etConfirmPassword.getText().toString();
                if (StringUtil.isBlank(account) | StringUtil.isBlank(password) | StringUtil.isBlank(confirmPassword)) {
                    showToast("用户名和密码不能为空");
                } else if (password.length() < 6) {
                    showToast("密码不能小于6位");
                } else if (!password.equals(confirmPassword)) {
                    showToast("两次输入密码不一致");
                } else {
                    register(account, password);
                }
            }
        });

        //点击输入框外则隐藏软键盘
        binding.llParentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });
    }

    /**
     * 注册新账号
     */
    private void register(String account, String password) {
        Map<String, Object> params = new HashMap<>();
        params.put("account", account);
        params.put("password", password);
        RequestManager.getInstance(this).executeRequest(HttpUrls.ADD_USER, params, new AppCallBack<ApiResponse<User>>() {

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

    }
}
