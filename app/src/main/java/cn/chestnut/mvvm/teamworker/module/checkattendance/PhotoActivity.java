package cn.chestnut.mvvm.teamworker.module.checkattendance;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityPhoneDirectoryBinding;
import cn.chestnut.mvvm.teamworker.databinding.ActivityPhotoBinding;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.utils.CommonUtil;
import cn.chestnut.mvvm.teamworker.utils.GlideLoader;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/23 16:17:42
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class PhotoActivity extends BaseActivity {

    private ActivityPhotoBinding binding;

    private float screenWidth;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("图片");

    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_photo, viewGroup, true);
    }

    @Override
    protected void initData() {
        screenWidth = CommonUtil.getScreenWidth(this);
    }

    @Override
    protected void initView() {
        GlideLoader.displayImage(this, HttpUrls.GET_PHOTO + getIntent().getStringExtra("photoUri"), binding.ivPhoto);
    }
}
