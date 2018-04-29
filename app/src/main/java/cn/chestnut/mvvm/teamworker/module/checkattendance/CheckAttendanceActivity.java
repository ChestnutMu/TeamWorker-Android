package cn.chestnut.mvvm.teamworker.module.checkattendance;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityCheckAttendanceBinding;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.model.Attendance;
import cn.chestnut.mvvm.teamworker.utils.FormatDateUtil;
import cn.chestnut.mvvm.teamworker.utils.GlideLoader;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/28 15:26:20
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class CheckAttendanceActivity extends BaseActivity {

    private ActivityCheckAttendanceBinding binding;

    private AttendanceAdapter adapter;

    private List<Attendance> attendanceList;

    private String teamId;

    private String teamUserId;

    private String teamUserName;

    private String teamUserAvatar;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("考勤记录");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_check_attendance, viewGroup, true);
        initData();
        initView();
        addListener();
    }

    @Override
    protected void initData() {
        teamId = getIntent().getStringExtra("teamId");
        teamUserId = getIntent().getStringExtra("teamUserId");
        teamUserName = getIntent().getStringExtra("teamUserName");
        teamUserAvatar = getIntent().getStringExtra("teamUserAvatar");
        attendanceList = new ArrayList<>();
        adapter = new AttendanceAdapter(attendanceList);
    }

    @Override
    protected void initView() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.swipeTarget.setLayoutManager(manager);
        binding.swipeTarget.setAdapter(adapter);

        binding.tvUserName.setText(teamUserName);
        GlideLoader.displayImage(this,
                HttpUrls.GET_PHOTO + teamUserAvatar,
                binding.ivAvatar);
    }

    @Override
    protected void addListener() {
        binding.calendarView.setOnDateSelectedListener(new CalendarView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Calendar calendar, boolean isClick) {
                binding.tvTime.setText(calendar.getYear() + "年 " + calendar.getMonth() + "月");
                getAttendance(calendar);
            }
        });

        binding.tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.calendarView.showYearSelectLayout(binding.calendarView.getCurYear());
            }
        });
    }

    private void getAttendance(Calendar calendar) {
        Map<String, Object> params = new HashMap<>();
        params.put("teamId", teamId);
        params.put("teamUserId", teamUserId);
        params.put("startTime", FormatDateUtil.getTodayMinTimeMillis(calendar));
        params.put("endTime", FormatDateUtil.getTodayMaxTimeMillis(calendar));
        params.put("pageNum", 1);
        params.put("pageSize", 1000);
        showProgressDialog(this);
        RequestManager.getInstance(this).executeRequest(HttpUrls.GET_PUNCH_CLOCK_RECORDS_BY_TEAM, params, new AppCallBack<ApiResponse<List<Attendance>>>() {
            @Override
            public void next(ApiResponse<List<Attendance>> response) {
                if (response.isSuccess()) {
                    if (attendanceList.size() > 0) {
                        attendanceList.clear();
                    }
                    if (response.getData().size() > 0) {
                        attendanceList.addAll(response.getData());
                        adapter.notifyDataSetChanged();
                    }

                } else {
                    showToast(response.getMessage());
                }
            }

            @Override
            public void error(Throwable error) {
                hideProgressDialog();
            }

            @Override
            public void complete() {
                hideProgressDialog();
            }

        });
    }
}
