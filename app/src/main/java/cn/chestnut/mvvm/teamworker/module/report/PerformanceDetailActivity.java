package cn.chestnut.mvvm.teamworker.module.report;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityPerformanceDetailBinding;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.model.Performance;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/29 14:55:02
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class PerformanceDetailActivity extends BaseActivity {

    private ActivityPerformanceDetailBinding binding;

    private Performance performance;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("绩效自评");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_performance_detail, viewGroup, true);
        initView();
        addListener();
    }

    @Override
    protected void initView() {
        performance = (Performance) getIntent().getSerializableExtra("performance");
        binding.setPerformance(performance);
    }

}
