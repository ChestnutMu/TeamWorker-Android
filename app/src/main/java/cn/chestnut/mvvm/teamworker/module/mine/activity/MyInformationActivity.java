package cn.chestnut.mvvm.teamworker.module.mine.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.content.LocalBroadcastManager;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import cn.chestnut.mvvm.teamworker.Constant;
import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityMyInformationBinding;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.module.massage.bean.User;
import cn.chestnut.mvvm.teamworker.utils.CommonUtil;
import cn.chestnut.mvvm.teamworker.utils.EmojiUtil;
import cn.chestnut.mvvm.teamworker.utils.Log;
import cn.chestnut.mvvm.teamworker.utils.PreferenceUtil;
import cn.chestnut.mvvm.teamworker.utils.StringUtil;
import cn.chestnut.mvvm.teamworker.utils.photo.ProcessPhotoUtils;

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
                ProcessPhotoUtils processPhotoUtils = new ProcessPhotoUtils(MyInformationActivity.this);
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
                                    CommonUtil.showToast("该号码格式不正确哦", MyInformationActivity.this);
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
    private void updateMyinformation(User user) {
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
                        Intent intent = new Intent(Constant.ActionConstant.ACTION_UPDATE_NICKNAME);
                        intent.putExtra("nickname", response.getData().getNickname());
                        LocalBroadcastManager.getInstance(MyInformationActivity.this).sendBroadcast(intent);
                    }
                    binding.tvTelephone.setText(response.getData().getTelephone());
                    binding.tvSex.setText(response.getData().getSex());
                    binding.tvBirthday.setText(response.getData().getBirthday());
                    binding.tvRegion.setText(response.getData().getRegion());
                }
                showToast(response.getMessage());
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


}
