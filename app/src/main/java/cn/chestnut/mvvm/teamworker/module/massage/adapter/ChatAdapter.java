package cn.chestnut.mvvm.teamworker.module.massage.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.chestnut.mvvm.teamworker.Constant;
import cn.chestnut.mvvm.teamworker.databinding.ItemChatBinding;
import cn.chestnut.mvvm.teamworker.main.adapter.BaseRecyclerViewAdapter;
import cn.chestnut.mvvm.teamworker.main.common.MyApplication;
import cn.chestnut.mvvm.teamworker.model.ChatMessage;
import cn.chestnut.mvvm.teamworker.model.MessageVo;
import cn.chestnut.mvvm.teamworker.model.User;
import cn.chestnut.mvvm.teamworker.model.UserInfo;
import cn.chestnut.mvvm.teamworker.module.massage.MessageDaoUtils;
import cn.chestnut.mvvm.teamworker.utils.Log;
import cn.chestnut.mvvm.teamworker.utils.PreferenceUtil;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/1/16 22:03:58
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class ChatAdapter extends BaseRecyclerViewAdapter<ChatMessage, ItemChatBinding> {

    private String userId;


    public ChatAdapter(List<ChatMessage> mItems, String userId) {
        super(mItems);
        this.userId = userId;
    }

    @Override
    protected void handleViewHolder(ItemChatBinding binding, final ChatMessage obj, final int position) {
        if (obj.getSenderId().equals(userId)) {
            binding.llRight.setVisibility(View.VISIBLE);
            binding.llLeft.setVisibility(View.GONE);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
            layoutParams.setMargins(dip2px(48), 0, 0, 0);
            binding.tvRightContent.setLayoutParams(layoutParams);
        } else {
            binding.llLeft.setVisibility(View.VISIBLE);
            binding.llRight.setVisibility(View.GONE);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
            layoutParams.setMargins(0, 0, dip2px(48), 0);
            binding.tvLeftContent.setLayoutParams(layoutParams);
        }

    }

    /**
     *  根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */

    public static int dip2px(float dpValue) {

        final float scale = MyApplication.getInstance().getResources().getDisplayMetrics().density;

        return (int) (dpValue * scale + 0.5f);
    }

}
