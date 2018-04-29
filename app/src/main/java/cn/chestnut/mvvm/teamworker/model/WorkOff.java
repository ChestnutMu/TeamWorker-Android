package cn.chestnut.mvvm.teamworker.model;

import java.io.Serializable;
import java.util.Date;

import cn.chestnut.mvvm.teamworker.BR;
import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.main.adapter.BindingItem;
import cn.chestnut.mvvm.teamworker.utils.FormatDateUtil;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/3 09:46:38
 * Description：请假Bean
 * Email: xiaoting233zhang@126.com
 */

public class WorkOff extends BindingItem implements Serializable {

    private String workOffId;

    /*用户id*/
    private String userId;

    /*用户昵称*/
    private String userNickname;

    /*用户头像*/
    private String userAvatar;

    /*团队id*/
    private String teamId;

    /*请假类型*/
    private String workOffType;

    /*内容*/
    private String workOffReason;

    /*图片*/
    private String photo;

    /*开始时间*/
    private Long startTime;

    /*结束时间*/
    private Long endTime;

    /*提交时间*/
    private Long commitTime;

    /*处理id人*/
    private String adminId;

    /*处理人昵称*/
    private String adminNickname;

    /*处理人头像*/
    private String adminAvatar;

    /*处理信息*/
    private String handleReason;

    /*处理时间*/
    private Long handleTime;

    /*状态 WorkOffConstants*/
    /*-1收回请求 0 已申请，待审批；1 已审批，通过；2 已审批，不通过*/
    private Integer status;

    public String getWorkOffId() {
        return workOffId;
    }

    public void setWorkOffId(String workOffId) {
        this.workOffId = workOffId;
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

    public String getWorkOffType() {
        return workOffType;
    }

    public void setWorkOffType(String workOffType) {
        this.workOffType = workOffType;
    }

    public String getWorkOffReason() {
        return workOffReason;
    }

    public void setWorkOffReason(String workOffReason) {
        this.workOffReason = workOffReason;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getStartTime() {
        return FormatDateUtil.timeParseDetail(startTime);
    }

    public String getEndTime() {
        return FormatDateUtil.timeParseDetail(endTime);
    }

    public String getCommitTime() {
        return FormatDateUtil.timeParseDetail(commitTime);
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public void setCommitTime(Long commitTime) {
        this.commitTime = commitTime;
    }

    public void setHandleTime(Long handleTime) {
        this.handleTime = handleTime;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getAdminNickname() {
        return adminNickname;
    }

    public void setAdminNickname(String adminNickname) {
        this.adminNickname = adminNickname;
    }

    public String getAdminAvatar() {
        return adminAvatar;
    }

    public void setAdminAvatar(String adminAvatar) {
        this.adminAvatar = adminAvatar;
    }

    public String getHandleReason() {
        return handleReason;
    }

    public void setHandleReason(String handleReason) {
        this.handleReason = handleReason;
    }

    public String getWaitedTime() {
        return FormatDateUtil.getTimeRange(commitTime) + "发起";
    }

    public String getHandleTime() {
        return FormatDateUtil.timeParseDetail(handleTime);
    }

    //-1收回请求 0 已申请，待审批；1 已审批，通过；2 已审批，不通过
    public String getStatus() {
        if (status == -1) {
            return "已被收回";
        } else if (status == 0) {
            return "已申请，待审批";
        } else if (status == 1) {
            return "已审批，通过";
        } else if (status == 2) {
            return "已审批，不通过";
        } else {
            return "";
        }
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public int getViewType() {
        return R.layout.item_work_off_list;
    }

    @Override
    public int getViewVariableId() {
        return BR.workOff;
    }
}
