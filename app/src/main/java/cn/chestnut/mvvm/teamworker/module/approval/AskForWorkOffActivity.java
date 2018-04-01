package cn.chestnut.mvvm.teamworker.module.approval;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
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
import cn.chestnut.mvvm.teamworker.model.Department;
import cn.chestnut.mvvm.teamworker.model.User;
import cn.chestnut.mvvm.teamworker.utils.CommonUtil;
import cn.chestnut.mvvm.teamworker.utils.Log;
import cn.chestnut.mvvm.teamworker.utils.StringUtil;
import cn.chestnut.mvvm.teamworker.utils.photo.ProcessPhotoUtils;
import cn.chestnut.mvvm.teamworker.widget.GlideLoader;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/3/13 12:08:43
 * Description：请假
 * Email: xiaoting233zhang@126.com
 */

public class AskForWorkOffActivity extends BaseActivity {

    // TODO: 2018/4/1 修改图片上传的逻辑

    private ActivityAskForWorkoffBinding binding;

    private String dateAndTime;

    private String qiniuToken;

    private String pictureKey;

    private String filePath;

    private String approverId;

    private ProcessPhotoUtils processPhotoUtils;

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
            filePath = cursor.getString(column_index);
            Log.d("filePath " + filePath);
        } else if (requestCode == ProcessPhotoUtils.SHOOT_PHOTO_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            filePath = processPhotoUtils.getMyPhotoFile().getPath();
            Log.d("filePath " + filePath);
        }
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
                processPhotoUtils = new ProcessPhotoUtils(AskForWorkOffActivity.this);
                processPhotoUtils.startPhoto();
            }
        });

        binding.ivApprover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDepartment();
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
                        dateAndTime = dateAndTime + " " + selectHourOfDay + ":" + selectMinute;
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
        Dialog dialog = new AlertDialog.Builder(this).setTitle("请选择请假类型")
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

    private void uploadPicture(String data, String token) {
        MyApplication.getUploadManager().put(data, null, token,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject res) {
                        //res包含hash、key等信息，具体字段取决于上传策略的设置
                        if (info.isOK()) {
                            Log.i("qiniu Upload Success");
                            try {
                                pictureKey = res.getString("key");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            GlideLoader.displayImage(AskForWorkOffActivity.this, HttpUrls.GET_PHOTO + pictureKey, binding.ivPicture);
                        } else {
                            Log.i("qiniu Upload Fail");
                            //如果失败，这里可以把info信息上报自己的服务器，便于后面分析上传错误原因
                        }
                        Log.i("qiniu " + key + ",\r\n " + info + ",\r\n " + res);
                    }
                }, null);

    }

    private void uploadPicture(final String data) {

        if (StringUtil.isEmpty(qiniuToken))

            RequestManager.getInstance(this).executeRequest(HttpUrls.GET_QINIUTOKEN, null, new AppCallBack<ApiResponse<String>>() {

                @Override
                public void next(ApiResponse<String> response) {
                    if (response.isSuccess()) {
                        qiniuToken = response.getData();
                        uploadPicture(data, qiniuToken);
                    }
                }

                @Override
                public void error(Throwable error) {
                    Log.e(error.toString());
                }

                @Override
                public void complete() {

                }

            });

        else {
            uploadPicture(data, qiniuToken);
        }
    }

    private void selectDepartment() {
        RequestManager.getInstance(this).executeRequest(HttpUrls.GET_DEPARTMENT_BY_USERID, null, new AppCallBack<ApiResponse<List<Department>>>() {
            @Override
            public void next(ApiResponse<List<Department>> response) {
                if (response.isSuccess()) {
                    final List<Department> departmentList = response.getData();
                    int departmentSize = departmentList.size();
                    if (departmentSize > 0) {
                        String[] departmentName = new String[departmentSize];
                        for (int i = 0; i < departmentSize; i++) {
                            departmentName[i] = departmentList.get(i).getDepartmentName();
                        }
                        new AlertDialog.Builder(AskForWorkOffActivity.this)
                                .setTitle("请选择部门")
                                .setItems(departmentName, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        selectApprover(departmentList.get(which).getDepartmentId());
                                    }
                                }).show();
                    }
                } else {
                    showToast(response.getMessage());
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

    private void selectApprover(String departmentId) {

        Map<String, Object> param = new HashMap<>(3);
        param.put("departmentId", departmentId);
        param.put("pageNum", 1);
        param.put("pageSize", 15);
        RequestManager.getInstance(this).executeRequest(HttpUrls.GET_USER_BY_DEPARTMENT, param, new AppCallBack<ApiResponse<List<User>>>() {
            @Override
            public void next(final ApiResponse<List<User>> response) {
                if (response.isSuccess()) {
                    // TODO: 2018/4/1 添加分页加载功能
                    CommonUtil.setBackgroundAlpha(0.5f, AskForWorkOffActivity.this);

                    PopupSelectApproverBinding popupBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.popup_select_approver, null, false);
                    BaseListViewAdapter<User> adapter = new BaseListViewAdapter<>(R.layout.item_work_off_approver, BR.user, response.getData());
                    popupBinding.lvWorkOffApprover.setAdapter(adapter);

                    final PopupWindow popupWindow = new PopupWindow(popupBinding.getRoot(), ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    popupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F8F8F8")));
                    popupWindow.setFocusable(true);
                    popupWindow.setOutsideTouchable(true);
                    popupWindow.showAtLocation(binding.getRoot(), Gravity.CENTER, 0, 0);

                    popupBinding.lvWorkOffApprover.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            User approver = response.getData().get(position);
                            approverId = approver.getUserId();
                            GlideLoader.displayImage(AskForWorkOffActivity.this, HttpUrls.GET_PHOTO + approver.getAvatar(), binding.ivApprover);
                            popupWindow.dismiss();
                        }
                    });
                    popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            CommonUtil.setBackgroundAlpha(1, AskForWorkOffActivity.this);
                        }
                    });


                } else {
                    showToast(response.getMessage());
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

    @BindingAdapter({"load_url_image"})
    public static void loadUrlImage(ImageView view, String url) {
        Log.d("url = " + HttpUrls.GET_PHOTO + url);
        GlideLoader.displayImage(MyApplication.getInstance(), HttpUrls.GET_PHOTO + url, view);
    }

}