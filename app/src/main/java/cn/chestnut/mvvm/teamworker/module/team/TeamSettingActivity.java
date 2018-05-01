package cn.chestnut.mvvm.teamworker.module.team;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityTeamSettingBinding;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/30 13:09:16
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class TeamSettingActivity extends BaseActivity {

    private ActivityTeamSettingBinding binding;

    private String teamId;

    public static final int RESULT_CODE_GIVE_UP_TEAM_OWNER = 1;

    public static final int RESULT_CODE_RELEASE_TEAM = 2;

    private static final int REQUEST_CODE_SELECT_NEW_OWNER = 3;

    private boolean isTeamOwnerTransfered = false;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("团队设置");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_team_setting, viewGroup, true);
        initData();
        addListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_SELECT_NEW_OWNER) {
            isTeamOwnerTransfered = true;
        }
    }

    @Override
    protected void initData() {
        teamId = getIntent().getStringExtra("teamId");
    }

    @Override
    protected void addListener() {
        binding.llGiveUpOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeamSettingActivity.this, SelectNewOwnerActivity.class);
                intent.putExtra("teamId", teamId);
                startActivityForResult(intent, REQUEST_CODE_SELECT_NEW_OWNER);
            }
        });

        binding.llReleaseTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReleaseTeamDialog();
            }
        });
    }


    private void showReleaseTeamDialog() {
        new AlertDialog.Builder(this)
                .setTitle("解散团队")
                .setMessage("确定要解散该团队吗？")
                .setPositiveButton("确定解散", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        releaseTeam();
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

    private void releaseTeam() {
        ArrayMap<String, String> params = new ArrayMap<>(1);
        params.put("teamId", teamId);
        RequestManager.getInstance(this).executeRequest(HttpUrls.RELEASE_TEAM, params, new AppCallBack<ApiResponse<Object>>() {
            @Override
            public void next(ApiResponse<Object> response) {
                if (response.isSuccess()) {
                    showToast("已成功解散该团队");
                    setResult(RESULT_CODE_RELEASE_TEAM);
                    finish();
                } else {
                    showToast("解散团队失败");
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

    @Override
    public void finish() {
        if (isTeamOwnerTransfered) {
            setResult(RESULT_CODE_GIVE_UP_TEAM_OWNER);
        }
        super.finish();
    }
}
