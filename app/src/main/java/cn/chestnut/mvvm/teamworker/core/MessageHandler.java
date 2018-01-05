package cn.chestnut.mvvm.teamworker.core;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/26 11:21:09
 * Description：不在UI线程,不能修改视图,但可以做其他操作
 * Email: xiaoting233zhang@126.com
 */

public interface MessageHandler {
    /**
     * 不在UI线程,不能修改视图,但可以做其他操作
     *
     * @param ex
     * @param exType
     * @param msgId
     * @param info
     */
    void onException(Exception ex, int exType, int msgId, String info);

    /**
     * 不在UI线程,不能修改视图,但可以做其他操作
     *
     * @param response
     */
    void onMessageReceived(Object... response);

    /**
     * 不在UI线程,不能修改视图,但可以做其他操作
     */
    void onSessionClosed();

    /**
     * 不在UI线程,不能修改视图,但可以做其他操作
     */
    void onSessionConnect();

    /**
     * 不在UI线程,不能修改视图,但可以做其他操作
     */
    void onSessionTimeout();
}
