package cn.chestnut.mvvm.teamworker.module.work;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
import cn.chestnut.mvvm.teamworker.main.activity.MainActivity;
import cn.chestnut.mvvm.teamworker.main.adapter.BaseListViewAdapter;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.main.common.BaseFragment;
import cn.chestnut.mvvm.teamworker.model.Team;
import cn.chestnut.mvvm.teamworker.module.approval.AskForWorkOffActivity;
import cn.chestnut.mvvm.teamworker.module.approval.PurchaseListActivity;
import cn.chestnut.mvvm.teamworker.module.approval.ReimbursementListActivity;
import cn.chestnut.mvvm.teamworker.module.approval.UseGoodListActivity;
import cn.chestnut.mvvm.teamworker.module.approval.WorkOffListActivity;
import cn.chestnut.mvvm.teamworker.module.checkattendance.PunchClockActivity;
import cn.chestnut.mvvm.teamworker.module.report.DayReportListActivity;
import cn.chestnut.mvvm.teamworker.module.report.MonthReportListActivity;
import cn.chestnut.mvvm.teamworker.module.report.PerformanceListActivity;
import cn.chestnut.mvvm.teamworker.module.report.WeekReportListActivity;
import cn.chestnut.mvvm.teamworker.module.team.TeamInformationActivity;
import cn.chestnut.mvvm.teamworker.module.team.TeamManagementActivity;
import cn.chestnut.mvvm.teamworker.module.team.TeamMemberActivity;
import cn.chestnut.mvvm.teamworker.module.team.TeamSettingActivity;
import cn.chestnut.mvvm.teamworker.module.team.ViewTeamInformationActivity;
import cn.chestnut.mvvm.teamworker.module.user.NewFriendActivity;
import cn.chestnut.mvvm.teamworker.utils.CommonUtil;
import cn.chestnut.mvvm.teamworker.utils.Log;
import cn.chestnut.mvvm.teamworker.utils.PreferenceUtil;
import cn.chestnut.mvvm.teamworker.utils.StringUtil;

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

    private Team currentTeam;

    private TextView tvTitle;

    public static final int NORMAL_MEMBER = 0;

    public static final int MANAGER = 1;

    public static final int TEAM_OWNER = 2;

    private final int REQUEST_CODE_TEAM_INFORMATION = 3;

    private final int REQUEST_CODE_TEAM_SETTING = 4;

    public static int MY_DATA_TYPE = 1;

    public static int TEAM_DATA_TYPE = 2;

    private int userRoleType;//0普通成员 1管理员 2团队所有者

    @Override
    protected void setBaseTitle(final TextView titleView) {
        tvTitle = titleView;
        tvTitle.setText("");
        tvTitle.setCompoundDrawablesWithIntrinsicBounds(
                getResources().getDrawable(R.mipmap.icon_change_team),
                null, null, null);
        tvTitle.setCompoundDrawablePadding(3);
        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActionPopup();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("回调，解散了,resultCode:" + resultCode);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_TEAM_INFORMATION) {
                currentTeam = (Team) data.getSerializableExtra("team");
                tvTitle.setText(currentTeam.getTeamName());
            } else if (requestCode == MainActivity.REQUEST_CODE_BUILD_TEAM) {
                teamList.add((Team) data.getSerializableExtra("newTeam"));
                updateView();
            }
        } else if (resultCode == TeamSettingActivity.RESULT_CODE_GIVE_UP_TEAM_OWNER) {
            hideManagementPlatform();
        } else if (requestCode == TeamSettingActivity.RESULT_CODE_RELEASE_TEAM) {
            getMyTeam();
        }
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
        binding.llUseGood.setOnClickListener(new View.OnClickListener() {//报销申请
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UseGoodListActivity.class);
                intent.putExtra("teamId", currentTeamId);
                intent.putExtra("useGoodType", WorkFragment.MY_DATA_TYPE);
                startActivity(intent);
            }
        });

        binding.llReimbursement.setOnClickListener(new View.OnClickListener() {//报销申请
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ReimbursementListActivity.class);
                intent.putExtra("teamId", currentTeamId);
                intent.putExtra("reimbursementType", WorkFragment.MY_DATA_TYPE);
                startActivity(intent);
            }
        });

        binding.ivWorkoff.setOnClickListener(new View.OnClickListener() {//请假申请
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WorkOffListActivity.class);
                intent.putExtra("teamId", currentTeamId);
                intent.putExtra("workOffType", MY_DATA_TYPE);
                startActivity(intent);
            }
        });

        binding.llPurchase.setOnClickListener(new View.OnClickListener() {//报销申请
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PurchaseListActivity.class);
                intent.putExtra("teamId", currentTeamId);
                intent.putExtra("purchaseType", WorkFragment.MY_DATA_TYPE);
                startActivity(intent);
            }
        });

        binding.llAttendance.setOnClickListener(new View.OnClickListener() {//考勤打卡
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PunchClockActivity.class);
                intent.putExtra("teamId", currentTeamId);
                startActivity(intent);
            }
        });

        binding.llViewTeamInformaion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ViewTeamInformationActivity.class);
                intent.putExtra("team", currentTeam);
                startActivity(intent);
            }
        });

        binding.llViewMember.setOnClickListener(new View.OnClickListener() {//查看团队成员
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(new Intent(getActivity(), TeamMemberActivity.class));
                intent.putExtra("teamId", currentTeamId);
                intent.putExtra("roleType", userRoleType);
                startActivity(intent);
            }
        });

        binding.llDayReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DayReportListActivity.class);
                intent.putExtra("teamId", currentTeamId);
                intent.putExtra("dayReportType", MY_DATA_TYPE);
                startActivity(intent);
            }
        });

        binding.llWeekReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WeekReportListActivity.class);
                intent.putExtra("teamId", currentTeamId);
                intent.putExtra("weekReportType", MY_DATA_TYPE);
                startActivity(intent);
            }
        });

        binding.llMonthReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MonthReportListActivity.class);
                intent.putExtra("teamId", currentTeamId);
                intent.putExtra("monthReportType", MY_DATA_TYPE);
                startActivity(intent);
            }
        });

        binding.llPerformance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PerformanceListActivity.class);
                intent.putExtra("teamId", currentTeamId);
                intent.putExtra("performanceType", MY_DATA_TYPE);
                startActivity(intent);
            }
        });

        //设置管理员平台中四个按钮的监听器
        binding.llTeamManagement.setOnClickListener(new View.OnClickListener() {//团队管理
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TeamManagementActivity.class);
                intent.putExtra("teamId", currentTeamId);
                intent.putExtra("roleType", userRoleType);
                startActivity(intent);
            }
        });

        binding.llTeamInformation.setOnClickListener(new View.OnClickListener() {//团队资料
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TeamInformationActivity.class);
                intent.putExtra("team", currentTeam);
                startActivityForResult(intent, REQUEST_CODE_TEAM_INFORMATION);
            }
        });

        binding.llSetting.setOnClickListener(new View.OnClickListener() {//团队设置
            @Override
            public void onClick(View v) {
                if (userRoleType == TEAM_OWNER) {//如果用户在团队中的角色为"团队所有者"
                    Intent intent = new Intent(getActivity(), TeamSettingActivity.class);
                    intent.putExtra("teamId", currentTeamId);
                    startActivityForResult(intent, REQUEST_CODE_TEAM_SETTING);
                } else {
                    showToast("只有团队所有者可以进行团队设置");
                }
            }
        });

        binding.llTeamMember.setOnClickListener(new View.OnClickListener()

        {//团队成员
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(new Intent(getActivity(), TeamMemberActivity.class));
                intent.putExtra("teamId", currentTeamId);
                intent.putExtra("roleType", userRoleType);
                startActivity(intent);
            }
        });

    }

    private void showActionPopup() {

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
        popupWindow.showAsDropDown(tvTitle, -150, 0);

        popupBinding.lvTeam.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentTeam = teamList.get(position);
                currentTeamId = currentTeam.getTeamId();
                tvTitle.setText(teamList.get(position).getTeamName());
                getTeamRelation();
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
        showProgressDialog(getActivity());
        RequestManager.getInstance(getActivity()).executeRequest(HttpUrls.GET_MY_TEAMS, null,
                new AppCallBack<ApiResponse<List<Team>>>() {
                    @Override
                    public void next(ApiResponse<List<Team>> response) {
                        if (response.isSuccess()) {
                            if (teamList.size() > 0) {
                                teamList.clear();
                            }
                            teamList.addAll(response.getData());
                            updateView();
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

    private void updateView() {
        workMyTeamAdapter.notifyDataSetChanged();
        if (teamList.size() > 0) {
            //默认设置当前所在的Team工作台为第一个
            currentTeam = teamList.get(0);
            currentTeamId = currentTeam.getTeamId();
            tvTitle.setText(currentTeam.getTeamName());
            tvTitle.setClickable(true);
            getTeamRelation();
        } else {
            tvTitle.setText("");
            tvTitle.setClickable(false);
            hideManagementPlatform();
        }
    }

    //获取用户在团队中的角色,根据角色来设置是否显示管理员平台
    private void getTeamRelation() {
        Map<String, String> param = new HashMap<>(1);
        param.put("teamId", currentTeamId);

        RequestManager.getInstance(getActivity()).executeRequest(HttpUrls.GET_TEAM_RELATION, param,
                new AppCallBack<ApiResponse<Map<String, String>>>() {
                    @Override
                    public void next(ApiResponse<Map<String, String>> response) {
                        if (response.isSuccess()) {
                            userRoleType = Integer.parseInt(response.getData().get("type"));
                            if (userRoleType == TEAM_OWNER || userRoleType == MANAGER) {//如果用户在团队中的角色为"团队所有者"或管理员
                                showManagementPlatform();
                            } else {
                                hideManagementPlatform();
                            }
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
     * 显示管理者平台
     */
    private void showManagementPlatform() {
        binding.llManagePlatform.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏管理者平台
     */
    private void hideManagementPlatform() {
        binding.llManagePlatform.setVisibility(View.GONE);

    }
}

