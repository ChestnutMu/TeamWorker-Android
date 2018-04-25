package cn.chestnut.mvvm.teamworker.module.massage.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.ImageView;
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
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.main.common.MyApplication;
import cn.chestnut.mvvm.teamworker.model.Chat;
import cn.chestnut.mvvm.teamworker.model.ChatMessage;
import cn.chestnut.mvvm.teamworker.model.UserInfo;
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

    public static final String BUNDLE_CHAT = "bundle_chat";//聊天室

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

    private ImageView ivSetting;

    public static final String BROADCAST_INTENT_TYPE = "broadcast_intent_type";
    public static final String BROADCAST_INTENT_NAME = "broadcast_intent_name";
    public static final String BROADCAST_INTENT_MESSAGE = "broadcast_intent_message";
    public static final String BROADCAST_INTENT_CHAT = "broadcast_intent_chat";

    public TextView titleView;

    @Override
    protected void setBaseTitle(TextView titleView) {
        this.titleView = titleView;
        chat = (Chat) getIntent().getSerializableExtra(BUNDLE_CHAT);
        this.titleView.setText(chat.getChatName());
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_chat, viewGroup, true);
        initData();
        initView();
        addListener();
    }

    @Override
    public void setButton(TextView edit, ImageView add, ImageView search) {
        ivSetting = add;
        ivSetting.setVisibility(View.VISIBLE);
        if (chat.getChatType().equals(Constant.ChatType.TYPE_CHAT_DOUBLE)) {
            ivSetting.setImageDrawable(getResources().getDrawable(R.mipmap.icon_single_people));
        } else {
            ivSetting.setImageDrawable(getResources().getDrawable(R.mipmap.icon_multi_people));
        }
        ivSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ivSetting 点击");
                goToChatSetting();
            }
        });
    }

    private void goToChatSetting() {
        ChatSettingActivity.startActivity(this, chat);
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
    }

    protected void initView() {
        binding.rcRecord.setAdapter(chatAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.rcRecord.setLayoutManager(linearLayoutManager);
//        updateUerInfo();
        count = QueryBuilder.internalCreate(DaoManager.getDaoSession().getDao(ChatMessage.class))
                .where(ChatMessageDao.Properties.ChatId.eq(chat.getChatId())).buildCount().count();
        getMessageFromLocal(true);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("ChatActivity " + intent.getAction());
                if (Constant.ActionConstant.ACTION_UPDATE_CHAT.equals(intent.getAction())) {
                    int type = intent.getIntExtra(BROADCAST_INTENT_TYPE, 0);
                    Log.d("ChatActivity type " + type);
                    if (type == 0) {
                        //更新名字
                        String name = intent.getStringExtra(BROADCAST_INTENT_NAME);
                        titleView.setText(name);
                        ChatMessage chatMessage = (ChatMessage) intent.getSerializableExtra(BROADCAST_INTENT_MESSAGE);
                        messageList.add(chatMessage);
                        chatAdapter.notifyItemChanged(messageList.size() - 1);
                    } else if (type == 1) {
                        //更新记录
                        count = QueryBuilder.internalCreate(DaoManager.getDaoSession().getDao(ChatMessage.class))
                                .where(ChatMessageDao.Properties.ChatId.eq(chat.getChatId())).buildCount().count();
                        getMessageFromLocal(true);
                    } else if (type == 2) {
                        //更新头像
                        ChatMessage chatMessage = (ChatMessage) intent.getSerializableExtra(BROADCAST_INTENT_MESSAGE);
                        messageList.add(chatMessage);
                        chatAdapter.notifyItemChanged(messageList.size() - 1);
                    } else if (type == 3) {
                        //更新人员变动
                        ChatMessage chatMessage = (ChatMessage) intent.getSerializableExtra(BROADCAST_INTENT_MESSAGE);
                        messageList.add(chatMessage);
                        chatAdapter.notifyItemChanged(messageList.size() - 1);
                    } else if (type == 4) {
                        finish();
                    } else if (type == 5) {
                        Chat newChat = (Chat) intent.getSerializableExtra(BROADCAST_INTENT_CHAT);
                        if (!chat.getAdminId().equals(newChat.getAdminId()))
                            chat.setAdminId(newChat.getAdminId());
                        if (!chat.getUserList().equals(newChat.getUserList()))
                            chat.setUserList(newChat.getUserList());
                        if (!chat.getChatName().equals(newChat.getChatName())) {
                            chat.setChatName(newChat.getChatName());
                            titleView.setText(chat.getChatName());
                        }
                        if (chat.getChatPic() == null || !chat.getChatPic().equals(newChat.getChatPic()))
                            chat.setChatPic(newChat.getChatPic());
                        chat.setUpdateTime(newChat.getUpdateTime());
                    }
                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(Constant.ActionConstant.ACTION_UPDATE_CHAT));
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
                if (isRefresh) {
                    messageList.clear();
                    chatAdapter.notifyDataSetChanged();
                }
                isMore = false;
            }
        } else {
            if (isRefresh) {
                messageList.clear();
                chatAdapter.notifyDataSetChanged();
            }
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
        mHandler = null;
        asyncSessionMessage = null;
        asyncSession = null;
        if (receiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
            receiver = null;
        }
    }
}
