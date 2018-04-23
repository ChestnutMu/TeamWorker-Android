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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.chestnut.mvvm.teamworker.BR;
import cn.chestnut.mvvm.teamworker.Constant;
import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.FragmentWorkBinding;
import cn.chestnut.mvvm.teamworker.databinding.PopupChangeTeamBinding;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.adapter.BaseListViewAdapter;
import cn.chestnut.mvvm.teamworker.main.common.BaseFragment;
import cn.chestnut.mvvm.teamworker.model.Team;
import cn.chestnut.mvvm.teamworker.module.approval.AskForWorkOffActivity;
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

    private BroadcastReceiver receiver;

    private BaseListViewAdapter workMyTeamAdapter;

    private ArrayList<Team> teamList;

    private String currentTeamId;

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
        initData();
        initView();
        addListener();
    }

    //设置界面右上角的消息铃铛按钮和菜单按钮
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
        getNotSendRequestCountByUserId();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        teamList = new ArrayList<>();
        getMyTeam();


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
        workMyTeamAdapter = new BaseListViewAdapter<>(R.layout.item_work_team, BR.team, teamList);
    }

    /**
     * 添加监听器
     */
    private void addListener() {

        binding.llDecrusement.setOnClickListener(new View.OnClickListener() {//报销申请
            @Override
            public void onClick(View v) {

            }
        });

        binding.ivWorkoff.setOnClickListener(new View.OnClickListener() {//请假申请
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AskForWorkOffActivity.class));
            }
        });

        binding.llAttendance.setOnClickListener(new View.OnClickListener() {//考勤打卡
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CheckAttendanceActivity.class));
            }
        });

        binding.llWorkReport.setOnClickListener(new View.OnClickListener() {//工作汇报
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void showActionPopup(View add) {

        CommonUtil.setBackgroundAlpha(0.5f, getActivity());

        final PopupChangeTeamBinding popupBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.popup_change_team, null, false);
        workMyTeamAdapter = new BaseListViewAdapter(R.layout.item_work_team, BR.team, teamList);
        popupBinding.lvTeam.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        popupBinding.lvTeam.setAdapter(workMyTeamAdapter);
        final PopupWindow popupWindow = new PopupWindow(popupBinding.getRoot());
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(add, -150, 0);

        popupBinding.lvTeam.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

    //获取我所在的所有团队
    private void getMyTeam() {
        RequestManager.getInstance(getActivity()).executeRequest(HttpUrls.GET_MY_TEAMS, null,
                new AppCallBack<ApiResponse<List<Team>>>() {
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

    //获取用户在团队中的角色
    private void getTeamRelation() {
        Map<String, String> param = new HashMap<>(1);
        param.put("teamId", currentTeamId);
        RequestManager.getInstance(getActivity()).executeRequest(HttpUrls.GET_TEAM_RELATION, param,
                new AppCallBack<ApiResponse<Map<String, String>>>() {
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

            binding.llTeamInfo.setOnClickListener(new View.OnClickListener() {//团队资料
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
                    intent.putExtra("roleType", userRoleType);
                    startActivity(intent);
                }
            });
        } else {
            binding.llManagePlatform.setVisibility(View.GONE);
        }
    }
}

