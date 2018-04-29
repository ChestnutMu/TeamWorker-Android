package cn.chestnut.mvvm.teamworker.module.massage.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;

import org.greenrobot.greendao.async.AsyncOperation;
import org.greenrobot.greendao.async.AsyncOperationListener;
import org.greenrobot.greendao.async.AsyncSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.QueryBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.chestnut.mvvm.teamworker.Constant;
import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityChatSettingBinding;
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
import cn.chestnut.mvvm.teamworker.module.massage.adapter.ChatSettingUserAdapter;
import cn.chestnut.mvvm.teamworker.utils.EmojiUtil;
import cn.chestnut.mvvm.teamworker.utils.EntityUtil;
import cn.chestnut.mvvm.teamworker.utils.Log;
import cn.chestnut.mvvm.teamworker.utils.PreferenceUtil;
import cn.chestnut.mvvm.teamworker.utils.StringUtil;
import cn.chestnut.mvvm.teamworker.utils.photo.ProcessPhotoUtils;
import cn.chestnut.mvvm.teamworker.utils.sqlite.DaoManager;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/24 12:05:18
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class ChatSettingActivity extends BaseActivity {

    ActivityChatSettingBinding binding;

    public static final String BUNDLE_CHAT = "bundle_chat";//聊天室

    private String userId;

    private ChatSettingUserAdapter adapter;

    private List<UserInfo> userInfoList;

    private List<String> userIdList;

    private AsyncSession asyncSession;

    private UserInfo add = new UserInfo();
    private UserInfo remove = new UserInfo();

    private ProcessPhotoUtils processPhotoUtils;
    private String qiniuToken;


    private BroadcastReceiver receiver;

    public static final String BROADCAST_INTENT_USER_ID_LIST = "broadcast_intent_user_id_list";

    public static void startActivity(Context mContext, Chat chat) {
        Intent intent = new Intent(mContext, ChatSettingActivity.class);
        intent.putExtra(BUNDLE_CHAT, chat);
        mContext.startActivity(intent);
    }

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("聊天详情");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_chat_setting, viewGroup, true);
        initData();
        initView();
        addListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ProcessPhotoUtils.UPLOAD_PHOTO_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri originalUri = data.getData(); // 获得图片的uri
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = managedQuery(originalUri, proj, null, null, null);
            //获得用户选择的图片的索引值
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            // 将光标移至开头 ，这个很重要，不小心很容易引起越界
            cursor.moveToFirst();
            //获得图片的uri
            String filePath = cursor.getString(column_index);
            Log.d("filePath " + filePath);
            uploadPicture(filePath);
        } else if (requestCode == ProcessPhotoUtils.SHOOT_PHOTO_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            String filePath = processPhotoUtils.getMyPhotoFile().getPath();
            Log.d("filePath " + filePath);
            uploadPicture(filePath);
        }
    }

    /**
     * 初始化数据
     */
    protected void initData() {
        asyncSession = DaoManager.getDaoSession().startAsyncSession();
        userId = PreferenceUtil.getInstances(this).getPreferenceString("userId");
        binding.setChat((Chat) getIntent().getSerializableExtra(BUNDLE_CHAT));
        updateChatInfo();
    }

    /**
     * 初始化界面
     */
    protected void initView() {
        userIdList = gson.fromJson(binding.getChat().getUserList(), new TypeToken<List<String>>() {
        }.getType());
        userInfoList = new ArrayList<>();
        for (String userId : userIdList) {
            UserInfo userInfo = new UserInfo();
            userInfo.setUserId(userId);
            userInfoList.add(userInfo);
        }
        if (binding.getChat().getAdminId().equals(userId)) {
            add.setAvatar("add");
            remove.setAvatar("remove");
            userInfoList.add(add);
            userInfoList.add(remove);
        }
        adapter = new ChatSettingUserAdapter(userInfoList);
        binding.recyclerView.setAdapter(adapter);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 5);
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setNestedScrollingEnabled(false);

        updateUerInfo();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("ChatSettingActivity onReceive" + intent.getAction());
                if (Constant.ActionConstant.ACTION_UPDATE_CHAT_SETTING.equals(intent.getAction())) {
                    String userIdListJson = intent.getStringExtra(BROADCAST_INTENT_USER_ID_LIST);
                    Log.d("userIdListJson = " + userIdListJson);
                    userIdList = gson.fromJson(userIdListJson, new TypeToken<List<String>>() {
                    }.getType());
                    userInfoList.clear();
                    for (String userId : userIdList) {
                        UserInfo userInfo = new UserInfo();
                        userInfo.setUserId(userId);
                        userInfoList.add(userInfo);
                    }
                    if (binding.getChat().getAdminId().equals(userId)) {
                        add.setAvatar("add");
                        remove.setAvatar("remove");
                        userInfoList.add(add);
                        userInfoList.add(remove);
                    }
                    adapter.notifyDataSetChanged();
                    updateUerInfo();
                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(Constant.ActionConstant.ACTION_UPDATE_CHAT_SETTING));
    }

    /**
     * 添加监听器
     */
    protected void addListener() {
        adapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserInfo userInfo = userInfoList.get(position);
                if (userInfo.equals(add)) {
                    Log.d("添加用户");
                    CreateMultiChatActivity.startActivity(ChatSettingActivity.this, binding.getChat().getChatId(), binding.getChat().getUserList());
                } else if (userInfo.equals(remove)) {
                    Log.d("移除用户");
                    if (binding.getChat().getAdminId().equals(userId)) {
                        List<UserInfo> chatUserInfoList = new ArrayList<>();
                        chatUserInfoList.addAll(userInfoList);
                        chatUserInfoList.remove(chatUserInfoList.size() - 1);
                        chatUserInfoList.remove(chatUserInfoList.size() - 1);
                        DeleteChatUserActivity.startActivity(ChatSettingActivity.this, binding.getChat().getChatId(), gson.toJson(chatUserInfoList));
                    } else {
                        DeleteChatUserActivity.startActivity(ChatSettingActivity.this, binding.getChat().getChatId(), gson.toJson(userInfoList));
                    }
                } else {
                    Log.d("点击用户");
                }
            }
        });
        binding.btnClearRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearRecord();
            }
        });
        if (userId.equals(binding.getChat().getAdminId())) {
            binding.chatNameLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final EditText editText = new EditText(ChatSettingActivity.this);
                    editText.setText(binding.getChat().getChatName());
                    new AlertDialog.Builder(ChatSettingActivity.this)
                            .setTitle("名称")
                            .setView(editText)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        String chatName = EmojiUtil.emojiConvert(editText.getText().toString());
                                        if (chatName.length() >= 15) {
                                            showToast("已超过限定字数");
                                            return;
                                        }
                                        updateChatInfo(chatName);
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create()
                            .show();
                }
            });
            binding.chatPicLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (processPhotoUtils == null)
                        processPhotoUtils = new ProcessPhotoUtils(ChatSettingActivity.this);
                    processPhotoUtils.startPhoto();
                }
            });
        }

        binding.btnDelAndOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delAndOut();
            }
        });

    }

    private void delAndOut() {
        Map<String, Object> param = new HashMap<>(1);
        param.put("chatId", binding.getChat().getChatId());
        showProgressDialog(this);
        RequestManager.getInstance(this).executeRequest(HttpUrls.GO_OUT_CHAT, param, new AppCallBack<ApiResponse<Object>>() {
            @Override
            public void next(ApiResponse<Object> response) {
                if (response.isSuccess() || response.getStatus() == 2) {
                    asyncSession.runInTx(new Runnable() {
                        @Override
                        public void run() {
                            Database db = DaoManager.getDaoSession().getDatabase();
                            db.execSQL("DELETE FROM CHAT_MESSAGE where CHAT_ID = ? ", new String[]{binding.getChat().getChatId()});
                            db.execSQL("DELETE FROM CHAT where CHAT_ID = ? ", new String[]{binding.getChat().getChatId()});

                            //更新聊天室
                            Intent intent = new Intent(Constant.ActionConstant.ACTION_UPDATE_CHAT);
                            intent.putExtra(ChatActivity.BROADCAST_INTENT_TYPE, 4);
                            LocalBroadcastManager.getInstance(MyApplication.getInstance()).sendBroadcast(intent);
                        }
                    });

                    updateMessageLayout();
                    finish();
                } else {
                    showToast(response.getMessage());
                }
            }

            @Override
            public void error(Throwable error) {
                hideProgressDialog();
            }

            @Override
            public void complete() {
                hideProgressDialog();
            }
        });
    }


    private void updateAvatar(String avatar) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("chatId", binding.getChat().getChatId());
        param.put("chatPic", avatar);
        showProgressDialog(this);
        RequestManager.getInstance(this).executeRequest(HttpUrls.CHANGE_CHAT_INFO, param, new AppCallBack<ApiResponse<Chat>>() {
            @Override
            public void next(ApiResponse<Chat> response) {
                if (response.isSuccess()) {
                    Chat newChat = response.getData();
                    //保存到本地
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setChatMessageId(EntityUtil.getIdByTimeStampAndRandom());
                    chatMessage.setChatId(newChat.getChatId());
                    chatMessage.setSenderId(userId);
                    chatMessage.setType(Constant.ChatMessageType.TYPE_MESSAGE_CHANGE_PIC);
                    chatMessage.setDone(true);
                    chatMessage.setSendTime(newChat.getUpdateTime());

                    newChat.setLastMessage("你修改聊天室头像");
                    asyncSession.insert(chatMessage);
                    asyncSession.insertOrReplace(newChat);

                    //更新当前界面
                    binding.setChat(newChat);

                    //更新聊天室
                    Intent intent = new Intent(Constant.ActionConstant.ACTION_UPDATE_CHAT);
                    intent.putExtra(ChatActivity.BROADCAST_INTENT_TYPE, 2);
                    intent.putExtra(ChatActivity.BROADCAST_INTENT_MESSAGE, chatMessage);
                    LocalBroadcastManager.getInstance(MyApplication.getInstance()).sendBroadcast(intent);

                    updateMessageLayout();
                } else {
                    showToast(response.getMessage());
                }
            }

            @Override
            public void error(Throwable error) {
                hideProgressDialog();
            }

            @Override
            public void complete() {
                hideProgressDialog();
            }
        });
    }

    private void updateChatInfo(String chatName) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("chatId", binding.getChat().getChatId());
        param.put("chatName", chatName);
        showProgressDialog(this);
        RequestManager.getInstance(this).executeRequest(HttpUrls.CHANGE_CHAT_INFO, param, new AppCallBack<ApiResponse<Chat>>() {
            @Override
            public void next(ApiResponse<Chat> response) {
                if (response.isSuccess()) {
                    Chat newChat = response.getData();
                    binding.setChat(newChat);
                    binding.getChat().setChatName(newChat.getChatName());
                    //保存到本地
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setChatMessageId(EntityUtil.getIdByTimeStampAndRandom());
                    chatMessage.setChatId(newChat.getChatId());
                    chatMessage.setMessage(newChat.getChatName());
                    chatMessage.setSenderId(userId);
                    chatMessage.setType(Constant.ChatMessageType.TYPE_MESSAGE_CHANGE_NAME);
                    chatMessage.setDone(true);
                    chatMessage.setSendTime(newChat.getUpdateTime());

                    newChat.setLastMessage("你修改聊天室名称为“" + newChat.getChatName() + "”");
                    asyncSession.insert(chatMessage);
                    asyncSession.insertOrReplace(newChat);

                    //更新当前界面
                    binding.tvChatName.setText(newChat.getChatName());

                    //更新聊天室
                    Intent intent = new Intent(Constant.ActionConstant.ACTION_UPDATE_CHAT);
                    intent.putExtra(ChatActivity.BROADCAST_INTENT_TYPE, 0);
                    intent.putExtra(ChatActivity.BROADCAST_INTENT_NAME, binding.getChat().getChatName());
                    intent.putExtra(ChatActivity.BROADCAST_INTENT_MESSAGE, chatMessage);
                    LocalBroadcastManager.getInstance(MyApplication.getInstance()).sendBroadcast(intent);

                    updateMessageLayout();
                } else {
                    showToast(response.getMessage());
                }
            }

            @Override
            public void error(Throwable error) {
                hideProgressDialog();
            }

            @Override
            public void complete() {
                hideProgressDialog();
            }
        });
    }

    /**
     * 更新消息界面
     */
    private void updateMessageLayout() {
        Log.d("updateMessageLayout 更新信息界面");
        Intent intent = new Intent(Constant.ActionConstant.UPDATE_MESSAGE_CHAT_LAYOUT);
        LocalBroadcastManager.getInstance(MyApplication.getInstance()).sendBroadcast(intent);
    }

    private void clearRecord() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("确定清除该聊天室的所有聊天记录？")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        asyncSession.runInTx(new Runnable() {
                            @Override
                            public void run() {
                                Database db = DaoManager.getDaoSession().getDatabase();
                                db.execSQL("DELETE FROM CHAT_MESSAGE where CHAT_ID = ? ", new String[]{binding.getChat().getChatId()});
                                binding.getChat().setLastMessage("");
                                asyncSession.insertOrReplace(binding.getChat());
                                //更新聊天室
                                Intent intent = new Intent(Constant.ActionConstant.ACTION_UPDATE_CHAT);
                                intent.putExtra(ChatActivity.BROADCAST_INTENT_TYPE, 1);
                                LocalBroadcastManager.getInstance(MyApplication.getInstance()).sendBroadcast(intent);
                            }
                        });
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }


    /**
     * 更新用户信息
     */
    private void updateUerInfo() {
        //从全局缓存中查找
        final List<String> notUserList = new ArrayList<>();
        for (int i = 0; i < userInfoList.size(); i++) {
            UserInfo userInfo = userInfoList.get(i);
            if (userInfo.getUserId() == null) continue;
            UserInfo temp = MyApplication.userInfoMap.get(userInfo.getUserId());
            if (temp == null) {
                boolean isUpdate = PreferenceUtil.getInstances(MyApplication.getInstance()).
                        getPreferenceBooleanHaveTime(Constant.PreferenceKey.USER_INFO_WAITING + userInfo.getUserId());
                if (!isUpdate) {
                    PreferenceUtil.getInstances(MyApplication.getInstance()).
                            savePreferenceBooleanBySecond(Constant.PreferenceKey.USER_INFO_WAITING + userInfo.getUserId(), true, 10L);
                    Log.d("更新用户信息 userId = " + userInfo.getUserId());
                    notUserList.add(userInfo.getUserId());
                }
            }
        }

        asyncSession.setListenerMainThread(new AsyncOperationListener() {

            @Override
            public void onAsyncOperationCompleted(AsyncOperation operation) {
                if (operation.isFailed()) {
                    Log.d("ChatSettingActivity asyncSession 获取数据异常");
                    //从服务器创建并保存到本地
                    getUserInfoFromServer(notUserList);
                    return;
                }
                Log.d("ChatSettingActivity asyncSession operation.getType()= " + operation.getType());
                if (operation.getType() == AsyncOperation.OperationType.QueryList) {
                    Object obj = operation.getResult();
                    Log.d("ChatSettingActivity asyncSession 获取数据 obj = " + obj);
                    updateUserInfos(obj, notUserList);
                }
            }
        });
        asyncSession.queryList(QueryBuilder.internalCreate(DaoManager.getDaoSession().getDao(UserInfo.class))
                .where(UserInfoDao.Properties.UserId.in(notUserList))
                .build());
    }

    private void updateUserInfos(Object obj, List<String> userList) {
        if (obj != null && obj instanceof List) {
            List<UserInfo> data = (List<UserInfo>) obj;
            if (data.isEmpty()) {
                getUserInfoFromServer(userList);
            } else {
                for (UserInfo userInfo : data) {
                    PreferenceUtil.getInstances(MyApplication.getInstance()).deleteKey(Constant.PreferenceKey.USER_INFO_WAITING + userInfo.getUserId());
                    Log.d("更新用户信息完成 userId = " + userId);
                    MyApplication.userInfoMap.put(userInfo.getUserId(), userInfo);
                    userList.remove(userInfo.getUserId());
                }
                adapter.notifyDataSetChanged();
                getUserInfoFromServer(userList);
            }
        } else {
            getUserInfoFromServer(userList);
        }
    }

    private void getUserInfoFromServer(final List<String> userList) {
        if (userList.isEmpty()) return;
        Map<String, Object> param = new HashMap<>(1);
        param.put("userList", gson.toJson(userList));
        RequestManager.getInstance(this).executeRequest(HttpUrls.GET_USER_LIST_INFO, param, new AppCallBack<ApiResponse<List<UserInfo>>>() {
            @Override
            public void next(ApiResponse<List<UserInfo>> response) {
                if (response.isSuccess()) {
                    for (UserInfo userInfo : response.getData()) {
                        PreferenceUtil.getInstances(MyApplication.getInstance()).deleteKey(Constant.PreferenceKey.USER_INFO_WAITING + userInfo.getUserId());
                        Log.d("更新用户信息完成 userId = " + userId);
                        MyApplication.userInfoMap.put(userInfo.getUserId(), userInfo);
                    }
                    asyncSession.insertOrReplaceInTx(UserInfo.class, response.getData());
                    adapter.notifyDataSetChanged();
                } else {
                    for (String userId : userList) {
                        PreferenceUtil.getInstances(MyApplication.getInstance()).deleteKey(Constant.PreferenceKey.USER_INFO_WAITING + userId);
                        Log.d("更新用户信息失败 userId = " + userId);
                    }
                    showToast(response.getMessage());
                }
            }

            @Override
            public void error(Throwable error) {
                for (String userId : userList) {
                    PreferenceUtil.getInstances(MyApplication.getInstance()).deleteKey(Constant.PreferenceKey.USER_INFO_WAITING + userId);
                    Log.d("更新用户信息完成 userId = " + userId);
                }
            }

            @Override
            public void complete() {

            }
        });
    }

    private void uploadPicture(String data, String token) {
        String key = null;
        MyApplication.getUploadManager().put(data, key, token,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject res) {
                        //res包含hash、key等信息，具体字段取决于上传策略的设置
                        if (info.isOK()) {
                            Log.i("qiniu Upload Success");
                            try {
                                updateAvatar(res.getString("key"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.i("qiniu Upload Fail");
                            //如果失败，这里可以把info信息上报自己的服务器，便于后面分析上传错误原因
                        }
                        Log.i("qiniu " + key + ",\r\n " + info + ",\r\n " + res);
                    }
                }, null);

    }

    private void uploadPicture(final String data) {

        if (StringUtil.isEmpty(qiniuToken))

            RequestManager.getInstance(this).executeRequest(HttpUrls.GET_QINIUTOKEN, null, new AppCallBack<ApiResponse<String>>() {

                @Override
                public void next(ApiResponse<String> response) {
                    if (response.isSuccess()) {
                        qiniuToken = response.getData();
                        uploadPicture(data, qiniuToken);
                    } else {
                        showToast(response.getMessage());
                    }
                }

                @Override
                public void error(Throwable error) {
                    Log.e(error.toString());
                }

                @Override
                public void complete() {

                }

            });

        else {
            uploadPicture(data, qiniuToken);
        }

    }

    private void updateChatInfo() {
        Map<String, Object> param = new HashMap<>(1);
        List<String> chatIdList = new ArrayList<>(1);
        chatIdList.add(binding.getChat().getChatId());
        param.put("chatList", gson.toJson(chatIdList));
        RequestManager.getInstance(this).executeRequest(HttpUrls.GET_CHAT_LIST, param, new AppCallBack<ApiResponse<List<Chat>>>() {

            @Override
            public void next(ApiResponse<List<Chat>> response) {
                if (response.isSuccess()) {
                    if (response.getData().isEmpty()) {
                        showToast("聊天室已被删除");
                    } else {
                        Chat newChat = response.getData().get(0);
                        if (!newChat.getUserList().equals(binding.getChat().getUserList())) {
                            binding.setChat(newChat);
                            userIdList = gson.fromJson(binding.getChat().getUserList(), new TypeToken<List<String>>() {
                            }.getType());
                            userInfoList.clear();
                            for (String userId : userIdList) {
                                UserInfo userInfo = new UserInfo();
                                userInfo.setUserId(userId);
                                userInfoList.add(userInfo);
                            }
                            if (binding.getChat().getAdminId().equals(userId)) {
                                add.setAvatar("add");
                                remove.setAvatar("remove");
                                userInfoList.add(add);
                                userInfoList.add(remove);
                            }
                            adapter.notifyDataSetChanged();
                            updateUerInfo();
                        } else {
                            binding.setChat(newChat);
                        }
                        //更新聊天室
                        Intent intent = new Intent(Constant.ActionConstant.ACTION_UPDATE_CHAT);
                        intent.putExtra(ChatActivity.BROADCAST_INTENT_TYPE, 5);
                        intent.putExtra(ChatActivity.BROADCAST_INTENT_CHAT, newChat);
                        LocalBroadcastManager.getInstance(MyApplication.getInstance()).sendBroadcast(intent);

                        asyncSession.insertOrReplace(newChat);
                    }
                } else {
                    showToast(response.getMessage());
                }
            }

            @Override
            public void error(Throwable error) {
                Log.e(error.toString());
            }

            @Override
            public void complete() {

            }

        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        asyncSession.setListenerMainThread(null);
        asyncSession = null;
        if (receiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
            receiver = null;
        }
    }


}
