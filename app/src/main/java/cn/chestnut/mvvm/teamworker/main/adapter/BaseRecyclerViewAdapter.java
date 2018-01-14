package cn.chestnut.mvvm.teamworker.main.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.List;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/29 15:38:11
 * Description：RecyclerView适配器
 * Email: xiaoting233zhang@126.com
 */

public class BaseRecyclerViewAdapter<T extends BindingItem, TD extends ViewDataBinding> extends RecyclerView.Adapter<SimpleViewHolder<TD>> {
    protected List<T> mItems;

    protected AdapterView.OnItemClickListener onItemClickListener;

    protected AdapterView.OnItemLongClickListener onItemLongClickListener;

    public BaseRecyclerViewAdapter(List<T> mItems) {
        this.mItems = mItems;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        TD binding = DataBindingUtil.inflate(inflater, viewType, parent, false);
        SimpleViewHolder viewHolder = new SimpleViewHolder(binding.getRoot());
        viewHolder.setBinding(binding);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder<TD> holder, final int position) {
        holder.getBinding().setVariable(mItems.get(position).getViewVariableId(), mItems.get(position));
        holder.getBinding().getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(null, holder.getBinding().getRoot(), position, getItemId(position));
                }
            }
        });
        holder.getBinding().getRoot().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onItemLongClickListener != null) {
                    return onItemLongClickListener.onItemLongClick(null, holder.getBinding().getRoot(), position, getItemId(position));
                }
                return false;
            }
        });
        handleViewHolder(holder, holder.getBinding(), mItems.get(position), position);
        holder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

    public AdapterView.OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public AdapterView.OnItemLongClickListener getOnItemLongClickListener() {
        return onItemLongClickListener;
    }

    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).getViewType();
    }

    /**
     * 处理item界面元素
     *
     * @param binding
     * @param obj
     * @param position
     */
    protected void handleViewHolder(TD binding, T obj, int position) {

    }

    /**
     * 处理item界面元素
     *
     * @param holder
     * @param binding
     * @param obj
     * @param position
     */
    protected void handleViewHolder(SimpleViewHolder<TD> holder, TD binding, T obj, int position) {
        handleViewHolder(holder.getBinding(), mItems.get(position), position);
    }
}
