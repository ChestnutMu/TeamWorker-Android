package cn.chestnut.mvvm.teamworker.model;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/5 18:35:48
 * Description：权限
 * Email: xiaoting233zhang@126.com
 */

public class Permission {

    private String permissionId;

    private String permissionName;

    private String permissionUri;

    public String getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(String permissionId) {
        this.permissionId = permissionId;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    public String getPermissionUri() {
        return permissionUri;
    }

    public void setPermissionUri(String permissionUri) {
        this.permissionUri = permissionUri;
    }
}
