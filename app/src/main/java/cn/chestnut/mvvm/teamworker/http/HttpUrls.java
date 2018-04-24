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
    public static final String HTTPHOST = "http://172.18.92.153";

    public static final String GET_PHOTO = "http://p2fnlgaq8.bkt.clouddn.com/";

    //---------端口------------
    public static final String PORT = "8090";

    public static String getUrls(String api) {
        return HTTPHOST + ":" + PORT + api;
    }

    //获取更新信息
    public static final String GET_UPDATE_INFO = "";

    //------------------user接口--------------------------------------------------------
    //用户名密码登陆
    public static final String LOGIN = "/user/unauth/login";

    //token登陆
    public static final String REMEMBER_ME = "/user/rememberMe";

    //注册
    public static final String REGISTER = "/user/unauth/register";

    //通过账号搜索用户
    public static final String SEARCH_USER = "/user/searchUser";

    //获取用户信息(附带是否为我的好友信息：isMyFriend)
    public static final String GET_USER_DETAIL = "/user/getUserDetail";

    //获取我的好友
    public static final String GET_MY_FRIENDS = "/user/getMyFriends";

    //获取我的好友的信息
    public static final String GET_USER_LIST_INFO_BY_PERSONAL = "/user/getUserListInfoByPersonal";

    //发送好友申请
    public static final String SEND_FRIEND_REQUEST = "/user/sendFriendRequest";

    //接受好友请求
    public static final String ACCEPTED_REQUEST = "/user/acceptedRequest";

    //删除好友
    public static final String DEL_FRIEND = "/user/delFriend";

    //获取其还未接受成功的好友请求消息
    public static final String GET_REQUEST_BY_USERID = "/user/getRequestByUserId";

    //获取其还未接收成功的好友请求数量
    public static final String COUNT_NOT_SEND_REQUEST_BY_USERID = "/user/countNotSendRequestByUserId";

    //获取用户个人信息
    public static final String GET_MY_INFORMATION = "/user/getMyInformation";

    //修改用户个人信息
    public static final String UPDATE_MY_INFORMATION = "/user/updateMyInformation";

    //获取消息中用户的信息
    public static final String GET_USER_INFO = "/user/getUserInfo";


    //------------------消息接口---------------------------------------------------------------------
    //获取用户还未接收的消息列表
    public static final String GET_NOT_SEND_MESSAGES_BY_USER_ID = "/message/getNotSendMessagesByUserId";

    //创建聊天室
    public static final String BUILD_CHAT = "/chat/buildChat";

    //获取列表用户信息
    public static final String GET_USER_LIST_INFO = "/user/getUserListInfo";

    //聊天室发送消息
    public static final String SEND_CHAT_MESSAGE = "/chat/sendChatMessage";

    //获取聊天室
    public static final String GET_CHAT_LIST = "/chat/getChatList";

    //修改聊天室信息
    public static final String CHANGE_CHAT_INFO = "/chat/changeChatInfo";


    //-----------------------团队接口------------------------------------------------------
    //创建一个团队
    public static final String BUILD_TEAM = "/team/buildTeam";

    //获取我所属的团队
    public static final String GET_MY_TEAMS = "/team/getMyTeams";

    //获取我所属的团队
    public static final String GET_TEAM_RELATION = "/team/getTeamRelation";

    //获取团队人员
    public static final String GET_TEAMERS = "/team/getTeamers";

    //添加团队人员（只有团队owner和管理员可以添加）
    public static final String ADD_TEAM_USER = "/team/addTeamUser";

    //删除团队人员（只有团队owner和管理员可以添加）
    public static final String DEL_TEAM_USER = "/team/delTeamUser";

    //修改团队人员权限（只有团队owner可以修改）
    public static final String AUTH_TEAM_USER = "/team/authTeamUser";

    //--------------------------------考勤接口-------------------------------------------------------
    //获取个人打卡记录
    public static final String GET_PUNCH_CLOCK_RECORDS = "/attendance/getPunchClockRecords";

    //获取团队人员打卡记录
    public static final String GET_PUNCH_CLOCK_RECORDS_BY_TEAM = "/attendance/getPunchClockRecordsByTeam";

    //添加考勤打卡记录
    public static final String PUNCH_CLOCK = "/attendance/punchClock";

    //-------------------------第三方接口------------------------------------------------------------
    //获取七牛云token
    public static final String GET_QINIUTOKEN = "/third/getQiniuToken";


    //-------------------------请假接口--------------------------------------------------------------
    //请假
    public static final String APPLY_WORK_OFF = "/workoff/applyWorkOff";

    //审批请假
    public static final String APPROVE_WORK_OFF = "/workoff/approveWorkOff";

    //-------------------------中国地区接口-----------------------------------------------------------
    //获取省或市或县
    public static final String GET_ADDRESSES = "/address/getAddresses";

}
