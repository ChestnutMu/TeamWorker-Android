package cn.chestnut.mvvm.teamworker.module.work;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.chestnut.mvvm.teamworker.BR;
import cn.chestnut.mvvm.teamworker.Constant;
import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.FragmentWorkBinding;
import cn.chestnut.mvvm.teamworker.databinding.PopupAddActionBinding;
import cn.chestnut.mvvm.teamworker.databinding.PopupChangeTeamBinding;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.adapter.BaseListViewAdapter;
import cn.chestnut.mvvm.teamworker.main.common.BaseFragment;
import cn.chestnut.mvvm.teamworker.model.Team;
import cn.chestnut.mvvm.teamworker.module.approval.ApprovalActivity;
import cn.chestnut.mvvm.teamworker.module.checkattendance.CheckAttendanceActivity;
import cn.chestnut.mvvm.teamworker.module.team.TeamManagementActivity;
import cn.chestnut.mvvm.teamworker.module.team.TeamMemberActivity;
import cn.chestnut.mvvm.teamworker.module.user.NewFriendActivity;
import cn.chestnut.mvvm.teamworker.utils.CommonUtil;
import cn.chestnut.mvvm.teamworker.utils.Log;
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

    private ArrayList<String> applicationNameList;

    private ArrayList<Integer> applicationDrawableList;

    private BroadcastReceiver receiver;

    private ViewStub viewStub;

    private BaseListViewAdapter workMyTeamAdapter;

    private ArrayList<Team> teamList;

    private String currentTeamId;

    public static int NORMAL_MEMBER = 0;

    public static int MANAGER = 1;

    public static int TEAM_OWNER = 2;

    private int userRoleType;//0普通人 1管理员 2团队所有者

    @Override
    protected void setBaseTitle(final TextView titleView) {
        titleView.setText("");
        titleView.setBackgroundResource(R.mipmap.icon_change_team);
        titleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActionPopup(titleView);
            }
        });
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_work, viewGroup, true);
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
        Log.d("DirectoryFragment onResume");
        initData();
        initView();
        addListener();
        getNotSendRequestCountByUserId();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        teamList = new ArrayList<>();
        getMyTeam();

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

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constant.ActionConstant.ACTION_GET_NEW_FRIEND_REQUEST)) {
                    getNotSendRequestCountByUserId();
                }
            }
        };
    }

    /**
     * 初始化界面
     */
    private void initView() {
        gridViewAdapter = new GridViewAdapter(getActivity(), applicationNameList, applicationDrawableList);
        workMyTeamAdapter = new BaseListViewAdapter<>(R.layout.item_work_team, BR.team, teamList);
        binding.gvCommonApps.setAdapter(gridViewAdapter);
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

    }

    private void showActionPopup(View add) {

        CommonUtil.setBackgroundAlpha(0.5f, getActivity());

        final PopupChangeTeamBinding popupBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.popup_change_team, null, false);
        workMyTeamAdapter = new BaseListViewAdapter(R.layout.item_work_team, BR.team, teamList);
        popupBinding.lvMineAdd.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        popupBinding.lvMineAdd.setAdapter(workMyTeamAdapter);
        final PopupWindow popupWindow = new PopupWindow(popupBinding.getRoot());
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(add, -150, 0);

        popupBinding.lvMineAdd.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentTeamId = teamList.get(position).getTeamId();
                updateView();
                popupWindow.dismiss();
            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                CommonUtil.setBackgroundAlpha(1, getActivity());
            }
        });
    }

    private void updateView() {
        getTeamRelation();
    }

    private void getNotSendRequestCountByUserId() {
        String userId = PreferenceUtil.getInstances(getActivity()).getPreferenceString("userId");
        Map param = new HashMap<String, String>(1);
        param.put("userId", userId);
        RequestManager.getInstance(getActivity()).executeRequest(HttpUrls.COUNT_NOT_SEND_REQUEST_BY_USERID, param, new AppCallBack<ApiResponse<Integer>>() {
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

    private void getMyTeam() {
        RequestManager.getInstance(getActivity()).executeRequest(HttpUrls.GET_MY_TEAMS, null, new AppCallBack<ApiResponse<List<Team>>>() {
            @Override
            public void next(ApiResponse<List<Team>> response) {
                if (response.isSuccess()) {
                    if (teamList.size() > 0) {
                        teamList.clear();
                    }
                    teamList.addAll(response.getData());
                    workMyTeamAdapter.notifyDataSetChanged();
                    if (teamList.size() > 0) {
                        currentTeamId = teamList.get(0).getTeamId();//默认设置当前所在的Team工作台
                        updateView();
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

    private void getTeamRelation() {
        Map<String, String> param = new HashMap<>(1);
        param.put("teamId", currentTeamId);
        RequestManager.getInstance(getActivity()).executeRequest(HttpUrls.GET_TEAM_RELATION, param, new AppCallBack<ApiResponse<Map<String, String>>>() {
            @Override
            public void next(ApiResponse<Map<String, String>> response) {
                if (response.isSuccess()) {
                    userRoleType = Integer.parseInt(response.getData().get("type"));
                    showManagementPlatform(userRoleType);
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

    /**
     * 根据用户在团队中的角色来显示或隐藏管理者平台
     *
     * @param userRoleType
     */
    private void showManagementPlatform(final int userRoleType) {
        if (userRoleType == TEAM_OWNER || userRoleType == MANAGER) {//如果用户在团队中的角色为"团队所有者"或管理员
            binding.llManagePlatform.setVisibility(View.VISIBLE);
            binding.llTeamManagement.setOnClickListener(new View.OnClickListener() {//团队管理
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), TeamManagementActivity.class);
                    intent.putExtra("teamId", currentTeamId);
                    intent.putExtra("type", userRoleType);
                    startActivity(intent);
                }
            });
            binding.llTeamInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            binding.llOrganization.setOnClickListener(new View.OnClickListener() {//组织结构
                @Override
                public void onClick(View v) {

                }
            });
            binding.llTeamMember.setOnClickListener(new View.OnClickListener() {//团队成员
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(new Intent(getActivity(), TeamMemberActivity.class));
                    intent.putExtra("teamId", currentTeamId);
                    startActivity(intent);
                }
            });
        } else {
            binding.llManagePlatform.setVisibility(View.GONE);
        }
    }
}

