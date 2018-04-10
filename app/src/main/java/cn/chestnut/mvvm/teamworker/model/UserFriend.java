package cn.chestnut.mvvm.teamworker.model;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/9 21:49:35
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class UserFriend {
    private User user;

    private boolean friend;//是否为我的朋友

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isFriend() {
        return friend;
    }

    public void setFriend(boolean friend) {
        this.friend = friend;
    }
}
