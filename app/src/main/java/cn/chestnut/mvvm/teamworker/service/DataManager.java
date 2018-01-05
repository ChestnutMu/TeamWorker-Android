package cn.chestnut.mvvm.teamworker.service;

import android.app.Activity;

import com.google.gson.Gson;

import java.util.Map;

import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RQ;

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
    private Gson gson;


    public static DataManager getInstance(Activity activity) {
        if (dataManager == null) {
            dataManager = new DataManager();
        }
        dataManager.activity = activity;
        return dataManager;
    }

    public static void clearActivity() {
        if (dataManager != null && dataManager.activity != null) {
            dataManager.activity = null;
        }
    }

    /**
     * @param api
     * @param params
     * @param <T>
     * @return
     */
    public <T> void executeRequest(final String api, final Map<String, Object> params, final AppCallBack<ApiResponse<T>> callBack) {
        RQ.post(HttpUrls.getUrls(api), params, callBack);
    }

//    new RQCallBack<T>((BaseActivity) activity) {
//        @Override
//        public void success(ApiResponse<T> obj) {
//            //获取subscriber的返回类型
////                        ParameterizedType type=(ParameterizedType)subscriber.getClass().getGenericInterfaces()[0];
////                        Type typeOfT=type.getActualTypeArguments()[0];
////                        String jsonStr = json.toString();
////                        ApiResponse<T> result=gson.fromJson(jsonStr,typeOfT);
//            subscriber.onNext(obj);
//            subscriber.onCompleted();
//        }
//
//        @Override
//        public void fail(ApiResponse<T> json) {
//            String jsonStr = json.toString();
//
//            subscriber.onError(new Throwable(jsonStr));
//        }
//    }
}
