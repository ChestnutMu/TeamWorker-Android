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
//    public static final String HTTPHOST = "http://172.18.210.71";
    public static final String HTTPHOST = "http://192.168.199.241";
//    public static final String HTTPHOST = "http://172.18.92.153";
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

    //注册
    public static final String ADD_USER = "/user/addUser";

    //获取用户个人信息
    public static final String GET_MY_INFORMATION = "/user/getMyInformation";

    //修改用户个人信息
    public static final String UPDATE_MY_INFORMATION = "/user/updateMyInformation";

    //获取用户还未接收的消息列表
    public static final String GET_NOT_SEND_MESSAGES_BY_USER_ID = "/Message/getNotSendMessagesByUserId";

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

    //获取消息中用户的信息
    public static final String GET_USER_INFO = "/user/getUserInfo";

    //获取七牛云token
    public static final String GET_QINIUTOKEN = "/third/getQiniuToken";


}
