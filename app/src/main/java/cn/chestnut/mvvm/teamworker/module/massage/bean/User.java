package cn.chestnut.mvvm.teamworker.module.massage.bean;

import java.io.Serializable;

import cn.chestnut.mvvm.teamworker.BR;
import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.main.adapter.BindingItem;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/1/2 22:28:36
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class User extends BindingItem implements Serializable{
    private String userId;

    private String account;

    private String password;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public int getViewType() {
        return R.layout.item_user;
    }

    @Override
    public int getViewVariableId() {
        return BR.user;
    }
}
