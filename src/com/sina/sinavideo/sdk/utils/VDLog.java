package com.sina.sinavideo.sdk.utils;

import android.util.Log;

public class VDLog {

    /**
     * 打印debug级别的日志
     * 
     * @param tag
     *            Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg
     *            The message you would like logged.
     * @return The number of bytes written.
     */
    public static int d(String tag, String msg) {
        if (VDApplication.getInstance().mDebug) {
            if (msg == null) {
                msg = "null";
            }
            return Log.d(tag, msg);
        }
        return 0;
    }

    public static int i(String tag, String msg) {
        if (VDApplication.getInstance().mDebug) {
            if (msg == null) {
                msg = "null";
            }
            return Log.i(tag, msg);
        }
        return 0;
    }

    public static int v(String tag, String msg) {
        if (VDApplication.getInstance().mDebug) {
            if (msg == null) {
                msg = "null";
            }
            return Log.v(tag, msg);
        }
        return 0;
    }

    /**
     * 打印debug级别的日志
     * 
     * @param tag
     *            Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg
     *            The message you would like logged.
     * @param tr
     *            An exception to log
     * @return The number of bytes written.
     */
    public static int d(String tag, String msg, Throwable tr) {
        if (VDApplication.getInstance().mDebug) {
            if (msg == null) {
                msg = "null";
            }
            return Log.d(tag, msg, tr);
        }
        return 0;
    }

    public static int i(String tag, String msg, Throwable tr) {
        if (VDApplication.getInstance().mDebug) {
            if (msg == null) {
                msg = "null";
            }
            return Log.i(tag, msg, tr);
        }
        return 0;
    }

    public static int w(String tag, String msg) {
        if (VDApplication.getInstance().mDebug) {
            if (msg == null) {
                msg = "null";
            }
            return Log.w(tag, msg);
        }
        return 0;
    }

    public static int w(String tag, String msg, Throwable tr) {
        if (VDApplication.getInstance().mDebug) {
            if (msg == null) {
                msg = "null";
            }
            return Log.w(tag, msg, tr);
        }
        return 0;
    }

    public static int e(String tag, String msg) {
        if (VDApplication.getInstance().mDebug) {
            if (msg == null) {
                msg = "null";
            }
            return Log.e(tag, msg);
        }
        return 0;
    }

    public static int e(String tag, String msg, Throwable tr) {
        if (VDApplication.getInstance().mDebug) {
            if (msg == null) {
                msg = "null";
            }
            return Log.e(tag, msg, tr);
        }
        return 0;
    }

}