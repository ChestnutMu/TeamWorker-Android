package cn.chestnut.mvvm.teamworker.module.checkattendance;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import java.util.HashMap;
import java.util.Map;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityCheckAttendanceBinding;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.main.common.MyApplication;
import cn.chestnut.mvvm.teamworker.model.Attendance;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.utils.Log;
import cn.chestnut.mvvm.teamworker.utils.PreferenceUtil;


/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/1/3 11:59:33
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class CheckAttendanceActivity extends BaseActivity {

    private ActivityCheckAttendanceBinding binding;

    //定位
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    private double altitude;//经度
    private double latitude;//纬度
    private String detailAddress;//详细地址

    private String userId;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("考勤");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_check_attendance, viewGroup, true);
        initData();
        initView();
        addListener();
    }

    protected void initData() {
        userId = PreferenceUtil.getInstances(this).getPreferenceString("userId");
    }

    protected void initView() {
        initLocation();
        getAttendance();
    }

    protected void addListener() {
        binding.upWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commitUpWork();
            }
        });
        binding.downWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commitDownWork();
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
                        altitude = aMapLocation.getAltitude();
                        latitude = aMapLocation.getLatitude();
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
        params.put("userId", userId);
        showProgressDialog(this);
        RequestManager.getInstance(this).executeRequest(HttpUrls.GET_ATTENDANCE, params, new AppCallBack<ApiResponse<Attendance>>() {
            @Override
            public void next(ApiResponse<Attendance> response) {
                if (response.isSuccess()) {
                    binding.setAttendance(response.getData());
                }else {
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

    private void commitUpWork() {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("altitude", altitude);
        params.put("latitude", latitude);
        params.put("punchInAddress", detailAddress);
        showProgressDialog(this);
        RequestManager.getInstance(this).executeRequest(HttpUrls.PUNCH_IN, params, new AppCallBack<ApiResponse<Attendance>>() {
            @Override
            public void next(ApiResponse<Attendance> response) {
                if (response.isSuccess()) {
                    binding.setAttendance(response.getData());
                }
                showToast(response.getMessage());
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

    private void commitDownWork() {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("altitude", altitude);
        params.put("latitude", latitude);
        params.put("punchOutAddress", detailAddress);
        showProgressDialog(this);
        RequestManager.getInstance(this).executeRequest(HttpUrls.PUNCH_OUT, params, new AppCallBack<ApiResponse<Attendance>>() {
            @Override
            public void next(ApiResponse<Attendance> response) {
                if (response.isSuccess()) {
                    binding.setAttendance(response.getData());
                }
                showToast(response.getMessage());
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mLocationClient!=null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
            mLocationClient = null;

        }
    }
}
