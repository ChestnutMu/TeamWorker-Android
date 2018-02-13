package cn.chestnut.mvvm.teamworker.module.massage.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.chestnut.mvvm.teamworker.Constant;
import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityChatBinding;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.module.massage.MessageDaoUtils;
import cn.chestnut.mvvm.teamworker.module.massage.adapter.ChatAdapter;
import cn.chestnut.mvvm.teamworker.module.massage.bean.Message;
import cn.chestnut.mvvm.teamworker.module.massage.bean.MessageUser;
import cn.chestnut.mvvm.teamworker.module.massage.bean.MessageVo;
import cn.chestnut.mvvm.teamworker.socket.SendProtocol;
import cn.chestnut.mvvm.teamworker.utils.CommonUtil;
import cn.chestnut.mvvm.teamworker.utils.EmojiUtil;
import cn.chestnut.mvvm.teamworker.utils.EntityUtil;
import cn.chestnut.mvvm.teamworker.utils.Log;
import cn.chestnut.mvvm.teamworker.utils.PreferenceUtil;
import cn.chestnut.mvvm.teamworker.utils.StringUtil;
import cn.chestnut.mvvm.teamworker.utils.TimeManager;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/31 10:22:42
 * Description：聊天
 * Email: xiaoting233zhang@126.com
 */

public class ChatActivity extends BaseActivity {

    private ActivityChatBinding binding;
    private ChatAdapter chatAdapter;
    private List<MessageVo> messageVoList;

    private MessageDaoUtils messageDaoUtils;
    private Gson gson = new Gson();

    private String userId;
    private String chatId;
    private List<String> senderIdList;

    private static final long MILLISECOND_OF_TWO_HOUR = 60 * 60 * 1000;

    @Override
    protected void setBaseTitle(TextView titleView) {
        String chatName = getIntent().getStringExtra("chatName");
        if (StringUtil.isStringNotNull(chatName)) {
            titleView.setText(chatName);
        } else {
            titleView.setText(getIntent().getStringExtra("title"));
        }
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_chat, viewGroup, true);
        initData();
        initView();
        addListener();
    }

    @Override
    public void finish() {
        hideSoftInput(ChatActivity.this, binding.etInput);
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
        senderIdList.addAll(messageDaoUtils.queryMessageUserIdByChatId(chatId, userId));
        long updateTime = PreferenceUtil.getInstances(this).getPreferenceLong("updateTime");
        if (updateTime != 0 && updateTime > System.currentTimeMillis()) {
            // TODO: 2018/1/21 重写接口，参数改为一个List
//            updateUerInfo(senderIdList);
        }
        chatAdapter = new ChatAdapter(messageVoList, userId);

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Message newMessage = (Message) intent.getSerializableExtra("newMessage");
                Log.d(("ChatPersonalActivity收到一条新消息") + newMessage.toString());
                try {
                    newMessage.setContent(EmojiUtil.emojiRecovery(newMessage.getContent()));
                    newMessage.setChatName(EmojiUtil.emojiRecovery(newMessage.getChatName()));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (newMessage.getChatId().equals(chatId) && !newMessage.getSenderId().equals(userId)) {
                    MessageVo messageVo = new MessageVo();
                    messageVo.setMessage(newMessage);
                    messageVo.setMessageUser(messageDaoUtils.queryMessageUserByUserId(newMessage.getSenderId()));
                    messageVoList.add(messageVo);
                    chatAdapter.notifyDataSetChanged();
                    executeRequest(SendProtocol.MSG_ISREAD_MESSAGE, newMessage.getMessageId());
                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(Constant.ActionConstant.ACTION_GET_NEW_MESSAGE));
    }

    private void initView() {
        binding.rcRecord.setAdapter(chatAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.rcRecord.setLayoutManager(linearLayoutManager);
        scrollToBottom();
    }

    private void addListener() {
        binding.tvSend.setOnClickListener(new View.OnClickListener() {
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

        binding.rcRecord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftInput(ChatActivity.this, binding.etInput);
                return false;
            }
        });

        binding.etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (StringUtil.isStringNotNull(s.toString())) {
                    GradientDrawable gradientDrawable = (GradientDrawable) binding.tvSend.getBackground();
                    gradientDrawable.setColor(getResources().getColor(R.color.appTheme));
                    binding.tvSend.setBackground(gradientDrawable);
                    binding.tvSend.setClickable(true);
                } else {
                    GradientDrawable gradientDrawable = (GradientDrawable) binding.tvSend.getBackground();
                    gradientDrawable.setColor(getResources().getColor(R.color.garyLight2));
                    binding.tvSend.setBackground(gradientDrawable);
                    binding.tvSend.setClickable(false);
                }
            }
        });
    }


    private void scrollToBottom() {
        binding.rcRecord.scrollToPosition(messageVoList.size() - 1);
    }

    private void sendMessage() {
        String content = binding.etInput.getText().toString();
        if (StringUtil.isEmpty(content)) {
            CommonUtil.showToast("不能为空", this);
            return;
        }

        Message message = new Message();
        message.setMessageId(EntityUtil.getIdByTimeStampAndRandom());
        message.setSenderId(userId);
        if (senderIdList.size() == 1) {
            message.setReceiverId(senderIdList.get(0));
        }
        message.setContent(content);
        message.setChatId(chatId);
        message.setTitle("");
        message.setTime(TimeManager.getInstance().getServiceTime());
        messageDaoUtils.insertMessage(message);
        MessageVo messageVo = new MessageVo();
        messageVo.setMessage(message);
        messageVo.setMessageUser(messageDaoUtils.queryMessageUserByUserId(userId));
        messageVoList.add(messageVo);
        chatAdapter.notifyDataSetChanged();
        scrollToBottom();
        binding.etInput.setText("");

        try {
            content = EmojiUtil.emojiConvert(content);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Map<String, String> params = new HashMap<>();
        params.put("chatId", chatId);
        params.put("title", "");
        params.put("content", content);
        params.put("uids", gson.toJson(senderIdList));
        executeRequest(SendProtocol.MSG_SEND_MESSAGE, gson.toJson(params));
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
                    String nickName = null;
                    for (int i = 0; i < response.getData().size(); i++) {
                        try {
                            nickName = EmojiUtil.emojiConvert(response.getData().get(i).getNickname());
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        response.getData().get(i).setNickname(nickName);
                    }
                    //本地更新数据
                    messageDaoUtils.insertMultMessageUser(response.getData());

                    messageVoList.addAll(
                            messageDaoUtils.transferMessageVo(
                                    messageDaoUtils.queryMessageByChatId(userId)));
                    chatAdapter.notifyDataSetChanged();

                    //保存下一次需要更新的时间
                    PreferenceUtil.getInstances(ChatActivity.this).savePreferenceLong("updateTime", MILLISECOND_OF_TWO_HOUR + System.currentTimeMillis());
                } else {
                    CommonUtil.showToast(response.getMessage(), ChatActivity.this);
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

    public void hideSoftInput(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
