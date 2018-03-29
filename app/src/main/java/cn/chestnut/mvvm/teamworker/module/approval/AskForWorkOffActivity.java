package cn.chestnut.mvvm.teamworker.module.approval;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityAskForWorkoffBinding;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.utils.photo.ProcessPhotoUtils;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/3/13 12:08:43
 * Description：请假
 * Email: xiaoting233zhang@126.com
 */

public class AskForWorkOffActivity extends BaseActivity {

    private ActivityAskForWorkoffBinding binding;

    private String dateAndTime;

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
                dateAndTime = null;
                selectDateAndTime(0);// 0标识请假起始时间
            }
        });

        binding.llEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateAndTime = null;
                selectDateAndTime(1);// 1标识请假结束时间
            }
        });

        binding.llOffWorkType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectOffWorkType();
            }
        });

        binding.ivPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProcessPhotoUtils processPhotoUtils = new ProcessPhotoUtils(AskForWorkOffActivity.this);
                processPhotoUtils.startPhoto();
            }
        });

    }

    private void selectDateAndTime(final int timeType) {
        final Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = new DatePickerDialog(
                AskForWorkOffActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        int rightMonth = month + 1;
                        dateAndTime = year + "年" + rightMonth + "月" + dayOfMonth + "日";
                        selectTime(now, timeType);
                    }
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show();

    }

    private void selectTime(Calendar now, final int timeType) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(AskForWorkOffActivity.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int selectHourOfDay, int selectMinute) {
                        dateAndTime = dateAndTime + selectHourOfDay + ":" + selectMinute;
                        if (timeType == 0) {
                            binding.tvStartTime.setText(dateAndTime);
                        } else {
                            binding.tvEndTime.setText(dateAndTime);
                        }
                    }
                },
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }

    private void selectOffWorkType() {
        new AlertDialog.Builder(this).setTitle("请选择请假类型")
                .setItems(new String[]{"年假", "事假", "病假", "调休", "产假", "陪产假", "婚假", "例假", "丧假"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: {
                                binding.tvWorkOffType.setText("年假");
                                break;
                            }
                            case 1: {
                                binding.tvWorkOffType.setText("事假");
                                break;
                            }
                            case 2: {
                                binding.tvWorkOffType.setText("病假");
                                break;
                            }
                            case 3: {
                                binding.tvWorkOffType.setText("调休");
                                break;
                            }
                            case 4: {
                                binding.tvWorkOffType.setText("产假");
                                break;
                            }
                            case 5: {
                                binding.tvWorkOffType.setText("陪产假");
                                break;
                            }
                            case 6: {
                                binding.tvWorkOffType.setText("婚假");
                                break;
                            }
                            case 7: {
                                binding.tvWorkOffType.setText("例假");
                                break;
                            }
                            case 8: {
                                binding.tvWorkOffType.setText("丧假");
                                break;
                            }
                        }
                        dialog.dismiss();
                    }

                })
                .show();

    }
}