package cn.chestnut.mvvm.teamworker.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.SwipeLoadMoreTrigger;
import com.aspsine.swipetoloadlayout.SwipeTrigger;

import cn.chestnut.mvvm.teamworker.R;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/10 20:49:36
 * Description：加载更多时底部View
 * Email: xiaoting233zhang@126.com
 */

public class RefreshFooterView extends LinearLayout implements SwipeLoadMoreTrigger, SwipeTrigger {
    private TextView statusTV;
    private ImageView imageView;

    public RefreshFooterView(Context context) {
        this(context, null, 0);
    }

    public RefreshFooterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshFooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @Override
    public void onLoadMore() {
        statusTV.setText("正在加载...");

    }

    @Override
    public void onPrepare() {
        statusTV.setText("上拉加载");
        imageView.setVisibility(View.GONE);
    }

    @Override
    public void onMove(int i, boolean b, boolean b1) {

    }

    @Override
    public void onRelease() {
        statusTV.setText("上拉加载");
    }

    @Override
    public void onComplete() {
        statusTV.setText("加载成功！");
        imageView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onReset() {
        statusTV.setText("上拉加载");
    }

    private void initView(Context context) {//初始化自定义view
        //动态布局添加
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        View view = View.inflate(context, R.layout.refresh_footer_view, null);
        addView(view, lp);
        statusTV = (TextView) view.findViewById(R.id.tv_loadmore_status);
        imageView = (ImageView) view.findViewById(R.id.iv_loadmore_success);

    }
}
