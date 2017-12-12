package cn.chestnut.mvvm.teamworker.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.SwipeRefreshTrigger;
import com.aspsine.swipetoloadlayout.SwipeTrigger;

import cn.chestnut.mvvm.teamworker.R;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/10 20:49:36
 * Description：刷新界面时的头部View
 * Email: xiaoting233zhang@126.com
 */

public class RefreshHeaderView extends LinearLayout implements SwipeRefreshTrigger, SwipeTrigger {
    private TextView statusTV;
    private ImageView imageView;

    public RefreshHeaderView(Context context) {
        this(context, null, 0);
    }

    public RefreshHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @Override
    public void onRefresh() {
        statusTV.setText("正在加载...");

    }

    @Override
    public void onPrepare() {
        statusTV.setText("松开加载");
        imageView.setVisibility(View.GONE);
    }

    @Override
    public void onMove(int i, boolean b, boolean b1) {

    }

    @Override
    public void onRelease() {
        statusTV.setText("松开加载");
    }

    @Override
    public void onComplete() {
        statusTV.setText("刷新成功！");
        imageView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onReset() {
        statusTV.setText("松开加载");
    }

    private void initView(Context context) {//初始化自定义view
        //动态布局添加
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        View view = View.inflate(context, R.layout.refresh_header_view, null);
        addView(view, lp);
        statusTV = (TextView) view.findViewById(R.id.tv_refresh_status);
        imageView = (ImageView) view.findViewById(R.id.iv_refresh_success);

    }
}
