package cn.chestnut.mvvm.teamworker.http;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.internal.http.HttpDate;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.chestnut.mvvm.teamworker.main.common.MyApplication;
import cn.chestnut.mvvm.teamworker.utils.CommonUtil;
import cn.chestnut.mvvm.teamworker.utils.Log;
import cn.chestnut.mvvm.teamworker.utils.TimeManager;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/10 20:49:36
 * Description：网络请求
 * Email: xiaoting233zhang@126.com
 */

public class RQ {
    private static Gson gson = new Gson();
    private static OkHttpClient CLIENT = null;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static void getInstance() {
        if (CLIENT == null) {
            CLIENT = new OkHttpClient();
            CLIENT.setConnectTimeout(10, TimeUnit.SECONDS);
            CLIENT.setWriteTimeout(10, TimeUnit.SECONDS);
            CLIENT.setReadTimeout(30, TimeUnit.SECONDS);
        }
    }


    public static void get(String urlTo, List<String> params,
                           List<String> values, final AppCallBack<String> app) {
        if (!CommonUtil.checkNetState(MyApplication.getInstance())) {
            if (app != null) {
                JSONObject temp = new JSONObject();
                temp.put("success", false);
                temp.put("message", "无网络连接！");
                app.next(temp.toString());
                return;
            }
        } else {
            getInstance();
            FormEncodingBuilder builder = new FormEncodingBuilder();
            for (int i = 0; i < params.size(); i++) {
                String p = params.get(i);
                String v = values.get(i);
                builder.add(p, v);
                if (i == 0) {
                    urlTo += "?" + p + "=" + v;
                } else {
                    urlTo += "&" + p + "=" + v;
                }
            }
            final String url = urlTo;
            final RequestBody body = builder.build();
            Observable
                    .create(new OnSubscribe<String>() {
                        @Override
                        public void call(Subscriber<? super String> subscriber) {
                            try {
                                Request request = new Request.Builder().url(url)
                                        .post(body).build();
                                Response response = CLIENT.newCall(request)
                                        .execute();
                                int code = response.code();
                                if (code != 200) {
                                    JSONObject temp = new JSONObject();
                                    temp.put("status", false);
                                    temp.put("message", "请求服务器失败！");
                                    subscriber.onNext(temp.toJSONString());
                                    subscriber.onCompleted();
                                    response.body().close();
                                } else {
                                    // String reslut = URLDecoder.decode(
                                    // response.body().string(), "utf-8")
                                    // .trim();
                                    String reslut = response.body().string().trim();
                                    subscriber.onNext(reslut);
                                    subscriber.onCompleted();
                                }

                            } catch (Exception e) {
                                try {
                                    JSONObject temp = new JSONObject();
                                    temp.put("status", false);
                                    temp.put("message", "网络断开！");
                                    subscriber.onNext(temp.toJSONString());
                                    subscriber.onError(e);
                                    subscriber.onCompleted();
                                } catch (Exception e2) {
                                    e2.printStackTrace();
                                }
                            }
                        }
                    }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<String>() {
                        @Override
                        public void onCompleted() {
                            if (app != null) {
                                app.complete();
                            }
                        }

                        @Override
                        public void onError(Throwable t) {
                            t.printStackTrace();
                            if (app != null) {
                                app.error(t);
                            }
                        }

                        @Override
                        public void onNext(String result) {
                            if (app != null) {
                                app.next(result);
                            }
                        }
                    });
        }
    }

