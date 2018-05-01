package cn.chestnut.mvvm.teamworker.module.approval;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cn.chestnut.mvvm.teamworker.databinding.ItemReimbursementDetailBinding;
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

public class ReimbursementDetailAdapter extends BaseListViewAdapter<ReimbursementDetailActivity.ReimbursementDetail> {
    private ItemReimbursementDetailBinding binding;

    private List<ReimbursementDetailActivity.ReimbursementDetail> reimbursementDetailList;

    private Context context;

    private int itemLayoutId;


    public ReimbursementDetailAdapter(Context context, int itemLayoutId, int itemId, List<ReimbursementDetailActivity.ReimbursementDetail> objects) {
        super(itemLayoutId, itemId, objects);
        this.context = context;
        this.itemLayoutId = itemLayoutId;
        this.reimbursementDetailList = objects;
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

        ReimbursementDetailActivity.ReimbursementDetail reimbursementDetail = reimbursementDetailList.get(position);
        binding.tvTime.setText(reimbursementDetail.getTime());
        GlideLoader.displayImage(context, HttpUrls.GET_PHOTO + reimbursementDetail.getAvatar(), binding.ivAvatar);
        binding.tvNickname.setText(reimbursementDetail.getNickname());
        binding.tvAction.setText(reimbursementDetail.getAction());
        if (StringUtil.isStringNotNull(reimbursementDetail.getHandleReason())) {
            binding.llHandleReason.setVisibility(View.VISIBLE);
            binding.tvHandleReason.setText(reimbursementDetail.getHandleReason());
        } else {
            binding.llHandleReason.setVisibility(View.GONE);
        }
        return binding.getRoot();
    }

}
