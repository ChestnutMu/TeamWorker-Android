package cn.chestnut.mvvm.teamworker.module.approval;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cn.chestnut.mvvm.teamworker.databinding.ItemWorkOffDetailBinding;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.main.adapter.BaseListViewAdapter;
import cn.chestnut.mvvm.teamworker.utils.GlideLoader;
import cn.chestnut.mvvm.teamworker.utils.StringUtil;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/29 15:11:43
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class WorkOffDetailAdapter extends BaseListViewAdapter<WorkOffDetailActivity.WorkOffDetail> {
    private ItemWorkOffDetailBinding binding;

    private List<WorkOffDetailActivity.WorkOffDetail> workOffList;

    private Context context;

    private int itemLayoutId;


    public WorkOffDetailAdapter(Context context, int itemLayoutId, int itemId, List<WorkOffDetailActivity.WorkOffDetail> objects) {
        super(itemLayoutId, itemId, objects);
        this.context = context;
        this.itemLayoutId = itemLayoutId;
        this.workOffList = objects;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), itemLayoutId, parent, false);
        } else {
            binding = DataBindingUtil.getBinding(convertView);
        }

        if (position == 0) {
            binding.tvTopLine.setVisibility(View.INVISIBLE);
        } else {
            binding.tvTopLine.setVisibility(View.VISIBLE);
        }

        WorkOffDetailActivity.WorkOffDetail workOffDetail = workOffList.get(position);
        binding.tvTime.setText(workOffDetail.getTime());
        GlideLoader.displayImage(context, HttpUrls.GET_PHOTO + workOffDetail.getAvatar(), binding.ivAvatar);
        binding.tvNickname.setText(workOffDetail.getNickname());
        binding.tvAction.setText(workOffDetail.getAction());
        if (StringUtil.isStringNotNull(workOffDetail.getHandleReason())) {
            binding.llHandleReason.setVisibility(View.VISIBLE);
            binding.tvHandleReason.setText(workOffDetail.getHandleReason());
        } else {
            binding.llHandleReason.setVisibility(View.GONE);
        }
        return binding.getRoot();
    }

}
