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
    public static final String HTTPHOST = "http://192.168.199.241";
//    public static final String HTTPHOST = "http://http://13.230.251.164";

    public static final String GET_PHOTO = "http://p2fnlgaq8.bkt.clouddn.com/";

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

    //通过账号搜索用户
    public static final String SEARCH_USER = "/user/searchUser";

    //获取用户个人信息
    public static final String GET_MY_INFORMATION = "/user/getMyInformation";

    //修改用户个人信息
    public static final String UPDATE_MY_INFORMATION = "/user/updateMyInformation";

    //获取用户还未接收的消息列表
    public static final String GET_NOT_SEND_MESSAGES_BY_USER_ID = "/message/getNotSendMessagesByUserId";

    //创建一个部门
    public static final String ADD_DEPARTMENT = "/department/addDepartment";

    //获取一个部门的所有成员
    public static final String GET_USER_BY_DEPARTMENT = "/department/getUserByDepartment";

    //获取用户所属的部门
    public static final String GET_DEPARTMENT_BY_USERID = "/department/getDepartmentByUserId";

    //添加一条部门关系记录
    public static final String ADD_DEPARTMENT_RELATION = "/department/addDepartmentRelation";

    //添加多条部门关系记录
    public static final String ADD_DEPARTMENT_RELATIONS = "/department/addDepartmentRelations";

    //添加一条部门成员关系记录
    public static final String ADD_DEPARTMENT_MEMBER_RELATION = "/department/addDepartmentMemberRelation";

    //添加多条部门成员关系记录
    public static final String ADD_DEPARTMENT_MEMBER_RELATIONS = "/department/addDepartmentMemberRelations";

    //根据UID获取考勤记录
    public static final String GET_ATTENDANCE = "/attendance/getAttendance";

    //添加上班打卡记录
    public static final String PUNCH_IN = "/attendance/punchIn";

    //添加下班打卡记录
    public static final String PUNCH_OUT = "/attendance/punchOut";

    //获取消息中用户的信息
    public static final String GET_USER_INFO = "/user/getUserInfo";

    //获取七牛云token
    public static final String GET_QINIUTOKEN = "/third/getQiniuToken";

    //请假
    public static final String APPLY_WORK_OFF = "/workoff/applyWorkOff";

    //审批请假
    public static final String APPROVE_WORK_OFF = "/workoff/approveWorkOff";



}
