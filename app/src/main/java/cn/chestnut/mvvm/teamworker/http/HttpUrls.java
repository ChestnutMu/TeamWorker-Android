package cn.chestnut.mvvm.teamworker.http;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/10 20:49:36
 * Description：网络请求API
 * Email: xiaoting233zhang@126.com
 */

public class HttpUrls {
    //---------域名------------
    public static final String httpHost = "http://192.168.199.122:8090";
//    public static final String httpHost = "http://localhost:8090";

    //获取更新信息
    public static final String GET_UPDATE_INFO = "";

    //
    public static final String LOGIN = "/user/login";

    public static String getUrls(String api) {
        return httpHost + api;
    }

}
