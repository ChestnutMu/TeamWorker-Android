package cn.chestnut.mvvm.teamworker.module.team;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityTeamMemberBinding;
import cn.chestnut.mvvm.teamworker.databinding.PopupAddActionBinding;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.adapter.BaseListViewAdapter;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.model.User;
import cn.chestnut.mvvm.teamworker.module.mine.MyFriendActivity;
import cn.chestnut.mvvm.teamworker.module.user.SearchFriendActivity;
import cn.chestnut.mvvm.teamworker.module.user.UserInformationActivity;
import cn.chestnut.mvvm.teamworker.utils.CommonUtil;
import cn.chestnut.mvvm.teamworker.utils.PreferenceUtil;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/10 14:57:54
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class TeamMemberActivity extends BaseActivity {

    private ActivityTeamMemberBinding binding;

    private List<User> userList;

    private TeamMemberAdapter adapter;

    private String teamId;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("团队成员");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_team_member, viewGroup, true);
        initView();
        initData();
        addListener();
    }

    @Override
    public void setButton(TextView edit, final ImageView add, ImageView search) {
        add.setVisibility(View.VISIBLE);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActionPopup(add);
            }
        });
    }

    @Override
    protected void initView() {
        userList = new ArrayList<>();
        adapter = new TeamMemberAdapter(R.layout.item_team_member, BR.user, userList);
        binding.lvTeamMember.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        teamId = getIntent().getStringExtra("teamId");
        getTeamers();
    }

    @Override
    protected void addListener() {
        binding.lvTeamMember.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TeamMemberActivity.this, UserInformationActivity.class);
                intent.putExtra("userId", userList.get(position).getUserId());
                startActivity(intent);
            }
        });

        adapter.setOnDeleteMemberLisenter(new TeamMemberAdapter.OnDeleteMemberLisenter() {
            @Override
            public void onDeleteMember(String teamUserId, int position) {
                if (teamUserId.equals(PreferenceUtil.getInstances(TeamMemberActivity.this).getPreferenceString("userId"))) {
                    showToast("不能从团队中删除自己");
                } else {
                    showDeleteMemberDialog(teamUserId);
                }
            }
        });

    }

    private void showDeleteMemberDialog(final String teamUserId) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("确定要从团队中删除该成员吗？")
                .setPositiveButton("确定删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteMember(teamUserId);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private void deleteMember(String teamUserId) {
        Map<String, String> params = new HashMap<>(2);
        params.put("teamId", teamId);
        params.put("teamUserId", teamUserId);
        showProgressDialog(this);
        RequestManager.getInstance(this).executeRequest(HttpUrls.DEL_TEAM_USER, params, new AppCallBack<ApiResponse<Object>>() {
            @Override
            public void next(ApiResponse<Object> response) {
                showToast(response.getMessage());
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

    private void showActionPopup(View add) {
        List<AddAction> addActionList = new ArrayList<>(3);
        AddAction search = new AddAction(getResources().getDrawable(R.mipmap.icon_search_friend), "搜索添加成员");
        AddAction phone = new AddAction(getResources().getDrawable(R.mipmap.icon_phone_directory), "从手机通讯录添加成员");
        AddAction friend = new AddAction(getResources().getDrawable(R.mipmap.icon_firend_list), "从好友列表添加成员");
        addActionList.add(search);
        addActionList.add(phone);
        addActionList.add(friend);

        CommonUtil.setBackgroundAlpha(0.5f, this);

        final PopupAddActionBinding popupBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.popup_add_action, null, false);
        BaseListViewAdapter adapter = new BaseListViewAdapter(R.layout.item_add_member_action, BR.addAction, addActionList);
        popupBinding.lvMineAdd.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        popupBinding.lvMineAdd.setAdapter(adapter);
        final PopupWindow popupWindow = new PopupWindow(popupBinding.getRoot());
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(add, 0, 0);

        popupBinding.lvMineAdd.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    startActivity(new Intent(TeamMemberActivity.this, SearchFriendActivity.class));
                } else if (position == 1) {
                    startActivity(new Intent(TeamMemberActivity.this, PhoneDirectoryActivity.class));
                } else if (position == 2) {
                    startActivity(new Intent(TeamMemberActivity.this, MyFriendActivity.class));
                }
                popupWindow.dismiss();
            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                CommonUtil.setBackgroundAlpha(1, TeamMemberActivity.this);
            }
        });
    }

    private void getTeamers() {
        Map<String, String> param = new HashMap<>();
        param.put("teamId", teamId);
        RequestManager.getInstance(this).executeRequest(HttpUrls.GET_TEAMERS, param, new AppCallBack<ApiResponse<List<User>>>() {
            @Override
            public void next(ApiResponse<List<User>> response) {
                if (response.isSuccess()) {
                    if (response.isSuccess()) {
                        userList.addAll(response.getData());
                        adapter.notifyDataSetChanged();
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

    /**
     * 点击右上角的加号后，显示的PopupWindow内的listview的item内容
     */
    public class AddAction {
        private Drawable icon;

        private String action;

        public AddAction(Drawable icon, String action) {
            this.icon = icon;
            this.action = action;
        }

        public Drawable getIcon() {
            return icon;
        }

        public void setIcon(Drawable icon) {
            this.icon = icon;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }
    }
}
