package cn.chestnut.mvvm.teamworker.http;

import android.app.Activity;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import cn.chestnut.mvvm.teamworker.utils.PreferenceUtil;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/10 20:49:36
 * Description：数据请求管理类
 * Email: xiaoting233zhang@126.com
 */

public class RequestManager {
    static RequestManager requestManager;
    private Activity activity;
    private Gson gson = new Gson();

    public static RequestManager getInstance(Activity activity) {
        if (requestManager == null) {
            requestManager = new RequestManager();
        }
        requestManager.activity = activity;
        return requestManager;
    }

    public static void clearActivity() {
        if (requestManager != null && requestManager.activity != null) {
            requestManager.activity = null;
        }
    }

    /**
     * @param api
     * @param params
     * @param <T>
     * @return
     */
    public <T> void executeRequest(final String api, Map<String, Object> params, final AppCallBack<ApiResponse<T>> callBack) {
        Map<String, String> headers = new HashMap<>();
        headers.put("uid", PreferenceUtil.getInstances(activity).getPreferenceString("userId"));
        headers.put("token", PreferenceUtil.getInstances(activity).getPreferenceString("token"));
        RQ.post(HttpUrls.getUrls(api), headers, gson.toJson(params), callBack);
//        RQ.post(HttpUrls.getUrls(api), gson.toJson(params), callBack);
    }

    /**
     * @param api
     * @param params
     * @param <T>
     * @return
     */
    public <T> void executeRequest(final String api, Object params, final AppCallBack<ApiResponse<T>> callBack) {
        Map<String, String> headers = new HashMap<>();
        headers.put("uid", PreferenceUtil.getInstances(activity).getPreferenceString("userId"));
        headers.put("token", PreferenceUtil.getInstances(activity).getPreferenceString("token"));
        RQ.post(HttpUrls.getUrls(api), headers, gson.toJson(params), callBack);
//        RQ.post(HttpUrls.getUrls(api), gson.toJson(params), callBack);
    }

}
