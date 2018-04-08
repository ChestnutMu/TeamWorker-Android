package cn.chestnut.mvvm.teamworker.module.work;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.chestnut.mvvm.teamworker.BR;
import cn.chestnut.mvvm.teamworker.Constant;
import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.FragmentWorkBinding;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.common.BaseFragment;
import cn.chestnut.mvvm.teamworker.model.Team;
import cn.chestnut.mvvm.teamworker.module.approval.ApprovalActivity;
import cn.chestnut.mvvm.teamworker.module.checkattendance.CheckAttendanceActivity;
import cn.chestnut.mvvm.teamworker.module.team.BuildTeamActivity;
import cn.chestnut.mvvm.teamworker.module.user.NewFriendActivity;
import cn.chestnut.mvvm.teamworker.module.work.adapter.GridViewAdapter;
import cn.chestnut.mvvm.teamworker.utils.PreferenceUtil;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/1/6 18:49:31
 * Description：工作Fragment
 * Email: xiaoting233zhang@126.com
 */

public class WorkFragment extends BaseFragment {

    private FragmentWorkBinding binding;

    private GridViewAdapter gridViewAdapter;

    private ArrayList<Team> teamList;

    private ArrayList<String> applicationNameList;

    private ArrayList<Integer> applicationDrawableList;

    private WorkMyTeamAdapter workMyTeamAdapter;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("工作");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_work, viewGroup, true);
        initData();
        initView();
        addListener();
    }

    @Override
    public void setButton(TextView edit, ImageView add, ImageView search) {
        super.setButton(edit, add, search);
        add.setVisibility(View.VISIBLE);
        add.setImageDrawable(getResources().getDrawable(R.mipmap.icon_to_to_list));
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), WorkNotificationActivity.class));
            }
        });
        search.setVisibility(View.VISIBLE);
        search.setImageDrawable(getResources().getDrawable(R.mipmap.icon_notification));
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBadgeVisibility(View.INVISIBLE);
                startActivity(new Intent(getActivity(), NewFriendActivity.class));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getNotSendRequestCountByUserId();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        applicationNameList = new ArrayList<>();
        applicationNameList.add("考勤打卡");
        applicationNameList.add("审批");
        applicationNameList.add("考勤打卡");
        applicationNameList.add("审批");

        applicationDrawableList = new ArrayList<>();
        applicationDrawableList.add(R.mipmap.icon_attendance);
        applicationDrawableList.add(R.mipmap.icon_approval);
        applicationDrawableList.add(R.mipmap.icon_attendance);
        applicationDrawableList.add(R.mipmap.icon_approval);

        teamList = new ArrayList<>();
        getMyTeam();
    }

    /**
     * 初始化界面
     */
    private void initView() {
        gridViewAdapter = new GridViewAdapter(getActivity(), applicationNameList, applicationDrawableList);
        binding.gvCommonApps.setAdapter(gridViewAdapter);

        workMyTeamAdapter = new WorkMyTeamAdapter(R.layout.item_my_team, BR.team, teamList);
        binding.lvMyTeam.setAdapter(workMyTeamAdapter);
    }

    /**
     * 添加监听器
     */
    private void addListener() {
        binding.gvCommonApps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (applicationDrawableList.get(position)) {
                    case R.mipmap.icon_attendance:
                        startActivity(new Intent(getActivity(), CheckAttendanceActivity.class));
                        break;
                    case R.mipmap.icon_approval:
                        startActivity(new Intent(getActivity(), ApprovalActivity.class));
                        break;
                }
            }
        });

        binding.llBuildTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), BuildTeamActivity.class));
            }
        });
    }

    private void getMyTeam() {
        RequestManager.getInstance(getActivity()).executeRequest(HttpUrls.GET_MY_TEAMS, null, new AppCallBack<ApiResponse<List<Team>>>() {
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

    private void getNotSendRequestCountByUserId() {
        String userId = PreferenceUtil.getInstances(getActivity()).getPreferenceString("userId");
        Map param = new HashMap<String, String>(1);
        param.put("userId", userId);
        RequestManager.getInstance(getActivity()).executeRequest(HttpUrls.GET_NOT_SEND_REQUEST_COUNT_BY_USERID, param, new AppCallBack<ApiResponse<Integer>>() {
            @Override
            public void next(ApiResponse<Integer> response) {
                if (response.isSuccess()) {
                    if (response.getData() > 0) {
                        setBadgeVisibility(View.VISIBLE);
                    }
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
