package com.enjoyshop.utils;

import android.util.Log;

/**
 * <pre>
 *     author : 高磊华
 *     e-mail : 984992087@qq.com
 *     time   : 2017/08/07
 *     desc   : 日志的工具类
 *     version: 1.0
 * </pre>
 */


public class LogUtil {

    private static boolean bossLog = true;

    public static void d(String TAG, String msg, boolean isLog) {
        if (bossLog && isLog)
            Log.d(TAG, msg);
    }

    public static void e(String TAG, String msg, boolean isLog) {
        if (bossLog && isLog)
            Log.e(TAG, msg);
    }

}
