package cn.chestnut.mvvm.teamworker.model;

import cn.chestnut.mvvm.teamworker.BR;
import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.main.adapter.BindingItem;
import cn.chestnut.mvvm.teamworker.utils.FormatDateUtil;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/1/3 14:30:21
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class Attendance extends BindingItem {

    private String attendanceId;

    private String teamId;

    private String userId;

    private String punchInPicture;//上班照片

    private String punchOutPicture;//下班照片

    private String punchInAddress;//上班地址

    private String punchOutAddress;//下班地址

    private long punchInTime;//上班打卡时间

    private long punchOutTime;//下班打卡时间

    public String getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(String attendanceId) {
        this.attendanceId = attendanceId;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPunchInPicture() {
        return punchInPicture;
    }

    public void setPunchInPicture(String punchInPicture) {
        this.punchInPicture = punchInPicture;
    }

    public String getPunchOutPicture() {
        return punchOutPicture;
    }

    public void setPunchOutPicture(String punchOutPicture) {
        this.punchOutPicture = punchOutPicture;
    }

    public String getPunchInAddress() {
        return punchInAddress;
    }

    public void setPunchInAddress(String punchInAddress) {
        this.punchInAddress = punchInAddress;
    }

    public String getPunchOutAddress() {
        return punchOutAddress;
    }

    public void setPunchOutAddress(String punchOutAddress) {
        this.punchOutAddress = punchOutAddress;
    }

    public long getPunchInTime() {
        return punchInTime;
    }

    public void setPunchInTime(long punchInTime) {
        this.punchInTime = punchInTime;
    }

    public long getPunchOutTime() {
        return punchOutTime;
    }

    public void setPunchOutTime(long punchOutTime) {
        this.punchOutTime = punchOutTime;
    }

    public String showPunchInTime() {
        return FormatDateUtil.timeParseDetail(getPunchInTime());
    }

    public String showPunchOutTime() {
        return FormatDateUtil.timeParseDetail(getPunchOutTime());
    }

    @Override
    public int getViewType() {
        return R.layout.item_my_attendance;
    }

    @Override
    public int getViewVariableId() {
        return BR.attendance;
    }
}
