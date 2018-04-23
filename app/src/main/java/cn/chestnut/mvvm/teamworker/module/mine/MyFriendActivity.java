package cn.chestnut.mvvm.teamworker.module.mine;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;

import org.greenrobot.greendao.async.AsyncOperation;
import org.greenrobot.greendao.async.AsyncOperationListener;
import org.greenrobot.greendao.async.AsyncSession;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.chestnut.mvvm.teamworker.BR;
import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityMyFriendBinding;
import cn.chestnut.mvvm.teamworker.db.ChatDao;
import cn.chestnut.mvvm.teamworker.db.UserDao;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.model.Chat;
import cn.chestnut.mvvm.teamworker.model.User;
import cn.chestnut.mvvm.teamworker.module.user.UserInformationActivity;
import cn.chestnut.mvvm.teamworker.utils.Log;
import cn.chestnut.mvvm.teamworker.utils.sqlite.DaoManager;
import cn.chestnut.mvvm.teamworker.widget.WordsIndexBar;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/7 14:34:12
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class MyFriendActivity extends BaseActivity {

    private ActivityMyFriendBinding binding;

    private List<User> myFriendList = new ArrayList<>();

    private MyFriendAdapter adapter;

    /*本地数据操作异步工具类*/
    private AsyncSession asyncSession;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("我的好友");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_my_friend, viewGroup, true);
        asyncSession = DaoManager.getDaoSession().startAsyncSession();
        initView();
        initData();
        addListener();
    }

    @Override
    protected void initView() {
        adapter = new MyFriendAdapter(R.layout.item_my_friend, BR.user, myFriendList);
        binding.lvMyFriend.setAdapter(adapter);
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
                UserInformationActivity.startActivity(MyFriendActivity.this, myFriendList.get(position).getUserId(),
                        true, myFriendList.get(position));
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
        Map<String, Integer> params = new HashMap<>(2);
        params.put("pageNum", 1);
        params.put("pageSize", 1000);
        RequestManager.getInstance(this).executeRequest(HttpUrls.GET_MY_FRIENDS, params, new AppCallBack<ApiResponse<List<User>>>() {
            @Override
            public void next(ApiResponse<List<User>> response) {
                if (response.isSuccess() && !response.getData().isEmpty()) {
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
                    for (User user : myFriendList) {
                        user.setFriend(true);
                    }
                    asyncSession.insertOrReplaceInTx(User.class, myFriendList);
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
