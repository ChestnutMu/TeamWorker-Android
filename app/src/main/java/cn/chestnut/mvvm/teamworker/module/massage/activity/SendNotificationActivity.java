package cn.chestnut.mvvm.teamworker.module.massage.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivitySendNotificationBinding;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.module.massage.adapter.UserAdapter;
import cn.chestnut.mvvm.teamworker.module.massage.bean.User;
import cn.chestnut.mvvm.teamworker.socket.ReceiverProtocol;
import cn.chestnut.mvvm.teamworker.utils.CommonUtil;
import cn.chestnut.mvvm.teamworker.utils.StringUtil;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/1/2 22:35:05
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class SendNotificationActivity extends BaseActivity {

    private ActivitySendNotificationBinding binding;
    private Gson gson = new Gson();
    private List<String> uidList;

    private final int request_code_select_person = 1001;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("发送通知");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_send_notification,viewGroup,true);
        initData();
        initView();
        addListener();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        uidList = new ArrayList<>();
    }

    private void initView() {
        setTitleBarVisible(true);
    }

    private void addListener() {
        binding.tvReceivers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SendNotificationActivity.this, SelectDepartmentActivity.class);
                startActivityForResult(intent, request_code_select_person);
            }
        });

        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotification();
                finish();
            }
        });
    }

    /**
     * 发送通知
     */
    private void sendNotification() {
        if (uidList.isEmpty()) {
            CommonUtil.showToast("至少有一位接受者", this);
            return;
        }
        String title = binding.etTitle.getText().toString();
        String content = binding.etContent.getText().toString();
        if (StringUtil.isEmpty(content)) {
            CommonUtil.showToast("不能为空", this);
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("title", title);
        params.put("content", content);
        params.put("uids", gson.toJson(uidList));
        executeRequest(ReceiverProtocol.MSG_SEND_MESSAGE, gson.toJson(params));
    }

    @Override
    public void onSessionMessageException(int msgId, Exception exception) {
        showToast("连接异常");
    }

    @Override
    public void onSessionTimeout() {
        showToast("发送超时");
    }

    @Override
    public void onSessionClosed() {
        showToast("连接关闭");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == request_code_select_person && resultCode == Activity.RESULT_OK) {
            User user = (User) data.getExtras().getSerializable("user");
            if (user != null) {
                uidList.add(user.getUserId());
                if (uidList.isEmpty()) {
                    binding.tvReceivers.setText(user.getAccount());
                } else {
                    binding.tvReceivers.setText(binding.tvReceivers.getText() +","+ user.getAccount());
                }
            }
        }
    }
}
