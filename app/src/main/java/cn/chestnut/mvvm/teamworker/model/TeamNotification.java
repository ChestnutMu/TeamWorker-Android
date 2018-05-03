package cn.chestnut.mvvm.teamworker.model;

import java.io.Serializable;
import java.util.Date;

import cn.chestnut.mvvm.teamworker.utils.FormatDateUtil;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/5/2 20:32:24
 * Description：团队公告
 * Email: xiaoting233zhang@126.com
 */

public class TeamNotification implements Serializable {
    private String teamNotificationId;

    /*团队id*/
    private String teamId;

    /*发送者昵称*/
    private String senderNickname;

    /*发送者头像*/
    private String senderAvatar;

    /*公告标题*/
    private String title;

    /*公告内容*/
    private String content;

    /*发送时间*/
    private Long time;

    /*图片*/
    private String photo;

    public String getTeamNotificationId() {
        return teamNotificationId;
    }

    public void setTeamNotificationId(String teamNotificationId) {
        this.teamNotificationId = teamNotificationId;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getSenderNickname() {
        return senderNickname;
    }

    public void setSenderNickname(String senderNickname) {
        this.senderNickname = senderNickname;
    }

    public String getSenderAvatar() {
        return senderAvatar;
    }

    public void setSenderAvatar(String senderAvatar) {
        this.senderAvatar = senderAvatar;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String showTime() {
        return FormatDateUtil.timeParseDetail(time);
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
