package cn.chestnut.mvvm.teamworker.utils;

import com.socks.library.KLog;

import cn.chestnut.mvvm.teamworker.BuildConfig;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/10 20:49:57
 * Description：Log工具类
 * Email: xiaoting233zhang@126.com
 */

public class Log {

    private static final String top = "\n╔═══════════════════════════════════════════════════════════════════════════════════════\n";
    private static final String bottom = "\n╚═══════════════════════════════════════════════════════════════════════════════════════";

    private Log() {

    }

    public static boolean isDebuggable() {
        return BuildConfig.DEBUG;
    }

    public static void v(String message) {
        if (!isDebuggable()) {
            return;
        }
        KLog.v(top + message + bottom);
    }

    public static void d(String message) {
        if (!isDebuggable()) {
            return;
        }
        KLog.d(top + message + bottom);
    }

    public static void i(String message) {
        if (!isDebuggable()) {
            return;
        }
        KLog.i(top + message + bottom);
    }

    public static void w(String message) {
        if (!isDebuggable()) {
            return;
        }
        KLog.w(top + message + bottom);
    }

    public static void e(String message) {
        if (!isDebuggable()) {
            return;
        }
        KLog.e(top + message + bottom);
    }
}
