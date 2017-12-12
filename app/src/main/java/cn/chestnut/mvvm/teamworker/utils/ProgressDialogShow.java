

package cn.chestnut.mvvm.teamworker.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import cn.chestnut.mvvm.teamworker.R;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/10 20:49:36
 * Description：进度条窗口显示
 * Email: xiaoting233zhang@126.com
 */
public class ProgressDialogShow {
    private static Dialog loadingDialog;

    /**
     * 显示加载动画
     *
     * @param context
     * @return
     */
    public static void showProgress(Context context) {
        AnimationDrawable animationDrawable;
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.loading_dialog, null);// 得到加载view
        LinearLayout layout = v.findViewById(R.id.dialog_view);// 加载布局
        if (loadingDialog == null) {
            loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog
        }
        loadingDialog.setCancelable(false);// 不可以用“返回键”取消
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));// 设置布局
        loadingDialog.show();
        ImageView spaceshipImage = v.findViewById(R.id.img);
        spaceshipImage.setImageResource(R.drawable.loading_animation);
        animationDrawable = (AnimationDrawable) spaceshipImage.getDrawable();
        animationDrawable.start();
    }

    /**
     * 关闭 加载动画
     *
     */
    public static void cancleProgressDialog() {
        if (loadingDialog == null) {
            return;
        }
        try {
            loadingDialog.dismiss();
        } catch (Exception e) {

        }
        loadingDialog = null;
    }
}
