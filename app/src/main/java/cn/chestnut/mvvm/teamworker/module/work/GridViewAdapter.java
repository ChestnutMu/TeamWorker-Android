package cn.chestnut.mvvm.teamworker.module.work;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cn.chestnut.mvvm.teamworker.R;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/1/6 21:47:50
 * Description：GridView适配器
 * Email: xiaoting233zhang@126.com
 */

public class GridViewAdapter extends BaseAdapter {
    private ArrayList<String> mNameList = new ArrayList<String>();
    private ArrayList<Integer> mDrawableList = new ArrayList<Integer>();
    private LayoutInflater mInflater;
    private Context mContext;

    public GridViewAdapter(Context context, ArrayList<String> nameList, ArrayList<Integer> drawableList) {
        mNameList = nameList;
        mDrawableList = drawableList;
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return mNameList.size();
    }

    @Override
    public Object getItem(int position) {
        return mNameList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemViewTag viewTag;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_gv_common_apps, null);

            // construct an item tag
            viewTag = new ItemViewTag((ImageView) convertView.findViewById(R.id.grid_icon), (TextView) convertView.findViewById(R.id.grid_name));
            convertView.setTag(viewTag);
        } else {
            viewTag = (ItemViewTag) convertView.getTag();
        }

        // set name
        viewTag.mName.setText(mNameList.get(position));

        // set icon
        viewTag.mIcon.setBackgroundResource(mDrawableList.get(position));
        return convertView;
    }

    class ItemViewTag {
        protected ImageView mIcon;
        protected TextView mName;

        /**
         * The constructor to construct a navigation view tag
         *
         * @param name the name view of the item
         * @param icon the icon view of the item
         */
        public ItemViewTag(ImageView icon, TextView name) {
            this.mName = name;
            this.mIcon = icon;
        }
    }
}
