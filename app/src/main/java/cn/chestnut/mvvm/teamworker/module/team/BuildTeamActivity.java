package cn.chestnut.mvvm.teamworker.module.team;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityBuildTeamBinding;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/3 22:20:12
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class BuildTeamActivity extends BaseActivity {

    ActivityBuildTeamBinding binding;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("创建团队");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_build_team, viewGroup, true);
        addListener();
    }

    private void addListener() {
        binding.llPersonnelScale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new AlertDialog.Builder(BuildTeamActivity.this).setTitle("请选择团队人员规模")
                        .setItems(new String[]{"1~10人", "11~20人", "21~50人", "51~100人", "101~200人", "201~500人", "501~1000人", "1001~2000人"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        binding.tvPersonnelScale.setText("1~10人");
                                        break;
                                    case 1:
                                        binding.tvPersonnelScale.setText("11~20人");
                                        break;
                                    case 2:
                                        binding.tvPersonnelScale.setText("21~50人");
                                        break;
                                    case 3:
                                        binding.tvPersonnelScale.setText("51~100人");
                                        break;
                                    case 4:
                                        binding.tvPersonnelScale.setText("101~200人");
                                        break;
                                    case 5:
                                        binding.tvPersonnelScale.setText("201~500人");
                                        break;
                                    case 6:
                                        binding.tvPersonnelScale.setText("501~1000人");
                                        break;
                                    case 7:
                                        binding.tvPersonnelScale.setText("1001~2000人");
                                        break;

                                }
                                dialog.dismiss();
                            }
                        }).create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
            }
        });

        binding.llAddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BuildTeamActivity.this,PhoneDirectoryActivity.class));
            }
        });
    }

}
