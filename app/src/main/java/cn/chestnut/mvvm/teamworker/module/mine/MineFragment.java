package cn.chestnut.mvvm.teamworker.module.mine;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.greendao.async.AsyncSession;

import java.io.UnsupportedEncodingException;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.FragmentMineBinding;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.main.common.BaseFragment;
import cn.chestnut.mvvm.teamworker.main.common.MyApplication;
import cn.chestnut.mvvm.teamworker.module.checkattendance.PunchClockActivity;
import cn.chestnut.mvvm.teamworker.socket.TeamWorkerClient;
import cn.chestnut.mvvm.teamworker.utils.EmojiUtil;
import cn.chestnut.mvvm.teamworker.utils.PreferenceUtil;
import cn.chestnut.mvvm.teamworker.utils.GlideLoader;
import cn.chestnut.mvvm.teamworker.utils.sqlite.DaoManager;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/1/3 12:13:22
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class MineFragment extends BaseFragment {

    private FragmentMineBinding binding;

    private AsyncSession asyncSession;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("我的");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mine, viewGroup, true);
        initView();
        addListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        getMyInfomation();
    }

    private void initView() {
        asyncSession = DaoManager.getDaoSession().startAsyncSession();
        getMyInfomation();
    }

    /**
     * 添加监听器
     */
    private void addListener() {
        binding.llCheckAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), PunchClockActivity.class));
            }
        });
        binding.llLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("退出登录")
                        .setMessage("确定要退出登录吗？")
                        .setPositiveButton("确定退出", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                logout();
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
        binding.llMyInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), MyInformationActivity.class));
            }
        });
        binding.llMyFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), MyFriendActivity.class));
            }
        });

    }

    private void logout() {
        closeService();
        clesrUserInfo();
        DaoManager.closeConnection();
        goLogin();
    }

    /**
     * 关闭服务器
     */
    public void closeService() {
        TeamWorkerClient.getIntenace().closeService();
    }

    /**
     * 清理用户数据
     */
    private void clesrUserInfo() {
        PreferenceUtil.getInstances(MyApplication.getInstance()).deleteKey("userId");
        PreferenceUtil.getInstances(MyApplication.getInstance()).deleteKey("telephone");
        PreferenceUtil.getInstances(MyApplication.getInstance()).deleteKey("token");
        PreferenceUtil.getInstances(MyApplication.getInstance()).deleteKey("nickname");
    }

    /**
     * 退出登录跳转到登录界面，并重启
     */
    private void goLogin() {
        Intent i = MyApplication.getInstance().getPackageManager()
                .getLaunchIntentForPackage(MyApplication.getInstance().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        MyApplication.getInstance().startActivity(i);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * 获取个人信息
     */
    private void getMyInfomation() {
        try {
            binding.tvNickname.setText(EmojiUtil.emojiRecovery(PreferenceUtil.getInstances(getContext()).getPreferenceString("nickname")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        GlideLoader.displayImage(getActivity(), HttpUrls.GET_PHOTO +
                PreferenceUtil.getInstances(getContext()).getPreferenceString("avatar"), binding.ivAvatar);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        asyncSession = null;
    }
}
