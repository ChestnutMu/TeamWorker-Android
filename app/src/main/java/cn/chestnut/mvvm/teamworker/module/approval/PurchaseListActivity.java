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

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityPurchaseListBinding;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.adapter.BaseRecyclerViewAdapter;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.model.Purchase;
import cn.chestnut.mvvm.teamworker.module.work.WorkFragment;
import cn.chestnut.mvvm.teamworker.utils.Log;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/28 21:49:18
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class PurchaseListActivity extends BaseActivity {

    private ActivityPurchaseListBinding binding;

    private List<Purchase> purchaseList;

    private BaseRecyclerViewAdapter adapter;

    private int pageNum = 1;

    private static int pageSize = 15;

    private int purchaseType;

    private String teamId;

    private final int REQUEST_CODE_PURCHASE_LIST = 1;

    private boolean isRefresh = false;

    private boolean isLoadMore = false;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("采购申请列表");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_purchase_list, viewGroup, true);
        initView();
        initData();
        addListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("resultCode:" + requestCode);
        if (requestCode == REQUEST_CODE_PURCHASE_LIST && resultCode == RESULT_OK) {
            isRefresh = true;
            isLoadMore = false;
            if (purchaseType == WorkFragment.MY_DATA_TYPE) {
                getMyPurchases();
            } else {
                getPurchasesForTeam();
            }
        }
    }

    @Override
    public void setButton(TextView edit, ImageView add, ImageView search) {
        if (purchaseType == WorkFragment.MY_DATA_TYPE) {
            add.setVisibility(View.VISIBLE);
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PurchaseListActivity.this, AskForPurchaseActivity.class);
                    intent.putExtra("teamId", teamId);
                    startActivityForResult(intent, REQUEST_CODE_PURCHASE_LIST);
                }
            });
        }
    }

    @Override
    protected void initView() {
        purchaseList = new ArrayList<>();
        adapter = new BaseRecyclerViewAdapter(purchaseList);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.swipeTarget.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        binding.swipeTarget.setLayoutManager(manager);
        binding.swipeTarget.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        teamId = getIntent().getStringExtra("teamId");
        purchaseType = getIntent().getIntExtra("purchaseType", 0);
        if (purchaseType == WorkFragment.MY_DATA_TYPE) {
            getMyPurchases();
        } else if (purchaseType == WorkFragment.TEAM_DATA_TYPE) {
            getPurchasesForTeam();
        }
    }

    @Override
    protected void addListener() {
        binding.swipeToLoadLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                isLoadMore = false;
                if (purchaseType == WorkFragment.MY_DATA_TYPE) {
                    getMyPurchases();
                } else if (purchaseType == WorkFragment.TEAM_DATA_TYPE) {
                    getPurchasesForTeam();
                }
                binding.swipeToLoadLayout.setRefreshing(false);
            }
        });

        binding.swipeToLoadLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                isRefresh = false;
                isLoadMore = true;
                if (purchaseType == WorkFragment.MY_DATA_TYPE) {
                    getMyPurchases();
                } else if (purchaseType == WorkFragment.TEAM_DATA_TYPE) {
                    getPurchasesForTeam();
                }
                binding.swipeToLoadLayout.setLoadingMore(false);
            }
        });

        adapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(PurchaseListActivity.this, PurchaseDetailActivity.class);
                intent.putExtra("purchase", purchaseList.get(position));
                startActivityForResult(intent, REQUEST_CODE_PURCHASE_LIST);
            }
        });
    }

    private void getMyPurchases() {
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
        RequestManager.getInstance(this).executeRequest(HttpUrls.GET_PURCHASES, params, new AppCallBack<ApiResponse<List<Purchase>>>() {
            @Override
            public void next(ApiResponse<List<Purchase>> response) {
                if (response.isSuccess()) {
                    if (isRefresh) {
                        if (purchaseList.size() > 0) {
                            purchaseList.clear();
                        }
                    }
                    if (response.getData().size() > 0) {
                        purchaseList.addAll(response.getData());
                    } else if (pageNum == 1) {
                        showToast("无采购申请数据");
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

    private void getPurchasesForTeam() {
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
        RequestManager.getInstance(this).executeRequest(HttpUrls.GET_PURCHASES_FOR_TEAM, params, new AppCallBack<ApiResponse<List<Purchase>>>() {
            @Override
            public void next(ApiResponse<List<Purchase>> response) {
                if (response.isSuccess()) {
                    if (isRefresh) {
                        if (purchaseList.size() > 0) {
                            purchaseList.clear();
                        }
                    }
                    if (response.getData().size() > 0) {
                        purchaseList.addAll(response.getData());
                    } else if (pageNum == 1) {
                        showToast("无采购申请数据");
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
