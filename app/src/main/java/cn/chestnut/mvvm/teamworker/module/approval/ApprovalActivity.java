package cn.chestnut.mvvm.teamworker.module.approval;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityApprovalBinding;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.module.work.GridViewAdapter;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/3/11 14:49:38
 * Description：审批
 * Email: xiaoting233zhang@126.com
 */

public class ApprovalActivity extends BaseActivity {

    private ActivityApprovalBinding binding;
    private GridViewAdapter gridViewAdapter;
    private ArrayList<String> nameList;
    private ArrayList<Integer> drawableList;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("审批");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_approval, viewGroup, true);
        initData();
        initView();
        addListener();
    }

    protected void initData() {
        nameList = new ArrayList<>();
        nameList.add("请假");
        nameList.add("报销");

        drawableList = new ArrayList<>();
        drawableList.add(R.mipmap.icon_offwork);
        drawableList.add(R.mipmap.icon_reimbursement);
    }

    protected void initView() {
        gridViewAdapter = new GridViewAdapter(this, nameList, drawableList);
        binding.gvApproval.setAdapter(gridViewAdapter);
    }

    protected void addListener() {
        binding.gvApproval.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (drawableList.get(position)) {
                    case R.mipmap.icon_offwork:
                        startActivity(new Intent(ApprovalActivity.this, AskForWorkOffActivity.class));
                        break;
                    case R.mipmap.icon_decrusement:
                        startActivity(new Intent(ApprovalActivity.this, ApprovalActivity.class));
                        break;
                }
            }
        });

    }

}
