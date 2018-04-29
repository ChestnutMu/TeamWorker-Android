package cn.chestnut.mvvm.teamworker.module.approval;

import java.util.List;

import cn.chestnut.mvvm.teamworker.main.adapter.BaseRecyclerViewAdapter;
import cn.chestnut.mvvm.teamworker.model.Address;
import cn.chestnut.mvvm.teamworker.model.Attendance;
import cn.chestnut.mvvm.teamworker.model.WorkOff;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/1/2 22:05:46
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class WorkOffListAdapter extends BaseRecyclerViewAdapter {

    public WorkOffListAdapter(List<WorkOff> mItems) {
        super(mItems);
    }

}
