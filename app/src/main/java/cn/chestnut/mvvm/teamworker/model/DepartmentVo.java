package cn.chestnut.mvvm.teamworker.model;

import java.util.List;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/7 21:36:32
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class DepartmentVo {

    private Department department;

    private List<String> userIds;

    public DepartmentVo(Department department, List<String> userIds) {
        this.department = department;
        this.userIds = userIds;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }
}