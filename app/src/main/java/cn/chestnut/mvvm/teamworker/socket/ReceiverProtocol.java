package cn.chestnut.mvvm.teamworker.socket;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/26 11:21:09
 * Description：接收长连接信息的协议
 * Email: xiaoting233zhang@126.com
 */
public class ReceiverProtocol {

    /**
     * 账号在其他设备登录，强制下线
     */
    public static final int DUPLICATE_LOGIN = 1004;

    /**
     * 接收新一条新消息
     */
    public static final int RECEIVE_NEW_MESSAGE = 1005;

    /**
     * 接收新的好友请求
     */
    public static final int RECEIVE_NEW_FRIEND_REQUEST = 1006;
}
