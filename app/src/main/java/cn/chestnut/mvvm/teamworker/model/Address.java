package cn.chestnut.mvvm.teamworker.model;

import java.io.Serializable;

import cn.chestnut.mvvm.teamworker.BR;
import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.main.adapter.BindingItem;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/7 22:29:21
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class Address extends BindingItem implements Serializable {

    private String addressId;

    private String prAddressId;

    private String name;

    private Integer level;

    private Integer status;

    /*是否终点 自动判断*/
    private Boolean end;

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getPrAddressId() {
        return prAddressId;
    }

    public void setPrAddressId(String prAddressId) {
        this.prAddressId = prAddressId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Boolean getEnd() {
        return end;
    }

    public void setEnd(Boolean end) {
        this.end = end;
    }


    @Override
    public int getViewType() {
        return R.layout.item_select_region;
    }

    @Override
    public int getViewVariableId() {
        return BR.address;
    }
}
