package cn.chestnut.mvvm.teamworker.module.massage.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

import org.greenrobot.greendao.async.AsyncOperation;
import org.greenrobot.greendao.async.AsyncOperationListener;
import org.greenrobot.greendao.async.AsyncSession;
import org.greenrobot.greendao.query.QueryBuilder;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.chestnut.mvvm.teamworker.Constant;
import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityChatBinding;
import cn.chestnut.mvvm.teamworker.db.ChatMessageDao;
import cn.chestnut.mvvm.teamworker.db.UserInfoDao;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.main.common.MyApplication;
import cn.chestnut.mvvm.teamworker.model.Chat;
import cn.chestnut.mvvm.teamworker.model.ChatMessage;
import cn.chestnut.mvvm.teamworker.model.UserInfo;
import cn.chestnut.mvvm.teamworker.module.massage.MessageDaoUtils;
import cn.chestnut.mvvm.teamworker.module.massage.adapter.ChatAdapter;
import cn.chestnut.mvvm.teamworker.socket.ReceiverProtocol;
import cn.chestnut.mvvm.teamworker.utils.EmojiUtil;
import cn.chestnut.mvvm.teamworker.utils.EntityUtil;
import cn.chestnut.mvvm.teamworker.utils.Log;
import cn.chestnut.mvvm.teamworker.utils.PreferenceUtil;
import cn.chestnut.mvvm.teamworker.utils.StringUtil;
import cn.chestnut.mvvm.teamworker.utils.sqlite.DaoManager;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/31 10:22:42
 * Description：聊天
 * Email: xiaoting233zhang@126.com
 */

public class ChatActivity extends BaseActivity {

    private ActivityChatBinding binding;

    public static final String BUNDLE_CHAT = "bundle_chat";//更新信息

    private Chat chat;

    private ChatAdapter chatAdapter;
    private List<ChatMessage> messageList;

    private Map<String, UserInfo> userMap;

    private String userId;

    private BroadcastReceiver receiver;

    /*本地数据操作异步工具类*/
    private AsyncSession asyncSessionMessage;
    private AsyncSession asyncSession;

    private boolean isMore = true;

    private int pageNum = 1;
    private int pageSize = 20;

    private volatile long count = 0;

