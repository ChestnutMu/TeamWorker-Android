package cn.chestnut.mvvm.teamworker.http;


import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.zip.DataFormatException;

import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/10 20:49:36
 * Description：网络请求回调实现
 * Email: xiaoting233zhang@126.com
 */

public abstract class RQCallBack<T>
        implements AppCallBack<T> {
    BaseActivity baseActivity;

    public RQCallBack() {
    }

    public RQCallBack(BaseActivity baseActivity) {
        this.baseActivity = baseActivity;
    }

    @Override
    public void next(T response) {
        fail(response);
        success(response);
    }

    @Override
    public void complete() {
        if (baseActivity != null) {
            baseActivity.hideProgressDialog();

        }
    }

    public abstract void success(T response);

    public abstract void fail(T response);

    @Override
    public void error(Throwable error) {
        if (baseActivity != null) {
            baseActivity.hideProgressDialog();
            if (error != null) {
                if (error instanceof ConnectException) {
                    baseActivity.showToast("服务器连接失败！");
                } else if (error instanceof SocketTimeoutException) {
                    baseActivity.showToast("连接超时!");
                } else if (error instanceof DataFormatException) {
                    baseActivity.showToast("数据格式异常!");
                } else {
                    baseActivity.showToast("请求异常!");
                }
            }

        }
    }
}
