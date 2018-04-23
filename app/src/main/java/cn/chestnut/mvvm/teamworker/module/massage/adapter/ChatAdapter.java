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

    private OnUpdateMessageUserLayoutListener onUpdateLocalListener;
    private OnUpdateMessageUserLayoutListener onUpdateServerListener;

//    private Map<String, UserInfo> userMap;

    public ChatAdapter(List<ChatMessage> mItems, String userId) {
        super(mItems);
        this.userId = userId;
//        this.userMap = userMap;
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

//        if (obj.getUser() == null) {
//            //当前缓存获取
//            User user = userMap.get(obj.getSenderId());
//            if (null == user) {
//                //保证同一个user只获取一次信息
//                boolean isUpdate = PreferenceUtil.getInstances(MyApplication.getInstance()).getPreferenceBoolean(Constant.PreferenceKey.USER_INFO_WAITING + obj.getSenderId());
//                if (!isUpdate)
//                    //本地数据库获取并更新
//                    if (onUpdateLocalListener != null) {
//                        PreferenceUtil.getInstances(MyApplication.getInstance()).savePreferenceBoolean(Constant.PreferenceKey.USER_INFO_WAITING + obj.getSenderId(), true);
//                        onUpdateLocalListener.onUpdate(obj, position, new OnUpdateLocalDataListener() {
//                            @Override
//                            public void onSuccess(User user) {
//                                userMap.put(user.getUserId(), user);
//                                PreferenceUtil.getInstances(MyApplication.getInstance()).deleteKey(Constant.PreferenceKey.USER_INFO_WAITING + user.getUserId());
//                                notifyDataSetChanged();
//                            }
//
//                            @Override
//                            public void onFailure() {
//                                //服务器数据库获取并更新
//                                onUpdateServerListener.onUpdate(obj, position, new OnUpdateLocalDataListener() {
//                                    @Override
//                                    public void onSuccess(User user) {
//                                        userMap.put(user.getUserId(), user);
//                                        PreferenceUtil.getInstances(MyApplication.getInstance()).deleteKey(Constant.PreferenceKey.USER_INFO_WAITING + user.getUserId());
//                                        notifyDataSetChanged();
//                                    }
//
//                                    @Override
//                                    public void onFailure() {
//                                        PreferenceUtil.getInstances(MyApplication.getInstance()).deleteKey(Constant.PreferenceKey.USER_INFO_WAITING + obj.getSenderId());
//                                        Log.d("服务器获取用户信息失败");
//                                    }
//                                });
//                            }
//                        });
//                    }
//            } else {
//                //缓存有数据
//                obj.setUser(user);
//            }
//            //本地获取
////            User messageUser = MessageDaoUtils.getDaoSession().queryMessageUserByUserId(chatUserId);
////            //本地SQLite没有记录，则从服务器拿并插入本地
////            if (messageUser == null) {
////                updateMessageUser(this, obj, false);
////            }
////            //本地有记录，再判断是否到更新时间，是则从服务器拿并更新本地
////            else if (updateTime != 0 || updateTime < System.currentTimeMillis()) {
////                obj.setMessageUser(messageUser);
////            } else updateMessageUser(this, obj, true);
//        }

    }


    public void setOnUpdateLocalListener(OnUpdateMessageUserLayoutListener onUpdateLocalListener) {
        this.onUpdateLocalListener = onUpdateLocalListener;
    }

    public void setOnUpdateServerListener(OnUpdateMessageUserLayoutListener onUpdateServerListener) {
        this.onUpdateServerListener = onUpdateServerListener;
    }

    public interface OnUpdateMessageUserLayoutListener {
        void onUpdate(ChatMessage obj, int position, OnUpdateLocalDataListener listener);
    }

    public interface OnUpdateLocalDataListener {
        void onSuccess(User user);

        void onFailure();
    }

    /**
     *  根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */

    public static int dip2px(float dpValue) {

        final float scale = MyApplication.getInstance().getResources().getDisplayMetrics().density;

        return (int) (dpValue * scale + 0.5f);
    }

}
