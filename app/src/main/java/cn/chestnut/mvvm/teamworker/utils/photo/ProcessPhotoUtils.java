
package cn.chestnut.mvvm.teamworker.utils.photo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.Window;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/10 20:49:36
 * Description：图片处理工具类
 * Email: xiaoting233zhang@126.com
 */
public class ProcessPhotoUtils {

    private File myPhotoFile;// 照片文件
    private File myPhotoDir;// 照片文件夹
    private Bitmap photoBm; // 用于存储缩小后的图片
    private Bitmap photoBitmap; // 读取到的原始图片

    private Context mContext;// 调用的activity类

    /**
     * Creates a new instance of ProcessPhotoUtils
     *
     * @param context
     */
    public ProcessPhotoUtils(Context context) {
        this.mContext = context;
        // bitmapUtil = BitmapUtil.create(context);
    }

    /**
     * @desc 点击添加图片按钮时调用，弹出对话框供用户选择照片的获取方式
     */
    public void startPhoto() {
        AlertDialog dialog = new AlertDialog.Builder(mContext).setTitle("插入图片")
                .setItems(new String[]{"相机拍摄", "相册上传"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: {
                                shootPhoto();// 进入相机拍摄
                                break;
                            }
                            case 1: {
                                uploadPhoto();// 进入手机相册
                                break;
                            }
                        }
                        dialog.dismiss();
                    }

                }).create();
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        dialog.show();
    }

    /**
     * @desc 选择相册上传方式时调用，打开相册管理
     */
    private void uploadPhoto() {

        Intent intent = new Intent();
        /* 开启Pictures画面Type设定为image */
        intent.setType("image/*");
        /* 使用Intent.ACTION_GET_CONTENT这个Action */
        intent.setAction(Intent.ACTION_PICK);
		/* 取得相片后返回本画面 */
        ((Activity) mContext).startActivityForResult(intent, 1);
    }

    /**
     * @desc 选择照相方式时调用，打开自带照相机功能
     */
    private void shootPhoto() {
        // 相机拍摄上传
        File path = Environment.getExternalStorageDirectory(); // 获取内存卡路径（部分设备不可用，经测定制机能用）
        myPhotoDir = new File(path.getAbsolutePath() + "/DCIM/Camera"); // 设置新路径文件夹
        if (!myPhotoDir.exists()) // 目录是否存在
        {
            myPhotoDir.mkdirs(); // 创建该目录
        }
        String uuid = UUID.randomUUID().toString();
        String fileName = uuid + ".jpg";
        myPhotoFile = new File(myPhotoDir, fileName);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(myPhotoFile));
        ((Activity) mContext).startActivityForResult(intent, 2);
    }

    /**
     * @param strPath
     * @return
     * @desc 传入路径，返回压缩文件
     */
    public static File compressBitmap(Context context, String strPath) {
        try {

            byte[] data = ProcessPhotoUtils.getimage(strPath);
            String uuid = UUID.randomUUID().toString();
            String fileName = uuid + ".jpg";
            File folder = FilePathUtil.getDiskCacheDir(context, "images");
            if (!folder.exists()) {
                folder.mkdirs();
            }
            File compressFile = new File(folder.getAbsoluteFile(), fileName);

            FileOutputStream fileOutputStream = null;
            fileOutputStream = new FileOutputStream(compressFile);
            fileOutputStream.write(data);
            fileOutputStream.flush();
            fileOutputStream.close();
            return compressFile;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @param image
     * @return
     * @desc 质量压缩方法
     */
    public static byte[] compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length > (500 * 1024)) { // 循环判断如果压缩后图片是否大于500kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        byte[] data = baos.toByteArray();
        return data;
    }

    /**
     * @param srcPath
     * @return
     * @desc 图片按比例大小压缩方法（根据路径获取图片并压缩）
     */
    public static byte[] getimage(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 现在主流手机比较多是800*480分辨率，所以高和宽设置为
        float hh = 800f;// 这里设置高度为800f
        float ww = 480f;// 这里设置宽度为480f
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
    }

    /**
     * @param image
     * @return
     * @desc 图片按比例大小压缩方法（根据Bitmap图片压缩）
     * @Create Date 2013年8月27日 上午9:56:23
     */
    public static byte[] comp(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if (baos.toByteArray().length / 1024 > 1024) {// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;// 这里设置高度为800f
        float ww = 480f;// 这里设置宽度为480f
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
    }
}
