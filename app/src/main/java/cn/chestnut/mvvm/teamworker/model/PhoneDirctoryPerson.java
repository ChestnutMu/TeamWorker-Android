package cn.chestnut.mvvm.teamworker.model;

import cn.chestnut.mvvm.teamworker.utils.StringUtil;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/5 10:39:49
 * Description：通讯录联系人
 * Email: xiaoting233zhang@126.com
 */

public class PhoneDirctoryPerson {

    //姓名
    private String name;

    //拼音
    private String pinyin;

    //拼音首字母
    private String wordHeader;

    //电话号码
    private String phone;

    public PhoneDirctoryPerson(String name, String phone) {
        this.name = name;
        this.phone = phone;
        this.pinyin = StringUtil.toPinyin(name);
        wordHeader = pinyin.substring(0, 1);
    }

    public String getPinyin() {
        return pinyin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWordHeader() {
        return wordHeader.toUpperCase();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}
