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
 * Description：物品领用Bean
 * Email: xiaoting233zhang@126.com
 */

public class UseGood extends BindingItem implements Serializable {

    private String useGoodId;

    /*用户id*/
    private String userId;

    /*用户昵称*/
    private String userNickname;

    /*用户头像*/
    private String userAvatar;

    /*团队id*/
    private String teamId;

    /*物品用途*/
    private String goodPurpose;

    /*物品名称*/
    private String goodName;

    /*物品数量*/
    private String goodCount;

    /*领用详情*/
    private String useDetails;

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

    public String getUseGoodId() {
        return useGoodId;
    }

    public void setUseGoodId(String useGoodId) {
        this.useGoodId = useGoodId;
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

    public String getGoodPurpose() {
        return goodPurpose;
    }

    public void setGoodPurpose(String goodPurpose) {
        this.goodPurpose = goodPurpose;
    }

    public String getGoodName() {
        return goodName;
    }

    public void setGoodName(String goodName) {
        this.goodName = goodName;
    }

    public String getGoodCount() {
        return goodCount;
    }

    public void setGoodCount(String goodCount) {
        this.goodCount = goodCount;
    }

    public String getUseDetails() {
        return useDetails;
    }

    public void setUseDetails(String useDetails) {
        this.useDetails = useDetails;
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

    public String showCommitTime(){
        return FormatDateUtil.timeParseDetail(commitTime);
    }

    public void setCommitTime(Long commitTime) {
        this.commitTime = commitTime;
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

    public Long getHandleTime() {
        return handleTime;
    }

    public String showHandleTime(){
        return FormatDateUtil.timeParseDetail(handleTime);
    }

    public String getWaitedTime() {
        return FormatDateUtil.getTimeRange(commitTime) + "发起";
    }

    public void setHandleTime(Long handleTime) {
        this.handleTime = handleTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    //-1收回请求 0 已申请，待审批；1 已审批，通过；2 已审批，不通过
    public String showStatus() {
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

    @Override
    public int getViewType() {
        return R.layout.item_use_good_list;
    }

    @Override
    public int getViewVariableId() {
        return BR.useGood;
    }
}
