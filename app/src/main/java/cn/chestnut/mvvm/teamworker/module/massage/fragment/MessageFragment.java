package cn.chestnut.mvvm.teamworker.module.massage.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cn.chestnut.mvvm.teamworker.Constant;
import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.FragmentMessageBinding;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.activity.MainActivity;
import cn.chestnut.mvvm.teamworker.main.common.BaseFragment;
import cn.chestnut.mvvm.teamworker.module.massage.MessageDaoUtils;
import cn.chestnut.mvvm.teamworker.module.massage.activity.SendNotificationActivity;
import cn.chestnut.mvvm.teamworker.module.massage.adapter.MessageAdapter;
import cn.chestnut.mvvm.teamworker.module.massage.bean.Message;
import cn.chestnut.mvvm.teamworker.socket.SendProtocol;
import cn.chestnut.mvvm.teamworker.utils.CommonUtil;
import cn.chestnut.mvvm.teamworker.utils.Log;
import cn.chestnut.mvvm.teamworker.utils.PreferenceUtil;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/1/2 11:05:44
 * Description：消息Fragment
 * Email: xiaoting233zhang@126.com
 */

public class MessageFragment extends BaseFragment {

    private FragmentMessageBinding binding;
    private MessageAdapter messageAdapter;
    private LinkedList<Message> messageList = new LinkedList<>();
    private String userId;
    private MessageDaoUtils messageDaoUtils;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("消息");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_message, viewGroup, true);
        initData();
        initView();
        addListener();
    }

    @Override
    public void setButton(TextView edit, ImageView add, ImageView search) {
        super.setButton(edit, add, search);
        add.setVisibility(View.VISIBLE);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SendNotificationActivity.class);
                getActivity().startActivity(intent);
            }
        });
        search.setVisibility(View.VISIBLE);
    }

    private void initData() {
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Message newMessage = (Message) intent.getSerializableExtra("newMessage");
                Log.d("MessageFragment收到一条新消息" + newMessage.toString());
                messageDaoUtils.insertMessage(newMessage);
                boolean listHasSender = false;//消息列表中是否已经有该发送者item
                for (int i = 0; i < messageList.size(); i++) {
                    if (messageList.get(i).getSenderId().equals(newMessage.getSenderId()) && i != 0) {
                        messageList.add(newMessage);
                        messageAdapter.notifyItemMoved(i, 0);
                        listHasSender = true;
                        break;
                    }
                }
                if (!listHasSender) {
                    messageAdapter.notifyItemInserted(0);
                }
            }
        };

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, new IntentFilter(Constant.ActionConstant.ACTION_GET_NEW_MESSAGE));
        messageDaoUtils = new MessageDaoUtils(getActivity());
        userId = PreferenceUtil.getInstances(getActivity()).getPreferenceString("userId");


        messageList.addAll(messageDaoUtils.queryTopMessageByUserId(userId));
        getNotSendMessagesByUserId();
    }

    private void initView() {
        messageAdapter = new MessageAdapter(messageList);
        binding.recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

    }

    private void addListener() {
    }

    /**
     * 获取未获取消息列表
     */
    private void getNotSendMessagesByUserId() {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        RequestManager.getInstance(getActivity()).executeRequest(HttpUrls.GET_NOT_SEND_MESSAGES_BY_USER_ID, params, new AppCallBack<ApiResponse<List<Message>>>() {

            @Override
            public void next(ApiResponse<List<Message>> response) {
                if (response.isSuccess()) {
                    messageDaoUtils.insertMultMessage(response.getData());
                    messageList.clear();
                    messageList.addAll(messageDaoUtils.queryTopMessageByUserId(userId));
                    messageAdapter.notifyDataSetChanged();
                    for (int i = 0; i < response.getData().size(); i++) {
                        ((MainActivity) getActivity()).executeRequest(SendProtocol.MSG_ISSEND_MESSAGE,
                                response.getData().get(i).getMessageId());
                    }
                } else {
                    CommonUtil.showToast(response.getMessage(), getActivity());
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

    private void updateView() {

    }
}
