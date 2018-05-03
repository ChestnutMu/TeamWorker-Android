package cn.chestnut.mvvm.teamworker.module.massage.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;

import cn.chestnut.mvvm.teamworker.BR;
import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityTeamNotificationBinding;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.adapter.BaseListViewAdapter;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.model.ChatMessage;
import cn.chestnut.mvvm.teamworker.model.DayReport;
import cn.chestnut.mvvm.teamworker.model.TeamNotification;
import cn.chestnut.mvvm.teamworker.socket.ReceiverProtocol;
import cn.chestnut.mvvm.teamworker.utils.EmojiUtil;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/5/2 20:02:44
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class TeamNotificationActivity extends BaseActivity {

    private ActivityTeamNotificationBinding binding;

    private String teamId;

    private BaseListViewAdapter adapter;

    private List<TeamNotification> teamNotificationList;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("团队公告");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_team_notification, viewGroup, true);
        initData();
        addListener();
    }

    @Override
    protected void initData() {
        teamId = getIntent().getStringExtra("teamId");
        teamNotificationList = new ArrayList<>();
        adapter = new BaseListViewAdapter(R.layout.item_team_notification, BR.teamNotification, teamNotificationList);
        binding.lvTeamNotification.setAdapter(adapter);
        getTeamNotificationList();
    }

    @Override
    protected void addListener() {
        binding.lvTeamNotification.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TeamNotificationActivity.this,TeamNotificationDetailActivity.class);
                intent.putExtra("teamNotification",teamNotificationList.get(position));
                startActivity(intent);
            }
        });
    }

    private void getTeamNotificationList() {
        showProgressDialog(this);
        ArrayMap<String, String> param = new ArrayMap<>(1);
        param.put("teamId", teamId);
        RequestManager.getInstance(this).executeRequest(HttpUrls.GET_TEAM_NOTIFICATION_LIST, param, new AppCallBack<ApiResponse<List<TeamNotification>>>() {
            @Override
            public void next(ApiResponse<List<TeamNotification>> response) {
                if (response.isSuccess()) {
                    if (response.getData().size() > 0) {
                        teamNotificationList.addAll(response.getData());
                        adapter.notifyDataSetChanged();
                    } else {
                        showToast("无团队公告记录");
                    }
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
