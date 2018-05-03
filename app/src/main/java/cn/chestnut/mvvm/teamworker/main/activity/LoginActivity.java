package cn.chestnut.mvvm.teamworker.main.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import org.greenrobot.greendao.async.AsyncSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityLoginBinding;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.main.common.MyApplication;
import cn.chestnut.mvvm.teamworker.model.User;
import cn.chestnut.mvvm.teamworker.model.UserInfo;
import cn.chestnut.mvvm.teamworker.module.massage.MessageDaoUtils;
import cn.chestnut.mvvm.teamworker.utils.MD5;
import cn.chestnut.mvvm.teamworker.utils.PreferenceUtil;
import cn.chestnut.mvvm.teamworker.utils.StringUtil;
import cn.chestnut.mvvm.teamworker.utils.sqlite.DaoManager;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/12 18:11:57
 * Description：登陆Activity
 * Email: xiaoting233zhang@126.com
 */

public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding binding;

    private AsyncSession asyncSession;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("登陆");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        initView();
        addListener();
    }

    protected void initView() {
        setTranslucentStatus(true);
        if (PreferenceUtil.getInstances(MyApplication.getInstance()).getPreferenceBoolean("isShowLoginConflict")) {
            showDoubleLoginDialog();
            PreferenceUtil.getInstances(MyApplication.getInstance()).savePreferenceBoolean("isShowLoginConflict", false);
        } else {
            checkRememberLogin();
        }
    }

    protected void addListener() {
        binding.tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        binding.etPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    login();
                    return true;
                }
                return false;
            }
        });

        binding.tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        //点击输入框外则隐藏软键盘
        binding.flParentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

    }

    private void showDoubleLoginDialog() {
        new AlertDialog.Builder(LoginActivity.this)
                .setTitle("下线通知")
                .setMessage("已在其他设备登录")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create()
                .show();
    }

    /**
     * token验证登录
     */
    private void checkRememberLogin() {
        if (StringUtil.isStringNotNull(PreferenceUtil.getInstances(this).getPreferenceString("token"))) {
            showProgressDialog(this);
            RequestManager.getInstance(this).executeRequest(HttpUrls.REMEMBER_ME, null, new AppCallBack<ApiResponse<User>>() {
                @Override
                public void next(ApiResponse<User> response) {
                    if (response.isSuccess()) {
                        loginSuccess(response);
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

    private void loginSuccess(ApiResponse<User> response) {
        final UserInfo userInfo = new UserInfo();
        userInfo.setUserId(response.getData().getUserId());
        userInfo.setNickname(response.getData().getNickname());
        userInfo.setAvatar(response.getData().getAvatar());
        MyApplication.userInfoMap.put(userInfo.getUserId(), userInfo);

        String nickname = PreferenceUtil.getInstances(LoginActivity.this).getPreferenceString("nickname");
        String avatar = PreferenceUtil.getInstances(LoginActivity.this).getPreferenceString("avatar");

        boolean isFristLogin = false;

        PreferenceUtil.getInstances(LoginActivity.this).savePreferenceString("userId", response.getData().getUserId());
        asyncSession = DaoManager.getDaoSession().startAsyncSession();
        if (userInfo.getNickname() != null && !userInfo.getNickname().equals(nickname)) {
            if (userInfo.getAvatar() != null && !userInfo.getAvatar().equals(avatar)) {
                //两个都不一样
                MessageDaoUtils.updateChatMessageUserInfo(asyncSession, userInfo.getUserId(), userInfo.getNickname(), userInfo.getAvatar());
            } else {
                //nickname不一样
                MessageDaoUtils.updateChatMessageUserInfoNickname(asyncSession, userInfo.getUserId(), userInfo.getNickname());
            }
        } else {
            if (userInfo.getAvatar() != null && !userInfo.getAvatar().equals(avatar)) {
                //avatar不一样
                MessageDaoUtils.updateChatMessageUserInfoAvatar(asyncSession, userInfo.getUserId(), userInfo.getAvatar());
            }
        }
        asyncSession.insertOrReplace(response.getData());
        asyncSession.insertOrReplace(userInfo);

        PreferenceUtil.getInstances(LoginActivity.this).savePreferenceString("telephone", response.getData().getTelephone());
        PreferenceUtil.getInstances(LoginActivity.this).savePreferenceString("token", response.getData().getToken());
        PreferenceUtil.getInstances(LoginActivity.this).savePreferenceString("nickname", response.getData().getNickname());
        PreferenceUtil.getInstances(LoginActivity.this).savePreferenceString("avatar", response.getData().getAvatar());

        if (!isFristLogin) {
            showProgressDialog(this);
            RequestManager.getInstance(this).executeRequest(HttpUrls.GET_USER_LIST_INFO_BY_PERSONAL, null, new AppCallBack<ApiResponse<List<UserInfo>>>() {
                @Override
                public void next(ApiResponse<List<UserInfo>> response) {
                    if (response.isSuccess()) {
                        PreferenceUtil.getInstances(LoginActivity.this).savePreferenceBoolean("first_load_user_info:" + userInfo.getUserId(), true);
                        asyncSession.insertInTx(UserInfo.class, response.getData());

                        for (UserInfo temp : response.getData()) {
                            MyApplication.userInfoMap.put(temp.getUserId(), temp);
                        }

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        userLogin();
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
        } else {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            userLogin();
        }
    }

    private void login() {
        String account = binding.etAccount.getText().toString();
        String password = binding.etPassword.getText().toString();
        if (StringUtil.isBlank(account) && StringUtil.isBlank(password)) {
            showToast("手机号和密码不能为空");
        } else if (StringUtil.isChinaPhoneLegal(account)) {
            password = MD5.MD5(password);
            login(account, password);
        } else {
            showToast("手机号码格式不正确");
        }
    }

    /**
     * 账号密码登陆
     */
    private void login(String telephone, String password) {
        Map<String, Object> params = new HashMap<>();
        params.put("telephone", telephone);
        params.put("password", password);
        showProgressDialog(this);
        RequestManager.getInstance(this).executeRequest(HttpUrls.LOGIN, params, new AppCallBack<ApiResponse<User>>() {

            @Override
            public void next(ApiResponse<User> response) {
                if (response.isSuccess()) {
                    loginSuccess(response);
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
