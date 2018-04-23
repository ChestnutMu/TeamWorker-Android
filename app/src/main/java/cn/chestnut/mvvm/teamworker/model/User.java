package cn.chestnut.mvvm.teamworker.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.io.Serializable;

import cn.chestnut.mvvm.teamworker.BR;
import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.main.adapter.BindingItem;
import cn.chestnut.mvvm.teamworker.module.team.BuildTeamAdapter;
import cn.chestnut.mvvm.teamworker.utils.StringUtil;

import org.greenrobot.greendao.annotation.Generated;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/1/2 22:28:36
 * Description：用户Bean
 * Email: xiaoting233zhang@126.com
 */
@Entity
public class User extends BindingItem implements Serializable {

    private static final long serialVersionUID = 9003416166273698372L;
    @Id(autoincrement = true)
    private Long id;

    @Unique
    private String userId;

    private String token;

    private String nickname;

    private String avatar;

    private String telephone;

    private String sex;

    private String birthday;

    private String region;

    private boolean friend = false;

    @Generated(hash = 1076299761)
    public User(Long id, String userId, String token, String nickname,
                String avatar, String telephone, String sex, String birthday,
                String region, boolean friend) {
        this.id = id;
        this.userId = userId;
        this.token = token;
        this.nickname = nickname;
        this.avatar = avatar;
        this.telephone = telephone;
        this.sex = sex;
        this.birthday = birthday;
        this.region = region;
        this.friend = friend;
    }

    @Generated(hash = 586692638)
    public User() {
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

    public String getAbbreviation() {
        return getAbbreviation(nickname);
    }

    public String getPinyin() {
        return StringUtil.toPinyin(nickname);
    }

    public String getWordHeader() {
        return getPinyin().substring(0, 1).toUpperCase();
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isFriend() {
        return friend;
    }

    public void setFriend(boolean friend) {
        this.friend = friend;
    }

    private String getAbbreviation(String name) {
        if (name.length() == 1) {
            return name;
        } else if (name.length() > 1) {
            return name.substring(name.length() - 2, name.length());
        }
        return "";
    }

    @Override
    public int getViewType() {
        return R.layout.item_user;
    }

    @Override
    public int getViewVariableId() {
        return BR.user;
    }

    public int getOtherViewType(int viewType) {
        if (viewType == BuildTeamAdapter.BUILD_TEAM_VIEW_TYPE) {
            return R.layout.item_build_team;
        }
        return R.layout.item_user;
    }

    public boolean getFriend() {
        return this.friend;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof User) {
            if (userId.equals(((User) obj).getUserId()))
                return true;
        }
        return super.equals(obj);
    }
}
