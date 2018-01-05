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
    public static final String HTTPHOST = "http://192.168.199.122";
    //---------端口------------
    public static final String PORT = "8090";

    public static String getUrls(String api) {
        return HTTPHOST + ":" + PORT + api;
    }

    //获取更新信息
    public static final String GET_UPDATE_INFO = "";

    //用户名密码登陆
    public static final String LOGIN = "/user/login";

    //token登陆
    public static final String REMEMBER_ME = "/user/rememberMe";

    //获取用户消息列表
    public static final String GET_MESSAGES_BY_USERID = "/Message/getMessagesByUserId";

    //获取所有部门
    public static final String GET_ALL_DEPARTMENTS = "/Department/getDepartments";

    //获取一个部门所有成员
    public static final String GET_USER_BY_DEPARTMENT = "/Department/getUserByDepartment";

    //根据UID获取考勤记录
    public static final String GET_ATTENDANCE = "/Attendance/getAttendance";

    //添加上班打卡记录
    public static final String PUNCH_IN = "/Attendance/punchIn";

    //添加下班打卡记录
    public static final String PUNCH_OUT = "/Attendance/punchOut";



}
