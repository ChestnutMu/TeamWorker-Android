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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
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
import cn.chestnut.mvvm.teamworker.module.massage.activity.ChatActivity;
import cn.chestnut.mvvm.teamworker.module.massage.activity.SendNotificationActivity;
import cn.chestnut.mvvm.teamworker.module.massage.adapter.MessageAdapter;
import cn.chestnut.mvvm.teamworker.module.massage.bean.Message;
import cn.chestnut.mvvm.teamworker.module.massage.bean.MessageUser;
import cn.chestnut.mvvm.teamworker.module.massage.bean.MessageVo;
import cn.chestnut.mvvm.teamworker.socket.SendProtocol;
import cn.chestnut.mvvm.teamworker.utils.EmojiUtil;
import cn.chestnut.mvvm.teamworker.utils.Log;
import cn.chestnut.mvvm.teamworker.utils.PreferenceUtil;
import cn.chestnut.mvvm.teamworker.utils.StringUtil;

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
    private LinkedList<MessageVo> messageList;
    private String userId;
    private MessageDaoUtils messageDaoUtils;

    private static final long MILLISECOND_OF_TWO_HOUR = 60 * 60 * 1000;

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
                if (!newMessage.getSenderId().equals(userId)) {
                    try {
                        newMessage.setTitle(EmojiUtil.emojiRecovery(newMessage.getTitle()));
                        newMessage.setContent(EmojiUtil.emojiRecovery(newMessage.getContent()));
                        newMessage.setChatName(EmojiUtil.emojiRecovery(newMessage.getChatName()));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    messageDaoUtils.insertMessage(newMessage);
                }
                boolean listHasSender = false;//消息列表中是否已经有该发送者item
                for (int i = 0; i < messageList.size(); i++) {
                    if (messageList.get(i).getMessage().getChatId().equals(newMessage.getChatId())) {
                        messageList.remove(i);
                        MessageVo messageVo = new MessageVo();
                        messageVo.setMessage(newMessage);
                        messageList.add(0, messageVo);
                        messageAdapter.notifyDataSetChanged();
                        listHasSender = true;
                        break;
                    }
                }
                if (!listHasSender) {
                    MessageVo messageVo = new MessageVo();
                    messageVo.setMessage(newMessage);
                    messageList.add(0, messageVo);
                    messageAdapter.notifyItemInserted(0);
                    messageAdapter.notifyDataSetChanged();
                }
            }
        };

        messageDaoUtils = new MessageDaoUtils();
        userId = PreferenceUtil.getInstances(getActivity()).getPreferenceString("userId");

        messageList = new LinkedList<>();
        messageList.addAll(messageDaoUtils.transferMessageVo(messageDaoUtils.queryTopMessageByUserId(userId)));
        getNotSendMessagesByUserId();
    }

    private void initView() {
        messageAdapter = new MessageAdapter(messageList, getActivity());
        binding.recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
    }

    private void addListener() {
        messageAdapter.setOnUpdateMessageLayoutListener(new MessageAdapter.OnUpdateMessageLayoutListener() {
            @Override
            public void onUpdate(final MessageAdapter messageAdapter, MessageVo obj, final boolean isUpdate) {
                String chatUserId = obj.getMessage().getSenderId();
                if (chatUserId.equals(userId)) {
                    chatUserId = obj.getMessage().getReceiverId();
                }
                Map<String, Object> params = new HashMap<>();
                params.put("userId", chatUserId);
                RequestManager.getInstance(getActivity()).executeRequest(HttpUrls.GET_USER_INFO, params, new AppCallBack<ApiResponse<MessageUser>>() {

                    @Override
                    public void next(ApiResponse<MessageUser> response) {
                        if (response.isSuccess()) {
                            try {
                                response.getData().setNickname(EmojiUtil.emojiRecovery(response.getData().getNickname()));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            if (isUpdate) {
                                //本地更新数据
                                messageDaoUtils.updateMessageUser(response.getData());
                            } else {
                                //本地插入数据
                                messageDaoUtils.insertMessageUser(response.getData());
                            }
                            //保存下一次需要更新的时间
                            PreferenceUtil.getInstances(getActivity()).savePreferenceLong("updateTime", MILLISECOND_OF_TWO_HOUR + System.currentTimeMillis());
                            messageAdapter.notifyDataSetChanged();
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

        });
        messageAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("chatId", messageList.get(position).getMessage().getChatId());
                String chatName = messageList.get(position).getMessage().getChatName();
                if (StringUtil.isStringNotNull(chatName)) {
                    intent.putExtra("chatName", chatName);
                } else {
                    intent.putExtra("title", messageList.get(position).getMessage().getChatName());
                }
                getActivity().startActivity(intent);
            }
        });
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
                    for (int i = 0; i < response.getData().size(); i++) {
                        try {
                            response.getData().get(i).setTitle(EmojiUtil.emojiConvert(response.getData().get(i).getTitle()));
                            response.getData().get(i).setContent(EmojiUtil.emojiConvert(response.getData().get(i).getContent()));
                            response.getData().get(i).setChatName(EmojiUtil.emojiConvert(response.getData().get(i).getChatName()));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        ((MainActivity) getActivity()).executeRequest(SendProtocol.MSG_ISSEND_MESSAGE,
                                response.getData().get(i).getMessageId());
                    }
                    messageDaoUtils.insertMultMessage(response.getData());
                    messageList.clear();
                    messageList.addAll(messageDaoUtils.transferMessageVo(messageDaoUtils.queryTopMessageByUserId(userId)));
                    messageAdapter.notifyDataSetChanged();
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

}
