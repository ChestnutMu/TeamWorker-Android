package cn.chestnut.mvvm.teamworker.socket;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/26 11:21:09
 * Description：SocketIO请求对象
 * Email: xiaoting233zhang@126.com
 */

public class IORequest {

    private String uid;

    private String token;

    private int msgId;
    private String obj;

    public IORequest(String uid, String token, int msgId, String obj) {
        this.uid = uid;
        this.token = token;
        this.msgId = msgId;
        this.obj = obj;
    }

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public String getObj() {
        return obj;
    }

    public void setObj(String obj) {
        this.obj = obj;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
