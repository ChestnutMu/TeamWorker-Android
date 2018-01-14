package cn.chestnut.mvvm.teamworker.socket;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/26 11:21:09
 * Description：发送请求给长连接的协议
 * Email: xiaoting233zhang@126.com
 */

public class SendProtocol {
    /**
     * 连接认证
     * 键值对
     * uid uid
     * token 认证标识
     */
    public static final int CONNECTION_AUTHENTICATION = 1001;

    /**
     * 发送消息
     */
    public static final int MSG_SEND_MESSAGE = 1002;

    /**
     * 设置消息已读
     */
    public static final int MSG_ISREAD_MESSAGE = 1003;

    /**
     * 设置消息已接收
     */
    public static final int MSG_ISSEND_MESSAGE = 1006;
}
