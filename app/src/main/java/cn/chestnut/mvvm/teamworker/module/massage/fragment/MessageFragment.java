package cn.chestnut.mvvm.teamworker.module.massage.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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

import com.google.gson.Gson;

import org.greenrobot.greendao.async.AsyncOperation;
import org.greenrobot.greendao.async.AsyncOperationListener;
import org.greenrobot.greendao.async.AsyncSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.chestnut.mvvm.teamworker.BR;
import cn.chestnut.mvvm.teamworker.Constant;
import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.FragmentMessageBinding;
import cn.chestnut.mvvm.teamworker.databinding.PopupAddActionBinding;
import cn.chestnut.mvvm.teamworker.db.ChatDao;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.adapter.BaseListViewAdapter;
import cn.chestnut.mvvm.teamworker.main.common.BaseFragment;
import cn.chestnut.mvvm.teamworker.main.common.MyApplication;
import cn.chestnut.mvvm.teamworker.model.Chat;
import cn.chestnut.mvvm.teamworker.model.UserInfo;
import cn.chestnut.mvvm.teamworker.module.massage.activity.ChatActivity;
import cn.chestnut.mvvm.teamworker.module.massage.activity.CreateMultiChatActivity;
import cn.chestnut.mvvm.teamworker.module.massage.adapter.MessageAdapter;
import cn.chestnut.mvvm.teamworker.module.user.SearchFriendActivity;
import cn.chestnut.mvvm.teamworker.utils.CommonUtil;
import cn.chestnut.mvvm.teamworker.utils.Log;
import cn.chestnut.mvvm.teamworker.utils.PreferenceUtil;
import cn.chestnut.mvvm.teamworker.utils.sqlite.DaoManager;

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
    private BroadcastReceiver receiver;
    private Gson gson = new Gson();

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
                    startActivity(new Intent(getActivity(), CreateMultiChatActivity.class));
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
        asyncSession = DaoManager.getDaoSession().startAsyncSession();
        userId = PreferenceUtil.getInstances(getActivity()).getPreferenceString("userId");

        chatList = new ArrayList<>();
    }

    private void initView() {
        chatAdapter = new MessageAdapter(chatList);
        binding.recyclerView.setAdapter(chatAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        getChatFromLocal(true);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("更新界面 intent.getAction() = " + intent.getAction());
                if (intent.getAction().equals(Constant.ActionConstant.UPDATE_MESSAGE_CHAT_LAYOUT)) {
                    Log.d("更新界面");
                    getChatFromLocal(true);
                }
            }
        };
        LocalBroadcastManager.getInstance(MyApplication.getInstance()).registerReceiver(receiver, new IntentFilter(Constant.ActionConstant.UPDATE_MESSAGE_CHAT_LAYOUT));
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
                    Log.d("getChatFromLocal 获取数据异常");
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
        asyncSession.queryList(QueryBuilder.internalCreate(DaoManager.getDaoSession().getDao(Chat.class))
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
                if (isRefresh) {
                    chatList.clear();
                    chatAdapter.notifyDataSetChanged();
                }
                isMore = false;
            }
        } else {
            if (isRefresh) {
                chatList.clear();
                chatAdapter.notifyDataSetChanged();
            }
            isMore = false;
        }
    }

    private void addListener() {
        chatAdapter.setLayoutListener(new MessageAdapter.OnUpdateChatLayoutListener() {
            @Override
            public void onUpdate(Chat chat, String senderId, int position) {
                UserInfo userInfo = MyApplication.userInfoMap.get(senderId);
                if (userInfo == null) {
                    getUserInfoFromServer(chat, senderId, position);
                } else {
                    chat.setUserId(userInfo.getUserId());
                    chat.setChatName(userInfo.getNickname());
                    chat.setChatPic(userInfo.getAvatar());
                    asyncSession.insertOrReplace(chat);
//                    chatAdapter.notifyItemChanged(position);
                }
            }
        });
        chatAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Chat chat = chatList.get(position);
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra(ChatActivity.BUNDLE_CHAT, chat);
                startActivity(intent);
            }
        });
        chatAdapter.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDeleteTip(chatList.get(position), position);
                return false;
            }
        });
        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d("------->isSlideToBottom:" + recyclerView.canScrollVertically(1));
                if (!recyclerView.canScrollVertically(1)) {
                    getChatFromLocal(false);
                }
            }
        });
    }

    private void showDeleteTip(final Chat chat, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle("确定清除该聊天室？")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteChatAndMessage(chat, position);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private void deleteChatAndMessage(final Chat chat, final int position) {
        asyncSession.runInTx(new Runnable() {
            @Override
            public void run() {
                Database db = DaoManager.getDaoSession().getDatabase();
                db.execSQL("DELETE FROM CHAT_MESSAGE where CHAT_ID = ? ", new String[]{chat.getChatId()});
                db.execSQL("DELETE FROM CHAT where CHAT_ID = ? ", new String[]{chat.getChatId()});
            }
        });
        chatList.remove(position);
        //更新聊天室
        chatAdapter.notifyItemRemoved(position);
        chatAdapter.notifyDataSetChanged();
    }


    private void getUserInfoFromServer(final Chat chat, final String senderId, final int position) {
        boolean isUpdate = PreferenceUtil.getInstances(MyApplication.getInstance()).
                getPreferenceBooleanHaveTime(Constant.PreferenceKey.USER_INFO_WAITING + senderId);
        if (!isUpdate) {
            PreferenceUtil.getInstances(MyApplication.getInstance()).
                    savePreferenceBooleanBySecond(Constant.PreferenceKey.USER_INFO_WAITING + senderId, true, 10L);
            Log.d("更新用户信息 userId = " + userId);
        } else {
            return;
        }

        List<String> userList = new ArrayList<>();
        userList.add(senderId);
        Map<String, Object> param = new HashMap<>(1);
        param.put("userList", gson.toJson(userList));
        RequestManager.getInstance(MyApplication.getInstance()).executeRequest(HttpUrls.GET_USER_LIST_INFO, param, new AppCallBack<ApiResponse<List<UserInfo>>>() {
            @Override
            public void next(final ApiResponse<List<UserInfo>> response) {
                if (response.isSuccess()) {
                    UserInfo userInfo = response.getData().get(0);
                    MyApplication.userInfoMap.put(userInfo.getUserId(), userInfo);
                    chat.setUserId(userInfo.getUserId());
                    chat.setChatName(userInfo.getNickname());
                    chat.setChatPic(userInfo.getAvatar());
                    asyncSession.insertOrReplace(chat);
                    asyncSession.insertOrReplace(userInfo);
                    chatAdapter.notifyItemChanged(position);
                    PreferenceUtil.getInstances(MyApplication.getInstance()).deleteKey(Constant.PreferenceKey.USER_INFO_WAITING + userInfo.getUserId());
                } else {
                    Log.d(response.getMessage());
                }
            }

            @Override
            public void error(Throwable error) {
                PreferenceUtil.getInstances(MyApplication.getInstance()).deleteKey(Constant.PreferenceKey.USER_INFO_WAITING + senderId);
            }

            @Override
            public void complete() {
                PreferenceUtil.getInstances(MyApplication.getInstance()).deleteKey(Constant.PreferenceKey.USER_INFO_WAITING + senderId);
            }
        });
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
        if (receiver != null)
            LocalBroadcastManager.getInstance(MyApplication.getInstance()).unregisterReceiver(receiver);
    }
}
