package cn.chestnut.mvvm.teamworker.main.common;

import android.annotation.TargetApi;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityBaseBinding;
import cn.chestnut.mvvm.teamworker.service.DataManager;
import cn.chestnut.mvvm.teamworker.utils.CommonUtil;
import cn.chestnut.mvvm.teamworker.utils.ProgressDialogShow;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/10 20:49:36
 * Description：Activity基类
 * Email: xiaoting233zhang@126.com
 */

public abstract class BaseActivity extends AppCompatActivity {

    ActivityBaseBinding binding;
    /**
     * 需加载的activity布局资源
     */
//    private LinearLayout btn_back;

    LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflater = getLayoutInflater();
        CommonUtil.setBarTheme(BaseActivity.this);//设置状态栏
        binding= DataBindingUtil.setContentView(this, R.layout.activity_base);
        setBaseTitle(binding.baseTitleTv);
        initViews();

    }

    protected abstract void setBaseTitle(TextView titleView);

    /**
     * 获取控件资源
     */
    protected void initViews() {

        binding.backBtn.setOnClickListener(new BackOnClickListener());
        addContainerView(binding.baseContainerLayout, inflater);
//        edit = (TextView) findViewById(R.id.edit);
//        add = (ImageView) findViewById(R.id.add);
//        search = (ImageView) findViewById(R.id.search);
        setButton(binding.edit, binding.add, binding.search);

    }


    /**
     * 显示右上角按钮，由子activity根据具体情况重写
     */

    public void setButton(TextView edit, ImageView add, ImageView search) {
        edit.setVisibility(View.GONE);
        add.setVisibility(View.GONE);
        search.setVisibility(View.GONE);
    }

    protected abstract void addContainerView(ViewGroup viewGroup, LayoutInflater inflater);

    /**
     * 设置状态栏
     *
     * @param on
     */
    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    /**
     * 后退功能，子类可以重写,
     * 如退出定位、销毁实例等，也可以什么都不做
     */
    protected void back() {
        this.finish();
    }


    class BackOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            back();
        }
    }

    /**
     * 显示进度条对话框
     *
     *
     */

    public void showProgressDialog(BaseActivity baseActivity) {
        ProgressDialogShow.showProgress(baseActivity);

    }

    /**
     * 隐藏进度条对话框
     */
    public void hideProgressDialog() {
        ProgressDialogShow.cancleProgressDialog();
    }

    public void showToast(String stringRes) {
        try {
            CommonUtil.showToast(stringRes,BaseActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataManager.clearActivity();
    }

}
