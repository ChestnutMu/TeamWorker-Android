package cn.chestnut.mvvm.teamworker.main.common;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.service.DataManager;
import cn.chestnut.mvvm.teamworker.utils.PermissionsUtil;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/10 20:49:36
 * Description：主界面
 * Email: xiaoting233zhang@126.com
 */

public class MainActivity extends BaseActivity {

    private View view;
    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("Team Worker");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        view = inflater.inflate(R.layout.activity_main, null);
        initData();
        initView();
        viewGroup.addView(view);
    }

    private void initData() {
        PermissionsUtil.checkAndRequestPermissions(MainActivity.this);
    }

    private void initView() {

    }

    /**
     * 获取apk更新信息
     */
    private void getUpdateInfo(String url) {
        DataManager.getInstance(MainActivity.this).getUpdateInfo(url);
    }

}
