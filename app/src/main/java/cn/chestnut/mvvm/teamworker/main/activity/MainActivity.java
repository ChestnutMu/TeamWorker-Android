package cn.chestnut.mvvm.teamworker.main.activity;

import android.databinding.DataBindingUtil;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityMainBinding;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.module.mine.fragment.MineFragment;
import cn.chestnut.mvvm.teamworker.module.massage.fragment.MessageFragment;
import cn.chestnut.mvvm.teamworker.module.work.fragment.WorkFragment;
import cn.chestnut.mvvm.teamworker.socket.ReceiverProtocol;
import cn.chestnut.mvvm.teamworker.utils.Log;
import cn.chestnut.mvvm.teamworker.utils.PermissionsUtil;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/10 20:49:36
 * Description：主界面
 * Email: xiaoting233zhang@126.com
 */

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;
    //Tab 文字
    private final int[] TAB_TITLES = new int[]{R.string.fragment_message, R.string.fragment_work, R.string.fragment_mine};
    //Tab 图片
    private final int[] TAB_IMGS = new int[]{R.drawable.tab_message_selector, R.drawable.tab_work_selector, R.drawable.tab_mine_selector};
    //Fragment 数组
    private final Fragment[] TAB_FRAGMENTS = new Fragment[]{new MessageFragment(), new WorkFragment(), new MineFragment()};
    //Tab 数目
    private final int COUNT = TAB_TITLES.length;
    private MyViewPagerAdapter mAdapter;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("首页");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater,R.layout.activity_main,viewGroup,true);
        initView();
        initData();
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

    private void initData() {
        PermissionsUtil.checkAndRequestPermissions(MainActivity.this);
    }

    private void initView() {
        setTitleBarVisible(false);//设置BaseActivity定义的标题栏不可见
        setTabs(binding.tabLayout, this.getLayoutInflater(), TAB_TITLES, TAB_IMGS);
        mAdapter = new MyViewPagerAdapter(getSupportFragmentManager());
        binding.viewPager.setOffscreenPageLimit(3);
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
    }

    /**
     * @description: 设置添加Tab
     */
    private void setTabs(TabLayout tabLayout, LayoutInflater inflater, int[] tabTitlees, int[] tabImgs) {
        for (int i = 0; i < tabImgs.length; i++) {
            TabLayout.Tab tab = tabLayout.newTab();
            View view = inflater.inflate(R.layout.tab_custom, null);
            tab.setCustomView(view);

            TextView tvTitle = view.findViewById(R.id.tv_tab);
            tvTitle.setText(tabTitlees[i]);
            ImageView imgTab = view.findViewById(R.id.img_tab);
            imgTab.setImageResource(tabImgs[i]);
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



}
