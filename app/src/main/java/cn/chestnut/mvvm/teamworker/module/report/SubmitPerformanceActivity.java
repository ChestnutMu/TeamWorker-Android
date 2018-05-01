package cn.chestnut.mvvm.teamworker.module.report;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivitySubmitPerformanceBinding;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.main.common.MyApplication;
import cn.chestnut.mvvm.teamworker.utils.GlideLoader;
import cn.chestnut.mvvm.teamworker.utils.Log;
import cn.chestnut.mvvm.teamworker.utils.PreferenceUtil;
import cn.chestnut.mvvm.teamworker.utils.StringUtil;
import cn.chestnut.mvvm.teamworker.utils.photo.ProcessPhotoUtils;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/9 17:11:57
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class SubmitPerformanceActivity extends BaseActivity {

    private ActivitySubmitPerformanceBinding binding;

    private ProcessPhotoUtils processPhotoUtils;

    private String teamId;

    private String qiniuToken;

    private String pictureKey;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("绩效自评");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_submit_performance, viewGroup, true);
        initData();
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

    @Override
    protected void initData() {
        teamId = getIntent().getStringExtra("teamId");
        processPhotoUtils = new ProcessPhotoUtils(this);
    }

    @Override
    protected void addListener() {
        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.ivPicture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        processPhotoUtils = new ProcessPhotoUtils(SubmitPerformanceActivity.this);
                        processPhotoUtils.startPhoto();
                    }
                });

                if (StringUtil.isStringNotNull(binding.etLastWorkTask.getText().toString())
                        && StringUtil.isStringNotNull(binding.etFinishedWork.getText().toString())
                        && StringUtil.isStringNotNull(binding.etReachRate.getText().toString())
                        && StringUtil.isStringNotNull(binding.etThisWorkTask.getText().toString())
                        && StringUtil.isStringNotNull(binding.etSelfEvaluation.getText().toString())
                        && StringUtil.isStringNotNull(binding.etWorkPlan.getText().toString())
                        ) {
                    submitReport();
                } else {
                    showToast("请填写带红色*号的信息");
                }
            }
        });
    }

    private void uploadPicture(final String filePath, String token) {
        showProgressDialog(this);
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
                                GlideLoader.displayImage(SubmitPerformanceActivity.this, filePath, binding.ivPicture);
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

    private void submitReport() {
        Map<String, Object> params = new ArrayMap<>();
        params.put("teamId", teamId);
        params.put("userNickname", PreferenceUtil.getInstances(this).getPreferenceString("nickname"));
        params.put("userAvatar", PreferenceUtil.getInstances(this).getPreferenceString("avatar"));
        params.put("lastWorkTask", binding.etLastWorkTask.getText().toString());
        params.put("finishedWork", binding.etFinishedWork.getText().toString());
        params.put("reachRate", binding.etReachRate.getText().toString());
        params.put("selfEvaluation", binding.etSelfEvaluation.getText().toString());
        params.put("thisWorkTask", binding.etThisWorkTask.getText().toString());
        params.put("workPlan", binding.etWorkPlan.getText().toString());
        if (StringUtil.isStringNotNull(pictureKey)) {
            params.put("photo", pictureKey);
        }        showProgressDialog(this);
        RequestManager.getInstance(this).executeRequest(HttpUrls.SUBMIT_PERFORMANCE, params, new AppCallBack<ApiResponse<Object>>() {
            @Override
            public void next(ApiResponse<Object> response) {
                showToast(response.getMessage());
                if (response.isSuccess()) {
                    setResult(RESULT_OK);
                    finish();
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
