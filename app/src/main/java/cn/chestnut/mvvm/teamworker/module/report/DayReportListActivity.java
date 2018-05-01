package cn.chestnut.mvvm.teamworker.module.report;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityDayReportListBinding;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.adapter.BaseRecyclerViewAdapter;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.model.DayReport;
import cn.chestnut.mvvm.teamworker.module.approval.AskForWorkOffActivity;
import cn.chestnut.mvvm.teamworker.module.approval.WorkOffDetailActivity;
import cn.chestnut.mvvm.teamworker.module.work.WorkFragment;
import cn.chestnut.mvvm.teamworker.utils.Log;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/28 21:49:18
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class DayReportListActivity extends BaseActivity {

    private ActivityDayReportListBinding binding;

    private List<DayReport> dayReportList;

    private BaseRecyclerViewAdapter adapter;

    private int pageNum = 1;

    private static int pageSize = 15;

    private int dayReportType;

    private String teamId;

    private TextView tvTitle;

    private final int REQUEST_CODE_DAY_REPORT_LIST = 1;

    private boolean isRefresh = false;

    private boolean isLoadMore = false;

    @Override
    protected void setBaseTitle(TextView titleView) {
        tvTitle = titleView;
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_day_report_list, viewGroup, true);
        initView();
        initData();
        addListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("resultCode:" + requestCode);
        if (requestCode == REQUEST_CODE_DAY_REPORT_LIST && resultCode == RESULT_OK) {
            if (dayReportType == WorkFragment.MY_DATA_TYPE) {
                isRefresh = true;
                isLoadMore = false;
                getMyDayReports();
            }
        }
    }

    @Override
    public void setButton(TextView edit, ImageView add, ImageView search) {
        if (dayReportType == WorkFragment.MY_DATA_TYPE) {
            add.setVisibility(View.VISIBLE);
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DayReportListActivity.this, SubmitDayReportActivity.class);
                    intent.putExtra("teamId", teamId);
                    startActivityForResult(intent, REQUEST_CODE_DAY_REPORT_LIST);
                }
            });
        }
    }

    @Override
    protected void initView() {
        dayReportList = new ArrayList<>();
        adapter = new BaseRecyclerViewAdapter(dayReportList);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.swipeTarget.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        binding.swipeTarget.setLayoutManager(manager);
        binding.swipeTarget.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        teamId = getIntent().getStringExtra("teamId");
        dayReportType = getIntent().getIntExtra("dayReportType", 0);
        if (dayReportType == WorkFragment.MY_DATA_TYPE) {
            tvTitle.setText("我的日报");
            getMyDayReports();
        } else if (dayReportType == WorkFragment.TEAM_DATA_TYPE) {
            tvTitle.setText("团队成员的日报");
            getDayReportsForTeam();
        }
    }

    @Override
    protected void addListener() {
        binding.swipeToLoadLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                isLoadMore = false;
                if (dayReportType == WorkFragment.MY_DATA_TYPE) {
                    getMyDayReports();
                } else if (dayReportType == WorkFragment.TEAM_DATA_TYPE) {
                    getDayReportsForTeam();
                }
                binding.swipeToLoadLayout.setRefreshing(false);
            }
        });

        binding.swipeToLoadLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                isRefresh = false;
                isLoadMore = true;
                if (dayReportType == WorkFragment.MY_DATA_TYPE) {
                    getMyDayReports();
                } else if (dayReportType == WorkFragment.TEAM_DATA_TYPE) {
                    getDayReportsForTeam();
                }
                binding.swipeToLoadLayout.setLoadingMore(false);
            }
        });

        adapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(DayReportListActivity.this, DayReportDetailActivity.class);
                intent.putExtra("dayReport", dayReportList.get(position));
                startActivityForResult(intent, REQUEST_CODE_DAY_REPORT_LIST);
            }
        });
    }

    private void getMyDayReports() {
        showProgressDialog(this);
        if (isRefresh) {
            pageNum = 1;
        } else if (isLoadMore) {
            pageNum++;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("teamId", teamId);
        params.put("pageNum", pageNum);
        params.put("pageSize", pageSize);
        RequestManager.getInstance(this).executeRequest(HttpUrls.GET_DAY_REPORTS, params, new AppCallBack<ApiResponse<List<DayReport>>>() {
            @Override
            public void next(ApiResponse<List<DayReport>> response) {
                if (response.isSuccess()) {
                    if (isRefresh) {
                        if (dayReportList.size() > 0) {
                            dayReportList.clear();
                        }
                    }
                    if (response.getData().size() > 0) {
                        dayReportList.addAll(response.getData());
                    } else if (pageNum == 1) {
                        showToast("无日报数据");
                    } else if (pageNum > 1) {
                        showToast("数据已加载完全");
                    }
                    adapter.notifyDataSetChanged();
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

    private void getDayReportsForTeam() {
        showProgressDialog(this);
        if (isRefresh) {
            pageNum = 1;
        } else if (isLoadMore) {
            pageNum++;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("teamId", teamId);
        params.put("pageNum", pageNum);
        params.put("pageSize", pageSize);
        RequestManager.getInstance(this).executeRequest(HttpUrls.GET_DAY_REPORTS_FOR_TEAM, params, new AppCallBack<ApiResponse<List<DayReport>>>() {
            @Override
            public void next(ApiResponse<List<DayReport>> response) {
                if (response.isSuccess()) {
                    if (isRefresh) {
                        if (dayReportList.size() > 0) {
                            dayReportList.clear();
                        }
                    }
                    if (response.getData().size() > 0) {
                        dayReportList.addAll(response.getData());
                    } else if (pageNum == 1) {
                        showToast("无日报数据");
                    } else if (pageNum > 1) {
                        showToast("数据已加载完全");
                    }
                    adapter.notifyDataSetChanged();
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
