package cn.chestnut.mvvm.teamworker.model;

import java.io.Serializable;

import cn.chestnut.mvvm.teamworker.BR;
import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.main.adapter.BindingItem;
import cn.chestnut.mvvm.teamworker.utils.FormatDateUtil;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/3 09:46:38
 * Description：日报Bean
 * Email: xiaoting233zhang@126.com
 */

public class DayReport extends BindingItem implements Serializable {

    private String dayReportId;

    /*用户id*/
    private String userId;

    /*用户昵称*/
    private String userNickname;

    /*用户头像*/
    private String userAvatar;

    /*团队id*/
    private String teamId;

    /*已完成工作*/
    private String finishedWork;

    /*未完成工作*/
    private String unfinishedWork;

    /*需协调工作*/
    private String needCoordinatedWork;

    /*备注*/
    private String remarks;

    /*图片*/
    private String photo;

    /*提交时间*/
    private Long commitTime;

    public String getDayReportId() {
        return dayReportId;
    }

    public void setDayReportId(String dayReportId) {
        this.dayReportId = dayReportId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getFinishedWork() {
        return finishedWork;
    }

    public void setFinishedWork(String finishedWork) {
        this.finishedWork = finishedWork;
    }

    public String getUnfinishedWork() {
        return unfinishedWork;
    }

    public void setUnfinishedWork(String unfinishedWork) {
        this.unfinishedWork = unfinishedWork;
    }

    public String getNeedCoordinatedWork() {
        return needCoordinatedWork;
    }

    public void setNeedCoordinatedWork(String needCoordinatedWork) {
        this.needCoordinatedWork = needCoordinatedWork;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Long getCommitTime() {
        return commitTime;
    }

    public String showCommitTime() {
        return FormatDateUtil.timeParseDetail(commitTime);
    }

    public void setCommitTime(Long commitTime) {
        this.commitTime = commitTime;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public int getViewType() {
        return R.layout.item_day_report_list;
    }

    @Override
    public int getViewVariableId() {
        return BR.dayReport;
    }
}
