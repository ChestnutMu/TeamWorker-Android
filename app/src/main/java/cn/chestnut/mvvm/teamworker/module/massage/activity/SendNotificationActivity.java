package cn.chestnut.mvvm.teamworker.module.massage.activity;

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

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivitySendNotificationBinding;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.main.common.MyApplication;
import cn.chestnut.mvvm.teamworker.utils.GlideLoader;
import cn.chestnut.mvvm.teamworker.utils.Log;
import cn.chestnut.mvvm.teamworker.utils.StringUtil;
import cn.chestnut.mvvm.teamworker.utils.photo.ProcessPhotoUtils;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/1/2 22:35:05
 * Description：发送通知Activity
 * Email: xiaoting233zhang@126.com
 */

public class SendNotificationActivity extends BaseActivity {

    private ActivitySendNotificationBinding binding;

    private String qiniuToken;

    private String pictureKey;

    private String teamId;

    private ProcessPhotoUtils processPhotoUtils;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("发送公告");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_send_notification, viewGroup, true);
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
        processPhotoUtils = new ProcessPhotoUtils(this);
        teamId = getIntent().getStringExtra("teamId");
    }

    protected void initView() {
        setTitleBarVisible(true);
    }

    protected void addListener() {
        binding.ivPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processPhotoUtils = new ProcessPhotoUtils(SendNotificationActivity.this);
                processPhotoUtils.startPhoto();
            }
        });

        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtil.isStringNotNull(binding.etTitle.getText().toString()) && StringUtil.isStringNotNull(binding.etContent.getText().toString())) {
                    sendNotification();
                } else {
                    showToast("请填写带*号的信息");
                }
            }
        });
    }

    /**
     * 发送通知
     */
    private void sendNotification() {
        showProgressDialog(this);
        ArrayMap<String, String> params = new ArrayMap<>(4);
        params.put("teamId", teamId);
        params.put("title", binding.etTitle.getText().toString());
        params.put("content", binding.etContent.getText().toString());
        params.put("photo", pictureKey);
        RequestManager.getInstance(this).executeRequest(HttpUrls.SEND_NOTIFICATION, params, new AppCallBack<ApiResponse<Object>>() {
            @Override
            public void next(ApiResponse<Object> response) {
                if (response.isSuccess()) {
                    showToast("发送通知成功");
                    finish();
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
                                GlideLoader.displayImage(SendNotificationActivity.this, filePath, binding.ivPicture);
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
}
