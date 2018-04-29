package cn.chestnut.mvvm.teamworker.model;

import org.junit.experimental.theories.FromDataPoints;

import java.io.Serializable;

import cn.chestnut.mvvm.teamworker.BR;
import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.main.adapter.BindingItem;
import cn.chestnut.mvvm.teamworker.utils.EmojiUtil;
import cn.chestnut.mvvm.teamworker.utils.FormatDateUtil;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/1/2 22:03:53
 * Description：部门
 * Email: xiaoting233zhang@126.com
 */

public class Team extends BindingItem implements Serializable {
    private String teamId;

    private String teamName;

    private String teamBadge;

    private String teamIndustry;

    private String teamDesc;

    private String teamRegion;

    private long createTime;

    private long updateTime;

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

    public String getTeamDesc() {
        return teamDesc;
    }

    public void setTeamDesc(String teamDesc) {
        this.teamDesc = teamDesc;
    }

    public String getCreateTime() {
        return FormatDateUtil.timeParseDetail(createTime);
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
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
