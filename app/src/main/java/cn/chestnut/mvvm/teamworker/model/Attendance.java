package cn.chestnut.mvvm.teamworker.model;

import cn.chestnut.mvvm.teamworker.utils.FormatDateUtil;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/1/3 14:30:21
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class Attendance {

    private String attendanceId;

    private String userId;

    private String altitude;//经度

    private String latitude;//纬度

    private String punchInAddress;//上班地址

    private String punchOutAddress;//下班地址

    private Long punchInTime;//上班打卡时间

    private Long punchOutTime;//下班打卡时间

    public String getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(String attendanceId) {
        this.attendanceId = attendanceId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
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

    public Long getPunchInTime() {
        return punchInTime;
    }

    public void setPunchInTime(Long punchInTime) {
        this.punchInTime = punchInTime;
    }

    public Long getPunchOutTime() {
        return punchOutTime;
    }

    public void setPunchOutTime(Long punchOutTime) {
        this.punchOutTime = punchOutTime;
    }

    public String showPunchInTime() {
        if (punchInTime != null) {
            return FormatDateUtil.timeParseDetail(punchInTime);
        }
        else return "";
    }

    public String showPunchOutTime() {
        if (punchOutTime != null) {
            return FormatDateUtil.timeParseDetail(punchOutTime);
        }
        else return "";
    }

}
