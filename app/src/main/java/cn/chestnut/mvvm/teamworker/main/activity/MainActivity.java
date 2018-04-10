package cn.chestnut.mvvm.teamworker.main.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.chestnut.mvvm.teamworker.Constant;
import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityMainBinding;
import cn.chestnut.mvvm.teamworker.databinding.TabCustomBinding;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.module.directory.DirectoryFragment;
import cn.chestnut.mvvm.teamworker.module.massage.fragment.MessageFragment;
import cn.chestnut.mvvm.teamworker.module.mine.MineFragment;
import cn.chestnut.mvvm.teamworker.module.work.WorkFragment;
import cn.chestnut.mvvm.teamworker.socket.ReceiverProtocol;
import cn.chestnut.mvvm.teamworker.utils.Log;
import cn.chestnut.mvvm.teamworker.utils.PermissionsUtil;
import cn.chestnut.mvvm.teamworker.utils.PreferenceUtil;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/10 20:49:36
 * Description：主界面
 * Email: xiaoting233zhang@126.com
 */

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;

    private List<TabCustomBinding> tabBindingList;

    //Tab 文字
    private final int[] TAB_TITLES = new int[]{R.string.fragment_message, R.string.fragment_work, R.string.fragment_directory, R.string.fragment_mine};
    //Tab 图片
    private final int[] TAB_IMGS = new int[]{R.drawable.tab_message_selector, R.drawable.tab_work_selector, R.drawable.tab_directory_selector, R.drawable.tab_mine_selector};
    //Fragment 数组
    private final Fragment[] TAB_FRAGMENTS = new Fragment[]{new MessageFragment(), new WorkFragment(), new DirectoryFragment(), new MineFragment()};
    //Tab 数目
    private final int COUNT = TAB_TITLES.length;

    private MyViewPagerAdapter mAdapter;

    private BroadcastReceiver receiver;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("首页");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_main, viewGroup, true);
        initData();
        initView();
    }

    @Override
    public void onSessionMessage(int msgId, Object object) {

        switch (msgId) {
            case ReceiverProtocol.RECEIVE_NEW_MESSAGE:
                break;

            default:
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        getNotSendRequestCountByUserId();
    }

    protected void initData() {
        PermissionsUtil.checkAndRequestPermissions(this);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constant.ActionConstant.ACTION_GET_NEW_FRIEND_REQUEST)) {
                    getNotSendRequestCountByUserId();
                }
            }
        };
    }

    protected void initView() {
        setTitleBarVisible(false);//设置BaseActivity定义的标题栏不可见
        tabBindingList = new ArrayList<>(COUNT);
        setTabs(binding.tabLayout, this.getLayoutInflater(), TAB_TITLES, TAB_IMGS);
        mAdapter = new MyViewPagerAdapter(getSupportFragmentManager());
        binding.viewPager.setOffscreenPageLimit(4);
        binding.viewPager.setAdapter(mAdapter);
        binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout));
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        //更新未处理的好友请求消息的角标数量
        getNotSendRequestCountByUserId();
    }

    /**
     * @description: 设置添加Tab
     */
    private void setTabs(TabLayout tabLayout, LayoutInflater inflater, int[] tabTitles, int[] tabImgs) {
        for (int i = 0; i < tabImgs.length; i++) {
            TabLayout.Tab tab = tabLayout.newTab();
            TabCustomBinding tabBinding = DataBindingUtil.inflate(inflater, R.layout.tab_custom, null, false);
            tabBindingList.add(tabBinding);
            tab.setCustomView(tabBinding.getRoot());
            tabBinding.tvTab.setText(tabTitles[i]);
            tabBinding.imgTab.setImageResource(tabImgs[i]);
            tabLayout.addTab(tab);
        }
    }

    /**
     * @description: ViewPager 适配器
     */
    private class MyViewPagerAdapter extends FragmentPagerAdapter {
        public MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return TAB_FRAGMENTS[position];
        }

        @Override
        public int getCount() {
            return COUNT;
        }
    }

    private void getNotSendRequestCountByUserId() {
        String userId = PreferenceUtil.getInstances(this).getPreferenceString("userId");
        Map param = new HashMap<String, String>(1);
        param.put("userId", userId);
        RequestManager.getInstance(this).executeRequest(HttpUrls.COUNT_NOT_SEND_REQUEST_BY_USERID, param, new AppCallBack<ApiResponse<Integer>>() {
            @Override
            public void next(ApiResponse<Integer> response) {
                if (response.isSuccess()) {
                    if (response.getData() > 0) {
                        tabBindingList.get(1).tvBadge.setVisibility(View.VISIBLE);
                        tabBindingList.get(1).tvBadge.setText(response.getData());
                    } else {
                        tabBindingList.get(1).tvBadge.setVisibility(View.INVISIBLE);
                    }
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
