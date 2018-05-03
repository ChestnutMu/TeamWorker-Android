package cn.chestnut.mvvm.teamworker;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/10 20:49:36
 * Description：常量
 * Email: xiaoting233zhang@126.com
 */


public class Constant {

    /**
     * 聊天室类型 0双人聊天室 1多人聊天室
     */
    public class ChatType {
        public static final int TYPE_CHAT_DOUBLE = 0;
        public static final int TYPE_CHAT_MULTIPLAYER = 1;
    }


    /**
     * 消息类型 null或者0 默认普通消息 1修改聊天室名称 2修改聊天室头像 3人员变动
     */
    public class ChatMessageType {
        public static final int TYPE_MESSAGE_NORMAL = 0;
        public static final int TYPE_MESSAGE_CHANGE_NAME = 1;
        public static final int TYPE_MESSAGE_CHANGE_PIC = 2;
        public static final int TYPE_MESSAGE_CHANGE_PEOPLE_ADD = 3;
        public static final int TYPE_MESSAGE_CHANGE_PEOPLE_REMOVE = 4;
        public static final int TYPE_MESSAGE_PEOPLE_OUT = 5;
    }

    //PreferenceUtil key
    public class PreferenceKey {

        public static final String USER_INFO_WAITING = "user_info_waiting:";//是否在更新用户信息

        public static final String CHAT_INFO_WAITING = "chat_info_waiting:";//是否在更新聊天室信息

    }

    //action 广播标志类
    public class ActionConstant {

        public static final String ACTION_GET_UPDATE_INFO_SUCCESS = "action_get_update_info_success";//获取更新信息成功
        public static final String ACTION_GET_UPDATE_INFO_FAILE = "action_get_update_info_fail";//获取更新信息失败

        public static final String ACTION_GET_NEW_MESSAGE = "action_get_new_message";//收到一条新消息

        public static final String UPDATE_MESSAGE_CHAT_LAYOUT = "update_message_chat_layout";//更新消息聊天室界面

        public static final String UPDATE_TEAM_NOTIFICATION_BADGE = "update_team_notification_badge";//更新工作界面中的团队公告角标

        public static final String ACTION_GET_NEW_FRIEND_REQUEST = "action_get_new_friend_request";//收到一条新的好友请求


        public static final String ACTION_UPDATE_CHAT = "action_update_chat";//更新聊天室

        public static final String ACTION_UPDATE_CHAT_SETTING = "action_update_chat_setting";//更新聊天室设置

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
