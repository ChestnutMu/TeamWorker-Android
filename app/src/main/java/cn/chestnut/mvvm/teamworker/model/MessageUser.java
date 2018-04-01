package cn.chestnut.mvvm.teamworker.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Unique;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/29 14:43:32
 * Description：消息列表中的用户信息
 * Email: xiaoting233zhang@126.com
 */

@Entity
public class MessageUser {

    @Id(autoincrement = true)
    private Long id;

    @Unique
    private String userId;

    private String nickname;

    @Generated(hash = 1622123696)
    public MessageUser(Long id, String userId, String nickname) {
        this.id = id;
        this.userId = userId;
        this.nickname = nickname;
    }

    @Generated(hash = 1201440511)
    public MessageUser() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
