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
import cn.chestnut.mvvm.teamworker.core.OnHandlerSessionListener;
import cn.chestnut.mvvm.teamworker.core.TeamWorkerMessageHandler;
import cn.chestnut.mvvm.teamworker.databinding.ActivityBaseBinding;
import cn.chestnut.mvvm.teamworker.service.DataManager;
import cn.chestnut.mvvm.teamworker.socket.ReceiverProtocol;
import cn.chestnut.mvvm.teamworker.utils.CommonUtil;
import cn.chestnut.mvvm.teamworker.utils.Log;
import cn.chestnut.mvvm.teamworker.utils.ProgressDialogShow;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/10 20:49:36
 * Description：Activity基类
 * Email: xiaoting233zhang@126.com
 */

public abstract class BaseActivity extends AppCompatActivity implements OnHandlerSessionListener {

    private ActivityBaseBinding binding;

    private LayoutInflater inflater;

    private TeamWorkerMessageHandler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = MyApplication.getWuYuMessageHandler();
        mHandler.addOnTWHandlerSessionListener(this);

        inflater = getLayoutInflater();
        CommonUtil.setBarTheme(BaseActivity.this);//设置状态栏
        binding = DataBindingUtil.setContentView(this, R.layout.activity_base);
        setBaseTitle(binding.baseTitleTv);
        initViews();

    }

    protected abstract void setBaseTitle(TextView titleView);

    /**
     * 获取控件资源
     */
    protected void initViews() {

        binding.btnBack.setOnClickListener(new BackOnClickListener());
        addContainerView(binding.baseContainerLayout, inflater);
        setButton(binding.edit, binding.add, binding.search);

    }

    /**
     * 设置是否显示标题栏
     */

    public void setTitleBarVisible(boolean isVisible) {
        binding.layoutTitleBar.setVisibility(isVisible ? View.VISIBLE : View.GONE);
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
            CommonUtil.showToast(stringRes, BaseActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 发送请求长连接服务器
     */
    synchronized public void executeRequest(int msgId, String obj) {

        if (CommonUtil.checkNetState(this)) {
            if (mHandler != null)
                mHandler.send(msgId, obj);
        } else {
            CommonUtil.showToast("网络不太好哦", this);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataManager.clearActivity();
        mHandler.removeTWHandlerSessionListener(this);
    }

    /**
     * 长连接登陆
     */
    public void userLogin() {
        mHandler.userLogin();
    }

    @Override
    public void onSessionMessage(int msgId, Object object) {

        switch (msgId) {
            case ReceiverProtocol.USER_MESSAGE:

                break;

            default:
                break;
        }

    }

    @Override
    public void onSessionMessageException(int msgId, Exception exception) {
        Log.d(exception == null ? "exception is null !" : "" + exception.getMessage());

    }

    @Override
    public void onSessionClosed() {

    }

    @Override
    public void onSessionConnect() {

    }

    @Override
    public void onSessionTimeout() {

    }

}
