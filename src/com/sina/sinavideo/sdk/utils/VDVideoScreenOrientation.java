/**
 * 全屏部分的变量暂存以及调整当前屏幕方向等等的函数包
 * 
 * @author sunxiao
 */

package com.sina.sinavideo.sdk.utils;

import java.lang.reflect.Field;

import com.sina.sinavideo.sdk.VDVideoViewController;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class VDVideoScreenOrientation {

    private static boolean mIsNeedSensor = true;

    public static boolean getIsNeedSensor() {
        return mIsNeedSensor;
    }

    public static void setNoSensor(Context context) {
        mIsNeedSensor = false;
        if (context != null) {
            DisplayMetrics metrics = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int w = metrics.widthPixels;
            int h = metrics.heightPixels;
            if (w > h) {
                ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {
                ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }
    }

    public static void setSensor(Context context) {
        mIsNeedSensor = true;
        if (context != null) {
            ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

    public static void setOnlyLandscape(Context context) {
        mIsNeedSensor = false;
        if (context != null) {
            if (android.os.Build.VERSION.SDK_INT >= 9) {
                ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            }
        }
    }

    public static void setLandscape(Context context) {
        mIsNeedSensor = true;
        if (context != null) {
            if (android.os.Build.VERSION.SDK_INT >= 9 && !VDVideoFullModeController.getInstance().getIsScreenLock()) {
                ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            } else {
                ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }
    }

    public static void setPortrait(Context context) {
        mIsNeedSensor = true;
        if (context != null) {
            if (android.os.Build.VERSION.SDK_INT >= 9 && !VDVideoFullModeController.getInstance().getIsScreenLock()) {
                ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            } else {
                ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }
    }

    public static int getOrientationConfig(Context context) {
        if (context != null) {
            return ((Activity) context).getRequestedOrientation();
        }
        return -1;
    }

    public static boolean getIsLandscape(Context context) {
        if (context != null) {
            return ((Activity) context).getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        }
        return false;
    }

    public static boolean getIsPortrait(Context context) {
        if (context != null) {
            return ((Activity) context).getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        }
        return false;
    }

    public static void setStatusBarVisible(Context ctt, boolean isVisible) {
        VDVideoViewController controller = VDVideoViewController.getInstance(ctt);
        if (controller == null) {
            return;
        }
        Activity context = (Activity) controller.getContext();
        if (context == null) {
            return;
        }
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        if (isVisible) {
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            ((Activity) context).getWindow().setAttributes(lp);
            if (!VDUtility.isSamsungNoteII()) {
                ((Activity) context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            }
        } else {
            lp.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            ((Activity) context).getWindow().setAttributes(lp);
            ((Activity) context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    /**
     * 取得状态栏的高度，取法有点诡异，看看是否有其他方式可以拿到
     * 
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            return context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            return (int) (0.5D + (context.getResources().getDisplayMetrics().densityDpi / 160F) * 25);
        }
    }
}
