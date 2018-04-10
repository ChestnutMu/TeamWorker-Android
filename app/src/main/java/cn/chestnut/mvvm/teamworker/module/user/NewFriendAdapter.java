package cn.chestnut.mvvm.teamworker.module.user;

import android.content.Context;
import android.view.View;

import java.util.List;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ItemNewFriendBinding;
import cn.chestnut.mvvm.teamworker.main.adapter.BaseRecyclerViewAdapter;
import cn.chestnut.mvvm.teamworker.model.NewFriendRequest;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/6 21:36:51
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class NewFriendAdapter extends BaseRecyclerViewAdapter<NewFriendRequest, ItemNewFriendBinding> {

    private Context context;

    private AcceptFriendRequestListener acceptFriendRequestListener;

    public NewFriendAdapter(Context context, List<NewFriendRequest> mItems) {
        super(mItems);
        this.context = context;
    }

    @Override
    public void handleViewHolder(ItemNewFriendBinding binding, NewFriendRequest newFriendRequest, final int position) {
        final NewFriendRequest request = mItems.get(position);
        if (request.isAccepted()) {
            binding.tvAccept.setBackground(context.getResources().getDrawable(R.drawable.bg_tv_is_accepted));
            binding.tvAccept.setTextColor(context.getResources().getColor(R.color.dark_gray));
            binding.tvAccept.setClickable(false);
        } else {
            binding.tvAccept.setBackground(context.getResources().getDrawable(R.drawable.bg_tv_to_accept));
            binding.tvAccept.setTextColor(context.getResources().getColor(R.color.white));
            binding.tvAccept.setClickable(true);
            binding.tvAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    acceptFriendRequestListener.acceptFriendRequest(request, position);
                }
            });
        }
    }

    public interface AcceptFriendRequestListener {
        void acceptFriendRequest(NewFriendRequest request, int position);
    }

    public void setAcceptFriendRequestListener(AcceptFriendRequestListener acceptFriendRequestListener) {
        this.acceptFriendRequestListener = acceptFriendRequestListener;
    }
}
