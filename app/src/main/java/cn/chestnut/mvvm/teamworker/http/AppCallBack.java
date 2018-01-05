package cn.chestnut.mvvm.teamworker.http;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/10 20:49:36
 * Description：网络请求回调接口
 * Email: xiaoting233zhang@126.com
 */

public interface AppCallBack<T> {
	void next(T response);

	void error(Throwable error);

	void complete();

	void before();
}
