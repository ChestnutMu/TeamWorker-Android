package cn.chestnut.mvvm.teamworker.http;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/1/1 22:41:37
 * Description：接口返回数据
 * Email: xiaoting233zhang@126.com
 */

public class ApiResponse<T> {
    private int status;        // 返回码，0为成功，其他为失败
    private String message;    // 返回信息
    private T data;            // 返回数据

    public boolean isSuccess() {
        return status == HttpResponseCodes.SUCCESS;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
