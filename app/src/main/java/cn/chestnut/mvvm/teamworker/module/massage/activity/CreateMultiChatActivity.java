package cn.chestnut.mvvm.teamworker.module.massage.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

import org.greenrobot.greendao.async.AsyncOperation;
import org.greenrobot.greendao.async.AsyncOperationListener;
import org.greenrobot.greendao.async.AsyncSession;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.chestnut.mvvm.teamworker.BR;
import cn.chestnut.mvvm.teamworker.Constant;
import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityCreateMultiChatBinding;
import cn.chestnut.mvvm.teamworker.db.UserDao;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.main.common.MyApplication;
import cn.chestnut.mvvm.teamworker.model.Chat;
import cn.chestnut.mvvm.teamworker.model.User;
import cn.chestnut.mvvm.teamworker.module.massage.adapter.ChatUserAdapter;
import cn.chestnut.mvvm.teamworker.module.massage.adapter.ChosenUserAdapter;
import cn.chestnut.mvvm.teamworker.module.user.UserInformationActivity;
import cn.chestnut.mvvm.teamworker.utils.Log;
import cn.chestnut.mvvm.teamworker.utils.PreferenceUtil;
import cn.chestnut.mvvm.teamworker.utils.sqlite.DaoManager;
import cn.chestnut.mvvm.teamworker.widget.WordsIndexBar;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/23 22:22:04
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class CreateMultiChatActivity extends BaseActivity {

    private ActivityCreateMultiChatBinding binding;

    private List<User> myFriendList = new ArrayList<>();

    private List<User> chooseUserList = new ArrayList<>();

    private ChatUserAdapter adapter;
    private ChosenUserAdapter chosenUserAdapter;

    /*本地数据操作异步工具类*/
    private AsyncSession asyncSession;

    private TextView tvFinish;

    private boolean isCreateChat = true;
    private String chatId;
    private Set<String> chosenUserIdSet;
    private List<String> chooseName;

    //是否是创建聊天室还是添加用户
    public static final String BUNDLE_TYPE_IS_CREATE = "bundle_type_is_create";
    //添加用户的聊天室id
    public static final String BUNDLE_CHAT_ID = "bundle_chat_id";
    //添加用户的原本用户
    public static final String BUNDLE_CHAT_USER_LIST = "bundle_chat_user_list";

    private String userId;

    public static void startActivity(Context mContext, String chatId, String userIdSetJson) {
        Intent intent = new Intent(mContext, CreateMultiChatActivity.class);
        intent.putExtra(BUNDLE_TYPE_IS_CREATE, false);
        intent.putExtra(BUNDLE_CHAT_ID, chatId);
        intent.putExtra(BUNDLE_CHAT_USER_LIST, userIdSetJson);
        mContext.startActivity(intent);
    }

    public static void startActivity(Context mContext) {
        Intent intent = new Intent(mContext, CreateMultiChatActivity.class);
        intent.putExtra(BUNDLE_TYPE_IS_CREATE, true);
        mContext.startActivity(intent);
    }


    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("选择联系人");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_create_multi_chat, viewGroup, true);
        asyncSession = DaoManager.getDaoSession().startAsyncSession();
        initView();
        initData();
        addListener();
    }

    @Override
    public void setButton(TextView edit, ImageView add, ImageView search) {
        tvFinish = edit;
        tvFinish.setVisibility(View.VISIBLE);
        tvFinish.setText("完成");
        tvFinish.setTextColor(getResources().getColor(R.color.separation));
        tvFinish.setClickable(false);
        tvFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("点击 tvFinish");
                buildChatOrAddUser();
            }
        });
    }

    private void buildChatOrAddUser() {
        if (isCreateChat) {
            Log.d("创建聊天室");
            Map<String, Object> param = new HashMap<>(3);
            String chatName = "";
            for (int i = 0; i < chooseName.size() - 1; i++) {
                chatName = chatName + chooseName.get(i) + "、";
            }
            chatName = chatName + chooseName.get(chooseName.size() - 1);
            param.put("chatType", Constant.ChatType.TYPE_CHAT_MULTIPLAYER);
            param.put("chatName", chatName);
            Set<String> userList = new HashSet<>(adapter.getChooseUserIdSet().size() + 1);
            userList.add(userId);
            userList.addAll(adapter.getChooseUserIdSet());
            param.put("userList", gson.toJson(userList));
            showProgressDialog(this);
            RequestManager.getInstance(this).executeRequest(HttpUrls.BUILD_CHAT, param, new AppCallBack<ApiResponse<Chat>>() {
                @Override
                public void next(ApiResponse<Chat> response) {
                    if (response.isSuccess()) {
                        Chat chat = response.getData();
                        //保存到本地
                        asyncSession.insert(chat);
                        //跳转
                        handleChat(chat);
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
        } else {
            Log.d("添加用户");
            Map<String, Object> param = new HashMap<>(2);
            param.put("chatId", chatId);
            Set<String> userList = new HashSet<>(adapter.getChooseUserIdSet().size() + adapter.getChosenUserIdSet().size());
            userList.addAll(adapter.getChosenUserIdSet());
            userList.addAll(adapter.getChooseUserIdSet());
            param.put("userList", gson.toJson(userList));
            showProgressDialog(this);
            RequestManager.getInstance(this).executeRequest(HttpUrls.CHANGE_CHAT_INFO, param, new AppCallBack<ApiResponse<Chat>>() {
                @Override
                public void next(ApiResponse<Chat> response) {
                    if (response.isSuccess()) {
                        Chat chat = response.getData();
                        //保存到本地
                        asyncSession.insertOrReplace(chat);
                        //跳转
                        handleChat(chat);
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
    }

    private void handleChat(Chat chat) {
        Log.d("handleChat 更新信息界面");
        Intent intentUpdate = new Intent(Constant.ActionConstant.UPDATE_MESSAGE_CHAT_LAYOUT);
        LocalBroadcastManager.getInstance(MyApplication.getInstance()).sendBroadcast(intentUpdate);

        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(ChatActivity.BUNDLE_CHAT, chat);
        startActivity(intent);
        finish();
    }

    @Override
    protected void initView() {
        chooseName = new ArrayList<>();
        userId = PreferenceUtil.getInstances(this).getPreferenceString("userId");
        isCreateChat = getIntent().getBooleanExtra(BUNDLE_TYPE_IS_CREATE, true);
        adapter = new ChatUserAdapter(R.layout.item_choose_user, BR.user, myFriendList);

        if (!isCreateChat) {
            chatId = getIntent().getStringExtra(BUNDLE_CHAT_ID);
            String json = getIntent().getStringExtra(BUNDLE_CHAT_USER_LIST);
            chosenUserIdSet = gson.fromJson(json, new TypeToken<HashSet<String>>() {
            }.getType());
            adapter.getChosenUserIdSet().addAll(chosenUserIdSet);
        }

        binding.lvMyFriend.setAdapter(adapter);

        chosenUserAdapter = new ChosenUserAdapter(chooseUserList);
        binding.rvShowChoosePerson.setAdapter(chosenUserAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.rvShowChoosePerson.setLayoutManager(manager);
    }

    @Override
    protected void initData() {
        getMyFriendsFormLocal();
    }

    @Override
    protected void addListener() {
        binding.lvMyFriend.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (myFriendList.size() > 0) {
                    binding.wordIndexBar.setTouchIndex(myFriendList.get(firstVisibleItem).getWordHeader());
                }
            }
        });

        binding.wordIndexBar.setOnWordsChangeListener(new WordsIndexBar.OnWordChangeListener() {
            @Override
            public void onWordChange(String words) {
                if (myFriendList.size() > 0) {
                    updateListView(words);
                }
            }
        });

        binding.lvMyFriend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("点击按钮" + position);
                User chooseUser = myFriendList.get(position);
                if (!adapter.getChosenUserIdSet().contains(chooseUser.getUserId())) {
                    if (adapter.getChooseUserIdSet().contains(chooseUser.getUserId())) {
                        //设置没选择
                        adapter.getChooseUserIdSet().remove(chooseUser.getUserId());
                        adapter.notifyDataSetChanged();

                        chooseUserList.remove(chooseUser);
                        chosenUserAdapter.notifyDataSetChanged();
                        if (chooseUserList.isEmpty()) {
                            tvFinish.setText("完成");
                            tvFinish.setTextColor(getResources().getColor(R.color.separation));
                            tvFinish.setClickable(false);
                        } else {
                            tvFinish.setText("完成(" + chooseUserList.size() + ")");
                            tvFinish.setTextColor(getResources().getColor(R.color.white));
                        }

                        chooseName.remove(chooseUser.getNickname());
                    } else {
                        //设置选择
                        adapter.getChooseUserIdSet().add(chooseUser.getUserId());
                        adapter.notifyDataSetChanged();

                        chooseUserList.add(chooseUser);
                        chosenUserAdapter.notifyDataSetChanged();

                        tvFinish.setText("完成(" + chooseUserList.size() + ")");
                        tvFinish.setTextColor(getResources().getColor(R.color.white));
                        tvFinish.setClickable(true);

                        chooseName.add(chooseUser.getNickname());
                    }
                }
            }
        });
    }

    private void getMyFriendsFormLocal() {
        asyncSession.setListenerMainThread(new AsyncOperationListener() {

            @Override
            public void onAsyncOperationCompleted(AsyncOperation operation) {
                if (operation.isFailed()) {
                    Log.d("checkHasChatAndUpdate 获取数据异常");
                    getMyFriends();
                    return;
                }
                Log.d("operation.getType()= " + operation.getType());
                if (operation.getType() == AsyncOperation.OperationType.QueryList) {
                    Object obj = operation.getResult();
                    Log.d("获取数据 obj = " + obj);

                    handleData(obj);
                }
            }
        });
        asyncSession.queryList(QueryBuilder.internalCreate(DaoManager.getDaoSession().getDao(User.class))
                .where(UserDao.Properties.Friend.eq(true))
                .build());
    }

    private void handleData(Object obj) {
        asyncSession.setListenerMainThread(null);
        if (obj != null && obj instanceof List) {
            Log.d("obtainDataFromLocalDatabase: " + obj);
            List<User> data = (List<User>) obj;
            if (!data.isEmpty()) {
                myFriendList.clear();
                myFriendList.addAll(data);
                Collections.sort(myFriendList, new Comparator<User>() {
                    @Override
                    public int compare(User lmf, User rmf) {
                        //根据拼音进行排序
                        return lmf.getPinyin().compareTo(rmf.getPinyin());
                    }
                });
                adapter.notifyDataSetChanged();
                getMyFriends();
            } else {
                getMyFriends();
            }
        } else {
            getMyFriends();
        }
    }

    private void getMyFriends() {
        asyncSession.setListenerMainThread(null);
        Map<String, Integer> params = new HashMap<>(2);
        params.put("pageNum", 1);
        params.put("pageSize", 1000);
        RequestManager.getInstance(this).executeRequest(HttpUrls.GET_MY_FRIENDS, params, new AppCallBack<ApiResponse<List<User>>>() {
            @Override
            public void next(ApiResponse<List<User>> response) {
                if (response.isSuccess() && !response.getData().isEmpty()) {
                    if (myFriendList.isEmpty()) {
                        myFriendList.addAll(response.getData());
                        Collections.sort(myFriendList, new Comparator<User>() {
                            @Override
                            public int compare(User lmf, User rmf) {
                                //根据拼音进行排序
                                return lmf.getPinyin().compareTo(rmf.getPinyin());
                            }
                        });
                        adapter.notifyDataSetChanged();
                        for (User user : myFriendList) {
                            user.setFriend(true);
                        }
                        asyncSession.insertOrReplaceInTx(User.class, myFriendList);
                    } else {
                        for (User user : response.getData()) {
                            user.setFriend(true);
                            myFriendList.remove(user);
                        }
                        for (User user : myFriendList) {
                            user.setFriend(false);
                        }
                        asyncSession.insertOrReplaceInTx(User.class, myFriendList);
                        myFriendList.clear();
                        myFriendList.addAll(response.getData());
                        Collections.sort(myFriendList, new Comparator<User>() {
                            @Override
                            public int compare(User lmf, User rmf) {
                                //根据拼音进行排序
                                return lmf.getPinyin().compareTo(rmf.getPinyin());
                            }
                        });
                        adapter.notifyDataSetChanged();
                        asyncSession.insertOrReplaceInTx(User.class, myFriendList);
                    }
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

    /**
     * @param words 首字母
     */
    private void updateListView(String words) {
        for (int i = 0; i < myFriendList.size(); i++) {
            String headerWord = myFriendList.get(i).getWordHeader();
            //将手指按下的字母与列表中相同字母开头的项找出来
            if (words.equals(headerWord)) {
                //将列表选中哪一个
                binding.lvMyFriend.setSelection(i);
                //找到开头的一个即可
                return;
            }
        }
    }
}
