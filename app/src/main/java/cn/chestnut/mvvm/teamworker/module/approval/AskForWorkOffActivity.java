package cn.chestnut.mvvm.teamworker.module.approval;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityApprovalBinding;
import cn.chestnut.mvvm.teamworker.databinding.ActivityAskForWorkoffBinding;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.module.checkattendance.activity.CheckAttendanceActivity;
import cn.chestnut.mvvm.teamworker.module.massage.bean.User;
import cn.chestnut.mvvm.teamworker.module.mine.activity.MyInformationActivity;
import cn.chestnut.mvvm.teamworker.module.work.adapter.GridViewAdapter;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/3/13 12:08:43
 * Description：请假
 * Email: xiaoting233zhang@126.com
 */

public class AskForWorkOffActivity extends BaseActivity {

    private ActivityAskForWorkoffBinding binding;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("请假");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_ask_for_workoff, viewGroup, true);
        initData();
        initView();
        addListener();
    }

    private void initData() {

    }


    private void initView() {
    }

    private void addListener() {
        binding.llStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = new DatePickerDialog(
                        AskForWorkOffActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                int rightMonth = month + 1;
                                String birthday = year + "年" + rightMonth + "月" + dayOfMonth + "日";
                                User user = new User();
                                user.setBirthday(birthday);
                            }
                        },
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show();
            }
        });
    }
}