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
 * Description：报销Bean
 * Email: xiaoting233zhang@126.com
 */

public class Reimbursement extends BindingItem implements Serializable {

    private String reimbursementId;

    /*用户id*/
    private String userId;

    /*用户昵称*/
    private String userNickname;

    /*用户头像*/
    private String userAvatar;

    /*团队id*/
    private String teamId;

    /*报销金额*/
    private String reimbursementMoney;

    /*报销类型*/
    private String reimbursementType;

    /*报销明细*/
    private String reimbursementDetail;

    /*图片*/
    private String photo;

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

    /*-1收回请求 0 已申请，待审批；1 已审批，通过；2 已审批，不通过*/
    private Integer status;

    public String getReimbursementId() {
        return reimbursementId;
    }

    public void setReimbursementId(String reimbursementId) {
        this.reimbursementId = reimbursementId;
    }

    public String getReimbursementMoney() {
        return reimbursementMoney;
    }

    public void setReimbursementMoney(String reimbursementMoney) {
        this.reimbursementMoney = reimbursementMoney;
    }

    public String getReimbursementType() {
        return reimbursementType;
    }

    public void setReimbursementType(String reimbursementType) {
        this.reimbursementType = reimbursementType;
    }

    public String getReimbursementDetail() {
        return reimbursementDetail;
    }

    public void setReimbursementDetail(String reimbursementDetail) {
        this.reimbursementDetail = reimbursementDetail;
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

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getCommitTime() {
        return FormatDateUtil.timeParseDetail(commitTime);
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
        return R.layout.item_reimbursement_list;
    }

    @Override
    public int getViewVariableId() {
        return BR.decrusement;
    }
}
