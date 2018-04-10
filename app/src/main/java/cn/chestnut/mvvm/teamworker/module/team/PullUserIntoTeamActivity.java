package cn.chestnut.mvvm.teamworker.module.team;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.chestnut.mvvm.teamworker.BR;
import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityPullIntoTeamBinding;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.adapter.BaseListViewAdapter;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.model.Team;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/10 16:43:48
 * Description：拉取用户加入团队
 * Email: xiaoting233zhang@126.com
 */

public class PullUserIntoTeamActivity extends BaseActivity {

    private ActivityPullIntoTeamBinding binding;

    private BaseListViewAdapter workMyTeamAdapter;

    private ArrayList<Team> teamList;

    private String teamUserId;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("拉入团队");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_pull_into_team, viewGroup, true);
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
        initView();
        addListener();
    }

    /**
     * 初始化数据
     */
    protected void initData() {
        teamUserId = getIntent().getStringExtra("userId");
        teamList = new ArrayList<>();
        getMyTeam();

    }

    /**
     * 初始化界面
     */
    protected void initView() {
        workMyTeamAdapter = new BaseListViewAdapter<>(R.layout.item_team, BR.team, teamList);
        binding.lvMyTeam.setAdapter(workMyTeamAdapter);
    }

    /**
     * 添加监听器
     */
    protected void addListener() {
        binding.lvMyTeam.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                addTeamUser(teamList.get(position).getTeamId());
            }
        });
    }

    private void addTeamUser(String teamId) {
        Map<String, String> params = new HashMap<>(2);
        params.put("teamUserId", teamUserId);
        params.put("teamId", teamId);
        RequestManager.getInstance(this).executeRequest(HttpUrls.ADD_TEAM_USER, params, new AppCallBack<ApiResponse<List<Team>>>() {
            @Override
            public void next(ApiResponse<List<Team>> response) {
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

    private void getMyTeam() {
        RequestManager.getInstance(this).executeRequest(HttpUrls.GET_MY_TEAMS, null, new AppCallBack<ApiResponse<List<Team>>>() {
            @Override
            public void next(ApiResponse<List<Team>> response) {
                if (response.isSuccess()) {
                    teamList.addAll(response.getData());
                    workMyTeamAdapter.notifyDataSetChanged();
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
