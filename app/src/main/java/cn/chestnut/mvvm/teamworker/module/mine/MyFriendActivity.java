package cn.chestnut.mvvm.teamworker.module.mine;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.chestnut.mvvm.teamworker.BR;
import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityMyFriendBinding;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.model.MyFriend;
import cn.chestnut.mvvm.teamworker.module.user.UserInformationActivity;
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

    private List<MyFriend> myFriendList = new ArrayList<>();

    private MyFriendAdapter adapter;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("我的好友");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_my_friend, viewGroup, true);
        initView();
        initData();
        addListener();
    }

    @Override
    protected void initData() {
        getMyFriends();
    }

    @Override
    protected void initView() {
        adapter = new MyFriendAdapter(R.layout.item_my_friend, BR.myFriend, myFriendList);
        binding.lvMyFriend.setAdapter(adapter);
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
                Intent intent = new Intent(MyFriendActivity.this, UserInformationActivity.class);
                intent.putExtra("userId", myFriendList.get(position).getUserId());
                startActivity(intent);
            }
        });
    }

    private void getMyFriends() {
        showProgressDialog(this);
        RequestManager.getInstance(this).executeRequest(HttpUrls.GET_MY_FRIENDS, null, new AppCallBack<ApiResponse<List<MyFriend>>>() {
            @Override
            public void next(ApiResponse<List<MyFriend>> response) {
                if (response.isSuccess()) {
                    myFriendList.addAll(response.getData());
                }
                Collections.sort(myFriendList, new Comparator<MyFriend>() {
                    @Override
                    public int compare(MyFriend lmf, MyFriend rmf) {
                        //根据拼音进行排序
                        return lmf.getPinyin().compareTo(rmf.getPinyin());
                    }
                });
                adapter.notifyDataSetChanged();
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