    @Override
    protected void setBaseTitle(TextView titleView) {
        chat = (Chat) getIntent().getSerializableExtra(BUNDLE_CHAT);
        titleView.setText(chat.getChatName());
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

    protected void initData() {
        asyncSession = DaoManager.getDaoSession().startAsyncSession();
        asyncSessionMessage = DaoManager.getDaoSession().startAsyncSession();
        userId = PreferenceUtil.getInstances(this).getPreferenceString("userId");
        messageList = new ArrayList<>();
        userMap = new HashMap<>();

        chatAdapter = new ChatAdapter(messageList, userId);

//        receiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                Message newMessage = (Message) intent.getSerializableExtra("newMessage");
//                Log.d(("ChatPersonalActivity收到一条新消息") + newMessage.toString());
//                try {
//                    newMessage.setContent(EmojiUtil.emojiRecovery(newMessage.getContent()));
//                    newMessage.setChatName(EmojiUtil.emojiRecovery(newMessage.getChatName()));
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//                if (newMessage.getChatId().equals(chatId) && !newMessage.getSenderId().equals(userId)) {
//                    MessageVo messageVo = new MessageVo();
//                    messageVo.setMessage(newMessage);
//                    messageVo.setMessageUser(DaoManager.queryMessageUserByUserId(newMessage.getSenderId()));
//                    messageVoList.add(messageVo);
//                    chatAdapter.notifyDataSetChanged();
//                    executeRequest(SendProtocol.MSG_ISREAD_MESSAGE, newMessage.getMessageId());
//                }
//            }
//        };

//        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(Constant.ActionConstant.ACTION_GET_NEW_MESSAGE));
    }

    protected void initView() {
        binding.rcRecord.setAdapter(chatAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.rcRecord.setLayoutManager(linearLayoutManager);
//        updateUerInfo();
        count = QueryBuilder.internalCreate(DaoManager.getDaoSession().getDao(ChatMessage.class))
                .where(ChatMessageDao.Properties.ChatId.eq(chat.getChatId())).buildCount().count();
        getMessageFromLocal(true);
    }

    //防止多次加载数据
    volatile boolean loading = false;

    private void getMessageFromLocal(final boolean isRefresh) {
        if (!isRefresh) {
            if (loading) return;
            loading = true;
        }
        if (isRefresh) {
            pageNum = 1;
            isMore = true;
        } else if (!isMore) {
            return;
        }
        asyncSessionMessage.setListenerMainThread(new AsyncOperationListener() {

            @Override
            public void onAsyncOperationCompleted(AsyncOperation operation) {
                if (operation.isFailed()) {
                    Log.d("ChatActivity asyncSessionMessage 获取数据异常");
                    loading = false;
                    return;
                }
                Log.d("ChatActivity asyncSessionMessage operation.getType()= " + operation.getType());
                if (operation.getType() == AsyncOperation.OperationType.QueryList) {
                    Object obj = operation.getResult();
                    Log.d("ChatActivity asyncSessionMessage 获取数据 obj = " + obj);

                    handleData(obj, isRefresh);
                    loading = false;
                }
            }
        });
        int starNum = (int) (count - pageNum * pageSize);
        Log.d("(starNum) " + (starNum));
        if (starNum < 0) {
            Log.d("(pageSize + starNum) " + (pageSize + starNum));
            asyncSessionMessage.queryList(QueryBuilder.internalCreate(DaoManager.getDaoSession().getDao(ChatMessage.class))
                    .where(ChatMessageDao.Properties.ChatId.eq(chat.getChatId()))
                    .orderAsc(ChatMessageDao.Properties.SendTime)
                    .offset(0)
                    .limit((pageSize + starNum))
                    .build());
            return;
        }
        asyncSessionMessage.queryList(QueryBuilder.internalCreate(DaoManager.getDaoSession().getDao(ChatMessage.class))
                .where(ChatMessageDao.Properties.ChatId.eq(chat.getChatId()))
                .orderAsc(ChatMessageDao.Properties.SendTime)
                .offset(starNum)
                .limit(pageSize)
                .build());
    }

    private void handleData(Object obj, boolean isRefresh) {
        if (obj != null && obj instanceof List) {
            Log.d("obtainDataFromLocalDatabase: " + obj);
            List<ChatMessage> data = (List<ChatMessage>) obj;
            if (!data.isEmpty()) {
                int itemCount = data.size();
                if (isRefresh) {
                    messageList.clear();
                    messageList.addAll(data);
                    chatAdapter.notifyDataSetChanged();
                    scrollToBottom();
                    pageNum++;
                } else {
                    messageList.addAll(0, data);
                    chatAdapter.notifyItemRangeInserted(0, itemCount);
                    chatAdapter.notifyItemRangeChanged(0, itemCount);
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

    @Override
    public void onSessionMessage(int msgId, Object object) {
        switch (msgId) {
            case ReceiverProtocol.MSG_SEND_CHAT_MESSAGE:
                handleChatMessage(object);
            case ReceiverProtocol.MSG_SEND_CHAT_MANY_MESSAGE:
                handleManyChatMessage(object);
            default:
                break;
        }
    }

    private void handleChatMessage(Object object) {
        ChatMessage newMessage = gson.fromJson(
                object.toString(), new TypeToken<ChatMessage>() {
                }.getType());
        if (chat.getChatId().equals(newMessage.getChatId())) {
            try {
                newMessage.setMessage(EmojiUtil.emojiRecovery(newMessage.getMessage()));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
//            count++;
            messageList.add(newMessage);
            chatAdapter.notifyItemInserted(messageList.size());
            chatAdapter.notifyDataSetChanged();
        }
    }

    private void handleManyChatMessage(Object object) {
        List<ChatMessage> newMessageList = gson.fromJson(
                object.toString(), new TypeToken<List<ChatMessage>>() {
                }.getType());
        List<ChatMessage> currentList = new ArrayList<>();
        for (ChatMessage chatMessage : newMessageList) {
            if (chat.getChatId().equals(chatMessage.getChatId())) {
                try {
                    chatMessage.setMessage(EmojiUtil.emojiRecovery(chatMessage.getMessage()));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                currentList.add(chatMessage);
            }
        }
//        count += currentList.size();
        messageList.addAll(currentList);
        chatAdapter.notifyDataSetChanged();
    }

    protected void addListener() {
        binding.tvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        binding.etInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.sendEmptyMessageDelayed(0, 250);
            }
        });
        binding.rcRecord.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d("------->isSlideToTop:" + recyclerView.canScrollVertically(-1));
//                Log.d("------->isSlideToBottom:" + recyclerView.canScrollVertically(1));
                if (!recyclerView.canScrollVertically(-1)) {
                    getMessageFromLocal(false);
                }
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
        binding.rcRecord.scrollToPosition(messageList.size() - 1);
    }

    private void sendMessage() {
        String content = binding.etInput.getText().toString();
        if (StringUtil.isEmpty(content)) {
            showToast("不能为空");
            return;
        }
        binding.etInput.setText("");

        final ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChatMessageId(EntityUtil.getIdByTimeStampAndRandom());
        chatMessage.setChatId(chat.getChatId());
        chatMessage.setMessage(content);
        chatMessage.setSenderId(userId);
        chatMessage.setSendTime(MyApplication.currentServerTimeMillis());
        //个人信息
        UserInfo userInfo = MyApplication.userInfoMap.get(userId);
        chatMessage.setNickname(userInfo.getNickname());
        chatMessage.setAvatar(userInfo.getAvatar());
//        chatMessage.setUser(userMap.get(userId));
        chatMessage.setDone(false);
        messageList.add(chatMessage);
        chatAdapter.notifyItemChanged(messageList.size() - 1);
        scrollToBottom();
        chat.setLastMessage(content);
        chat.setUpdateTime(chatMessage.getSendTime());
        asyncSessionMessage.insertOrReplace(chat);
        asyncSessionMessage.insert(chatMessage);
//        count++;
        try {
            content = EmojiUtil.emojiConvert(content);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        final int updatePosition = messageList.size() - 1;
        Map<String, Object> param = new HashMap<>(2);
        param.put("chatId", chat.getChatId());
        param.put("message", content);
        RequestManager.getInstance(this).executeRequest(HttpUrls.SEND_CHAT_MESSAGE, param, new AppCallBack<ApiResponse<Object>>() {
            @Override
            public void next(ApiResponse<Object> response) {
                if (response.isSuccess()) {
                    chatMessage.setDone(true);
                    asyncSessionMessage.insertOrReplace(chatMessage);
                    chatAdapter.notifyItemChanged(updatePosition);
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
        Log.d("sendMessage 更新信息界面");
        Intent intent = new Intent(Constant.ActionConstant.UPDATE_MESSAGE_CHAT_LAYOUT);
        LocalBroadcastManager.getInstance(MyApplication.getInstance()).sendBroadcast(intent);

    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                scrollToBottom();
            }
        }
    };


//    /**
//     * 更新用户信息
//     */
//    private void updateUerInfo() {
//        final List<String> userList = gson.fromJson(chat.getUserList(), new TypeToken<List<String>>() {
//        }.getType());
//        //从全局缓存中查找
//        final List<String> notUserList = new ArrayList<>();
//        for (String userId : userList) {
//            UserInfo userInfo = MyApplication.userInfoMap.get(userId);
//            if (userInfo == null) {
//                boolean isUpdate = PreferenceUtil.getInstances(MyApplication.getInstance()).
//                        getPreferenceBoolean(Constant.PreferenceKey.USER_INFO_WAITING + userId);
//                if (!isUpdate) {
//                    PreferenceUtil.getInstances(MyApplication.getInstance()).
//                            savePreferenceBoolean(Constant.PreferenceKey.USER_INFO_WAITING + userId, true);
//                    Log.d("更新用户信息 userId = " + userId);
//                    notUserList.add(userId);
//                }
//            } else {
//                userMap.put(userId, userInfo);
//            }
//        }
//
//        asyncSession.setListenerMainThread(new AsyncOperationListener() {
//
//            @Override
//            public void onAsyncOperationCompleted(AsyncOperation operation) {
//                if (operation.isFailed()) {
//                    Log.d("ChatActivity asyncSession 获取数据异常");
//                    //从服务器创建并保存到本地
//                    getUserInfoFromServer(notUserList);
//                    return;
//                }
//                Log.d("ChatActivity asyncSession operation.getType()= " + operation.getType());
//                if (operation.getType() == AsyncOperation.OperationType.QueryList) {
//                    Object obj = operation.getResult();
//                    Log.d("ChatActivity asyncSession 获取数据 obj = " + obj);
//                    updateUserInfos(obj, notUserList);
//                }
//            }
//        });
//        asyncSession.queryList(QueryBuilder.internalCreate(DaoManager.getDaoSession().getDao(UserInfo.class))
//                .where(UserInfoDao.Properties.UserId.in(notUserList))
//                .build());
//    }
//
//    private void updateUserInfos(Object obj, List<String> userList) {
//        if (obj != null && obj instanceof List) {
//            List<UserInfo> data = (List<UserInfo>) obj;
//            if (data.isEmpty()) {
//                getUserInfoFromServer(userList);
//            } else {
//                for (UserInfo userInfo : data) {
//                    PreferenceUtil.getInstances(MyApplication.getInstance()).deleteKey(Constant.PreferenceKey.USER_INFO_WAITING + userInfo.getUserId());
//                    Log.d("更新用户信息完成 userId = " + userId);
//                    MyApplication.userInfoMap.put(userInfo.getUserId(), userInfo);
//                    userMap.put(userInfo.getUserId(), userInfo);
//                    userList.remove(userInfo.getUserId());
//                }
//                chatAdapter.notifyDataSetChanged();
//                getUserInfoFromServer(userList);
//            }
//        } else {
//            getUserInfoFromServer(userList);
//        }
//    }
//
//    private void getUserInfoFromServer(final List<String> userList) {
//        if (userList.isEmpty()) return;
//        Map<String, Object> param = new HashMap<>(1);
//        param.put("userList", gson.toJson(userList));
//        RequestManager.getInstance(this).executeRequest(HttpUrls.GET_USER_LIST_INFO, param, new AppCallBack<ApiResponse<List<UserInfo>>>() {
//            @Override
//            public void next(ApiResponse<List<UserInfo>> response) {
//                if (response.isSuccess()) {
//                    for (UserInfo userInfo : response.getData()) {
//                        PreferenceUtil.getInstances(MyApplication.getInstance()).deleteKey(Constant.PreferenceKey.USER_INFO_WAITING + userInfo.getUserId());
//                        Log.d("更新用户信息完成 userId = " + userId);
//                        MyApplication.userInfoMap.put(userInfo.getUserId(), userInfo);
//                        userMap.put(userInfo.getUserId(), userInfo);
//                        chatAdapter.notifyDataSetChanged();
//                    }
//                    asyncSession.insertOrReplace(response.getData());
//                } else {
//                    for (String userId : userList) {
//                        PreferenceUtil.getInstances(MyApplication.getInstance()).deleteKey(Constant.PreferenceKey.USER_INFO_WAITING + userId);
//                        Log.d("更新用户信息完成 userId = " + userId);
//                    }
//                    showToast(response.getMessage());
//                }
//            }
//
//            @Override
//            public void error(Throwable error) {
//                for (String userId : userList) {
//                    PreferenceUtil.getInstances(MyApplication.getInstance()).deleteKey(Constant.PreferenceKey.USER_INFO_WAITING + userId);
//                    Log.d("更新用户信息完成 userId = " + userId);
//                }
//            }
//
//            @Override
//            public void complete() {
//
//            }
//        });
//    }

    public void hideSoftInput(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        asyncSessionMessage = null;
        asyncSession = null;
    }
}
