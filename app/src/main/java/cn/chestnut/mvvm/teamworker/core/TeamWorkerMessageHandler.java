package cn.chestnut.mvvm.teamworker.core;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.util.ArrayList;

import cn.chestnut.mvvm.teamworker.socket.ReceiverProtocol;
import cn.chestnut.mvvm.teamworker.socket.TeamWorkerClient;
import cn.chestnut.mvvm.teamworker.main.common.MyApplication;
import cn.chestnut.mvvm.teamworker.utils.Log;
import cn.chestnut.mvvm.teamworker.utils.PreferenceUtil;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/10 20:49:36
 * Description：网络请求回调接口
 * Email: xiaoting233zhang@126.com
 */

public class TeamWorkerMessageHandler extends Handler implements MessageHandler {

    /**
     * 连接失败 或会话关闭或被服务器踢了
     **/
    public final static int SESSION_CLOSE = -1;
    /**
     * 连接并建立会话成功
     **/
    public final static int SESSION_CONNECT = -2;
    /**
     * 连接或访问超时
     **/
    public final static int SESSION_TIMEOUT = -3;

    private static TeamWorkerMessageHandler TeamWorkerMessageHandler;

    private ArrayList<OnHandlerSessionListener> mSessionListeners;

    private TeamWorkerMessageHandler() {
        super();
    }

    synchronized public static TeamWorkerMessageHandler getInstance() {
        if (TeamWorkerMessageHandler == null)
            TeamWorkerMessageHandler = new TeamWorkerMessageHandler();

        return TeamWorkerMessageHandler;
    }

    /**
     * 会话消息监听器,注册事件才能响应客户端与服务器连接的所有状态
     **/
    public void addOnTWHandlerSessionListener(OnHandlerSessionListener listener) {
        if (mSessionListeners == null)
            mSessionListeners = new ArrayList<OnHandlerSessionListener>();

        if (!mSessionListeners.contains(listener))
            mSessionListeners.add(listener);
    }

    /**
     * 删除会话消息监听器
     **/
    public void removeTWHandlerSessionListener(OnHandlerSessionListener listener) {
        if (mSessionListeners != null && mSessionListeners.contains(listener))
            mSessionListeners.remove(listener);
    }

    /**
     * 使用长连接发送数据
     *
     * @param msgId
     * @param obj
     */
    public void send(int msgId, String obj) {
        String userId = PreferenceUtil.getInstances(MyApplication.getInstance()).getPreferenceString("userId");
        String token = PreferenceUtil.getInstances(MyApplication.getInstance()).getPreferenceString("token");
        TeamWorkerClient.getIntenace().send(userId, token, msgId, obj);
    }

    @Override
    synchronized public void handleMessage(Message msg) {
        super.handleMessage(msg);

        int msgId = msg.arg1;
        Object response = msg.obj;

        switch (msgId) {
            case ReceiverProtocol.DUPLICATE_LOGIN:
                loginConflict();
                return;
            case ReceiverProtocol.USER_MESSAGE:
                handleUserNotifyMessage(response);
                return;
            default:
                break;
        }

        if (mSessionListeners != null)
            for (OnHandlerSessionListener listener : mSessionListeners) {
                notifySessionListeners(msgId, response, listener);
            }
    }

    private void notifySessionListeners(int msgId, Object response, OnHandlerSessionListener listener) {
        if (listener != null) {
            try {
                switch (msgId) {
                    case SESSION_CLOSE:
                        if (listener != null)
                            listener.onSessionClosed();
                        break;
                    case SESSION_CONNECT:
                        if (listener != null)
                            listener.onSessionConnect();
                        break;
                    case SESSION_TIMEOUT:
                        makeToast("当前网络不佳 , 请求访问超时！");
                        if (listener != null)
                            listener.onSessionTimeout();
                        break;
                    default:
                        Log.d("向" + listener.getClass().getName() + "发送通知");
                        Log.d("内容："+response);
                        listener.onSessionMessage(msgId, response);
                        break;
                }
            } catch (Exception ex) {
                listener.onSessionMessageException(msgId, ex);
            }
        } else {
            Log.d(" ReceivedInterface is null , TeamWorkerMessageHandler can not notify Some of the impl");
        }
    }

    private void sendMessage(int msgId, Object object) {
        Message msg = this.obtainMessage();

        // 消息ID 主要用于做分发
        msg.arg1 = msgId;
        msg.obj = object;
        sendMessage(msg);
    }

    @Override
    public void onException(Exception ex, int exType, int msgId, String info) {
        if (ex != null)
            Log.d("发送请求异常或者长连接运行时出异常了  [msgId=" + msgId + " , errorMsg=" + ex.getMessage() + " , engineInfo=" + info + "]");
    }

    @Override
    public void onMessageReceived(Object... response) {
        Log.d(String.valueOf((Integer) response[0]));
        sendMessage((Integer) response[0], response[1]);
    }

    @Override
    public void onSessionClosed() {
        Log.d("不好，链接关闭了!");
        sendMessage(SESSION_CLOSE, null);
    }

    @Override
    public void onSessionConnect() {
        Log.d("不错哦，连接服务器成功!");
        sendMessage(SESSION_CONNECT, null);
    }

    @Override
    public void onSessionTimeout() {
        Log.d("不好，访问服务器或链接服务器超时!");
        sendMessage(SESSION_TIMEOUT, null);
    }

    private void makeToast(String text) {
        Toast.makeText(MyApplication.getInstance(), text, Toast.LENGTH_SHORT).show();
    }

    /**
     * 用户登录<br />
     */
    public void userLogin() {
        TeamWorkerClient.getIntenace().userLogin();
    }

    /**
     * 连接服务器,成功或失敗,都回调session,并重置为不是抢登关闭<br />
     */
    public void connectService() {
        TeamWorkerClient.getIntenace().connectService();
    }

    /**
     * 关闭服务器
     */
    public void closeService() {
        TeamWorkerClient.getIntenace().closeService();
    }

    /**
     * app处于后台的下线通知
     */
    private void loginConflict() {
        Log.d("已在其他设备登录");
//        if (AppUtil.isBackground(WuYuApplication.getContext())) {
//            closeService();
//            UserManager.clearBingAccountInfo();
//            NotificationUtil.showNotifyWithVibrate(WuYuApplication.getContext(), "下线通知", "已在其他设备登录", WuYuApplication.class);
//        } else {
//            closeService();
//            UserManager.clearBingAccountInfo();
//            // 显示登陆冲突dialog
//            UserManager.setLoginConflict(true);
//            goLogin();
//        }
    }


    /**
     * 处理用户消息通知
     *
     * @param response
     */
    private void handleUserNotifyMessage(Object response) {
        Log.d(response.toString());
    }


    /**
     * 退出登录跳转到登录界面，并重启
     */
    private void goLogin() {
//        Intent i = WuYuApplication.getContext().getPackageManager()
//                .getLaunchIntentForPackage(WuYuApplication.getContext().getPackageName());
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        WuYuApplication.getContext().startActivity(i);
//        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
