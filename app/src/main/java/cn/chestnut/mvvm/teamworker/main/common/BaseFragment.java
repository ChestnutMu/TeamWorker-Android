package cn.chestnut.mvvm.teamworker.main.common;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.FragmentBaseBinding;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/1/2 11:06:11
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public abstract class BaseFragment extends Fragment {

    private FragmentBaseBinding binding;
    private LayoutInflater mInflater;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_base, container, false);
        setBaseTitle(binding.tvTitle);
        mInflater = inflater;
        initViews();
        return binding.getRoot();
    }

    protected abstract void setBaseTitle(TextView titleView);

    protected abstract void addContainerView(ViewGroup viewGroup, LayoutInflater inflater);

    /**
     * 获取控件资源
     */
    protected void initViews() {
        addContainerView(binding.baseContainerLayout, mInflater);
        setButton(binding.edit, binding.add, binding.search);

    }

    /**
     * 显示右上角按钮，由子activity根据具体情况重写
     */

    public void setButton(TextView edit, ImageView add, ImageView search) {
        edit.setVisibility(View.GONE);
        add.setVisibility(View.GONE);
        search.setVisibility(View.GONE);
    }

}
