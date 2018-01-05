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
     * 重复登录
     */
    public static final int DUPLICATE_LOGIN = 1001;//连接

    /**
     * 发送消息
     */
    public static final int MSG_SEND_MESSAGE = 1002;//发消息

    /**
     * 设置消息已读
     */
    public static final int MSG_ISREAD_MESSAGE = 1003;//已读


    /**
     * 消息通知
     */
    public static final int USER_MESSAGE = 1234;
}
