package cn.chestnut.mvvm.teamworker.module.approval;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.ArrayMap;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.chestnut.mvvm.teamworker.BR;
import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityAskForWorkoffBinding;
import cn.chestnut.mvvm.teamworker.databinding.PopupSelectApproverBinding;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.adapter.BaseListViewAdapter;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.main.common.MyApplication;
import cn.chestnut.mvvm.teamworker.model.Team;
import cn.chestnut.mvvm.teamworker.model.User;
import cn.chestnut.mvvm.teamworker.model.WorkOff;
import cn.chestnut.mvvm.teamworker.utils.CommonUtil;
import cn.chestnut.mvvm.teamworker.utils.GlideLoader;
import cn.chestnut.mvvm.teamworker.utils.Log;
import cn.chestnut.mvvm.teamworker.utils.PreferenceUtil;
import cn.chestnut.mvvm.teamworker.utils.StringUtil;
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

    private String qiniuToken;

    private String pictureKey;

    private String teamId;

    private ProcessPhotoUtils processPhotoUtils;

    private static int SELECT_START_TIME = 0;

    private static int SELECT_END_TIME = 1;

    private Long startTime;

    private Long endTime;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("申请请假");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_ask_for_workoff, viewGroup, true);
        initData();
        initView();
        addListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ProcessPhotoUtils.UPLOAD_PHOTO_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri originalUri = data.getData(); // 获得图片的uri
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = managedQuery(originalUri, proj, null, null, null);
            //获得用户选择的图片的索引值
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            // 将光标移至开头 ，这个很重要，不小心很容易引起越界
            cursor.moveToFirst();
            //获得图片的uri
            String filePath = cursor.getString(column_index);
            Log.d("filePath " + filePath);
            if (StringUtil.isStringNotNull(filePath)) {
                uploadPicture(filePath);
            }
        } else if (requestCode == ProcessPhotoUtils.SHOOT_PHOTO_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            String filePath = processPhotoUtils.getMyPhotoFile().getPath();
            if (StringUtil.isStringNotNull(filePath)) {
                uploadPicture(filePath);
            }
            Log.d("filePath " + filePath);
        }
    }

    protected void initData() {
        teamId = getIntent().getStringExtra("teamId");
    }


    protected void initView() {
    }

    protected void addListener() {
        binding.llStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateAndTime = null;
                selectDateAndTime(SELECT_START_TIME);// 0标识请假起始时间
            }
        });

        binding.llEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateAndTime = null;
                selectDateAndTime(SELECT_END_TIME);// 1标识请假结束时间
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
                processPhotoUtils = new ProcessPhotoUtils(AskForWorkOffActivity.this);
                processPhotoUtils.startPhoto();
            }
        });

        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtil.isStringNotNull(binding.tvWorkOffType.getText().toString())
                        && StringUtil.isStringNotNull(binding.tvStartTime.getText().toString())
                        && StringUtil.isStringNotNull(binding.tvEndTime.getText().toString())
                        && StringUtil.isStringNotNull(binding.etWorkOffReason.getText().toString())) {
                    applyWorkOff();
                } else {
                    showToast("请填写带红色*号的信息");
                }
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
                        selectTime(now, year, rightMonth, dayOfMonth, timeType);
                    }
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show();

    }

    private void selectTime(final Calendar now, final int year, final int month, final int day, final int timeType) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(AskForWorkOffActivity.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int selectHourOfDay, int selectMinute) {
                        dateAndTime = dateAndTime + " " + selectHourOfDay + ":" + selectMinute;
                        now.set(year, month, day, selectHourOfDay, selectMinute);
                        if (timeType == SELECT_START_TIME) {
                            startTime = now.getTimeInMillis();
                            binding.tvStartTime.setText(dateAndTime);
                        } else {
                            endTime = now.getTimeInMillis();
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
        Dialog dialog = new AlertDialog.Builder(this)
                .setItems(new String[]{"年假", "事假", "病假", "调休", "产假", "陪产假", "婚假", "例假", "丧假"},
                        new DialogInterface.OnClickListener() {
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

                        }).create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

    }

    private void uploadPicture(final String filePath, String token) {
        MyApplication.getUploadManager().put(filePath, null, token,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject res) {
                        hideProgressDialog();
                        //res包含hash、key等信息，具体字段取决于上传策略的设置
                        if (info.isOK()) {
                            Log.i("qiniu Upload Success");
                            try {
                                pictureKey = res.getString("key");
                                showToast("图片上传成功");
                                GlideLoader.displayImage(AskForWorkOffActivity.this, filePath, binding.ivPicture);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.i("qiniu Upload Fail");
                            showToast("图片上传失败");
                            //如果失败，这里可以把info信息上报自己的服务器，便于后面分析上传错误原因
                        }
                        Log.i("qiniu " + key + ",\r\n " + info + ",\r\n " + res);
                    }
                }, null);

    }

    private void uploadPicture(final String filePath) {
        showProgressDialog(this);
        if (StringUtil.isEmpty(qiniuToken))
            RequestManager.getInstance(this).executeRequest(HttpUrls.GET_QINIUTOKEN, null, new AppCallBack<ApiResponse<String>>() {

                @Override
                public void next(ApiResponse<String> response) {
                    if (response.isSuccess()) {
                        qiniuToken = response.getData();
                        uploadPicture(filePath, qiniuToken);
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

        else {
            uploadPicture(filePath, qiniuToken);
        }
    }

    private void applyWorkOff() {
        Map<String, Object> params = new ArrayMap<>();
        params.put("teamId", teamId);
        params.put("userNickname", PreferenceUtil.getInstances(this).getPreferenceString("nickname"));
        params.put("userAvatar", PreferenceUtil.getInstances(this).getPreferenceString("avatar"));
        params.put("workOffType", binding.tvWorkOffType.getText().toString());
        params.put("workOffReason", binding.etWorkOffReason.getText().toString());
        if (StringUtil.isStringNotNull(pictureKey)) {
            params.put("photo", pictureKey);
        }
        params.put("startTime", startTime);
        params.put("endTime", endTime);

        RequestManager.getInstance(this).executeRequest(HttpUrls.APPLY_WORK_OFF, params, new AppCallBack<ApiResponse<WorkOff>>() {
            @Override
            public void next(ApiResponse<WorkOff> response) {
                showToast(response.getMessage());
                if (response.isSuccess()) {
                    setResult(RESULT_OK);
                    finish();
                }
            }

            @Override
            public void error(Throwable error) {

            }

            @Override
            public void complete() {

            }
        });
    }

}