package cn.chestnut.mvvm.teamworker.main.adapter;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/10 20:49:36
 * Description：简单通用RecyclerView数据绑定的adapter的ViewHolder
 * Email: xiaoting233zhang@126.com
 */
public class SimpleViewHolder<TD extends ViewDataBinding> extends RecyclerView.ViewHolder {
    private TD binding;

    public SimpleViewHolder(View itemView) {
        super(itemView);
    }

    public TD getBinding() {
        return binding;
    }

    public void setBinding(TD binding) {
        this.binding = binding;
    }
}
