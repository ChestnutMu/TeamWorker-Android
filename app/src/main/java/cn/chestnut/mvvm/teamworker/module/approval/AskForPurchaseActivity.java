package cn.chestnut.mvvm.teamworker.module.approval;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
import cn.chestnut.mvvm.teamworker.databinding.ActivityAskForPurchaseBinding;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.main.common.MyApplication;
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
 * Description：采购
 * Email: xiaoting233zhang@126.com
 */

public class AskForPurchaseActivity extends BaseActivity {

    private ActivityAskForPurchaseBinding binding;

    private String qiniuToken;

    private String pictureKey;

    private String teamId;

    private ProcessPhotoUtils processPhotoUtils;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("申请采购");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_ask_for_purchase, viewGroup, true);
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
        binding.llPayType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPayType();
            }
        });

        binding.ivPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processPhotoUtils = new ProcessPhotoUtils(AskForPurchaseActivity.this);
                processPhotoUtils.startPhoto();
            }
        });

        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtil.isStringNotNull(binding.etPurchaseReason.getText().toString())
                        && StringUtil.isStringNotNull(binding.etGoodName.getText().toString())
                        && StringUtil.isStringNotNull(binding.etGoodCount.getText().toString())
                        && StringUtil.isStringNotNull(binding.etGoodPrice.getText().toString())) {
                    applyPurchase();
                } else {
                    showToast("请填写带红色*号的信息");
                }
            }
        });

    }

    private void selectPayType() {
        Dialog dialog = new AlertDialog.Builder(this)
                .setItems(new String[]{"现金", "银行卡汇款", "支付宝", "微信支付"},
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0: {
                                        binding.tvPurchaseType.setText("现金");
                                        break;
                                    }
                                    case 1: {
                                        binding.tvPurchaseType.setText("银行卡汇款");
                                        break;
                                    }
                                    case 2: {
                                        binding.tvPurchaseType.setText("支付宝");
                                        break;
                                    }
                                    case 3: {
                                        binding.tvPurchaseType.setText("微信支付");
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
                                GlideLoader.displayImage(AskForPurchaseActivity.this, filePath, binding.ivPicture);
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

    private void applyPurchase() {
        Map<String, Object> params = new ArrayMap<>();
        params.put("teamId", teamId);
        params.put("userNickname", PreferenceUtil.getInstances(this).getPreferenceString("nickname"));
        params.put("userAvatar", PreferenceUtil.getInstances(this).getPreferenceString("avatar"));
        params.put("purchaseReason", binding.etPurchaseReason.getText().toString());
        params.put("goodName", binding.etGoodName.getText().toString());
        params.put("goodCount", binding.etGoodCount.getText().toString());
        params.put("goodPrice", binding.etGoodPrice.getText().toString());
        params.put("remarks", binding.etRemarks.getText().toString());
        if (StringUtil.isStringNotNull(pictureKey)) {
            params.put("photo", pictureKey);
        }
        RequestManager.getInstance(this).executeRequest(HttpUrls.APPLY_PURCHASE, params, new AppCallBack<ApiResponse<WorkOff>>() {
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