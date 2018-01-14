package cn.chestnut.mvvm.teamworker.module.massage.activity;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityChatPersonalBinding;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.module.massage.adapter.MessageAdapter;
import cn.chestnut.mvvm.teamworker.module.massage.bean.Message;
import cn.chestnut.mvvm.teamworker.module.massage.bean.MessageVo;
import cn.chestnut.mvvm.teamworker.socket.SendProtocol;
import cn.chestnut.mvvm.teamworker.utils.CommonUtil;
import cn.chestnut.mvvm.teamworker.utils.StringUtil;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/31 10:22:42
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class ChatPersonalActivity extends BaseActivity {

    private ActivityChatPersonalBinding binding;
    private MessageAdapter messageAdapter;
    private List<MessageVo> messageList;

    private Gson gson = new Gson();

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("消息");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat_personal);
        initData();
        addListener();
    }

    @Override
    public void onSessionMessage(int msgId, Object object) {
        switch (msgId) {
            case 1: {
                CommonUtil.showToast("新消息", this);
                Message message = gson.fromJson(object.toString(), new TypeToken<Message>() {
                }.getType());
                MessageVo messageVo = new MessageVo();
                messageVo.setMessage(message);
                messageList.add(messageVo);
                messageAdapter.notifyDataSetChanged();

                executeRequest(SendProtocol.MSG_ISREAD_MESSAGE, message.getMessageId());
            }
            break;
        }
    }

    private void addListener() {
        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList, this);
        binding.rcRecord.setAdapter(messageAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.rcRecord.setLayoutManager(manager);
        binding.rcRecord.setNestedScrollingEnabled(false);

    }

    private void sendMessage() {
        String content = binding.etInput.getText().toString();
        if (StringUtil.isEmpty(content)) {
            CommonUtil.showToast("不能为空", this);
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("title", "");
        params.put("content", content);
        List<String> uidList = new ArrayList<>();
        uidList.add("1512983546736hBFqyp");
        uidList.add("1512970786104UpglD0");
        params.put("uids", gson.toJson(uidList));
        executeRequest(SendProtocol.MSG_SEND_MESSAGE, gson.toJson(params));
    }
}
