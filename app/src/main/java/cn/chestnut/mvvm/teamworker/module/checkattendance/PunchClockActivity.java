package cn.chestnut.mvvm.teamworker.module.checkattendance;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityPunchClockBinding;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.main.common.MyApplication;
import cn.chestnut.mvvm.teamworker.model.Attendance;
import cn.chestnut.mvvm.teamworker.utils.FormatDateUtil;
import cn.chestnut.mvvm.teamworker.utils.GlideLoader;
import cn.chestnut.mvvm.teamworker.utils.Log;
import cn.chestnut.mvvm.teamworker.utils.StringUtil;
import cn.chestnut.mvvm.teamworker.utils.photo.ProcessPhotoUtils;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/1/3 11:59:33
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class PunchClockActivity extends BaseActivity {

    private ActivityPunchClockBinding binding;

    private AttendanceAdapter adapter;

    //定位
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;

    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    private String detailAddress;//详细地址

    private List<Attendance> attendanceList;

    private String qiniuToken;

    private ProcessPhotoUtils processPhotoUtils;

    private String uploadPictureKey;

    private String filePath;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("考勤");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_punch_clock, viewGroup, true);
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
            uploadPicture(filePath);
        } else if (requestCode == ProcessPhotoUtils.SHOOT_PHOTO_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            filePath = processPhotoUtils.getMyPhotoFile().getPath();
            Log.d("filePath " + filePath);
            uploadPicture(filePath);
        }
    }

    protected void initData() {
        initLocation();
        getAttendance();
    }

    protected void initView() {
        processPhotoUtils = new ProcessPhotoUtils(this);
        attendanceList = new ArrayList<>();
        adapter = new AttendanceAdapter(attendanceList);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.swipeTarget.setLayoutManager(manager);
        binding.swipeTarget.setAdapter(adapter);
    }

    protected void addListener() {
        binding.ivPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processPhotoUtils.startPhoto();
            }
        });

        binding.upWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                punchClock();
            }
        });

        //点击打卡记录列表中的图片，则查看大图
        adapter.setOnPunchPhotoClickListener(new AttendanceAdapter.OnPunchPhotoClickListener() {
            @Override
            public void onPunchInPhotoClick(int position) {
                Intent intent = new Intent(PunchClockActivity.this, PhotoActivity.class);
                intent.putExtra("photoUri", attendanceList.get(position).getPunchInPicture());
                startActivity(intent);
            }

            @Override
            public void onPunchOutPhotoClick(int position) {

            }
        });
    }

    /**
     * 定位初始化
     */
    private void initLocation() {
        //初始化定位
        mLocationClient = new AMapLocationClient(MyApplication.getInstance());
        //设置定位回调监听
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {
                        //可在其中解析amapLocation获取相应内容。
                        Log.d("onLocationChanged: amapLocation.getAddress() = " + aMapLocation.getAddress());
                        detailAddress = aMapLocation.getAddress();
                        binding.showLocation.setText(detailAddress);
                    } else {
                        //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                        Log.e("location Error, ErrCode:"
                                + aMapLocation.getErrorCode() + ", errInfo:"
                                + aMapLocation.getErrorInfo() + ":" + aMapLocation.getLocationDetail());
                    }
                }
            }
        });

        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //该方法默认为false。
        mLocationOption.setOnceLocation(true);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);

        locationMyself();
    }

    public void locationMyself() {
        Log.d("getPermissionBlankMethod--------------------------");
        //启动定位
        if (mLocationClient != null)
            mLocationClient.startLocation();
    }

    private void getAttendance() {
        Map<String, Object> params = new HashMap<>();
        params.put("teamId", getIntent().getStringExtra("teamId"));
        params.put("startTime", FormatDateUtil.getThisMonthFirstDayMinTimeMillsis());
        params.put("endTime", System.currentTimeMillis());
        params.put("pageNum", 1);
        params.put("pageSize", 1000);
        showProgressDialog(this);
        RequestManager.getInstance(this).executeRequest(HttpUrls.GET_PUNCH_CLOCK_RECORDS, params, new AppCallBack<ApiResponse<List<Attendance>>>() {
            @Override
            public void next(ApiResponse<List<Attendance>> response) {
                if (response.isSuccess()) {
                    attendanceList.addAll(response.getData());
                    adapter.notifyDataSetChanged();
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

    private void punchClock() {
        Map<String, Object> params = new HashMap<>();
        params.put("teamId", getIntent().getStringExtra("teamId"));
        params.put("punchClockAddress", detailAddress);
        if (StringUtil.isStringNotNull(uploadPictureKey)) {
            params.put("picture", uploadPictureKey);
        }

        showProgressDialog(this);
        RequestManager.getInstance(this).executeRequest(HttpUrls.PUNCH_CLOCK, params, new AppCallBack<ApiResponse<Attendance>>() {
            @Override
            public void next(ApiResponse<Attendance> response) {
                showToast(response.getMessage());
                if (response.isSuccess()) {
                    if (attendanceList.size() > 0) {
                        attendanceList.clear();
                    }
                    getAttendance();
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

    private void uploadPicture(String data, String token) {
        String key = null;
        showProgressDialog(this);
        MyApplication.getUploadManager().put(data, key, token,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject res) {
                        hideProgressDialog();
                        //res包含hash、key等信息，具体字段取决于上传策略的设置
                        if (info.isOK()) {
                            Log.i("qiniu Upload Success");
                            try {
                                uploadPictureKey = res.getString("key");
                                GlideLoader.displayImage(PunchClockActivity.this, filePath, binding.ivPicture);
                                showToast("图片上传成功");
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

    private void uploadPicture(final String data) {

        if (StringUtil.isEmpty(qiniuToken))

            RequestManager.getInstance(this).executeRequest(HttpUrls.GET_QINIUTOKEN, null, new AppCallBack<ApiResponse<String>>() {

                @Override
                public void next(ApiResponse<String> response) {
                    if (response.isSuccess()) {
                        qiniuToken = response.getData();
                        uploadPicture(data, qiniuToken);
                    } else {
                        showToast(response.getMessage());
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
            mLocationClient = null;

        }
    }
}
