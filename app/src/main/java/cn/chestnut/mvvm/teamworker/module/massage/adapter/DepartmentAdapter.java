package cn.chestnut.mvvm.teamworker.module.massage.adapter;

import android.databinding.ViewDataBinding;

import java.util.List;

import cn.chestnut.mvvm.teamworker.main.adapter.BaseRecyclerViewAdapter;
import cn.chestnut.mvvm.teamworker.main.adapter.BindingItem;
import cn.chestnut.mvvm.teamworker.module.massage.bean.Department;
import cn.chestnut.mvvm.teamworker.module.massage.bean.Message;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/1/2 22:05:46
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class DepartmentAdapter extends BaseRecyclerViewAdapter {

    public DepartmentAdapter(List<Department> mItems) {
        super(mItems);
    }

}
