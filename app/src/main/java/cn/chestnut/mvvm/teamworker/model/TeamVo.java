package cn.chestnut.mvvm.teamworker.model;

import java.util.List;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/7 21:36:32
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class TeamVo {

    private Team team;

    private List<String> userIds;

    public TeamVo(Team team, List<String> userIds) {
        this.team = team;
        this.userIds = userIds;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }
}