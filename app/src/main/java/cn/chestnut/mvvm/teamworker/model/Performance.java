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
 * Description：绩效Bean
 * Email: xiaoting233zhang@126.com
 */

public class Performance extends BindingItem implements Serializable {

    private String performanceId;

    /*用户id*/
    private String userId;

    /*用户昵称*/
    private String userNickname;

    /*用户头像*/
    private String userAvatar;

    /*团队id*/
    private String teamId;

    /*上月工作任务*/
    private String lastWorkTask;

    /*实际完成任务*/
    private String finishedWork;

    /*任务达成率*/
    private String reachRate;

    /*图片*/
    private String photo;

    /*上月工作自评*/
    private String selfEvaluation;

    /*本月工作任务*/
    private String thisWorkTask;

    /*本月工作计划*/
    private String workPlan;

    /*提交时间*/
    private Long commitTime;

    public String getPerformanceId() {
        return performanceId;
    }

    public void setPerformanceId(String performanceId) {
        this.performanceId = performanceId;
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

    public String getLastWorkTask() {
        return lastWorkTask;
    }

    public void setLastWorkTask(String lastWorkTask) {
        this.lastWorkTask = lastWorkTask;
    }

    public String getFinishedWork() {
        return finishedWork;
    }

    public void setFinishedWork(String finishedWork) {
        this.finishedWork = finishedWork;
    }

    public String getReachRate() {
        return reachRate;
    }

    public void setReachRate(String reachRate) {
        this.reachRate = reachRate;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getSelfEvaluation() {
        return selfEvaluation;
    }

    public void setSelfEvaluation(String selfEvaluation) {
        this.selfEvaluation = selfEvaluation;
    }

    public String getThisWorkTask() {
        return thisWorkTask;
    }

    public void setThisWorkTask(String thisWorkTask) {
        this.thisWorkTask = thisWorkTask;
    }

    public String getWorkPlan() {
        return workPlan;
    }

    public void setWorkPlan(String workPlan) {
        this.workPlan = workPlan;
    }

    public Long getCommitTime() {
        return commitTime;
    }

    public String showCommitTime(){
        return FormatDateUtil.timeParseDetail(commitTime);
    }

    public void setCommitTime(Long commitTime) {
        this.commitTime = commitTime;
    }

    @Override
    public int getViewType() {
        return R.layout.item_performance_list;
    }

    @Override
    public int getViewVariableId() {
        return BR.performance;
    }
}
