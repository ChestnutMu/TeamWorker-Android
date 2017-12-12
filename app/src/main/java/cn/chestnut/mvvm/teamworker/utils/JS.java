package cn.chestnut.mvvm.teamworker.utils;

import com.alibaba.fastjson.JSONObject;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/10 20:49:36
 * Description：JSON数据操作类
 * Email: xiaoting233zhang@126.com
 */
public class JS
{
	public static boolean getBoolean(JSONObject obj, String key, boolean def) {
		if (obj.containsKey(key)) {
			return obj.getBoolean(key);
		}
		return def;
	}

	public static String getString(JSONObject obj, String key) {
		if (obj.containsKey(key)) {
			return obj.getString(key);
		}
		return "";
	}

	public static int getInt(JSONObject obj, String key, int def) {
		if (obj.containsKey(key)) {
			return obj.getIntValue(key);
		}
		return def;
	}

	public static long getLong(JSONObject obj, String key, long def) {
		if (obj.containsKey(key)) {
			return obj.getLongValue(key);
		}
		return def;
	}

}
