package cn.chestnut.mvvm.teamworker.module.massage.fragment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.FragmentMessageBinding;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.main.common.BaseFragment;
import cn.chestnut.mvvm.teamworker.module.massage.activity.SendNotificationActivity;
import cn.chestnut.mvvm.teamworker.module.massage.adapter.MessageAdapter;
import cn.chestnut.mvvm.teamworker.module.massage.bean.Message;
import cn.chestnut.mvvm.teamworker.service.DataManager;
import cn.chestnut.mvvm.teamworker.utils.CommonUtil;
import cn.chestnut.mvvm.teamworker.utils.PreferenceUtil;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/1/2 11:05:44
 * Description：消息Fragment
 * Email: xiaoting233zhang@126.com
 */

public class MessageFragment extends BaseFragment {

    private FragmentMessageBinding binding;
    private MessageAdapter messageAdapter;
    private ArrayList<Message> messageList = new ArrayList<>();
    private String userId;
    private int pageNum = 1;
    private int pageSize = 15;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("消息");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_message, viewGroup, true);
        initData();
        initView();
    }

    @Override
    public void setButton(TextView edit, ImageView add, ImageView search) {
        super.setButton(edit, add, search);
        add.setVisibility(View.VISIBLE);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SendNotificationActivity.class);
                getActivity().startActivity(intent);
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        userId = PreferenceUtil.getInstances(getActivity()).getPreferenceString("userId");
        getMessagesByUserId(userId, pageNum, pageSize);
    }

    private void initView() {
        messageAdapter = new MessageAdapter(messageList);
        binding.swipeTarget.setAdapter(messageAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.swipeTarget.setLayoutManager(manager);
        binding.swipeToLoadLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (messageList != null) {
                    messageList.clear();
                    pageNum = 1;
                    getMessagesByUserId(userId, pageNum, pageSize);
                }
                binding.swipeToLoadLayout.setRefreshing(false);
            }
        });
        binding.swipeToLoadLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                if (messageList != null) {
                    pageNum++;
                    getMessagesByUserId(userId, pageNum, pageSize);
                }
                binding.swipeToLoadLayout.setLoadingMore(false);
            }
        });
    }

    /**
     * 获取消息列表
     */
    private void getMessagesByUserId(String userId, int pageNum, int pageSize) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("pageNum", pageNum);
        params.put("pageSize", pageSize);
        DataManager.getInstance(getActivity()).executeRequest(HttpUrls.GET_MESSAGES_BY_USERID, params, new AppCallBack<ApiResponse<List<Message>>>() {

            @Override
            public void next(ApiResponse<List<Message>> response) {
                if (response.isSuccess()) {
                    messageList.addAll(response.getData());
                    messageAdapter.notifyDataSetChanged();
                } else {
                    CommonUtil.showToast(response.getMessage(), getActivity());
                }
            }

            @Override
            public void error(Throwable error) {

            }

            @Override
            public void complete() {

            }

            @Override
            public void before() {
            }
        });

    }
}
