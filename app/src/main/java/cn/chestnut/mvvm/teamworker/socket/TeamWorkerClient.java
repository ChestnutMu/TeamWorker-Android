package cn.chestnut.mvvm.teamworker.socket;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.main.common.MyApplication;
import cn.chestnut.mvvm.teamworker.utils.Log;
import cn.chestnut.mvvm.teamworker.utils.PreferenceUtil;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/26 11:21:09
 * Description: 客户端发起Socket连接
 * Email: xiaoting233zhang@126.com
 */

public class TeamWorkerClient {
    private static TeamWorkerClient mSocketClient;
    private static TeamWorkerSocket socket;
    private static List<IORequest> requests = new CopyOnWriteArrayList<IORequest>();
    private String serverAddress = HttpUrls.HTTPHOST;
    private int serverPort = 8092;

    private TeamWorkerClient() {
        super();
    }

    synchronized public static TeamWorkerClient getIntenace() {
        if (mSocketClient == null)
            mSocketClient = new TeamWorkerClient();
        return mSocketClient;
    }

    /**
     * 1、通过地址获取动态指向IP <br />
     * 2、是否连接本地局域服务 如果是将直接读取 IP , PORT <br />
     *
     * @param isLocalService
     * @return
     */
    private void initTeamWorkerClent(String ip, int port, boolean isLocalService) {
        if (!isLocalService) {
            this.serverAddress = ip;
            this.serverPort = port;
            Log.d("动态配置信息  [Ip = " + serverAddress + " , Port = " + serverPort + "]");
        } else {
            Log.d("本地配置信息  [Ip = " + serverAddress + " , Port = " + serverPort + "]");
        }
    }

    /**
     * 启动连接
     */
    private void startSocket() {
        synchronized (mSocketClient) {
            if (socket != null && socket.isRuning())
                return;

            stopSocket();
            socket = new TeamWorkerSocket("TeamWorker");
            socket.setConnectionCallback(new TeamWorkerSocket.ConnectionCallback() {
                @Override
                public void onConnect() {
                    if (requests.isEmpty()) {
                        userLogin();
                    } else {
                        Iterator<IORequest> iterator = requests.iterator();
                        while (iterator.hasNext()) {
                            IORequest temp = iterator.next();
                            send(temp.getUid(), temp.getToken(), temp.getMsgId(), temp.getObj());
                        }
                        requests.clear();
                    }
                }

                @Override
                public void onDisconnect() {

                }
            });
            socket.start(MyApplication.getTeamWorkerMessageHandler(), serverAddress, serverPort);
            Log.d("synchronized.ClientSocket.start()!");
        }
    }

    /**
     * 结束连接
     */
    private void stopSocket() {
        if (socket != null) {
            socket.close();
            socket = null;
        }
    }

    public void send(Object... obj) {
        if (socket != null && socket.isRuning()) {
            socket.send(obj);
        } else {
            requests.add(new IORequest((String) obj[0], (String) obj[1], (int) obj[2], (String) obj[3]));
            Log.d("socket is null or not runing , trying to create socket and connect...");
            connectService();
        }
    }

    /**
     * 连接服务器,連接成功或失敗,都回調TeamWorkerHandle.session
     */
    public void connectService() {

        initTeamWorkerClent("1.1.1.1", 0000, true);
        startSocket();
    }

    public boolean isRuning() {
        return socket != null && socket.isRuning();
    }

    /**
     * 关闭服务器
     */
    public void closeService() {
        stopSocket();
    }

    /**
     * 用户登录,如果没有绑定就做重新发送,比如请验证码这一块
     */
    public void userLogin() {
        String userId = PreferenceUtil.getInstances(MyApplication.getInstance()).getPreferenceString("userId");
        String token = PreferenceUtil.getInstances(MyApplication.getInstance()).getPreferenceString("token");
        Log.d("开始登录,向服务器发送数据..." + "uerId:"+userId+" token:"+token+" msgId:1001 obj:null" );
        send(userId, token, SendProtocol.CONNECTION_AUTHENTICATION, null);
    }
}
