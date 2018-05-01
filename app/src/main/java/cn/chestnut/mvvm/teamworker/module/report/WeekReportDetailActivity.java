package cn.chestnut.mvvm.teamworker.module.report;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityDayReportDetailBinding;
import cn.chestnut.mvvm.teamworker.databinding.ActivityWeekReportDetailBinding;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.model.DayReport;
import cn.chestnut.mvvm.teamworker.model.WeekReport;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/29 14:55:02
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class WeekReportDetailActivity extends BaseActivity {

    private ActivityWeekReportDetailBinding binding;

    private WeekReport weekReport;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("周报");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_week_report_detail, viewGroup, true);
        initView();
        addListener();
    }

    @Override
    protected void initView() {
        weekReport = (WeekReport) getIntent().getSerializableExtra("weekReport");
        binding.setWeedReport(weekReport);
    }

}
