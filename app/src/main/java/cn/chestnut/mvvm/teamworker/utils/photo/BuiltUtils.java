/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.chestnut.mvvm.teamworker.utils.photo;

import android.os.Build;
import android.os.StrictMode;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/10 20:49:36
 * Description：Class containing some static utility methods.
 * Email: xiaoting233zhang@126.com
 */
public class BuiltUtils {
	private BuiltUtils() {
	}

	public static void enableStrictMode() {
		if (BuiltUtils.hasGingerbread()) {
			StrictMode.ThreadPolicy.Builder threadPolicyBuilder = new StrictMode.ThreadPolicy.Builder().detectAll()
					.penaltyLog();
			StrictMode.VmPolicy.Builder vmPolicyBuilder = new StrictMode.VmPolicy.Builder().detectAll().penaltyLog();

			if (BuiltUtils.hasHoneycomb()) {
				// threadPolicyBuilder.penaltyFlashScreen();
				// vmPolicyBuilder.setClassInstanceLimit(ImageGridActivity.class,
				// 1).setClassInstanceLimit(
				// ImageDetailActivity.class, 1);
			}
			StrictMode.setThreadPolicy(threadPolicyBuilder.build());
			StrictMode.setVmPolicy(vmPolicyBuilder.build());
		}
	}

	public static boolean hasFroyo() {
		// Can use static final constants like FROYO, declared in later versions
		// of the OS since they are inlined at compile time. This is guaranteed
		// behavior.
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
	}

	public static boolean hasGingerbread() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
	}

	public static boolean hasHoneycomb() {
		return Build.VERSION.SDK_INT >= 11;// Build.VERSION_CODES.HONEYCOMB;
	}

	public static boolean hasHoneycombMR1() {
		return Build.VERSION.SDK_INT >= 12;// Build.VERSION_CODES.HONEYCOMB_MR1;
	}

	public static boolean hasJellyBean() {
		return Build.VERSION.SDK_INT >= 16;// Build.VERSION_CODES.JELLY_BEAN;
	}
}
