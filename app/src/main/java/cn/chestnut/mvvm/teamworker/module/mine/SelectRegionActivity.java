package cn.chestnut.mvvm.teamworker.module.mine;

import android.content.Intent;
import android.databinding.DataBindingUtil;
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
import cn.chestnut.mvvm.teamworker.databinding.ActivitySelectRegionBinding;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.model.Address;
import cn.chestnut.mvvm.teamworker.utils.Log;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/8 19:08:25
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class SelectRegionActivity extends BaseActivity {

    private ActivitySelectRegionBinding binding;

    private SelectRegionAdapter adapter;

    private List<Address> addressList;

    private String prAddressId = "";

    private String lastPrAddressId = "";

    private int pageSize = 15;

    private int pageNum = 1;

    private int maxCount = 0;

    private int level = 0;//0标识省, 1标识市, 2标识区

    private String province;

    private String city;

    private String area;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("选择地区");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_select_region, viewGroup, true);
        initData();
        initView();
        addListener();
    }

    @Override
    protected void initView() {
        adapter = new SelectRegionAdapter(addressList);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.swipeTarget.setLayoutManager(manager);
        binding.swipeTarget.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        addressList = new ArrayList<>();
        getAddressList();
    }

    @Override
    protected void addListener() {
        binding.swipeToLoadLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageNum = 1;
                if (addressList.size() > 0) {
                    addressList.clear();
                }
                getAddressList();
                binding.swipeToLoadLayout.setRefreshing(false);
            }
        });
        binding.swipeToLoadLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                pageNum++;
                getAddressList();
                binding.swipeToLoadLayout.setLoadingMore(false);
            }
        });
        adapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (level == 0) {
                    province = addressList.get(position).getName();
                    lastPrAddressId = prAddressId;
                    prAddressId = addressList.get(position).getAddressId();
                    if (addressList.size() > 0) {
                        addressList.clear();
                    }
                    pageNum = 1;
                    level = 1;
                    getAddressList();
                } else if (level == 1) {
                    city = addressList.get(position).getName();
                    lastPrAddressId = prAddressId;
                    prAddressId = addressList.get(position).getAddressId();
                    if (addressList.size() > 0) {
                        addressList.clear();
                    }
                    pageNum = 1;
                    level = 2;
                    getAddressList();
                } else {
                    area = addressList.get(position).getName();
                    Intent intent = new Intent();
                    intent.putExtra("region", province + city + area);
                    SelectRegionActivity.this.setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (level == 2) {
            prAddressId = lastPrAddressId;
            if (addressList.size() > 0) {
                addressList.clear();
            }
            pageNum = 1;
            level = 1;
            getAddressList();
        } else if (level == 1) {
            prAddressId = "";
            if (addressList.size() > 0) {
                addressList.clear();
            }
            pageNum = 1;
            level = 0;
            getAddressList();
        } else if (level == 0) {
            super.onBackPressed();
        }

    }

    private void getAddressList() {
        Map<String, String> params = new HashMap<>(3);
        params.put("prAddressId", prAddressId);
        params.put("pageSize", String.valueOf(pageSize));
        params.put("pageNum", String.valueOf(pageNum));
        showProgressDialog(this);
        RequestManager.getInstance(this).executeRequest(HttpUrls.GET_ADDRESSES, params, new AppCallBack<ApiResponse<List<Address>>>() {
            @Override
            public void next(ApiResponse<List<Address>> response) {
                if (response.isSuccess()) {
                    if (response.getData().size() > 0) {
                        addressList.addAll(response.getData());
                        adapter.notifyDataSetChanged();
                    } else if (pageNum > 1) {
                        showToast("数据已加载完全");
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

}

