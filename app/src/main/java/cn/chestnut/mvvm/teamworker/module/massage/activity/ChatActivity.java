package cn.chestnut.mvvm.teamworker.module.massage.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
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
import cn.chestnut.mvvm.teamworker.db.UserDao;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.main.common.MyApplication;
import cn.chestnut.mvvm.teamworker.model.Chat;
import cn.chestnut.mvvm.teamworker.model.ChatMessage;
import cn.chestnut.mvvm.teamworker.model.User;
import cn.chestnut.mvvm.teamworker.module.massage.MessageDaoUtils;
import cn.chestnut.mvvm.teamworker.module.massage.adapter.ChatAdapter;
import cn.chestnut.mvvm.teamworker.socket.ReceiverProtocol;
import cn.chestnut.mvvm.teamworker.utils.EmojiUtil;
import cn.chestnut.mvvm.teamworker.utils.EntityUtil;
import cn.chestnut.mvvm.teamworker.utils.Log;
import cn.chestnut.mvvm.teamworker.utils.PreferenceUtil;
import cn.chestnut.mvvm.teamworker.utils.StringUtil;

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

    private Map<String, User> userMap;

    private String userId;

    private BroadcastReceiver receiver;

    /*本地数据操作异步工具类*/
    private AsyncSession asyncSessionMessage;
    private AsyncSession asyncSession;

    private boolean isMore = true;

    private int pageNum = 1;
    private int pageSize = 20;

    private long count = 0;

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
        asyncSession = MessageDaoUtils.getDaoSession().startAsyncSession();
        asyncSessionMessage = MessageDaoUtils.getDaoSession().startAsyncSession();
        userId = PreferenceUtil.getInstances(this).getPreferenceString("userId");
        messageList = new ArrayList<>();
        userMap = new HashMap<>();

        chatAdapter = new ChatAdapter(messageList, userId, userMap);

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
//                    messageVo.setMessageUser(messageDaoUtils.queryMessageUserByUserId(newMessage.getSenderId()));
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
        updateUerInfo();
        count = QueryBuilder.internalCreate(MessageDaoUtils.getDaoSession().getDao(ChatMessage.class))
                .where(ChatMessageDao.Properties.ChatId.eq(chat.getChatId())).buildCount().count();
        getMessageFromLocal(true);
    }

    private void getMessageFromLocal(final boolean isRefresh) {
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
        asyncSessionMessage.queryList(QueryBuilder.internalCreate(MessageDaoUtils.getDaoSession().getDao(ChatMessage.class))
                .where(ChatMessageDao.Properties.ChatId.eq(chat.getChatId()))
                .orderAsc(ChatMessageDao.Properties.SendTime)
                .offset((int) (count - pageNum * pageSize))
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
                    pageNum++;
                    scrollToBottom();
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
            count++;
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
        count += currentList.size();
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
                Log.d("------->isSlideToBottom:" + recyclerView.canScrollVertically(-1));
                if (recyclerView.canScrollVertically(-1)) {
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
        chatMessage.setUser(userMap.get(userId));
        chatMessage.setDone(false);
        messageList.add(chatMessage);
        chatAdapter.notifyItemChanged(messageList.size() - 1);
        scrollToBottom();
        asyncSessionMessage.insert(chatMessage);
        try {
            content = EmojiUtil.emojiConvert(content);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Map<String, Object> param = new HashMap<>(2);
        param.put("chatId", chat.getChatId());
        param.put("message", content);
        RequestManager.getInstance(this).executeRequest(HttpUrls.SEND_CHAT_MESSAGE, param, new AppCallBack<ApiResponse<Object>>() {
            @Override
            public void next(ApiResponse<Object> response) {
                if (response.isSuccess()) {
                    chatMessage.setDone(true);
                    asyncSessionMessage.insertOrReplace(chatMessage);
                    chatAdapter.notifyItemChanged(messageList.size() - 1);
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

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                scrollToBottom();
            }
        }
    };


    /**
     * 更新用户信息
     */
    private void updateUerInfo() {
        final List<String> userList = gson.fromJson(chat.getUserList(), new TypeToken<List<String>>() {
        }.getType());
        asyncSession.setListenerMainThread(new AsyncOperationListener() {

            @Override
            public void onAsyncOperationCompleted(AsyncOperation operation) {
                if (operation.isFailed()) {
                    Log.d("获取数据异常");
                    //从服务器创建并保存到本地
                    getUserInfoFromServer(userList);
                    return;
                }
                Log.d("operation.getType()= " + operation.getType());
                if (operation.getType() == AsyncOperation.OperationType.QueryList) {
                    Object obj = operation.getResult();
                    Log.d("获取数据 obj = " + obj);
                    updateUserInfos(obj, userList);
                }
            }
        });
        asyncSession.queryList(QueryBuilder.internalCreate(MessageDaoUtils.getDaoSession().getDao(User.class))
                .where(UserDao.Properties.UserId.in(userList))
                .build());
    }

    private void updateUserInfos(Object obj, List<String> userList) {
        if (obj != null && obj instanceof List) {
            List<User> data = (List<User>) obj;
            if (data.isEmpty()) {
                getUserInfoFromServer(userList);
            } else {
                for (User user : data) {
                    userMap.put(user.getUserId(), user);
                    userList.remove(user.getUserId());
                }
                chatAdapter.notifyDataSetChanged();
                getUserInfoFromServer(userList);
            }
        } else {
            getUserInfoFromServer(userList);
        }
    }

    private void getUserInfoFromLocal(User user) {
        userMap.put(user.getUserId(), user);
        chatAdapter.notifyDataSetChanged();
    }

    private void getUserInfoFromServer(String userId) {
        List<String> userList = new ArrayList<>();
        userList.add(userId);
        getUserInfoFromServer(userList);
    }

    private void getUserInfoFromServer(List<String> userList) {
        Map<String, Object> param = new HashMap<>(1);
        param.put("userList", gson.toJson(userList));
        RequestManager.getInstance(this).executeRequest(HttpUrls.GET_USER_LIST_INFO, param, new AppCallBack<ApiResponse<List<User>>>() {
            @Override
            public void next(ApiResponse<List<User>> response) {
                if (response.isSuccess()) {
                    for (User user : response.getData()) {
                        userMap.put(user.getUserId(), user);
                        chatAdapter.notifyDataSetChanged();
                    }
                    asyncSession.insert(response.getData());
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
