package cn.chestnut.mvvm.teamworker.module.report;

import android.databinding.DataBindingUtil;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityWorkReportBinding;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/3 13:46:07
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class WorkReportActivity extends BaseActivity {

    private ActivityWorkReportBinding binding;

    //Tab 文字
    private final int[] TAB_TITLES = new int[]{R.string.day_report, R.string.week_report, R.string.month_report};

    //Fragment 数组
    private final Fragment[] TAB_FRAGMENTS = new Fragment[]{new DayReportFragment(), new WeekReportFragment(),new MonthReportFragment()};

    //Tab 数目
    private final int COUNT = TAB_TITLES.length;

    private MyViewPagerAdapter mAdapter;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("工作汇报");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_work_report, viewGroup, true);
        initView();
        initData();
    }

    protected void initData() {
    }

    protected void initView() {
        setTabs();
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
    private void setTabs() {
        for (int i = 0; i < COUNT; i++) {
            TabLayout.Tab tab = binding.tabLayout.newTab();
            tab.setText(TAB_TITLES[i]);
            binding.tabLayout.addTab(tab);
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
