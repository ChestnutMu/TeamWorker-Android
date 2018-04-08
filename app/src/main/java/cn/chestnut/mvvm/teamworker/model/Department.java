package cn.chestnut.mvvm.teamworker.model;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/8 17:29:14
 * Description：部门
 * Email: xiaoting233zhang@126.com
 */

public class Department {
    private String departmentId;

    private String departmentName;

    private String teamId; //所属团队/公司/机构的Id

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }
}
