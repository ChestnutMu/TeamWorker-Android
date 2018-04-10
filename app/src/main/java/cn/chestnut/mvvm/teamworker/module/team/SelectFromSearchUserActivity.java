package cn.chestnut.mvvm.teamworker.module.team;

import android.app.Activity;
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
import cn.chestnut.mvvm.teamworker.utils.PreferenceUtil;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/7 20:11:31
 * Description：搜索用户来加入团队
 * Email: xiaoting233zhang@126.com
 */

public class SelectFromSearchUserActivity extends BaseActivity {

    private ActivitySearchUserBinding binding;

    private BaseListViewAdapter<User> adapter;

    private List<User> userList;

    private int FROM_SELECT_SEARCH_USER = 1;

    private User selectedUser;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("搜索用户加入团队");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_search_user, viewGroup, true);
        initData();
        addListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == FROM_SELECT_SEARCH_USER) {
            Intent intent = new Intent();
            intent.putExtra("person", selectedUser);
            SelectFromSearchUserActivity.this.setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }

    @Override
    protected void initData() {
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
                selectedUser = userList.get(position);
                if (selectedUser.getUserId().equals(PreferenceUtil.getInstances(SelectFromSearchUserActivity.this).getPreferenceString("userId"))) {
                    showToast("无需选择自己，你会被自动加入到该团队，且为团队所有者");
                    return;
                }
                List<String> userIdList = getIntent().getExtras().getStringArrayList("userIdList");
                User user = userList.get(position);
                boolean isExist = false;
                for (String userId : userIdList) {
                    if (user.getUserId().equals(userId)) {
                        showToast("你选择的团队成员列表中已存在该用户");
                        isExist = true;
                        break;
                    }
                }
                if (!isExist) {
                    Intent intent = new Intent(SelectFromSearchUserActivity.this, InvitedUserInformationActivity.class);
                    intent.putExtra("userId", selectedUser.getUserId());
                    startActivityForResult(intent, FROM_SELECT_SEARCH_USER);
                }


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
