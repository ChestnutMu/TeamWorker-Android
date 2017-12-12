package cn.chestnut.mvvm.teamworker.http;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Environment;

import com.alibaba.fastjson.JSONObject;
import cn.chestnut.mvvm.teamworker.main.common.MyApplication;
import cn.chestnut.mvvm.teamworker.service.Constant;
import cn.chestnut.mvvm.teamworker.utils.CommonUtil;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
    public static OkHttpClient CLIENT = null;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static void getInstance() {
        if (CLIENT == null) {
            CLIENT = new OkHttpClient();
            CLIENT.setConnectTimeout(10, TimeUnit.SECONDS);
            CLIENT.setWriteTimeout(10, TimeUnit.SECONDS);
            CLIENT.setReadTimeout(30, TimeUnit.SECONDS);
        }
    }


    public final static void get(String urlTo, List<String> params,
                                 List<String> values, final AppCallBack<String> app) {
        if (!CommonUtil.checkNetState(MyApplication.getInstance())) {
            if (app != null) {
                JSONObject temp = new JSONObject();
                temp.put("success", false);
                temp.put("message", "无网络连接！");
                app.cb(temp.toString());
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
            if (app != null) {
                app.before();
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
                                app.cb(result);
                            }
                        }
                    });
        }
    }

    public final static void post(final String url, List<String> params,
                                  List<String> values, final AppCallBack<String> app) {
        if (!CommonUtil.checkNetState(MyApplication.getInstance())) {
            if (app != null) {
                JSONObject temp = new JSONObject();
                temp.put("status", false);
                temp.put("message", "无网络连接！");
                app.cb(temp.toString());
                return;
            }
        } else {
            getInstance();
            FormEncodingBuilder builder = new FormEncodingBuilder();
            for (int i = 0; i < params.size(); i++) {
                String p = params.get(i);
                String v = values.get(i);
                builder.add(p, v);
            }
            if (app != null) {
                app.before();
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
                                app.cb(result);
                            }
                        }
                    });
        }

    }

    public final static void post(final String url, String json, final AppCallBack<String> app) {
        if (!CommonUtil.checkNetState(MyApplication.getInstance())) {
            if (app != null) {
                JSONObject temp = new JSONObject();
                temp.put("success", false);
                temp.put("message", "无网络连接！");
                app.cb(temp.toString());
                return;
            }
        } else {
            getInstance();
            final RequestBody body = RequestBody.create(JSON, json);
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
                            temp.put("success", false);
                            temp.put("message", "请求服务器失败！");
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
                                app.cb(result);
                            }
                        }
                    });
        }

    }

    public final static void delete(final String url, List<String> params,
                                    List<String> values, final AppCallBack<String> app) {
        if (!CommonUtil.checkNetState(MyApplication.getInstance())) {
            if (app != null) {
                JSONObject temp = new JSONObject();
                temp.put("status", false);
                temp.put("message", "无网络连接！");
                app.cb(temp.toString());
                return;
            }
        } else {

            getInstance();
            FormEncodingBuilder builder = new FormEncodingBuilder();
            for (int i = 0; i < params.size(); i++) {
                String p = params.get(i);
                String v = values.get(i);
                builder.add(p, v);
            }
            if (app != null) {
                app.before();
            }
            final RequestBody body = builder.build();

            Observable.create(new OnSubscribe<String>() {
                @Override
                public void call(Subscriber<? super String> subscriber) {
                    try {
                        Request request = new Request.Builder().url(url)
                                .delete(body).build();
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
                                app.cb(result);
                            }
                        }
                    });
        }

    }

    /**
     * 上传文件
     *
     * @param url
     * @param paramsMap
     * @param app
     * @throws Exception
     */

    public final static void uploadFile(final String url, final HashMap<String, Object> paramsMap, final RQCallBack app) throws Exception {
        getInstance();
        if (app != null) {
            app.before();
        }
        Observable.create(new OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    MultipartBuilder builder = new MultipartBuilder();
                    builder.type(MultipartBuilder.FORM);
                    //追加参数
                    for (String key : paramsMap.keySet()) {
                        Object object = paramsMap.get(key);
                        if (!(object instanceof File)) {
                            builder.addFormDataPart(key, object.toString());
                        } else {
                            File file = (File) object;
                            builder.addFormDataPart(key, file.getName(), RequestBody.create(null, file));
                        }
                    }
                    //创建RequestBody
                    RequestBody requestBody = builder.build();
                    final Request request = new Request.Builder()
                            .url(url)
                            .post(requestBody).build();
                    Response response = CLIENT.newCall(request)
                            .execute();
                    if (!response.isSuccessful()) {
                        JSONObject temp = new JSONObject();
                        temp.put("status", false);
                        temp.put("message", "请求服务器失败！");
                        subscriber.onNext(temp.toJSONString());
                        subscriber.onCompleted();
                    } else {
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
        }).subscribeOn(Schedulers.newThread())
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
                            app.cb(result);
                        }
                    }
                });
    }


    /**
     * 下载apk
     *
     * @param downLoadUlr
     * @param app
     */
    public final static void downLoadAPK(final Activity activity, String downLoadUlr, final ProgressDialog progressDialog, final RQCallBack app) {
        if (!CommonUtil.checkNetState(MyApplication.getInstance())) {
            if (app != null) {
                JSONObject temp = new JSONObject();
                temp.put("status", false);
                temp.put("message", "无网络连接！");
                app.cb(temp.toString());
                return;
            }
        }
        getInstance();
        if (app != null) {
            app.before();
        }
        final String url = downLoadUlr;
        Observable
                .create(new OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        try {
                            Request request = new Request.Builder().url(url)
                                    .build();
                            Response response = CLIENT.newCall(request)
                                    .execute();
                            int code = response.code();
                            if (code != 200) {
                                response.body().close();
                                JSONObject temp = new JSONObject();
                                temp.put("status", false);
                                temp.put("message", "请求服务器失败！");
                                subscriber.onNext(temp.toJSONString());
                                subscriber.onCompleted();

                            } else {
                                InputStream is = null;//输入流
                                FileOutputStream fos = null;//输出流
                                try {
                                    is = response.body().byteStream();//获取输入流
                                    final long total = response.body().contentLength();//获取文件大小
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressDialog.setMax((int) total);//为progressDialog设置大小
                                        }
                                    });

                                    if (is != null) {
                                        File file = new File(Environment.getExternalStorageDirectory(), Constant.PreferenceConstants.APK_NAME);// 设置路径
                                        fos = new FileOutputStream(file);
                                        byte[] buf = new byte[1024];
                                        int ch = -1;
                                        int process = 0;
                                        while ((ch = is.read(buf)) != -1) {
                                            fos.write(buf, 0, ch);
                                            process += ch;
                                            final int Process = process;
                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progressDialog.setProgress(Process); //实时更新进度
                                                }
                                            });
                                        }

                                    }
                                    fos.flush();
                                    // 下载完成
                                    if (fos != null) {
                                        fos.close();
                                    }
                                    JSONObject temp = new JSONObject();
                                    temp.put("status", true);
                                    temp.put("message", "下载成功！");
                                    subscriber.onNext(temp.toJSONString());
                                    subscriber.onCompleted();
                                } catch (Exception e) {
                                    if (app != null) {
                                        JSONObject temp = new JSONObject();
                                        temp.put("status", false);
                                        temp.put("message", "下载失败");
                                        app.cb(temp.toString());
                                    }

                                } finally {
                                    try {
                                        if (is != null)
                                            is.close();
                                    } catch (IOException e) {
                                        if (app != null) {
                                            JSONObject temp = new JSONObject();
                                            temp.put("status", false);
                                            temp.put("message", "下载失败");
                                            app.cb(temp.toString());
                                        }
                                    }
                                    try {
                                        if (fos != null)
                                            fos.close();
                                    } catch (IOException e) {
                                        if (app != null) {
                                            JSONObject temp = new JSONObject();
                                            temp.put("status", false);
                                            temp.put("message", "下载失败");
                                            app.cb(temp.toString());
                                        }
                                    }
                                }
                            }

                        } catch (Exception e) {
                            JSONObject temp = new JSONObject();
                            temp.put("status", false);
                            temp.put("message", "网络断开");
                            subscriber.onNext(temp.toJSONString());
                            subscriber.onError(e);
                            subscriber.onCompleted();

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
                            app.cb(result);
                        }
                    }
                });

    }
}
