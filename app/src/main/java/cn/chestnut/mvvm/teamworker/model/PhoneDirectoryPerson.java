package cn.chestnut.mvvm.teamworker.model;

import java.io.Serializable;

import cn.chestnut.mvvm.teamworker.BR;
import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.main.adapter.BindingItem;
import cn.chestnut.mvvm.teamworker.utils.StringUtil;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/5 10:39:49
 * Description：通讯录联系人
 * Email: xiaoting233zhang@126.com
 */

public class PhoneDirectoryPerson extends BindingItem implements Serializable {

    //姓名
    private String name;

    //名字缩写
    private String abbreviation;

    //拼音
    private String pinyin;

    //拼音首字母
    private String wordHeader;

    //电话号码
    private String phone;

    public PhoneDirectoryPerson(String name, String phone) {
        this.name = name;
        this.phone = phone;
        this.pinyin = StringUtil.toPinyin(name);
        this.abbreviation = getAbbreviation(name);
        this.wordHeader = pinyin.substring(0, 1);
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

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public void setWordHeader(String wordHeader) {
        this.wordHeader = wordHeader;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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
        return R.layout.item_build_team;
    }

    @Override
    public int getViewVariableId() {
        return BR.teamPerson;
    }

}
