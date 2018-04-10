package cn.chestnut.mvvm.teamworker.model;

import java.io.Serializable;

import cn.chestnut.mvvm.teamworker.BR;
import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.main.adapter.BindingItem;
import cn.chestnut.mvvm.teamworker.module.team.BuildTeamAdapter;
import cn.chestnut.mvvm.teamworker.utils.StringUtil;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/1/2 22:28:36
 * Description：用户Bean
 * Email: xiaoting233zhang@126.com
 */

public class User extends BindingItem implements Serializable {
    private String userId;

    private String password;

    private String token;

    private String nickname;

    private String avatar;

    private String telephone;

    private String sex;

    private String birthday;

    private String region;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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
}
