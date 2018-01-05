package cn.chestnut.mvvm.teamworker.main.adapter;

import android.databinding.BaseObservable;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/10 20:49:36
 * Description：绑定数据对象必须继承以下
 * Email: xiaoting233zhang@126.com
 */

public abstract class BindingItem extends BaseObservable {
    /**
     * 返回布局id
     *
     * @return
     */
    public abstract int getViewType();

    /**
     * 返回对象id
     *
     * @return
     */
    public abstract int getViewVariableId();

}
