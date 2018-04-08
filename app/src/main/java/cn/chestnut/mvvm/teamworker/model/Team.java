package cn.chestnut.mvvm.teamworker.model;

import cn.chestnut.mvvm.teamworker.BR;
import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.main.adapter.BindingItem;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/1/2 22:03:53
 * Description：部门
 * Email: xiaoting233zhang@126.com
 */

public class Team extends BindingItem {
    private String teamId;

    private String teamName;

    private String teamBadge;

    private String teamIndustry;

    private String personnelScale;

    private String teamRegion;

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamBadge() {
        return teamBadge;
    }

    public void setTeamBadge(String teamBadge) {
        this.teamBadge = teamBadge;
    }

    public String getTeamIndustry() {
        return teamIndustry;
    }

    public void setTeamIndustry(String teamIndustry) {
        this.teamIndustry = teamIndustry;
    }

    public String getPersonnelScale() {
        return personnelScale;
    }

    public void setPersonnelScale(String personnelScale) {
        this.personnelScale = personnelScale;
    }

    public String getTeamRegion() {
        return teamRegion;
    }

    public void setTeamRegion(String teamRegion) {
        this.teamRegion = teamRegion;
    }

    @Override
    public int getViewType() {
        return R.layout.item_team;
    }

    @Override
    public int getViewVariableId() {
        return BR.team;
    }
}
