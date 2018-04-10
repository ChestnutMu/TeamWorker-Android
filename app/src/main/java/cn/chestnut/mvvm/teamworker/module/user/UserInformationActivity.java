package cn.chestnut.mvvm.teamworker.module.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.BindingAdapter;
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
import cn.chestnut.mvvm.teamworker.databinding.ActivityUserInformationBinding;
import cn.chestnut.mvvm.teamworker.databinding.PopupUserInfoMenuBinding;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.adapter.BaseListViewAdapter;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.model.UserFriend;
import cn.chestnut.mvvm.teamworker.module.massage.activity.ChatActivity;
import cn.chestnut.mvvm.teamworker.module.team.PullUserIntoTeamActivity;
import cn.chestnut.mvvm.teamworker.utils.CommonUtil;
import cn.chestnut.mvvm.teamworker.utils.Log;
import cn.chestnut.mvvm.teamworker.utils.PreferenceUtil;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/6 14:56:48
 * Description：用户资料
 * Email: xiaoting233zhang@126.com
 */

public class UserInformationActivity extends BaseActivity {

    private ActivityUserInformationBinding binding;

    private String userId;

    private UserFriend userFriend;

    private List<AddAction> addActionList;

    private BaseListViewAdapter adapter;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("详情资料");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_user_information, viewGroup, true);
        initData();
    }

    @Override
    public void setButton(TextView edit, final ImageView add, ImageView search) {
        add.setVisibility(View.VISIBLE);
        add.setImageDrawable(getResources().getDrawable(R.mipmap.icon_menu));
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenuPopup(add);
            }
        });
    }

    protected void initData() {
        userId = getIntent().getStringExtra("userId");
        addActionList = new ArrayList<>();
        getUserDetail();
    }

    protected void initView() {
        Log.d("userFriend:" + userFriend.isFriend());
        if (userFriend.isFriend() || userId.equals(PreferenceUtil.getInstances(this).getPreferenceString("userId"))) {
            binding.btnSubmit.setText("发送消息");
            binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(UserInformationActivity.this, ChatActivity.class);
                    intent.putExtra("receiverId", userId);
                }
            });
        } else {
            binding.btnSubmit.setText("添加到通讯录");
            binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(UserInformationActivity.this, RequestFriendActivity.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                }
            });
        }
        binding.setVariable(BR.userInformation, userFriend.getUser());
    }

    private void showMenuPopup(View add) {
        CommonUtil.setBackgroundAlpha(0.5f, this);

        final PopupUserInfoMenuBinding popupBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.popup_user_info_menu, null, false);
        adapter = new BaseListViewAdapter(R.layout.item_user_info_menu, BR.addAction, addActionList);
        popupBinding.lvMenu.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        popupBinding.lvMenu.setAdapter(adapter);
        final PopupWindow popupWindow = new PopupWindow(popupBinding.getRoot());
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(add, 0, 0);

        popupBinding.lvMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {//拉取用户加入团队
                    Intent intent = new Intent(UserInformationActivity.this, PullUserIntoTeamActivity.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                } else if (position == 1) {//推荐联系人名片

                } else if (position == 2) {//删除好友
                    showDeleteFriendDialog();
                }
                popupWindow.dismiss();
            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                CommonUtil.setBackgroundAlpha(1, UserInformationActivity.this);
            }
        });
    }

    private void showDeleteFriendDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("确定要删除该好友吗？")
                .setPositiveButton("确定删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteFriend();
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

    private void getUserDetail() {
        Map<String, String> param = new HashMap<>(1);
        param.put("friendId", getIntent().getStringExtra("userId"));
        RequestManager.getInstance(this).executeRequest(HttpUrls.GET_USER_DETAIL, param, new AppCallBack<ApiResponse<UserFriend>>() {
            @Override
            public void next(ApiResponse<UserFriend> response) {
                if (response.isSuccess()) {
                    userFriend = response.getData();
                    //如果该用户是自己或者是好友，那么显示"发送消息"的按钮，否则显示默认的"添加到通讯录"按钮
                    initView();
                    AddAction invite = new AddAction(getResources().getDrawable(R.mipmap.icon_invite), "拉入团队");
                    AddAction recommend = new AddAction(getResources().getDrawable(R.mipmap.icon_recommend), "推荐联系人");
                    AddAction deleteFriend = new AddAction(getResources().getDrawable(R.mipmap.icon_delete_friend), "删除朋友");
                    if (addActionList.size() > 0) {
                        addActionList.clear();
                    }
                    addActionList.add(invite);
                    addActionList.add(recommend);
                    if (userFriend.isFriend()) {
                        addActionList.add(deleteFriend);
                    }
                    adapter.notifyDataSetChanged();
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

    private void deleteFriend() {
        Map<String, String> param = new HashMap<>(1);
        param.put("friendId", userId);
        RequestManager.getInstance(this).executeRequest(HttpUrls.DEL_FRIEND, param, new AppCallBack<ApiResponse<Object>>() {
            @Override
            public void next(ApiResponse<Object> response) {
                if (response.isSuccess()) {
                    NewFriendRequestDaoUtils newFriendRequestDaoUtils = new NewFriendRequestDaoUtils();
                    // TODO: 2018/4/10 删除好友后，删除本地的好友请求状态，通过requesterId
                    showToast(response.getMessage());
                    getUserDetail();
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
     * 点击右上角的菜单按钮后，显示的PopupWindow内的listview的item内容
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

    @BindingAdapter({"load_sex_image"})
    public static void loadSexImage(ImageView view, String sex) {
        if (null != sex && sex.equals("女")) {
            view.setBackgroundResource(R.mipmap.icon_woman);
        } else {
            view.setBackgroundResource(R.mipmap.icon_man);
        }
    }


}
