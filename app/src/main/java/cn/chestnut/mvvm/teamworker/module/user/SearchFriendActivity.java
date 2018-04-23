package cn.chestnut.mvvm.teamworker.module.user;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.chestnut.mvvm.teamworker.BR;
import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivitySearchUserBinding;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.adapter.BaseListViewAdapter;
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

    private ActivitySearchUserBinding binding;

    private BaseListViewAdapter<User> adapter;

    private List<User> userList;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("添加朋友");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_search_user, viewGroup, true);
        initView();
        addListener();
    }

    @Override
    protected void initView() {
        userList = new ArrayList<>();
        adapter = new BaseListViewAdapter<>(R.layout.item_search_user, BR.user, userList);
        binding.lvUsers.setAdapter(adapter);
    }

    @Override
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

        binding.lvUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserInformationActivity.startActivity(SearchFriendActivity.this, userList.get(position).getUserId(), false, userList.get(position));
            }
        });
    }

    private void searchFriend() {
        showProgressDialog(this);
        if (userList.size() > 0) {
            userList.clear();
        }
        Map<String, String> param = new HashMap<>(1);
        param.put("keyword", binding.etSearchFriend.getText().toString());
        RequestManager.getInstance(this).executeRequest(HttpUrls.SEARCH_USER, param, new AppCallBack<ApiResponse<List<User>>>() {
            @Override
            public void next(ApiResponse<List<User>> response) {
                if (response.isSuccess()) {
                    userList.addAll(response.getData());
                    adapter.notifyDataSetChanged();
                } else {
                    showToast(response.getMessage());
                }
                hideProgressDialog();
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
