package cn.chestnut.mvvm.teamworker.module.report;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityMonthReportDetailBinding;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.model.MonthReport;
import cn.chestnut.mvvm.teamworker.model.WeekReport;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/29 14:55:02
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class MonthReportDetailActivity extends BaseActivity {

    private ActivityMonthReportDetailBinding binding;

    private MonthReport monthReport;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("月报");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_month_report_detail, viewGroup, true);
        initView();
        addListener();
    }

    @Override
    protected void initView() {
        monthReport = (MonthReport) getIntent().getSerializableExtra("monthReport");
        binding.setMonthReport(monthReport);
    }

}
