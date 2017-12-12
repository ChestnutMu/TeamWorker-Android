package cn.chestnut.mvvm.teamworker.service;

import android.app.Activity;
import android.content.Intent;

import com.alibaba.fastjson.JSONObject;

import java.util.LinkedList;
import java.util.List;

import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RQ;
import cn.chestnut.mvvm.teamworker.http.RQCallBack;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import rx.Observable;
import rx.Subscriber;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/10 20:49:36
 * Description：数据请求管理类
 * Email: xiaoting233zhang@126.com
 */

public class DataManager {
    static DataManager dataManager;
    private Activity activity;


    public static DataManager getInstance(Activity activity) {
        if (dataManager == null) {
            dataManager = new DataManager();
        }
        dataManager.activity = activity;
        return dataManager;
    }

    public static void clearActivity() {
        if (dataManager.activity != null) {
            dataManager.activity = null;
        }
    }

    /**
     * 获取更新信息
     *
     * @return
     */
    public void getUpdateInfo(String url) {
        List<String> params = new LinkedList<String>();
        List<String> values = new LinkedList<String>();

        RQ.get(HttpUrls.GET_UPDATE_INFO, params, values, new RQCallBack((BaseActivity) activity) {
            @Override
            public void success(JSONObject json) {
                String jsonStr = json.toString();
                Intent intent = new Intent();
                intent.setAction(Constant.ActionConstant.ACTION_GET_UPDATE_INFO_SUCCESS);
                intent.putExtra(Constant.BundleConstant.BUNDLE_GET_UPDATE_INFO, jsonStr);
                activity.sendBroadcast(intent);
            }

            @Override
            public void fail(JSONObject json) {
                String jsonStr = json.toString();
                Intent intent = new Intent();
                intent.setAction(Constant.ActionConstant.ACTION_GET_UPDATE_INFO_FAILE);
                intent.putExtra(Constant.BundleConstant.BUNDLE_GET_UPDATE_INFO, jsonStr);
                activity.sendBroadcast(intent);
            }
        });
    }

    /**
     * 登录
     * @param account    账号
     * @param password   密码
     */
    public Observable<String> login(final String account, final String password) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {
                List<String> params = new LinkedList<String>();
                List<String> values = new LinkedList<String>();
                params.add("account");
                params.add("password");
                values.add(account);
                values.add(password);
                RQ.post(HttpUrls.getUrls(HttpUrls.LOGIN), params, values, new RQCallBack((BaseActivity) activity) {
                    @Override
                    public void success(JSONObject json) {
                        String jsonStr = json.toString();

                        subscriber.onNext(jsonStr);
                        subscriber.onCompleted();
                    }

                    @Override
                    public void fail(JSONObject json) {
                        String jsonStr = json.toString();

                        subscriber.onError(new Throwable(jsonStr));
                    }
                });
            }
        });
    }
}
