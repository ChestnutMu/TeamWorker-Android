package cn.chestnut.mvvm.teamworker.module.massage.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.chestnut.mvvm.teamworker.Constant;
import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityChatPersonalBinding;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.module.massage.MessageDaoUtils;
import cn.chestnut.mvvm.teamworker.module.massage.adapter.ChatPersonalAdapter;
import cn.chestnut.mvvm.teamworker.module.massage.bean.Message;
import cn.chestnut.mvvm.teamworker.module.massage.bean.MessageUser;
import cn.chestnut.mvvm.teamworker.module.massage.bean.MessageVo;
import cn.chestnut.mvvm.teamworker.socket.SendProtocol;
import cn.chestnut.mvvm.teamworker.utils.CommonUtil;
import cn.chestnut.mvvm.teamworker.utils.EntityUtil;
import cn.chestnut.mvvm.teamworker.utils.Log;
import cn.chestnut.mvvm.teamworker.utils.PreferenceUtil;
import cn.chestnut.mvvm.teamworker.utils.StringUtil;
import cn.chestnut.mvvm.teamworker.utils.TimeManager;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/31 10:22:42
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class ChatPersonalActivity extends BaseActivity {

    private ActivityChatPersonalBinding binding;
    private ChatPersonalAdapter chatPersonalAdapter;
    private List<MessageVo> messageVoList;

    private MessageDaoUtils messageDaoUtils;
    private Gson gson = new Gson();

    private String userId;
    private String chatId;
    private List<String> senderIdList;

    private static final long MILLISECOND_OF_TWO_HOUR = 60 * 60 * 1000;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("消息");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_chat_personal, viewGroup, true);
        initData();
        initView();
        addListener();
    }

    @Override
    public void finish() {
        hideSoftInput(ChatPersonalActivity.this, binding.etInput);
        super.finish();
    }

    private void initData() {
        userId = PreferenceUtil.getInstances(this).getPreferenceString("userId");
        chatId = getIntent().getStringExtra("chatId");
        if (StringUtil.isEmpty(chatId)) {
            chatId = EntityUtil.getIdByTimeStampAndRandom();
        }
        messageVoList = new ArrayList<>();
        messageDaoUtils = new MessageDaoUtils(this);
        messageVoList.addAll(messageDaoUtils.transferMessageVo(
                messageDaoUtils.queryMessageByChatId(chatId)));
        senderIdList = new ArrayList<>();
        senderIdList.addAll(messageDaoUtils.queryMessageUserIdByChatId(chatId));
        Log.d("senderIdList " + senderIdList);
        long updateTime = PreferenceUtil.getInstances(this).getPreferenceLong("updateTime");
        if (updateTime != 0 && updateTime > System.currentTimeMillis()) {
//            updateUerInfo(senderIdList);
        }
        chatPersonalAdapter = new ChatPersonalAdapter(messageVoList, userId);

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Message newMessage = (Message) intent.getSerializableExtra("newMessage");
                Log.d(("ChatPersonalActivity收到一条新消息") + newMessage.toString());
                if (newMessage.getChatId().equals(chatId) && !newMessage.getSenderId().equals(userId)) {
                    Log.d("come!");
                    MessageVo messageVo = new MessageVo();
                    messageVo.setMessage(newMessage);
                    messageVo.setMessageUser(messageDaoUtils.queryMessageUserByUserId(newMessage.getSenderId()));
                    messageVoList.add(messageVo);
                    chatPersonalAdapter.notifyDataSetChanged();
                    executeRequest(SendProtocol.MSG_ISREAD_MESSAGE, newMessage.getMessageId());
                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(Constant.ActionConstant.ACTION_GET_NEW_MESSAGE));

    }

    private void initView() {
        binding.rcRecord.setAdapter(chatPersonalAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        binding.rcRecord.setLayoutManager(linearLayoutManager);
    }

    private void addListener() {
        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        binding.etInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.sendEmptyMessageDelayed(0, 250);
            }
        });

        chatPersonalAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                hideSoftInput(ChatPersonalActivity.this, binding.etInput);
            }
        });

    }


    private void scrollToBottom() {
        binding.rcRecord.scrollToPosition(messageVoList.size() - 1);
    }

    private void sendMessage() {
        Log.d("sendMessage");
        String content = binding.etInput.getText().toString();
        if (StringUtil.isEmpty(content)) {
            CommonUtil.showToast("不能为空", this);
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("chatId", chatId);
        params.put("title", "");
        params.put("content", content);
        params.put("uids", gson.toJson(senderIdList));
        executeRequest(SendProtocol.MSG_SEND_MESSAGE, gson.toJson(params));
        Message message = new Message();
        message.setMessageId(EntityUtil.getIdByTimeStampAndRandom());
        message.setSenderId(userId);
        message.setContent(content);
        message.setChatId(chatId);
        message.setTitle("");
        message.setTime(TimeManager.getInstance().getServiceTime());
        messageDaoUtils.insertMessage(message);
        MessageVo messageVo = new MessageVo();
        messageVo.setMessage(message);
        messageVo.setMessageUser(messageDaoUtils.queryMessageUserByUserId(userId));
        messageVoList.add(messageVo);
        chatPersonalAdapter.notifyDataSetChanged();
        scrollToBottom();

        binding.etInput.setText("");
    }

    android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    scrollToBottom();
                    break;
            }
        }
    };

    /**
     * 更新用户信息
     *
     * @param messageUserId
     */
    private void updateUerInfo(List<String> messageUserId) {
        RequestManager.getInstance(this).executeRequest(HttpUrls.GET_USER_INFO, messageUserId, new AppCallBack<ApiResponse<List<MessageUser>>>() {

            @Override
            public void next(ApiResponse<List<MessageUser>> response) {
                if (response.isSuccess()) {
                    //本地更新数据
                    messageDaoUtils.insertMultMessageUser(response.getData());

                    messageVoList.addAll(
                            messageDaoUtils.transferMessageVo(
                                    messageDaoUtils.queryMessageByChatId(userId)));
                    chatPersonalAdapter.notifyDataSetChanged();

                    //保存下一次需要更新的时间
                    PreferenceUtil.getInstances(ChatPersonalActivity.this).savePreferenceLong("updateTime", MILLISECOND_OF_TWO_HOUR + System.currentTimeMillis());
                } else {
                    CommonUtil.showToast(response.getMessage(), ChatPersonalActivity.this);
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

    public void showSoftInput(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    public void hideSoftInput(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
