package cn.chestnut.mvvm.teamworker.widget;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jaiky.imagespickers.ImageLoader;

import cn.chestnut.mvvm.teamworker.R;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/10 20:49:36
 * Description：创建图片加载器
 * Email: xiaoting233zhang@126.com
 */
public class GlideLoader {
    public static void displayImage(Context context, String path, ImageView imageView) {
        Glide.with(context)
                .load(path)
                .placeholder(R.drawable.global_img_default)
                .centerCrop()
                .into(imageView);
    }

}
