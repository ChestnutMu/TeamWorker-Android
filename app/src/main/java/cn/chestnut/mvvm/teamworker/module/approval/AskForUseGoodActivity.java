package cn.chestnut.mvvm.teamworker.module.approval;

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
import cn.chestnut.mvvm.teamworker.databinding.ActivityAskForUseGoodBinding;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.main.common.MyApplication;
import cn.chestnut.mvvm.teamworker.model.UseGood;
import cn.chestnut.mvvm.teamworker.model.WorkOff;
import cn.chestnut.mvvm.teamworker.utils.GlideLoader;
import cn.chestnut.mvvm.teamworker.utils.Log;
import cn.chestnut.mvvm.teamworker.utils.PreferenceUtil;
import cn.chestnut.mvvm.teamworker.utils.StringUtil;
import cn.chestnut.mvvm.teamworker.utils.photo.ProcessPhotoUtils;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/3/13 12:08:43
 * Description：物品领用申请
 * Email: xiaoting233zhang@126.com
 */

public class AskForUseGoodActivity extends BaseActivity {

    private ActivityAskForUseGoodBinding binding;

    private String qiniuToken;

    private String pictureKey;

    private String teamId;

    private ProcessPhotoUtils processPhotoUtils;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("物品领用申请");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_ask_for_use_good, viewGroup, true);
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
        binding.ivPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processPhotoUtils = new ProcessPhotoUtils(AskForUseGoodActivity.this);
                processPhotoUtils.startPhoto();
            }
        });

        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtil.isStringNotNull(binding.etGoodPurpose.getText().toString())
                        && StringUtil.isStringNotNull(binding.etGoodName.getText().toString())
                        ) {
                    applyUseGood();
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
                                GlideLoader.displayImage(AskForUseGoodActivity.this, filePath, binding.ivPicture);
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

    private void applyUseGood() {
        Map<String, Object> params = new ArrayMap<>();
        params.put("teamId", teamId);
        params.put("userNickname", PreferenceUtil.getInstances(this).getPreferenceString("nickname"));
        params.put("userAvatar", PreferenceUtil.getInstances(this).getPreferenceString("avatar"));
        params.put("goodPurpose", binding.etGoodPurpose.getText().toString());
        params.put("goodName", binding.etGoodName.getText().toString());
        params.put("goodCount", binding.etGoodCount.getText().toString());
        params.put("useDetails", binding.etUseDetails.getText().toString());
        if (StringUtil.isStringNotNull(pictureKey)) {
            params.put("photo", pictureKey);
        }

        RequestManager.getInstance(this).executeRequest(HttpUrls.APPLY_USE_GOOD, params, new AppCallBack<ApiResponse<UseGood>>() {
            @Override
            public void next(ApiResponse<UseGood> response) {
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