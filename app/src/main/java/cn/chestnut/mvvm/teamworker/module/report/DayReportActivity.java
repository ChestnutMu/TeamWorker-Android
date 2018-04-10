package cn.chestnut.mvvm.teamworker.module.report;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/9 17:11:57
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class DayReportActivity extends BaseActivity{



    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("日报");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {

    }
}
