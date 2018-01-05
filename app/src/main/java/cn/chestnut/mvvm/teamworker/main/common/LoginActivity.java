package cn.chestnut.mvvm.teamworker.main.common;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityLoginBinding;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpResponseCodes;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RQCallBack;
import cn.chestnut.mvvm.teamworker.main.bean.User;
import cn.chestnut.mvvm.teamworker.module.massage.activity.ChatPersonalActivity;
import cn.chestnut.mvvm.teamworker.service.DataManager;
import cn.chestnut.mvvm.teamworker.utils.CommonUtil;
import cn.chestnut.mvvm.teamworker.utils.Log;
import cn.chestnut.mvvm.teamworker.utils.PreferenceUtil;
import cn.chestnut.mvvm.teamworker.utils.StringUtil;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/12 18:11:57
 * Description：登陆Activity
 * Email: xiaoting233zhang@126.com
 */

public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("登陆");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        initView();
        initData();
        addListener();
    }

    private void initData() {
    }

    private void initView() {
        checkRememberLogin();
    }

    private void checkRememberLogin() {
        String userId = PreferenceUtil.getInstances(this).getPreferenceString("userId");
        String token = PreferenceUtil.getInstances(this).getPreferenceString("token");
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("token", token);
        DataManager.getInstance(this).executeRequest(HttpUrls.REMEMBER_ME, params, new AppCallBack<ApiResponse<User>>() {

            @Override
            public void next(ApiResponse<User> response) {
                if (response.isSuccess()) {
                    PreferenceUtil.getInstances(LoginActivity.this).savePreferenceString("userId", response.getData().getUserId());
                    PreferenceUtil.getInstances(LoginActivity.this).savePreferenceString("account", response.getData().getAccount());
                    PreferenceUtil.getInstances(LoginActivity.this).savePreferenceString("token", response.getData().getToken());
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    userLogin();
                }
                hideProgressDialog();

            }

            @Override
            public void error(Throwable error) {

                hideProgressDialog();
            }

            @Override
            public void complete() {

                hideProgressDialog();
            }

            @Override
            public void before() {
                showProgressDialog(LoginActivity.this);
            }
        });
    }

    private void addListener() {
        binding.ivLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = binding.etAccount.getText().toString();
                String password = binding.etPassword.getText().toString();
                if (StringUtil.isStringNotNull(account) && StringUtil.isStringNotNull(password)) {
                    login(account, password);
                } else {
                    CommonUtil.showToast("用户名和密码不能为空", LoginActivity.this);
                }
            }
        });
    }

    /**
     * 登陆
     */
    private void login(String account, String password) {
        Map<String, Object> params = new HashMap<>();
        params.put("account", account);
        params.put("password", password);
        DataManager.getInstance(this).executeRequest(HttpUrls.LOGIN, params, new AppCallBack<ApiResponse<User>>() {

            @Override
            public void next(ApiResponse<User> response) {
                if (response.isSuccess()) {
                    PreferenceUtil.getInstances(LoginActivity.this).savePreferenceString("userId", response.getData().getUserId());
                    PreferenceUtil.getInstances(LoginActivity.this).savePreferenceString("account", response.getData().getAccount());
                    PreferenceUtil.getInstances(LoginActivity.this).savePreferenceString("token", response.getData().getToken());
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    userLogin();
                } else {
                    showToast(response.getMessage());
                }
                hideProgressDialog();
            }

            @Override
            public void error(Throwable error) {

                hideProgressDialog();
            }

            @Override
            public void complete() {

                hideProgressDialog();
            }

            @Override
            public void before() {
                showProgressDialog(LoginActivity.this);
            }
        });

    }

}
