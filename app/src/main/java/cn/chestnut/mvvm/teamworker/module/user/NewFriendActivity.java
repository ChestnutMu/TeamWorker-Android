package cn.chestnut.mvvm.teamworker.module.user;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import org.greenrobot.greendao.async.AsyncSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityNewFriendBinding;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.main.common.MyApplication;
import cn.chestnut.mvvm.teamworker.model.NewFriendRequest;
import cn.chestnut.mvvm.teamworker.model.User;
import cn.chestnut.mvvm.teamworker.model.UserInfo;
import cn.chestnut.mvvm.teamworker.utils.Log;
import cn.chestnut.mvvm.teamworker.utils.PreferenceUtil;
import cn.chestnut.mvvm.teamworker.utils.sqlite.DaoManager;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/6 17:21:14
 * Description：新朋友
 * Email: xiaoting233zhang@126.com
 */

public class NewFriendActivity extends BaseActivity {

    private ActivityNewFriendBinding binding;

    private List<NewFriendRequest> requestList;

    private NewFriendAdapter adapter;

    private NewFriendRequestDaoUtils friendRequestDaoUtils;

    /*本地数据操作异步工具类*/
    private AsyncSession asyncSession;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("新朋友");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_new_friend, viewGroup, true);
        initView();
        initData();
        addListener();
    }

    protected void initData() {
        asyncSession = DaoManager.getDaoSession().startAsyncSession();
        friendRequestDaoUtils = new NewFriendRequestDaoUtils();
        getRequestByUserId();
    }

    protected void initView() {
        requestList = new ArrayList<>();
        adapter = new NewFriendAdapter(this, requestList);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    protected void addListener() {
        adapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserInformationActivity.startActivity(NewFriendActivity.this, requestList.get(position).getRequesterId());
            }
        });
        adapter.setAcceptFriendRequestListener(new NewFriendAdapter.AcceptFriendRequestListener() {
            @Override
            public void acceptFriendRequest(NewFriendRequest request, int position) {
                acceptRequest(request, position);
            }
        });
    }

    private void getFriendRequest() {
        if (requestList.size() > 0) {
            requestList.clear();
        }
        requestList.addAll(friendRequestDaoUtils.queryNewFriendRequestByUserId(
                PreferenceUtil.getInstances(this).getPreferenceString("userId")));
        if (requestList.size() > 0) {
            adapter.notifyDataSetChanged();
        } else {
            showToast("无好友请求");
        }
    }

    private void acceptRequest(final NewFriendRequest request, final int position) {
        Map<String, String> params = new HashMap<>(1);
        params.put("newFriendRequestId", request.getNewFriendRequestId());
        showProgressDialog(this);
        RequestManager.getInstance(this).executeRequest(HttpUrls.ACCEPTED_REQUEST, params, new AppCallBack<ApiResponse<User>>() {
            @Override
            public void next(ApiResponse<User> response) {
                if (response.isSuccess()) {
                    UserInfo userInfo = new UserInfo();
                    userInfo.setUserId(response.getData().getUserId());
                    userInfo.setNickname(response.getData().getNickname());
                    userInfo.setAvatar(response.getData().getAvatar());
                    asyncSession.insertOrReplace(response.getData());
                    asyncSession.insertOrReplace(userInfo);
                    MyApplication.userInfoMap.put(userInfo.getUserId(), userInfo);
                    request.setAccepted(true);
                    Log.d("接受了好友的请求,newFriendRequest.accepted=" + request.getAccepted());
                    friendRequestDaoUtils.updateNewFriendRequest(request);
                    adapter.notifyItemChanged(position);
                }
                showToast(response.getMessage());
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

    private void getRequestByUserId() {
        showProgressDialog(this);
        RequestManager.getInstance(this).executeRequest(HttpUrls.GET_REQUEST_BY_USERID, null, new AppCallBack<ApiResponse<List<NewFriendRequest>>>() {
            @Override
            public void next(ApiResponse<List<NewFriendRequest>> response) {
                if (response.isSuccess()) {
                    if (friendRequestDaoUtils.insertMultNewFriendRequest(response.getData())) {
                        getFriendRequest();
                    }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        asyncSession = null;
    }
}
