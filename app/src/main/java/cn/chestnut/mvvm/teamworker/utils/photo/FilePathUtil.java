

package cn.chestnut.mvvm.teamworker.utils.photo;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/10 20:49:36
 * Description：获取文件路径
 * Email: xiaoting233zhang@126.com
 */
public class FilePathUtil {
    /**
     * 获取一个可以的缓存文件夹 Get a usable cache directory (external if available,
     * internal otherwise).
     *
     * @param context    The context to use
     * @param uniqueName A unique directory name to append to the cache dir
     * @return The cache dir
     */
    public static File getDiskCacheDir(Context context, String uniqueName) {
        // Check if media is mounted or storage is built-in, if so, try and use
        // external cache dir
        // otherwise use internal cache dir
        final String cachePath = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !isExternalStorageRemovable() ? getExternalCacheDir(context).getPath() : context.getCacheDir()
                .getPath();

        return new File(cachePath + File.separator + uniqueName);
    }

    /**
     * 检查外部存储器是否可用 Check if external storage is built-in or removable.
     *
     * @return True if external storage is removable (like an SD card), false
     * otherwise.
     */
    public static boolean isExternalStorageRemovable() {
        if (BuiltUtils.hasGingerbread()) {
            return Environment.isExternalStorageRemovable();
        }
        return true;
    }

    /**
     * 获取外部磁盘路径 Get the external app cache directory.
     *
     * @param context The context to use
     * @return The external cache dir
     */
    private static File getExternalCacheDir(Context context) {
        // if (BuiltUtils.hasFroyo()) {
        // return context.getExternalCacheDir();
        // }

        // Before Froyo we need to construct the external cache dir ourselves
        final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
    }
}
