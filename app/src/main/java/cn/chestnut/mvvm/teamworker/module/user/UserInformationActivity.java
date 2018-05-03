package cn.chestnut.mvvm.teamworker.module.user;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.greenrobot.greendao.async.AsyncOperation;
import org.greenrobot.greendao.async.AsyncOperationListener;
import org.greenrobot.greendao.async.AsyncSession;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.chestnut.mvvm.teamworker.BR;
import cn.chestnut.mvvm.teamworker.Constant;
import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityUserInformationBinding;
import cn.chestnut.mvvm.teamworker.databinding.PopupUserInfoMenuBinding;
import cn.chestnut.mvvm.teamworker.db.ChatDao;
import cn.chestnut.mvvm.teamworker.db.UserDao;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.adapter.BaseListViewAdapter;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.main.common.MyApplication;
import cn.chestnut.mvvm.teamworker.model.Chat;
import cn.chestnut.mvvm.teamworker.model.User;
import cn.chestnut.mvvm.teamworker.model.UserFriend;
import cn.chestnut.mvvm.teamworker.model.UserInfo;
import cn.chestnut.mvvm.teamworker.module.massage.MessageDaoUtils;
import cn.chestnut.mvvm.teamworker.module.massage.activity.ChatActivity;
import cn.chestnut.mvvm.teamworker.module.team.PullUserIntoTeamActivity;
import cn.chestnut.mvvm.teamworker.utils.CommonUtil;
import cn.chestnut.mvvm.teamworker.utils.Log;
import cn.chestnut.mvvm.teamworker.utils.PreferenceUtil;
import cn.chestnut.mvvm.teamworker.utils.sqlite.DaoManager;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/6 14:56:48
 * Description：用户资料
 * Email: xiaoting233zhang@126.com
 */

public class UserInformationActivity extends BaseActivity {

    private ActivityUserInformationBinding binding;

    private String myUserId;

    private String userId;

    private UserFriend userFriend;

    private List<AddAction> addActionList;

    private BaseListViewAdapter adapter;

    /*本地数据操作异步工具类*/
    private AsyncSession asyncSession;

    //传递用户id
    public static final String BUNDLE_USER_ID = "bundle_user_id";

    //传递是否好友
    public static final String BUNDLE_USER_TYPE = "bundle_user_type";

    //传递用户信息
    public static final String BUNDLE_USER_INFO = "bundle_user_info";

    public static void startActivity(Context mContext, String userId, boolean isFriend, User user) {
        Intent intent = new Intent(mContext, UserInformationActivity.class);
        intent.putExtra(UserInformationActivity.BUNDLE_USER_ID, userId);
        intent.putExtra(UserInformationActivity.BUNDLE_USER_TYPE, isFriend);
        intent.putExtra(UserInformationActivity.BUNDLE_USER_INFO, user);
        mContext.startActivity(intent);
    }

    public static void startActivity(Context mContext, String userId) {
        Intent intent = new Intent(mContext, UserInformationActivity.class);
        intent.putExtra(UserInformationActivity.BUNDLE_USER_ID, userId);
        intent.putExtra(UserInformationActivity.BUNDLE_USER_TYPE, false);
        mContext.startActivity(intent);
    }

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
        asyncSession = DaoManager.getDaoSession().startAsyncSession();
        userId = getIntent().getStringExtra(BUNDLE_USER_ID);

        //预判断是否为好友
        boolean isFriend = getIntent().getBooleanExtra(BUNDLE_USER_TYPE, false);

        myUserId = PreferenceUtil.getInstances(this).getPreferenceString("userId");
        addActionList = new ArrayList<>();
        adapter = new BaseListViewAdapter(R.layout.item_user_info_menu, BR.addAction, addActionList);

        //判断是否有user传递进来
        if (getIntent().hasExtra(BUNDLE_USER_INFO)) {
            userFriend = new UserFriend();
            userFriend.setUser((User) getIntent().getSerializableExtra(BUNDLE_USER_INFO));
            userFriend.setFriend(isFriend);
            updateLayout();
        }

