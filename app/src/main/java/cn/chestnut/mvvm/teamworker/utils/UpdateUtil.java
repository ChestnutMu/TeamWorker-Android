package cn.chestnut.mvvm.teamworker.utils;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;

import java.io.File;

import cn.chestnut.mvvm.teamworker.Constant;
import cn.chestnut.mvvm.teamworker.module.update.bean.UpdateInfo;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/10 20:49:36
 * Description：apk更新方法
 * Email: xiaoting233zhang@126.com
 */


public class UpdateUtil {
    /**
     * 更新检测弹窗
     *
     * @param updateInfo
     * @param context
     */
    public static void showUpdateDialog(final UpdateInfo updateInfo, final Activity context) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setIcon(android.R.drawable.ic_dialog_info);
                builder.setTitle("检测到新版本：" + updateInfo.getVersion());
                builder.setMessage(updateInfo.getDescription());
                builder.setCancelable(false);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Environment.getExternalStorageState().equals(
                                Environment.MEDIA_MOUNTED)) {
                            downFile(updateInfo.getUpDateUlr(), context);
                        } else {
                            CommonUtil.showToast("sd卡不可用！", context);
                        }
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
    }

    /**
     * 下载apk操作
     */

    public static void downFile(final String url, final Activity context) {
        final ProgressDialog progressDialog = new ProgressDialog(context);//进度条，在下载的时候实时更新进度，提高用户友好度
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("正在下载");
        progressDialog.setMessage("请稍候...");
        progressDialog.setCancelable(true);
        progressDialog.setProgress(0);
        progressDialog.show();
//        RQ.downLoadAPK(context,url, progressDialog, new RQCallBack() {
//            @Override
//            public void success(JSONObject json) {
//                context.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        downSuccess(context, progressDialog);
//                    }
//                });
//            }
//
//            @Override
//            public void fail(JSONObject json) {
//
//                context.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        downFial(context, progressDialog);
//                    }
//                });
//            }
//        });
    }

    /**
     * 下载成功
     */

    public static void downSuccess(final Context context, final ProgressDialog progressDialog) {

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setTitle("下载完成");
        builder.setMessage("是否安装");
        builder.setCancelable(false);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_VIEW);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //android N的权限问题
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Uri contentUri = FileProvider.getUriForFile(context, "work.model.com.framework.fileprovider", new File(Environment.getExternalStorageDirectory(), Constant.PreferenceConstants.APK_NAME));//注意修改
                    intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                } else {
                    intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), Constant.PreferenceConstants.APK_NAME)), "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                context.startActivity(intent);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     * 下载失败
     */
    public static void downFial(final Context context, final ProgressDialog progressDialog) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setTitle("下载失败！");
        builder.setCancelable(false);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
