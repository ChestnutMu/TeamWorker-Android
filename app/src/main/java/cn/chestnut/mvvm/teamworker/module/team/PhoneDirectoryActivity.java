package cn.chestnut.mvvm.teamworker.module.team;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityPhoneDirectoryBinding;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/4 21:59:49
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class PhoneDirectoryActivity extends BaseActivity{

    private ActivityPhoneDirectoryBinding binding;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("选择团队成员");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_phone_directory,viewGroup,true);
    }


}
