package cn.chestnut.mvvm.teamworker.module.massage.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    private Gson gson;

    public ChatAdapter(List<ChatMessage> mItems, String userId) {
        super(mItems);
        this.userId = userId;
        gson = new Gson();
    }

    @Override
    protected void handleViewHolder(ItemChatBinding binding, final ChatMessage obj, final int position) {
        if (obj.getType() == null)
            if (obj.getSenderId().equals(userId)) {
                binding.llRight.setVisibility(View.VISIBLE);
                binding.llLeft.setVisibility(View.GONE);
                binding.tvShowTip.setVisibility(View.GONE);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
                layoutParams.setMargins(dip2px(48), 0, 0, 0);
                binding.tvRightContent.setLayoutParams(layoutParams);
            } else {
                binding.llLeft.setVisibility(View.VISIBLE);
                binding.llRight.setVisibility(View.GONE);
                binding.tvShowTip.setVisibility(View.GONE);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
                layoutParams.setMargins(0, 0, dip2px(48), 0);
                binding.tvLeftContent.setLayoutParams(layoutParams);
            }
        else {
            String message = "";
            if (obj.getType().equals(Constant.ChatMessageType.TYPE_MESSAGE_CHANGE_NAME)) {
                if (obj.getSenderId().equals(userId)) {
                    message = "你修改聊天室名称为“" + obj.getMessage() + "”";
                } else {
                    message = obj.getNickname() + "修改聊天室名称为“" + obj.getMessage() + "”";
                }
            } else if (obj.getType().equals(Constant.ChatMessageType.TYPE_MESSAGE_CHANGE_PIC)) {
                if (obj.getSenderId().equals(userId)) {
                    message = "你修改了聊天室头像";
                } else {
                    message = obj.getNickname() + "修改了聊天室头像";
                }
            } else if (obj.getType().equals(Constant.ChatMessageType.TYPE_MESSAGE_CHANGE_PEOPLE_ADD)) {
                List<UserInfo> userInfoList = gson.fromJson(obj.getMessage(), new TypeToken<ArrayList<UserInfo>>() {
                }.getType());
                String temp = "";
                for (int i = 0; i < userInfoList.size() - 1; i++) {
                    temp = temp + userInfoList.get(i).getNickname() + "、";
                }
                temp = temp + userInfoList.get(userInfoList.size() - 1).getNickname();
                if (obj.getSenderId().equals(userId)) {
                    message = "你邀请" + temp + "加入了聊天室";
                } else {
                    message = obj.getNickname() + "邀请" + temp + "加入了聊天室";
                }
            } else if (obj.getType().equals(Constant.ChatMessageType.TYPE_MESSAGE_CHANGE_PEOPLE_REMOVE)) {
                List<UserInfo> userInfoList = gson.fromJson(obj.getMessage(), new TypeToken<ArrayList<UserInfo>>() {
                }.getType());
                String temp = "";
                for (int i = 0; i < userInfoList.size() - 1; i++) {
                    temp = temp + userInfoList.get(i).getNickname() + "、";
                }
                temp = temp + userInfoList.get(userInfoList.size() - 1).getNickname();
                if (obj.getSenderId().equals(userId)) {
                    message = "你将" + temp + "移出了聊天室";
                } else {
                    message = obj.getNickname() + "将" + temp + "移出了聊天室";
                }
            }

            binding.tvShowTip.setText(message);
            binding.tvShowTip.setVisibility(View.VISIBLE);
            binding.llRight.setVisibility(View.GONE);
            binding.llLeft.setVisibility(View.GONE);
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
