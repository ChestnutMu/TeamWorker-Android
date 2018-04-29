package cn.chestnut.mvvm.teamworker.module.approval;

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

import cn.chestnut.mvvm.teamworker.BR;
import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityWorkOffListBinding;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.adapter.BaseListViewAdapter;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.model.WorkOff;
import cn.chestnut.mvvm.teamworker.module.checkattendance.PhotoActivity;
import cn.chestnut.mvvm.teamworker.module.mine.SelectRegionAdapter;
import cn.chestnut.mvvm.teamworker.utils.Log;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/28 21:49:18
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class WorkOffListActivity extends BaseActivity {

    private ActivityWorkOffListBinding binding;

    private List<WorkOff> workOffList;

    private WorkOffListAdapter adapter;

    public static int MY_WORK_OFF_TYPE = 1;

    public static int TEAM_WORK_OFF_TYPE = 2;

    private int pageNum = 1;

    private static int pageSize = 15;

    private int workOffType;

    private String teamId;

    private static int REQUEST_CODE_WORK_OFF_LIST = 1;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("请假条列表");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_work_off_list, viewGroup, true);
        initView();
        initData();
        addListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("resultCode:" + requestCode);
        if (requestCode == REQUEST_CODE_WORK_OFF_LIST && resultCode == RESULT_OK) {
            if (workOffType == MY_WORK_OFF_TYPE) {
                pageNum = 1;
                if (workOffList.size() > 0) {
                    workOffList.clear();
                }
                getMyAttendance();
            }
        }
    }

    @Override
    public void setButton(TextView edit, ImageView add, ImageView search) {
        if (workOffType == MY_WORK_OFF_TYPE) {
            add.setVisibility(View.VISIBLE);
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(WorkOffListActivity.this, AskForWorkOffActivity.class);
                    intent.putExtra("teamId", teamId);
                    startActivityForResult(intent, REQUEST_CODE_WORK_OFF_LIST);
                }
            });
        }
    }

    @Override
    protected void initView() {
        workOffList = new ArrayList<>();
        adapter = new WorkOffListAdapter(workOffList);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.swipeTarget.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        binding.swipeTarget.setLayoutManager(manager);
        binding.swipeTarget.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        teamId = getIntent().getStringExtra("teamId");
        workOffType = getIntent().getIntExtra("workOffType", 0);
        if (workOffType == MY_WORK_OFF_TYPE) {
            getMyAttendance();
        } else if (workOffType == TEAM_WORK_OFF_TYPE) {
            getWorkOffsForTeam();
        }
    }

    @Override
    protected void addListener() {
        binding.swipeToLoadLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (workOffList.size() > 0) {
                    workOffList.clear();
                }
                pageNum = 1;
                if (workOffType == MY_WORK_OFF_TYPE) {
                    getMyAttendance();
                } else if (workOffType == TEAM_WORK_OFF_TYPE) {
                    getWorkOffsForTeam();
                }
                binding.swipeToLoadLayout.setRefreshing(false);
            }
        });

        binding.swipeToLoadLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                pageNum++;
                if (workOffType == MY_WORK_OFF_TYPE) {
                    getMyAttendance();
                } else if (workOffType == TEAM_WORK_OFF_TYPE) {
                    getWorkOffsForTeam();
                }
                binding.swipeToLoadLayout.setLoadingMore(false);
            }
        });

        adapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(WorkOffListActivity.this, WorkOffDetailActivity.class);
                intent.putExtra("workOff", workOffList.get(position));
                startActivityForResult(intent, REQUEST_CODE_WORK_OFF_LIST);
            }
        });
    }

    private void getMyAttendance() {
        Map<String, Object> params = new HashMap<>();
        params.put("teamId", teamId);
        params.put("pageNum", pageNum);
        params.put("pageSize", pageSize);
        showProgressDialog(this);
        RequestManager.getInstance(this).executeRequest(HttpUrls.GET_WORK_OFFS, params, new AppCallBack<ApiResponse<List<WorkOff>>>() {
            @Override
            public void next(ApiResponse<List<WorkOff>> response) {
                if (response.isSuccess()) {
                    if (response.getData().size() > 0) {
                        workOffList.addAll(response.getData());
                    } else if (pageNum == 1) {
                        showToast("无请假条数据");
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

    private void getWorkOffsForTeam() {
        Map<String, Object> params = new HashMap<>();
        params.put("teamId", teamId);
        params.put("pageNum", pageNum);
        params.put("pageSize", pageSize);
        showProgressDialog(this);
        RequestManager.getInstance(this).executeRequest(HttpUrls.GET_WORK_OFFS_FOR_TEAM, params, new AppCallBack<ApiResponse<List<WorkOff>>>() {
            @Override
            public void next(ApiResponse<List<WorkOff>> response) {
                if (response.isSuccess()) {
                    if (response.getData().size() > 0) {
                        workOffList.addAll(response.getData());
                    } else if (pageNum == 1) {
                        showToast("无请假条数据");
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
