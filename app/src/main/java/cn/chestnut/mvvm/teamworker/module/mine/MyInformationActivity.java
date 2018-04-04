package cn.chestnut.mvvm.teamworker.module.mine;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityMyInformationBinding;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.main.common.MyApplication;
import cn.chestnut.mvvm.teamworker.model.User;
import cn.chestnut.mvvm.teamworker.utils.EmojiUtil;
import cn.chestnut.mvvm.teamworker.utils.Log;
import cn.chestnut.mvvm.teamworker.utils.PreferenceUtil;
import cn.chestnut.mvvm.teamworker.utils.StringUtil;
import cn.chestnut.mvvm.teamworker.utils.photo.ProcessPhotoUtils;
import cn.chestnut.mvvm.teamworker.utils.GlideLoader;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/1/7 18:58:23
 * Description：个人信息
 * Email: xiaoting233zhang@126.com
 */

public class MyInformationActivity extends BaseActivity {

    private ActivityMyInformationBinding binding;
    private String token;
    private String userId;

    private ProcessPhotoUtils processPhotoUtils;
    private String qiniuToken;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("个人信息");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_my_information, viewGroup, true);
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
            uploadPicture(filePath);
        } else if (requestCode == ProcessPhotoUtils.SHOOT_PHOTO_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            String filePath = processPhotoUtils.getMyPhotoFile().getPath();
            Log.d("filePath " + filePath);
            uploadPicture(filePath);
        }
    }

    /**
     * 初始化数据
     */

    private void initData() {
        token = PreferenceUtil.getInstances(this).getPreferenceString("token");
        userId = PreferenceUtil.getInstances(this).getPreferenceString("userId");
    }


    private void initView() {
        getMyInfomation(token, userId);
    }

    private void addListener() {
        //修改头像
        binding.llAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processPhotoUtils = new ProcessPhotoUtils(MyInformationActivity.this);
                processPhotoUtils.startPhoto();
            }
        });

        //修改昵称
        binding.llNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editText = new EditText(MyInformationActivity.this);
                new AlertDialog.Builder(MyInformationActivity.this)
                        .setTitle("昵称")
                        .setView(editText)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                User user = new User();
                                try {
                                    user.setNickname(EmojiUtil.emojiConvert(editText.getText().toString()));
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                updateMyinformation(user);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
            }
        });

        //修改手机
        binding.llTelephone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editText = new EditText(MyInformationActivity.this);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                new AlertDialog.Builder(MyInformationActivity.this)
                        .setTitle("手机")
                        .setView(editText)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!StringUtil.isChinaPhoneLegal(editText.getText().toString())) {
                                    showToast("该号码格式不正确哦");
                                } else {
                                    User user = new User();
                                    user.setTelephone(editText.getText().toString());
                                    updateMyinformation(user);
                                }
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
            }
        });

        //修改性别
        binding.llSex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] items = new String[]{"男", "女"};
                new AlertDialog.Builder(MyInformationActivity.this)
                        .setTitle("性别")
                        .setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                User user = new User();
                                user.setSex(items[which]);
                                updateMyinformation(user);
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
            }
        });
        //修改生日日期
        binding.llBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = new DatePickerDialog(
                        MyInformationActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                int rightMonth = month + 1;
                                String birthday = year + "年" + rightMonth + "月" + dayOfMonth + "日";
                                User user = new User();
                                user.setBirthday(birthday);
                                updateMyinformation(user);
                            }
                        },
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show();
            }
        });

    }

    /**
     * 获取个人信息
     */
    private void getMyInfomation(String token, String userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("token", token);
        params.put("userId", userId);
        RequestManager.getInstance(this).executeRequest(HttpUrls.GET_MY_INFORMATION, params, new AppCallBack<ApiResponse<User>>() {

            @Override
            public void next(ApiResponse<User> response) {
                if (response.isSuccess()) {
                    try {
                        binding.tvNickname.setText(EmojiUtil.emojiRecovery(response.getData().getNickname()));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    binding.tvTelephone.setText(response.getData().getTelephone());
                    binding.tvSex.setText(response.getData().getSex());
                    binding.tvBirthday.setText(response.getData().getBirthday());
                    binding.tvRegion.setText(response.getData().getRegion());
                    GlideLoader.displayImage(MyInformationActivity.this, HttpUrls.GET_PHOTO + response.getData().getAvatar(), binding.ivAvatar);
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

    }

    /**
     * 修改个人信息
     */
    private void updateMyinformation(final User user) {
        user.setUserId(userId);
        RequestManager.getInstance(this).executeRequest(HttpUrls.UPDATE_MY_INFORMATION, user, new AppCallBack<ApiResponse<User>>() {

            @Override
            public void next(ApiResponse<User> response) {
                if (response.isSuccess()) {
                    if (StringUtil.isStringNotNull(response.getData().getNickname())) {
                        String nickName = null;
                        try {
                            nickName = EmojiUtil.emojiRecovery(response.getData().getNickname());
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        binding.tvNickname.setText(nickName);
                    }
                    binding.tvTelephone.setText(response.getData().getTelephone());
                    binding.tvSex.setText(response.getData().getSex());
                    binding.tvBirthday.setText(response.getData().getBirthday());
                    binding.tvRegion.setText(response.getData().getRegion());
                    if (StringUtil.isStringNotNull(user.getAvatar())) {
                        GlideLoader glideLoader = new GlideLoader();
                        glideLoader.displayImage(MyInformationActivity.this, HttpUrls.GET_PHOTO + response.getData().getAvatar(), binding.ivAvatar);
                    }
                }
                Log.d("update_my_information" + response.getMessage());
            }

            @Override
            public void error(Throwable error) {
                Log.e(error.toString());
            }

            @Override
            public void complete() {

            }

        });

    }

    private void uploadPicture(String data, String token) {
        String key = null;
        MyApplication.getUploadManager().put(data, key, token,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject res) {
                        //res包含hash、key等信息，具体字段取决于上传策略的设置
                        if (info.isOK()) {
                            Log.i("qiniu Upload Success");
                            User user = new User();
                            try {
                                user.setAvatar(res.getString("key"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            updateMyinformation(user);
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


}
