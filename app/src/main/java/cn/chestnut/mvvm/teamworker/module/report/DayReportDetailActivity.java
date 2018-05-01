package cn.chestnut.mvvm.teamworker.module.report;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityDayReportDetailBinding;
import cn.chestnut.mvvm.teamworker.databinding.ActivityWorkOffDetailBinding;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.model.DayReport;
import cn.chestnut.mvvm.teamworker.model.WorkOff;
import cn.chestnut.mvvm.teamworker.utils.PreferenceUtil;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/29 14:55:02
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class DayReportDetailActivity extends BaseActivity {

    private ActivityDayReportDetailBinding binding;

    private DayReport dayReport;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("日报");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_day_report_detail, viewGroup, true);
        initView();
        addListener();
    }

    @Override
    protected void initView() {
        dayReport = (DayReport) getIntent().getSerializableExtra("dayReport");
        binding.setDayReport(dayReport);
    }

}
