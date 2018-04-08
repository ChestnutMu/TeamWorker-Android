package cn.chestnut.mvvm.teamworker.main.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import cn.chestnut.mvvm.teamworker.utils.Log;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/3/29 20:04:43
 * Description：ListView的Adapter基类
 * Email: xiaoting233zhang@126.com
 */

public class BaseListViewAdapter<T> extends BaseAdapter {

    private int itemLayoutId;
    private int itemId;

    private List<T> objects;

    private ViewDataBinding binding;

    public BaseListViewAdapter(int itemLayoutId, int itemId, List<T> objects) {
        this.itemLayoutId = itemLayoutId;
        this.itemId = itemId;
        this.objects = objects;
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), itemLayoutId, parent, false);
        } else {
            binding = DataBindingUtil.getBinding(convertView);
        }

        return binding.getRoot();
    }
}
