package cn.chestnut.mvvm.teamworker.service;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/10 20:49:36
 * Description：初始化常量
 * Email: xiaoting233zhang@126.com
 */


public class Constant {
    //action 广播标志类
    public class ActionConstant {

        public static final String ACTION_GET_UPDATE_INFO_SUCCESS = "action_get_update_info_success";//获取更新信息成功
        public static final String ACTION_GET_UPDATE_INFO_FAILE = "action_get_update_info_fail";//获取更新信息失败
    }

    //传值常量类
    public class BundleConstant {
        public static final String BUNDLE_GET_UPDATE_INFO = "bundle_get_message_info";//更新信息

    }

    //保存常量类
    public class PreferenceConstants {
        public static final String APK_NAME = "TeamWorker.apk";
    }

}
