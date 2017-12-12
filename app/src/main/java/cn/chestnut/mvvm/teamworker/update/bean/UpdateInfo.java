package cn.chestnut.mvvm.teamworker.update.bean;


import java.io.Serializable;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/10 20:49:36
 * Description：apk更新info
 * Email: xiaoting233zhang@126.com
 */


public class UpdateInfo implements Serializable {
    private String version;//版本号
    private String description;//更新描述
    private String upDateUlr;//下载apk地址

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUpDateUlr() {
        return upDateUlr;
    }

    public void setUpDateUlr(String upDateUlr) {
        this.upDateUlr = upDateUlr;
    }
}
