package cn.chestnut.mvvm.teamworker.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.io.Serializable;

import org.greenrobot.greendao.annotation.Generated;

import cn.chestnut.mvvm.teamworker.BR;
import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.main.adapter.BindingItem;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/12 9:52:36
 * Description：用户信息Bean
 * Email: xiaoting233zhang@126.com
 */
@Entity
public class UserInfo extends BindingItem implements Serializable {

    private static final long serialVersionUID = -5381748401555421680L;
    @Id(autoincrement = true)
    private Long id;

    @Unique
    private String userId;

    private String nickname;

    private String avatar;

    @Generated(hash = 1371941083)
    public UserInfo(Long id, String userId, String nickname, String avatar) {
        this.id = id;
        this.userId = userId;
        this.nickname = nickname;
        this.avatar = avatar;
    }

    @Generated(hash = 1279772520)
    public UserInfo() {
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getOtherViewType() {
        return R.layout.item_chosen_delete_user;
    }

    @Override
    public int getViewType() {
        return R.layout.item_chat_user;
    }

    @Override
    public int getViewVariableId() {
        return BR.userInfo;
    }
}
