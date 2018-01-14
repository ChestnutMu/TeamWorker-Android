package cn.chestnut.mvvm.teamworker.core;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.chestnut.mvvm.teamworker.Constant;
import cn.chestnut.mvvm.teamworker.main.common.MyApplication;
import cn.chestnut.mvvm.teamworker.socket.ReceiverProtocol;
import cn.chestnut.mvvm.teamworker.socket.TeamWorkerClient;
import cn.chestnut.mvvm.teamworker.utils.CommonUtil;
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    synchronized public void handleMessage(Message msg) {
        super.handleMessage(msg);

        int msgId = msg.arg1;
        Object response = msg.obj;

        switch (msgId) {
            case ReceiverProtocol.DUPLICATE_LOGIN:
                loginConflict();
                return;
            case ReceiverProtocol.RECEIVE_NEW_MESSAGE:
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
                        CommonUtil.showToast("当前网络不佳 , 请求访问超时！", MyApplication.getInstance());
                        if (listener != null)
                            listener.onSessionTimeout();
                        break;
                    default:
                        Log.d("向" + listener.getClass().getName() + "发送通知");
                        Log.d("内容：" + response);
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
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void loginConflict() {
        Log.d("已在其他设备登录");
        closeService();
        if (isForeground(MyApplication.getInstance())) {
            clearUserInfo();
            PreferenceUtil.getInstances(MyApplication.getInstance()).savePreferenceBoolean("isShowLoginConflict", true);
            goLogin();
        }
    }


    /**
     * 处理用户消息通知
     *
     * @param response
     */
    private void handleUserNotifyMessage(Object response) {
        Log.d("接收到一条新消息:" + response.toString());
        Intent intent = new Intent(Constant.ActionConstant.ACTION_GET_NEW_MESSAGE);
        cn.chestnut.mvvm.teamworker.module.massage.bean.Message newMessage = new Gson().fromJson(
                response.toString(),
                new TypeToken<cn.chestnut.mvvm.teamworker.module.massage.bean.Message>() {
                }.getType());
        intent.putExtra("newMessage", newMessage);
        LocalBroadcastManager.getInstance(MyApplication.getInstance()).sendBroadcast(intent);
    }

    /**
     * 退出登录跳转到登录界面，并重启
     */
    private void goLogin() {
        Intent i = MyApplication.getInstance().getPackageManager()
                .getLaunchIntentForPackage(MyApplication.getInstance().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        MyApplication.getInstance().startActivity(i);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private void clearUserInfo() {
        PreferenceUtil.getInstances(MyApplication.getInstance()).deleteKey("userId");
        PreferenceUtil.getInstances(MyApplication.getInstance()).deleteKey("account");
        PreferenceUtil.getInstances(MyApplication.getInstance()).deleteKey("token");
    }

    /**
     * 判断应用是否在前台
     *
     * @param context
     * @return
     */
    public static boolean isForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                    return false;
                } else {
                    return true;
                }
            }
        }
        return false;
    }
}
