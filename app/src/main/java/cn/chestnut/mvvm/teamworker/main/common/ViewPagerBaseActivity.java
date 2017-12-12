package cn.chestnut.mvvm.teamworker.main.common;

import android.databinding.DataBindingUtil;
import android.support.design.widget.TabLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityBaseViewpagerBinding;
import cn.chestnut.mvvm.teamworker.main.adapter.CommonViewPagerAdapter;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/10 20:49:36
 * Description：基类ViewPagerActivity
 * Email: xiaoting233zhang@126.com
 */


public abstract class ViewPagerBaseActivity
        extends BaseActivity {

    LayoutInflater inflater;

    ActivityBaseViewpagerBinding binding;

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {

        this.inflater = inflater;
        binding = DataBindingUtil.setContentView(this, R.layout.activity_base_viewpager);
        init();

    }

    /**
     * 获取viewPager的子页面集合
     *
     * @return
     */
    protected abstract List<View> getViewPagerChildViews(LayoutInflater inflater);

    /**
     * 初始化viewPager
     */
    List<View> mViews;

    private void init() {
        mViews = getViewPagerChildViews(inflater);

        //设置默认页面
        binding.childContainerView.setCurrentItem(0);
        binding.childContainerView.setAdapter(new CommonViewPagerAdapter(mViews));

        //动态设置标题栏数目
        binding.tab.setupWithViewPager(binding.childContainerView);
        binding.tab.setSelectedTabIndicatorColor(getResources().getColor(R.color.index_red));
        binding.tab.setTabTextColors(getResources().getColor(R.color.black), getResources().getColor(R.color.index_red));
        initTabs(binding.tab);
    }

    protected abstract void initTabs(TabLayout tab);

}




























