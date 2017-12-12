package cn.chestnut.mvvm.teamworker.main.common;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityLoginBinding;
import cn.chestnut.mvvm.teamworker.service.DataManager;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/12 18:11:57
 * Description：
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
    }

    private void initData() {
    }

    private void initView() {
        binding.ivLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(binding.etAccount.getText().toString(), binding.etPassword.getText().toString());
            }
        });
    }

    /**
     * 登陆
     */
    private void login(String account, String password) {
        DataManager.getInstance(this).login(account, password)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {
                        try {
                            org.json.JSONObject result = new org.json.JSONObject(s);
                            showToast(result.getString("message"));
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(intent);
                        } catch (JSONException e) {
                            Log.e("login_error",e.toString());
                        }
                    }
                });
    }
}
