package cn.chestnut.mvvm.teamworker.model;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/5 18:36:26
 * Description：用户权限
 * Email: xiaoting233zhang@126.com
 */

public class UserPermissionRelation {

    private String userId;

    private String departmentId;//user所在的子部门Id

    private int permissionRange;//0 标识整个公司; 1 所在部门及其子部门；2 标识特定部门

    private String permissonId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public int getPermissionRange() {
        return permissionRange;
    }

    public void setPermissionRange(int permissionRange) {
        this.permissionRange = permissionRange;
    }

    public String getPermissonId() {
        return permissonId;
    }

    public void setPermissonId(String permissonId) {
        this.permissonId = permissonId;
    }
}
