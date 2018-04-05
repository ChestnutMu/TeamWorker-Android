package cn.chestnut.mvvm.teamworker.module.work;

import android.databinding.DataBindingUtil;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.FragmentWorkWaitBinding;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/3 13:51:23
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class WorkWaitDealFragment extends Fragment {

    private FragmentWorkWaitBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_work_wait, container, false);
        initData();
        initView();
        addListener();
        return binding.getRoot();
    }

    private void initData() {

    }

    private void initView() {
    }

    private void addListener() {

    }

    private void getWorkWaitDealNotification(){

    }

}
