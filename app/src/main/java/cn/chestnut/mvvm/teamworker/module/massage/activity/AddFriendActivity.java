package cn.chestnut.mvvm.teamworker.module.massage.activity;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/5 22:30:50
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class AddFriendActivity extends BaseActivity{

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("添加朋友");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {

    }
}
