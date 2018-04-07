package cn.chestnut.mvvm.teamworker.module.user;

import android.content.Context;
import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityAddFriendBinding;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.model.User;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/5 22:30:50
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class SearchFriendActivity extends BaseActivity {

    private ActivityAddFriendBinding binding;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("添加朋友");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_add_friend, viewGroup, true);
        addListener();
    }

    protected void addListener() {
        //点击软键盘上的回车键进行搜索操作
        binding.etSearchFriend.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    searchFriend();
                    return true;
                }
                return false;
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

        binding.tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchFriend();
            }
        });
    }

    private void searchFriend() {
        Map param = new HashMap<String, String>(1);
        param.put("account", binding.etSearchFriend.getText().toString());
        RequestManager.getInstance(this).executeRequest(HttpUrls.SEARCH_USER, param, new AppCallBack<ApiResponse<String>>() {
            @Override
            public void next(ApiResponse<String> response) {
                if (response.isSuccess()) {
                    Intent intent = new Intent(SearchFriendActivity.this, UserInformationActivity.class);
                    intent.putExtra("userId", response.getData());
                    startActivity(intent);
                } else {
                    showToast(response.getMessage());
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
