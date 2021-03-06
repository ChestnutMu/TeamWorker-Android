package cn.chestnut.mvvm.teamworker.module.team;

import android.app.Activity;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.chestnut.mvvm.teamworker.BR;
import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivitySelectFromMyFriendBinding;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.model.User;
import cn.chestnut.mvvm.teamworker.module.mine.MyFriendAdapter;
import cn.chestnut.mvvm.teamworker.widget.WordsIndexBar;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/7 20:11:31
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class SelectFromMyFriendActivity extends BaseActivity {

    private ActivitySelectFromMyFriendBinding binding;

    private List<User> myFriendList = new ArrayList<>();

    private List<String> userIdList = new ArrayList<>();

    private MyFriendAdapter adapter;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("选择团队成员");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_select_from_my_friend, viewGroup, true);
        initView();
        initData();
        addListener();
    }

    @Override
    protected void initData() {
        userIdList = getIntent().getExtras().getStringArrayList("userIdList");
        getMyFriends();
    }

    @Override
    protected void initView() {
        adapter = new MyFriendAdapter(R.layout.item_my_friend, BR.user, myFriendList);
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
                User user = myFriendList.get(position);
                boolean isExist = false;
                for (String userId : userIdList) {
                    if (user.getUserId().equals(userId)) {
                        showToast("已选择的团队成员列表中已存在该好友");
                        isExist = true;
                        break;
                    }
                }
                if (!isExist) {
                    Intent intent = new Intent();
                    intent.putExtra("person", myFriendList.get(position));
                    SelectFromMyFriendActivity.this.setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    private void getMyFriends() {
        Map<String, Integer> params = new HashMap<>(2);
        params.put("pageNum", 1);
        params.put("pageSize", 1000);
        RequestManager.getInstance(this).executeRequest(HttpUrls.GET_MY_FRIENDS, params, new AppCallBack<ApiResponse<List<User>>>() {
            @Override
            public void next(ApiResponse<List<User>> response) {
                if (response.isSuccess()) {
                    myFriendList.addAll(response.getData());
                }
                Collections.sort(myFriendList, new Comparator<User>() {
                    @Override
                    public int compare(User lmf, User rmf) {
                        //根据拼音进行排序
                        return lmf.getPinyin().compareTo(rmf.getPinyin());
                    }
                });
                adapter.notifyDataSetChanged();
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
