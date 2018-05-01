package cn.chestnut.mvvm.teamworker.module.report;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.FragmentWorkReportListBinding;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.adapter.BaseRecyclerViewAdapter;
import cn.chestnut.mvvm.teamworker.model.WeekReport;
import cn.chestnut.mvvm.teamworker.module.work.WorkFragment;
import cn.chestnut.mvvm.teamworker.utils.CommonUtil;
import cn.chestnut.mvvm.teamworker.utils.ProgressDialogShow;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/3 13:51:23
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class WeekReportFragment extends Fragment {

    private FragmentWorkReportListBinding binding;

    private List<WeekReport> weekReportList;

    private BaseRecyclerViewAdapter adapter;

    private int pageNum = 1;

    private static int pageSize = 15;

    private String teamId;

    private static int REQUEST_CODE_WEEK_REPORT_LIST = 1;

    private boolean isRefresh = false;

    private boolean isLoadMore = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_work_report_list, container, false);
        initView();
        initData();
        addListener();
        return binding.getRoot();
    }

    private void initView() {
        weekReportList = new ArrayList<>();
        adapter = new BaseRecyclerViewAdapter(weekReportList);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.swipeTarget.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        binding.swipeTarget.setLayoutManager(manager);
        binding.swipeTarget.setAdapter(adapter);
    }

    private void initData() {
        teamId = getActivity().getIntent().getStringExtra("teamId");
        getWeekReportsForTeam();

    }

    private void addListener() {
        binding.swipeToLoadLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                isLoadMore = false;
                getWeekReportsForTeam();
                binding.swipeToLoadLayout.setRefreshing(false);
            }
        });

        binding.swipeToLoadLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                isLoadMore = true;
                isRefresh = false;
                getWeekReportsForTeam();
                binding.swipeToLoadLayout.setLoadingMore(false);
            }
        });

        adapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), WeekReportDetailActivity.class);
                intent.putExtra("weekReport", weekReportList.get(position));
                startActivityForResult(intent, REQUEST_CODE_WEEK_REPORT_LIST);
            }
        });
    }

    private void getWeekReportsForTeam() {
        if(isRefresh){
            pageNum = 1;
        }else if (isLoadMore) {
            pageNum++;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("teamId", teamId);
        params.put("pageNum", pageNum);
        params.put("pageSize", pageSize);
        ProgressDialogShow.showProgress(getActivity());
        RequestManager.getInstance(getActivity()).executeRequest(HttpUrls.GET_WEEK_REPORTS_FOR_TEAM, params, new AppCallBack<ApiResponse<List<WeekReport>>>() {
            @Override
            public void next(ApiResponse<List<WeekReport>> response) {
                if (response.isSuccess()) {
                    if (isRefresh) {
                        if (weekReportList.size() > 0) {
                            weekReportList.clear();
                        }
                    }
                    if (response.getData().size() > 0) {
                        weekReportList.addAll(response.getData());
                    } else if (pageNum == 1) {
                        CommonUtil.showToast("无周报数据", getActivity());
                    } else if (pageNum > 1) {
                        CommonUtil.showToast("数据已加载完全", getActivity());
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    CommonUtil.showToast(response.getMessage(), getActivity());
                }
            }

            @Override
            public void error(Throwable error) {
                ProgressDialogShow.cancleProgressDialog();
            }

            @Override
            public void complete() {
                ProgressDialogShow.cancleProgressDialog();
            }
        });
    }
}
