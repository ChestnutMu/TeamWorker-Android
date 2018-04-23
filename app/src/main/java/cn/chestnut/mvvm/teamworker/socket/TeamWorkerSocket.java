package cn.chestnut.mvvm.teamworker.socket;

import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicInteger;

import cn.chestnut.mvvm.teamworker.socket.core.MessageHandler;
import cn.chestnut.mvvm.teamworker.main.common.MyApplication;
import cn.chestnut.mvvm.teamworker.utils.Log;
import cn.chestnut.mvvm.teamworker.utils.PreferenceUtil;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/26 11:21:09
 * Description: Socket
 * Email: xiaoting233zhang@126.com
 */

public class TeamWorkerSocket {
    private static final String TAG_SEND_MESSAGE = "tag_user_send_message";
    private static final String TAG_RECEIVER_MESSAGE = "tag_user_receiver_message";
    private static final int EVENT_CONNECT = 1;
    private static final int EVENT_DISCONNECT = 2;
    private Socket mSocket;
    private MessageHandler messageHandler = null;
    private String serverIP;
    private int serverPort;
    private volatile boolean tiClose = true;
    private String name;
    private AtomicInteger reconnectErrorCount = new AtomicInteger(0);

    private ConnectionCallback connectionCallback;
    /**
     * 目前只处理连接上和断开连接事件
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int msgId = msg.arg1;
            switch (msgId) {
                case EVENT_CONNECT:
                    if (connectionCallback != null) {
                        connectionCallback.onConnect();
                    }
                    break;
                case EVENT_DISCONNECT:
                    if (connectionCallback != null) {
                        connectionCallback.onDisconnect();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private Emitter.Listener onReceiverMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("服务器发送数据 len: " + args.length);
            if (TeamWorkerSocket.this.messageHandler != null) {
                TeamWorkerSocket.this.messageHandler.onMessageReceived(args);
            }
        }
    };
    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("连网");
            if (TeamWorkerSocket.this.messageHandler != null) {
                Message msg = new Message();
                msg.arg1 = EVENT_CONNECT;
                mHandler.sendMessage(msg);
                TeamWorkerSocket.this.messageHandler.onSessionConnect();
            }
        }
    };
    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("断开连接");
            if (TeamWorkerSocket.this.messageHandler != null) {
                Message msg = new Message();
                msg.arg1 = EVENT_DISCONNECT;
                mHandler.sendMessage(msg);
                TeamWorkerSocket.this.messageHandler.onSessionClosed();
            }
        }
    };
    private Emitter.Listener onConnectTimeOut = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("连网超时");
            if (TeamWorkerSocket.this.messageHandler != null) {
                TeamWorkerSocket.this.messageHandler.onSessionTimeout();
            }
        }
    };
    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("连网失败");
            int count = reconnectErrorCount.incrementAndGet();
            if (TeamWorkerSocket.this.messageHandler != null) {
                TeamWorkerSocket.this.messageHandler.onException(null, 2, 0, "连网失败");
            }

            //五次失败重连
            if (count % 5 == 0) {
                reconnect();
            }
        }
    };

    public TeamWorkerSocket(String name) {
        this.name = name;
    }

    void setConnectionCallback(ConnectionCallback callback) {
        if (this.connectionCallback == null) {
            this.connectionCallback = callback;
        }
    }

    public synchronized boolean start(MessageHandler messageHandler, String serverIP, int serverPort) {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        return this._start(messageHandler, serverIP, serverPort);
    }

    private boolean _start(MessageHandler messageHandler, String ip, int port) {
        tiClose = true;
        if (this.isRuning()) {
            return false;
        } else {
            this.messageHandler = messageHandler;
            try {
                String userId = PreferenceUtil.getInstances(MyApplication.getInstance()).getPreferenceString("userId");
                String token = PreferenceUtil.getInstances(MyApplication.getInstance()).getPreferenceString("token");
                String uri = ip + ":" + port + "?socketAuthorizationU=" + userId
                        + "&socketAuthorizationT=" + token + "&socketAuthorizationI="
                        + token;
                Log.d("URI " + uri);
                this.mSocket = IO.socket(uri);
            } catch (URISyntaxException e) {
                e.printStackTrace();
                if (this.messageHandler != null) {
                    this.messageHandler.onException(e, 2, 0, "设置长连接失败");
                }
                this.close();
                return false;
            }
        }

        try {
            Log.d("连接服务器，IP： " + ip + "，连接端口：" + port);

            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectTimeOut);

            mSocket.on(TAG_RECEIVER_MESSAGE, onReceiverMessage);
            this.mSocket.connect();
        } catch (Exception e1) {
            if (e1 instanceof ConnectException) {
                e1.printStackTrace();
                if (this.messageHandler != null) {
                    this.messageHandler.onException(e1, 2, 0, "连网超时");
                }

                this.close();
                return false;
            }

            if (e1 instanceof IOException) {
                e1.printStackTrace();
                if (this.messageHandler != null) {
                    this.messageHandler.onException(e1, 2, 0, "连网失败");
                }

                this.close();
                return false;
            }

            e1.printStackTrace();
            if (this.messageHandler != null) {
                this.messageHandler.onException(e1, 2, 0, "连网失败");
            }

            this.close();
            return false;
        } catch (Throwable e2) {
            e2.printStackTrace();
            if (this.messageHandler != null) {
                this.messageHandler.onException(null, 2, 0, "连网失败");
            }

            this.close();
            return false;
        }

        return true;
    }

    public void send(Object... obj) {
        Log.d("发送数据 长度: " + obj.length + " msgId: " + obj[2] + " data: " + obj[3]);
        this.mSocket.emit(TAG_SEND_MESSAGE, obj);
    }

    /**
     * 重连
     */
    private void reconnect() {
        Log.d("Socket链接 reconnect");
        this.close();
        this._start(messageHandler, serverIP, serverPort);
    }

    public boolean isRuning() {
        return this.mSocket != null && this.mSocket.connected();
    }

    public synchronized void close() {
        if (!this.tiClose) {
            Log.d("当前Socket链接已关闭！");
        } else {
            Log.d("关闭服务器.run");
            this.tiClose = false;

            if (this.mSocket != null) {
                try {
                    this.mSocket.disconnect();

                    this.mSocket.off(Socket.EVENT_CONNECT, onConnect);
                    this.mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
                    this.mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
                    this.mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);

                    Log.d("关闭服务器.running");
                } catch (Exception var2) {
                    var2.printStackTrace();
                    Log.d("关闭服务器.socket.close().error." + var2.getMessage());
                }
            }

            this.mSocket.off();
            this.mSocket = null;
            Log.d("关闭服务器.finish");
            if (this.messageHandler != null) {
                this.messageHandler.onSessionClosed();
            }

        }
    }

    @Override
    protected void finalize() throws Throwable {
        Log.d("系统正在清理Socket对象的资源");
        super.finalize();
    }

    /**
     * 连接服务器回调给client处理未完成请求
     */
    interface ConnectionCallback {
        void onConnect();

        void onDisconnect();
    }
}
