package cn.chestnut.mvvm.teamworker.module.work;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.opengl.Visibility;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cn.chestnut.mvvm.teamworker.Constant;
import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.FragmentWorkBinding;
import cn.chestnut.mvvm.teamworker.main.common.BaseFragment;
import cn.chestnut.mvvm.teamworker.module.approval.ApprovalActivity;
import cn.chestnut.mvvm.teamworker.module.checkattendance.CheckAttendanceActivity;
import cn.chestnut.mvvm.teamworker.module.team.BuildTeamActivity;
import cn.chestnut.mvvm.teamworker.module.user.NewFriendActivity;
import cn.chestnut.mvvm.teamworker.module.user.UserInformationActivity;
import cn.chestnut.mvvm.teamworker.module.work.adapter.GridViewAdapter;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/1/6 18:49:31
 * Description：工作Fragment
 * Email: xiaoting233zhang@126.com
 */

public class WorkFragment extends BaseFragment {

    private FragmentWorkBinding binding;
    private GridViewAdapter gridViewAdapter;
    private ArrayList<String> nameList;
    private ArrayList<Integer> drawableList;

    private BroadcastReceiver receiver;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("工作");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_work, viewGroup, true);
        initData();
        initView();
        addListener();
    }

    @Override
    public void setButton(TextView edit, ImageView add, ImageView search) {
        super.setButton(edit, add, search);
        add.setVisibility(View.VISIBLE);
        add.setImageDrawable(getResources().getDrawable(R.mipmap.icon_to_to_list));
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), WorkNotificationActivity.class));
            }
        });
        search.setVisibility(View.VISIBLE);
        search.setImageDrawable(getResources().getDrawable(R.mipmap.icon_notification));
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), NewFriendActivity.class));
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        nameList = new ArrayList<>();
        nameList.add("考勤打卡");
        nameList.add("审批");
        nameList.add("考勤打卡");
        nameList.add("审批");

        drawableList = new ArrayList<>();
        drawableList.add(R.mipmap.icon_attendance);
        drawableList.add(R.mipmap.icon_approval);
        drawableList.add(R.mipmap.icon_attendance);
        drawableList.add(R.mipmap.icon_approval);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constant.ActionConstant.ACTION_SHOW_BADGE)) {
                    setBadgeVisibility(View.VISIBLE);
                }
            }
        };
    }

    /**
     * 初始化界面
     */
    private void initView() {
        gridViewAdapter = new GridViewAdapter(getActivity(), nameList, drawableList);
        binding.gvCommonApps.setAdapter(gridViewAdapter);
    }

    /**
     * 添加监听器
     */
    private void addListener() {
        binding.gvCommonApps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (drawableList.get(position)) {
                    case R.mipmap.icon_attendance:
                        startActivity(new Intent(getActivity(), CheckAttendanceActivity.class));
                        break;
                    case R.mipmap.icon_approval:
                        startActivity(new Intent(getActivity(), ApprovalActivity.class));
                        break;
                }
            }
        });

        binding.llBuildTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), BuildTeamActivity.class));
            }
        });
    }

}
