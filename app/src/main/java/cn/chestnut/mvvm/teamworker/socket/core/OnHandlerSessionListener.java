package cn.chestnut.mvvm.teamworker.socket.core;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/10 20:49:36
 * Description：在UI线程中,可以直接修改视图
 * Email: xiaoting233zhang@126.com
 */

public interface OnHandlerSessionListener {
    /**
     * 与服务器建立链接会话时的消息接收,默认为逻辑处理
     *
     * @param msgId
     * @param object
     */
    void onSessionMessage(int msgId, Object object);

    /**
     * 与服务器建立链接会话过程异常
     *
     * @param msgId
     * @param exception
     */
    void onSessionMessageException(int msgId, Exception exception);

    /**
     * 与服务器建立链接会话关闭时
     */
    void onSessionClosed();

    /**
     * 与服务器建立建立链接会话时
     */
    void onSessionConnect();

    /**
     * 与服务器建立链接会话请求超时时
     */
    void onSessionTimeout();
}