    public static void post(final String url, List<String> params,
                            List<Object> values, final AppCallBack<String> app) {
        if (!CommonUtil.checkNetState(MyApplication.getInstance())) {
            if (app != null) {
                JSONObject temp = new JSONObject();
                temp.put("status", false);
                temp.put("message", "无网络连接！");
                app.next(temp.toString());
                return;
            }
        } else {
            getInstance();
            FormEncodingBuilder builder = new FormEncodingBuilder();
            for (int i = 0; i < params.size(); i++) {
                String p = params.get(i);
                Object v = values.get(i);
                builder.add(p, v.toString());
            }
            final RequestBody body = builder.build();

            Observable.create(new OnSubscribe<String>() {
                @Override
                public void call(Subscriber<? super String> subscriber) {
                    try {
                        Request request = new Request.Builder().url(url)
                                .post(body).build();
                        Response response = CLIENT.newCall(request)
                                .execute();
                        int code = response.code();
                        if (code != 200) {
                            JSONObject temp = new JSONObject();
                            temp.put("status", false);
                            temp.put("message", "服务器请求失败！");
                            subscriber.onNext(temp.toJSONString());
                            subscriber.onCompleted();
                            response.body().close();
                        } else {
                            String reslutTextOrg = response.body().string()
                                    .trim();
                            String reslut = "";
                            try {
                                reslut = URLDecoder.decode(reslutTextOrg,
                                        "utf-8");
                            } catch (IllegalArgumentException e) {
                                reslut = reslutTextOrg;
                            }
                            subscriber.onNext(reslut);
                            subscriber.onCompleted();
                        }

                    } catch (Exception e) {
                        try {
                            JSONObject temp = new JSONObject();
                            temp.put("success", false);
                            temp.put("message", "网络断开！");
                            subscriber.onNext(temp.toJSONString());
                            subscriber.onError(e);
                            subscriber.onCompleted();
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    }
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<String>() {
                        @Override
                        public void onCompleted() {
                            if (app != null) {
                                app.complete();
                            }
                        }

                        @Override
                        public void onError(Throwable t) {
                            t.printStackTrace();
                            if (app != null) {
                                app.error(t);
                            }
                        }

                        @Override
                        public void onNext(String result) {
                            if (app != null) {
                                app.next(result);
                            }
                        }
                    });
        }

    }

    public static <T> void post(final String url, final Map<String, String> headers, String params, final AppCallBack<ApiResponse<T>> app) {
        Log.d("url = " + url + "      params = " + params);
        if (!CommonUtil.checkNetState(MyApplication.getInstance())) {
            if (app != null) {
                ApiResponse<T> temp = new ApiResponse<>();
                temp.setStatus(HttpResponseCodes.FAILED);
                temp.setMessage("无网络连接！");
                app.next(temp);
                return;
            }
        } else {
            getInstance();
            final RequestBody body = RequestBody.create(JSON, params);

            Observable.create(new OnSubscribe<ApiResponse<T>>() {
                @Override
                public void call(Subscriber<? super ApiResponse<T>> subscriber) {
                    try {
                        Request.Builder builder = new Request.Builder().url(url)
                                .post(body);
                        if (!headers.isEmpty()) {
                            for (Map.Entry<String, String> header : headers.entrySet()) {
                                builder.addHeader(header.getKey(), header.getValue());
                            }
                        }
                        Request request = builder.build();
                        Response response = CLIENT.newCall(request)
                                .execute();
                        int code = response.code();
                        if (code != 200) {

                            ApiResponse<T> temp = new ApiResponse<>();
                            temp.setStatus(HttpResponseCodes.FAILED);
                            temp.setMessage("服务器请求失败！");
                            subscriber.onNext(temp);
                            subscriber.onCompleted();
                            response.body().close();
                        } else {
                            String reslutTextOrg = response.body().string()
                                    .trim();
                            String reslut = "";
                            try {
                                reslut = URLDecoder.decode(reslutTextOrg,
                                        "utf-8");
                            } catch (IllegalArgumentException e) {
                                reslut = reslutTextOrg;
                            }
                            Log.d("result = " + reslut);
                            ParameterizedType type = (ParameterizedType) app.getClass().getGenericInterfaces()[0];
                            Type typeOfT = type.getActualTypeArguments()[0];
                            ApiResponse<T> temp = gson.fromJson(reslut, typeOfT);
                            subscriber.onNext(temp);
                            subscriber.onCompleted();
                        }

                    } catch (Exception error) {
                        try {
                            String errorMessage;

                            if (error instanceof ConnectException) {
                                errorMessage = "服务器连接失败！";
                            } else if (error instanceof SocketTimeoutException) {
                                errorMessage = "连接超时!";
                            } else {
                                errorMessage = "请求异常!";
                            }

                            ApiResponse<T> temp = new ApiResponse<>();
                            temp.setStatus(HttpResponseCodes.FAILED);
                            temp.setMessage(errorMessage);
                            subscriber.onNext(temp);
                            subscriber.onCompleted();
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    }
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ApiResponse<T>>() {
                        @Override
                        public void onCompleted() {
                            if (app != null) {
                                app.complete();
                            }
                        }

                        @Override
                        public void onError(Throwable t) {
                            t.printStackTrace();
                            if (app != null) {
                                app.error(t);
                            }
                        }

                        @Override
                        public void onNext(ApiResponse<T> result) {
                            if (app != null) {
                                app.next(result);
                            }
                        }
                    });
        }

    }

    public static <T> void post(final String url, String params, final AppCallBack<ApiResponse<T>> app) {
        post(url, null, params, app);
    }

    class TimeCalibrationInterceptor implements Interceptor {
        long minResponseTime = Long.MAX_VALUE;

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            long startTime = System.nanoTime();
            Response response = chain.proceed(request);
            long responseTime = System.nanoTime() - startTime;

            Headers headers = response.headers();
            calibration(responseTime, headers);
            return response;
        }

        private void calibration(long responseTime, Headers headers) {
            if (headers == null) {
                return;
            }

            //如果这一次的请求响应时间小于上一次，则更新本地维护的时间
            if (responseTime >= minResponseTime) {
                return;
            }

            String standardTime = headers.get("Date");
            if (!TextUtils.isEmpty(standardTime)) {
                Date parse = HttpDate.parse(standardTime);
                if (parse != null) {
                    // 客户端请求过程一般大于比收到响应时间耗时，所以没有简单的除2 加上去，而是直接用该时间
                    TimeManager.getInstance().initServerTime(parse.getTime());
                    minResponseTime = responseTime;
                }
            }
        }
    }
}