        getUserDetail();
    }

    protected void initView() {
        Log.d("userFriend:" + userFriend.isFriend());
        if (userFriend.isFriend() || userId.equals(myUserId)) {
            binding.btnSubmit.setText("发送消息");
            binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    findChatAndGoTOChat();
                }
            });
        } else {
            long count = QueryBuilder.internalCreate(DaoManager.getDaoSession().getDao(User.class))
                    .where(UserDao.Properties.UserId.eq(userFriend.getUser().getUserId()))
                    .where(UserDao.Properties.Friend.eq(true)).buildCount().count();
            if (count > 0) {
                binding.btnSubmit.setText("发送消息");
                binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        findChatAndGoTOChat();
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
        }
        binding.setVariable(BR.userInformation, userFriend.getUser());
    }

    /**
     * 从本地数据库找到chat，否则创建一个chat，跳转到ChatActivity
     */
    private void findChatAndGoTOChat() {
        asyncSession.setListenerMainThread(new AsyncOperationListener() {

            @Override
            public void onAsyncOperationCompleted(AsyncOperation operation) {
                if (operation.isFailed()) {
                    Log.d("UserInformationActivity 获取数据异常");
                    //从服务器创建并保存到本地
                    createChat();
                    return;
                }
                Log.d("operation.getType()= " + operation.getType());
                if (operation.getType() == AsyncOperation.OperationType.QueryUnique) {
                    Object obj = operation.getResult();
                    Log.d("获取数据 obj = " + obj);
                    if (null == obj) {
                        //从服务器创建并保存到本地
                        createChat();
                    } else {
                        handleChat((Chat) obj);
                    }
                }
            }
        });
        asyncSession.queryUnique(QueryBuilder.internalCreate(DaoManager.getDaoSession().getDao(Chat.class))
                .where(ChatDao.Properties.UserId.eq(userFriend.getUser().getUserId()))
                .build());
    }

    private void createChat() {
        asyncSession.setListenerMainThread(null);
        Map<String, Object> param = new HashMap<>(2);
        param.put("chatType", Constant.ChatType.TYPE_CHAT_DOUBLE);
        Set<String> userList = new HashSet<>(2);
        userList.add(userId);
        userList.add(myUserId);
        param.put("userList", gson.toJson(userList));
        showProgressDialog(this);
        RequestManager.getInstance(this).executeRequest(HttpUrls.BUILD_CHAT, param, new AppCallBack<ApiResponse<Chat>>() {
            @Override
            public void next(ApiResponse<Chat> response) {
                if (response.isSuccess()) {
                    Chat chat = response.getData();
                    //保存到本地
                    chat.setChatName(userFriend.getUser().getNickname());
                    chat.setChatPic(userFriend.getUser().getAvatar());
                    chat.setUserId(userFriend.getUser().getUserId());
                    chat.setLastMessage("");
                    asyncSession.insertOrReplace(chat);
                    //跳转
                    handleChat(chat);
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

    private void handleChat(Chat chat) {
        Log.d("handleChat 更新信息界面");
        Intent intentUpdate = new Intent(Constant.ActionConstant.UPDATE_MESSAGE_CHAT_LAYOUT);
        LocalBroadcastManager.getInstance(MyApplication.getInstance()).sendBroadcast(intentUpdate);

        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(ChatActivity.BUNDLE_CHAT, chat);
        startActivity(intent);
        finish();
    }

    private void showMenuPopup(View add) {
        CommonUtil.setBackgroundAlpha(0.5f, this);

        final PopupUserInfoMenuBinding popupBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.popup_user_info_menu, null, false);
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
        param.put("friendId", userId);
        RequestManager.getInstance(this).executeRequest(HttpUrls.GET_USER_DETAIL, param, new AppCallBack<ApiResponse<UserFriend>>() {
            @Override
            public void next(ApiResponse<UserFriend> response) {
                if (response.isSuccess()) {
                    userFriend = response.getData();
                    asyncSession.insertOrReplace(userFriend.getUser());
                    UserInfo userInfo=new UserInfo();
                    userInfo.setUserId(userFriend.getUser().getUserId());
                    userInfo.setAvatar(userFriend.getUser().getAvatar());
                    userInfo.setUserId(userFriend.getUser().getNickname());
                    asyncSession.insertOrReplace(userInfo);
                    updateLayout();
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

    private void updateLayout() {
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
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(userFriend.getUser().getUserId());
        userInfo.setNickname(userFriend.getUser().getNickname());
        userInfo.setAvatar(userFriend.getUser().getAvatar());
        MyApplication.userInfoMap.put(userInfo.getUserId(), userInfo);
        MessageDaoUtils.checkUserInfoOrUpdateToLocal(asyncSession, userFriend.getUser());
    }

    private void deleteFriend() {
        Map<String, String> param = new HashMap<>(1);
        param.put("friendId", userId);
        RequestManager.getInstance(this).executeRequest(HttpUrls.DEL_FRIEND, param, new AppCallBack<ApiResponse<Object>>() {
            @Override
            public void next(ApiResponse<Object> response) {
                if (response.isSuccess()) {
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        asyncSession = null;
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
