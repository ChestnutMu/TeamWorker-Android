package cn.chestnut.mvvm.teamworker.module.checkattendance;

import android.content.Context;
import android.view.View;

import java.util.List;

import cn.chestnut.mvvm.teamworker.databinding.ItemMyAttendanceBinding;
import cn.chestnut.mvvm.teamworker.main.adapter.BaseRecyclerViewAdapter;
import cn.chestnut.mvvm.teamworker.model.Attendance;
import cn.chestnut.mvvm.teamworker.utils.StringUtil;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/19 15:21:00
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class AttendanceAdapter extends BaseRecyclerViewAdapter<Attendance, ItemMyAttendanceBinding> {
    private OnPunchPhotoClickListener onPunchPhotoClickListener;

    public AttendanceAdapter(List<Attendance> mItems) {
        super(mItems);
    }

    @Override
    protected void handleViewHolder(ItemMyAttendanceBinding binding, Attendance obj, final int position) {
        if (mItems.get(position).getPunchOutTime() == 0) {
            binding.llPunchOut.setVisibility(View.GONE);
            binding.tvNotPunchOut.setVisibility(View.VISIBLE);
        } else {
            binding.llPunchOut.setVisibility(View.VISIBLE);
            binding.tvNotPunchOut.setVisibility(View.GONE);
        }
        binding.tvReportCount.setText(position + 1 + "");
        binding.ivPunchInPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPunchPhotoClickListener.onPunchInPhotoClick(position);
            }
        });
        binding.ivPunchOutPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPunchPhotoClickListener.onPunchOutPhotoClick(position);
            }
        });
    }

    interface OnPunchPhotoClickListener {
        void onPunchInPhotoClick(int position);

        void onPunchOutPhotoClick(int position);

    }

    public void setOnPunchPhotoClickListener(OnPunchPhotoClickListener onPunchPhotoClickListener) {
        this.onPunchPhotoClickListener = onPunchPhotoClickListener;
    }
}
