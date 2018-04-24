package cn.chestnut.mvvm.teamworker.module.massage.adapter;

import java.util.List;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ItemChatUserBinding;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.main.adapter.BaseRecyclerViewAdapter;
import cn.chestnut.mvvm.teamworker.main.common.MyApplication;
import cn.chestnut.mvvm.teamworker.model.UserInfo;
import cn.chestnut.mvvm.teamworker.utils.GlideLoader;
import cn.chestnut.mvvm.teamworker.utils.Log;
import cn.chestnut.mvvm.teamworker.utils.StringUtil;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/1/16 22:03:58
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class ChatSettingUserAdapter extends BaseRecyclerViewAdapter<UserInfo, ItemChatUserBinding> {

    public ChatSettingUserAdapter(List<UserInfo> mItems) {
        super(mItems);
    }

    @Override
    protected void handleViewHolder(ItemChatUserBinding binding, UserInfo obj, int position) {
        if (obj.getUserId() == null) {
            if (obj.getAvatar().equals("add")) {
                binding.ivAvatar.setImageDrawable(MyApplication.getInstance().getResources().getDrawable(R.mipmap.icon_add_people));
            } else {
                binding.ivAvatar.setImageDrawable(MyApplication.getInstance().getResources().getDrawable(R.mipmap.icon_remove_people));
            }
        } else if (obj.getNickname() == null) {
            UserInfo temp = MyApplication.userInfoMap.get(obj.getUserId());
            if (temp != null) {
                obj.setNickname(temp.getNickname());
                obj.setAvatar(temp.getAvatar());
                GlideLoader.displayImage(MyApplication.getInstance(), HttpUrls.GET_PHOTO + obj.getAvatar(), binding.ivAvatar);
            }
        }
    }
}
