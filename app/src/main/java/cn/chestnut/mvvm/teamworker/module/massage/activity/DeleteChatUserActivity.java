package cn.chestnut.mvvm.teamworker.module.massage.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

import org.greenrobot.greendao.async.AsyncSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.chestnut.mvvm.teamworker.BR;
import cn.chestnut.mvvm.teamworker.Constant;
import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityDeleteChatUserBinding;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.main.common.MyApplication;
import cn.chestnut.mvvm.teamworker.model.Chat;
import cn.chestnut.mvvm.teamworker.model.ChatMessage;
import cn.chestnut.mvvm.teamworker.model.User;
import cn.chestnut.mvvm.teamworker.model.UserInfo;
import cn.chestnut.mvvm.teamworker.module.massage.adapter.ChatUserAdapter;
import cn.chestnut.mvvm.teamworker.module.massage.adapter.ChosenDeleteUserAdapter;
import cn.chestnut.mvvm.teamworker.module.massage.adapter.ChosenUserAdapter;
import cn.chestnut.mvvm.teamworker.module.massage.adapter.DeleteChatUserAdapter;
import cn.chestnut.mvvm.teamworker.utils.EntityUtil;
import cn.chestnut.mvvm.teamworker.utils.Log;
import cn.chestnut.mvvm.teamworker.utils.PreferenceUtil;
import cn.chestnut.mvvm.teamworker.utils.sqlite.DaoManager;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/24 21:02:36
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class DeleteChatUserActivity extends BaseActivity {
    ActivityDeleteChatUserBinding binding;

    /*本地数据操作异步工具类*/
    private AsyncSession asyncSession;

    private List<UserInfo> chatUserList;

    private DeleteChatUserAdapter adapter;
    private ChosenDeleteUserAdapter chosenUserAdapter;

    private String userId;

    private String chatId;

    private TextView tvFinish;

    //添加用户的聊天室id
    public static final String BUNDLE_CHAT_ID = "bundle_chat_id";
    //添加用户的原本用户
    public static final String BUNDLE_CHAT_USER_LIST = "bundle_chat_user_list";

    public static void startActivity(Context mContext, String chatId, String userInfoListJson) {
        Intent intent = new Intent(mContext, DeleteChatUserActivity.class);
        intent.putExtra(BUNDLE_CHAT_ID, chatId);
        intent.putExtra(BUNDLE_CHAT_USER_LIST, userInfoListJson);
        mContext.startActivity(intent);
    }


    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("删除成员");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_delete_chat_user, viewGroup, true);
        asyncSession = DaoManager.getDaoSession().startAsyncSession();
        initData();
        initView();
        addListener();
    }

    /**
     * 初始化数据
     */
    protected void initData() {
    }

    /**
     * 初始化界面
     */
    protected void initView() {
        userId = PreferenceUtil.getInstances(this).getPreferenceString("userId");

        chatId = getIntent().getStringExtra(BUNDLE_CHAT_ID);
        String json = getIntent().getStringExtra(BUNDLE_CHAT_USER_LIST);
        chatUserList = gson.fromJson(json, new TypeToken<List<UserInfo>>() {
        }.getType());

        //去掉自己
        for (int i = 0; i < chatUserList.size(); i++) {
            if (chatUserList.get(i).getUserId().equals(userId)) {
                chatUserList.remove(i);
                break;
            }
        }

        adapter = new DeleteChatUserAdapter(R.layout.item_delete_chat_user, BR.userInfo, chatUserList);

        binding.lvMyFriend.setAdapter(adapter);

        chosenUserAdapter = new ChosenDeleteUserAdapter(adapter.getChooseUserIdSet());
        binding.rvShowChoosePerson.setAdapter(chosenUserAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.rvShowChoosePerson.setLayoutManager(manager);
    }

    /**
     * 添加监听器
     */
    protected void addListener() {
        binding.lvMyFriend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("点击按钮" + position);
                UserInfo chooseUser = chatUserList.get(position);
                if (adapter.getChooseUserIdSet().contains(chooseUser)) {
                    //设置没选择
                    adapter.getChooseUserIdSet().remove(chooseUser);
                    adapter.notifyDataSetChanged();

                    chosenUserAdapter.notifyDataSetChanged();
                    if (adapter.getChooseUserIdSet().isEmpty()) {
                        tvFinish.setText("完成");
                        tvFinish.setTextColor(getResources().getColor(R.color.separation));
                        tvFinish.setClickable(false);
                    } else {
                        tvFinish.setText("完成(" + adapter.getChooseUserIdSet().size() + ")");
                        tvFinish.setTextColor(getResources().getColor(R.color.white));
                    }
                } else {
                    //设置选择
                    adapter.getChooseUserIdSet().add(chooseUser);
                    adapter.notifyDataSetChanged();
                    chosenUserAdapter.notifyDataSetChanged();

                    tvFinish.setText("完成(" + adapter.getChooseUserIdSet().size() + ")");
                    tvFinish.setTextColor(getResources().getColor(R.color.white));
                    tvFinish.setClickable(true);
                }
            }
        });
    }

    @Override
    public void setButton(TextView edit, ImageView add, ImageView search) {
        tvFinish = edit;
        tvFinish.setVisibility(View.VISIBLE);
        tvFinish.setText("完成");
        tvFinish.setTextColor(getResources().getColor(R.color.separation));
        tvFinish.setClickable(false);
        tvFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("点击 tvFinish");
                delUser();
            }
        });
    }

    private void delUser() {
        Log.d("删除用户");
        Map<String, Object> param = new HashMap<>(2);
        param.put("chatId", chatId);
        Set<String> userList = new HashSet<>(chatUserList.size() + adapter.getChooseUserIdSet().size());
        showProgressDialog(this);
        List<String> notDelUserIdList = new ArrayList<>();
        for (UserInfo userInfo : chatUserList) {
            notDelUserIdList.add(userInfo.getUserId());
        }
        for (UserInfo userInfo : adapter.getChooseUserIdSet()) {
            notDelUserIdList.remove(userInfo.getUserId());
        }
        userList.addAll(notDelUserIdList);
        userList.add(userId);
        param.put("userList", gson.toJson(userList));
        RequestManager.getInstance(this).executeRequest(HttpUrls.CHANGE_CHAT_INFO, param, new AppCallBack<ApiResponse<Chat>>() {
            @Override
            public void next(ApiResponse<Chat> response) {
                if (response.isSuccess()) {
                    Chat chat = response.getData();
                    //保存到本地
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setChatMessageId(EntityUtil.getIdByTimeStampAndRandom());
                    chatMessage.setChatId(chat.getChatId());
                    chatMessage.setSenderId(userId);
                    chatMessage.setType(Constant.ChatMessageType.TYPE_MESSAGE_CHANGE_PEOPLE_REMOVE);
                    chatMessage.setDone(true);
                    chatMessage.setSendTime(chat.getUpdateTime());

                    chatMessage.setMessage(gson.toJson(adapter.getChooseUserIdSet()));
                    String temp = "";
                    for (int i = 0; i < adapter.getChooseUserIdSet().size() - 1; i++) {
                        temp = temp + adapter.getChooseUserIdSet().get(i).getNickname() + "、";
                    }
                    temp = temp + adapter.getChooseUserIdSet().get(adapter.getChooseUserIdSet().size() - 1).getNickname();

                    chat.setLastMessage("你将" + temp + "移出了聊天室");
                    asyncSession.insert(chatMessage);
                    asyncSession.insertOrReplace(chat);
                    //更新聊天室设置
                    Intent intentSetting = new Intent(Constant.ActionConstant.ACTION_UPDATE_CHAT_SETTING);
                    intentSetting.putExtra(ChatSettingActivity.BROADCAST_INTENT_USER_ID_LIST, chat.getUserList());
                    LocalBroadcastManager.getInstance(MyApplication.getInstance()).sendBroadcast(intentSetting);

                    //更新聊天室
                    Intent intent = new Intent(Constant.ActionConstant.ACTION_UPDATE_CHAT);
                    intent.putExtra(ChatActivity.BROADCAST_INTENT_TYPE, 3);
                    intent.putExtra(ChatActivity.BROADCAST_INTENT_MESSAGE, chatMessage);
                    LocalBroadcastManager.getInstance(MyApplication.getInstance()).sendBroadcast(intent);

                    updateMessageLayout();
                    finish();

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

    /**
     * 更新消息界面
     */
    private void updateMessageLayout() {
        Log.d("updateMessageLayout 更新信息界面");
        Intent intent = new Intent(Constant.ActionConstant.UPDATE_MESSAGE_CHAT_LAYOUT);
        LocalBroadcastManager.getInstance(MyApplication.getInstance()).sendBroadcast(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        asyncSession = null;
    }
}
