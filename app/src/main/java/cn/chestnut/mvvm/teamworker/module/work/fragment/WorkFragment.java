package cn.chestnut.mvvm.teamworker.module.work.fragment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.FragmentWorkBinding;
import cn.chestnut.mvvm.teamworker.main.common.BaseFragment;
import cn.chestnut.mvvm.teamworker.module.checkattendance.activity.CheckAttendanceActivity;
import cn.chestnut.mvvm.teamworker.module.work.adapter.GridViewAdapter;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/1/6 18:49:31
 * Description：工作Fragment
 * Email: xiaoting233zhang@126.com
 */

public class WorkFragment extends BaseFragment {

    private FragmentWorkBinding binding;
    private GridViewAdapter gridViewAdapter;
    private ArrayList<String> nameList;
    private ArrayList<Integer> drawableList;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("工作");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_work, viewGroup, true);
        initData();
        initView();
        addListener();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        nameList = new ArrayList<>();
        nameList.add("考勤打卡");
        nameList.add("审批");
        nameList.add("考勤打卡");
        nameList.add("审批");

        drawableList = new ArrayList<>();
        drawableList.add(R.mipmap.icon_attendance);
        drawableList.add(R.mipmap.icon_approval);
        drawableList.add(R.mipmap.icon_attendance);
        drawableList.add(R.mipmap.icon_approval);
    }

    /**
     * 初始化界面
     */
    private void initView() {
        gridViewAdapter = new GridViewAdapter(getActivity(), nameList, drawableList);
        binding.gvCommonApps.setAdapter(gridViewAdapter);
    }

    /**
     * 添加监听器
     */
    private void addListener() {
        binding.gvCommonApps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (drawableList.get(position)) {
                    case R.mipmap.icon_attendance:
                        startActivity(new Intent(getActivity(), CheckAttendanceActivity.class));
                        break;
                    case R.mipmap.icon_approval:
                        startActivity(new Intent(getActivity(), CheckAttendanceActivity.class));
                        break;
                }
            }
        });
    }

}
