package cn.chestnut.mvvm.teamworker.module.massage.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cn.chestnut.mvvm.teamworker.BR;
import cn.chestnut.mvvm.teamworker.Constant;
import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.FragmentMessageBinding;
import cn.chestnut.mvvm.teamworker.databinding.PopupAddActionBinding;
import cn.chestnut.mvvm.teamworker.db.ChatDao;
import cn.chestnut.mvvm.teamworker.db.ChatMessageDao;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.activity.MainActivity;
import cn.chestnut.mvvm.teamworker.main.adapter.BaseListViewAdapter;
import cn.chestnut.mvvm.teamworker.main.common.BaseFragment;
import cn.chestnut.mvvm.teamworker.model.Chat;
import cn.chestnut.mvvm.teamworker.model.ChatMessage;
import cn.chestnut.mvvm.teamworker.module.massage.MessageDaoUtils;
import cn.chestnut.mvvm.teamworker.module.massage.activity.ChatActivity;
import cn.chestnut.mvvm.teamworker.module.massage.adapter.MessageAdapter;
import cn.chestnut.mvvm.teamworker.model.Message;
import cn.chestnut.mvvm.teamworker.model.MessageUser;
import cn.chestnut.mvvm.teamworker.model.MessageVo;
import cn.chestnut.mvvm.teamworker.module.user.SearchFriendActivity;
import cn.chestnut.mvvm.teamworker.socket.SendProtocol;
import cn.chestnut.mvvm.teamworker.utils.CommonUtil;
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
    private MessageAdapter chatAdapter;
    private List<Chat> chatList;
    private String userId;
    private MessageDaoUtils messageDaoUtils;
    private BroadcastReceiver receiver;

    private static final long MILLISECOND_OF_TWO_HOUR = 60 * 60 * 1000;

    /*本地数据操作异步工具类*/
    private AsyncSession asyncSession;

    private boolean isMore = true;

    private int pageNum = 1;
    private int pageSize = 20;

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
    public void setButton(TextView edit, final ImageView add, ImageView search) {
        super.setButton(edit, add, search);
        add.setVisibility(View.VISIBLE);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActionPopup(add);
            }
        });
        search.setVisibility(View.VISIBLE);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void showActionPopup(View add) {
        List<AddAction> addActionList = new ArrayList<>(3);
        AddAction scanQR = new AddAction(getResources().getDrawable(R.mipmap.icon_qr_scan), "扫描二维码");
        AddAction addChat = new AddAction(getResources().getDrawable(R.mipmap.icon_add_chat), "发起聊天");
        AddAction addFriend = new AddAction(getResources().getDrawable(R.mipmap.icon_add_friend), "添加朋友");
        addActionList.add(scanQR);
        addActionList.add(addChat);
        addActionList.add(addFriend);

        CommonUtil.setBackgroundAlpha(0.5f, getActivity());

        final PopupAddActionBinding popupBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.popup_add_action, null, false);
        BaseListViewAdapter adapter = new BaseListViewAdapter(R.layout.item_add_action, BR.addAction, addActionList);
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

                } else if (position == 1) {

                } else if (position == 2) {
                    startActivity(new Intent(getActivity(), SearchFriendActivity.class));
                }
                popupWindow.dismiss();
            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                CommonUtil.setBackgroundAlpha(1, getActivity());
            }
        });
    }

    private void initData() {
        asyncSession = MessageDaoUtils.getDaoSession().startAsyncSession();
//        receiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                Message newMessage = (Message) intent.getSerializableExtra("newMessage");
//                Log.d("MessageFragment收到一条新消息" + newMessage.toString());
//                if (!newMessage.getSenderId().equals(userId)) {
//                    try {
//                        newMessage.setContent(EmojiUtil.emojiRecovery(newMessage.getContent()));
//                        newMessage.setChatName(EmojiUtil.emojiRecovery(newMessage.getChatName()));
//                    } catch (UnsupportedEncodingException e) {
//                        e.printStackTrace();
//                    }
//                    messageDaoUtils.insertMessage(newMessage);
//                }
//                boolean listHasSender = false;//消息列表中是否已经有该发送者item
//                for (int i = 0; i < chatList.size(); i++) {
//                    if (chatList.get(i).getMessage().getChatId().equals(newMessage.getChatId())) {
//                        chatList.remove(i);
//                        MessageVo messageVo = new MessageVo();
//                        messageVo.setMessage(newMessage);
//                        chatList.add(0, messageVo);
//                        messageAdapter.notifyDataSetChanged();
//                        listHasSender = true;
//                        break;
//                    }
//                }
//                if (!listHasSender) {
//                    MessageVo messageVo = new MessageVo();
//                    messageVo.setMessage(newMessage);
//                    chatList.add(0, messageVo);
//                    messageAdapter.notifyItemInserted(0);
//                    messageAdapter.notifyDataSetChanged();
//                }
//            }
//        };
//        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, new IntentFilter(Constant.ActionConstant.ACTION_GET_NEW_MESSAGE));

//        messageDaoUtils = new MessageDaoUtils();
        userId = PreferenceUtil.getInstances(getActivity()).getPreferenceString("userId");

        chatList = new ArrayList<>();
//        getNotSendMessagesByUserId();
    }

    private void initView() {
        chatAdapter = new MessageAdapter(chatList);
        binding.recyclerView.setAdapter(chatAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        getChatFromLocal(true);
    }

    private void getChatFromLocal(final boolean isRefresh) {
        if (isRefresh) {
            pageNum = 1;
            isMore = true;
        } else if (!isMore) {
            return;
        }
        asyncSession.setListenerMainThread(new AsyncOperationListener() {

            @Override
            public void onAsyncOperationCompleted(AsyncOperation operation) {
                if (operation.isFailed()) {
                    Log.d("获取数据异常");
                    return;
                }
                Log.d("operation.getType()= " + operation.getType());
                if (operation.getType() == AsyncOperation.OperationType.QueryList) {
                    Object obj = operation.getResult();
                    Log.d("获取数据 obj = " + obj);

                    handleData(obj, isRefresh);
                }
            }
        });
        asyncSession.queryList(QueryBuilder.internalCreate(MessageDaoUtils.getDaoSession().getDao(Chat.class))
                .orderDesc(ChatDao.Properties.UpdateTime)
                .offset((pageNum - 1) * pageSize)
                .limit(pageSize)
                .build());
    }

    private void handleData(Object obj, boolean isRefresh) {
        if (obj != null && obj instanceof List) {
            Log.d("obtainDataFromLocalDatabase: " + obj);
            List<Chat> data = (List<Chat>) obj;
            if (!data.isEmpty()) {
                int itemCount = data.size();
                if (isRefresh) {
                    chatList.clear();
                    chatList.addAll(data);
                    chatAdapter.notifyDataSetChanged();
                    pageNum++;
                } else {
                    chatList.addAll(data);
                    chatAdapter.notifyItemRangeInserted(chatList.size() - itemCount, itemCount);
                    chatAdapter.notifyItemRangeChanged(chatList.size() - itemCount, itemCount);
                    pageNum++;
                }
                if (itemCount < pageSize) {//没有更多数据了
                    isMore = false;
                }
            } else {
                isMore = false;
            }
        } else {
            isMore = false;
        }
    }

    private void addListener() {
//        messageAdapter.setOnUpdateMessageLayoutListener(new MessageAdapter.OnUpdateMessageLayoutListener() {
//            @Override
//            public void onUpdate(final MessageAdapter messageAdapter, MessageVo obj, final boolean isUpdate) {
//                String chatUserId = obj.getMessage().getSenderId();
//                if (chatUserId.equals(userId)) {
//                    chatUserId = obj.getMessage().getReceiverId();
//                }
//                Map<String, Object> params = new HashMap<>();
//                params.put("userId", chatUserId);
//                RequestManager.getInstance(getActivity()).executeRequest(HttpUrls.GET_USER_INFO, params, new AppCallBack<ApiResponse<MessageUser>>() {
//
//                    @Override
//                    public void next(ApiResponse<MessageUser> response) {
//                        if (response.isSuccess()) {
//                            try {
//                                response.getData().setNickname(EmojiUtil.emojiRecovery(response.getData().getNickname()));
//                            } catch (UnsupportedEncodingException e) {
//                                e.printStackTrace();
//                            }
//                            if (isUpdate) {
//                                //本地更新数据
//                                messageDaoUtils.updateMessageUser(response.getData());
//                            } else {
//                                //本地插入数据
//                                messageDaoUtils.insertMessageUser(response.getData());
//                            }
//                            //保存下一次需要更新的时间
//                            PreferenceUtil.getInstances(getActivity()).savePreferenceLong("updateTime", MILLISECOND_OF_TWO_HOUR + System.currentTimeMillis());
//                            messageAdapter.notifyDataSetChanged();
//                        } else {
//                            showToast(response.getMessage());
//                        }
//                    }
//
//                    @Override
//                    public void error(Throwable error) {
//                    }
//
//                    @Override
//                    public void complete() {
//
//                    }
//
//                });
//            }
//
//        });
        chatAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Chat chat = chatList.get(position);
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra(ChatActivity.BUNDLE_CHAT, chat);
                startActivity(intent);
            }
        });
        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d("------->isSlideToBottom:" + recyclerView.canScrollVertically(1));
                if (recyclerView.canScrollVertically(1)) {
                    getChatFromLocal(false);
                }
            }
        });
    }

    /**
     * 获取未获取消息列表
     */
    private void getNotSendMessagesByUserId() {
//        Map<String, Object> params = new HashMap<>();
//        params.put("userId", userId);
//        RequestManager.getInstance(getActivity()).executeRequest(HttpUrls.GET_NOT_SEND_MESSAGES_BY_USER_ID, params, new AppCallBack<ApiResponse<List<Message>>>() {
//
//            @Override
//            public void next(ApiResponse<List<Message>> response) {
//                if (response.isSuccess()) {
//                    for (int i = 0; i < response.getData().size(); i++) {
//                        try {
//                            response.getData().get(i).setContent(EmojiUtil.emojiConvert(response.getData().get(i).getContent()));
//                            response.getData().get(i).setChatName(EmojiUtil.emojiConvert(response.getData().get(i).getChatName()));
//                        } catch (UnsupportedEncodingException e) {
//                            e.printStackTrace();
//                        }
//                        ((MainActivity) getActivity()).executeRequest(SendProtocol.MSG_ISSEND_MESSAGE,
//                                response.getData().get(i).getMessageId());
//                    }
//                    messageDaoUtils.insertMultMessage(response.getData());
//                    chatList.clear();
//                    chatList.addAll(messageDaoUtils.transferMessageVo(messageDaoUtils.queryTopMessageByUserId(userId)));
//                    chatAdapter.notifyDataSetChanged();
//                } else {
//                    showToast(response.getMessage());
//                }
//            }
//
//            @Override
//            public void error(Throwable error) {
//            }
//
//            @Override
//            public void complete() {
//
//            }
//
//        });

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        asyncSession = null;
    }
}
