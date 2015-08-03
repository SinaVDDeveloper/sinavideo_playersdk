package com.sina.sinavideo.sdk.utils;

import com.sina.sinavideo.sdk.VDVideoViewController;
import com.sina.sinavideo.sdk.VDVideoViewLayerContextData;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;

/**
 * 对应于很复杂的转屏部分，单独独立出一个存储类来处理
 * 
 * @author sina
 */
public class VDVideoFullModeController {

    // 屏幕锁方式
    private boolean mIsFullScreenFromHand = false;
    private boolean mIsManual = false;
    private boolean mIsFullScreen = false;
    // 标识一下，用来去掉手动变量
    public int mInHandNum = 0;
    private boolean mIsScreenLock = false;
    private VDGravitySensorManager mGravitySensorManager = null;
    private Context mContext = null;
    public final static String TAG = "VDVideoFullModeData";
    // 默认是按照重力感应来处理
    private int mOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR;

    /**
     * 手动设置
     * 
     * @param isManual
     */
    public void setIsManual(boolean isManual) {
        if (isManual) {
            registerSensorManager();
        } else {
            // unRegisterSensorManager();
        }
        mIsManual = isManual;
    }

    /**
     * 判定是否手动设置
     * 
     * @return
     */
    public boolean getIsFromHand() {
        return mIsManual;
    }

    /**
     * 设置是否为全屏模式
     * 
     * @param isFullScreen
     */
    public void setIsFullScreen(boolean isFullScreen) {
        mIsFullScreen = isFullScreen;
        if (mIsManual) {
            mIsFullScreenFromHand = isFullScreen;
        }
    }

    /**
     * 判断当前是否为全屏模式
     * 
     * @return
     * @see {@link #setIsFullScreen(boolean)}
     */
    public boolean getIsFullScreen() {
        return mIsFullScreen;
    }

    /**
     * 判断当前是否处于竖屏模式
     * 
     * @return
     * @see {@link #setIsFullScreen(boolean)}
     */
    public boolean getIsPortrait() {
        if (mIsManual) {
            // 手动时候，使用另外的变量来判断
            return !mIsFullScreenFromHand;
        }
        return !mIsFullScreen;
    }

    /**
     * 判断当前屏幕是否被锁定
     * 
     * @return
     * @see {@link #setFullLock()}
     * @see {@link #releaseFullLock()}
     */
    public boolean getIsScreenLock() {
        return mIsScreenLock;
    }

    // !---------单例部分 begin---------//
    private static class VDVideoFullModeControllerInstance {

        public static VDVideoFullModeController instance = new VDVideoFullModeController();
    }

    /**
     * 获取{@link VDVideoFullModeController}类的实例
     * 
     * @return {@link VDVideoFullModeController}类实例对象
     */
    public static VDVideoFullModeController getInstance() {
        if (VDVideoFullModeControllerInstance.instance == null) {
            VDVideoFullModeControllerInstance.instance = new VDVideoFullModeController();
        }
        return VDVideoFullModeControllerInstance.instance;
    }

    // !---------单例部分 end---------//

    /**
     * 构造函数
     */
    public VDVideoFullModeController() {
        super();
        if (mGravitySensorManager == null) {
            mGravitySensorManager = new VDGravitySensorManager();
        }
    }

    /**
     * 获取{@link VDGravitySensorManager}类对象
     * 
     * @return
     */
    public VDGravitySensorManager getVDGravitySensorManager() {
        return mGravitySensorManager;
    }

    /**
     * 初始化数据
     * 
     * @param context
     */
    public void init(Context context) {
        if (context != null) {
            mContext = context;
            mOrientation = ((Activity) context).getRequestedOrientation();
            // 初始化当前的锁变量
            if (mOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    || mOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                mIsScreenLock = true;
            } else {
                mIsScreenLock = false;
            }
        }
        VDLog.d("VDVideoFullModeController","context ctt=" + mContext);
    }

    /**
     * 释放数据和对象
     */
    public void release() {
        // VDVideoFullModeControllerInstance.instance = null;
        // 清理传感器监听部分
        // unRegisterSensorManager();
        mGravitySensorManager = null;
        // 上下文句柄部分去掉
        mContext = null;
    }

    /**
     * 注册重力感应监听
     */
    public void registerSensorManager() {
        if (mIsScreenLock) {
            // 锁住屏幕了。就不能执行触发了
            return;
        }
        if (mGravitySensorManager == null) {
            mGravitySensorManager = new VDGravitySensorManager();
        }
        mGravitySensorManager.register(mContext);
    }

    /**
     * 注销重力感应监听
     */
    public void unRegisterSensorManager() {
        if (mGravitySensorManager != null) {
            mGravitySensorManager.release();
        }
    }

    /**
     * 设置是否开启重力感应功能
     * 
     * @param enable
     */
    public void enableSensor(boolean enable) {
        if (mGravitySensorManager != null) {
            mGravitySensorManager.enableSensor(enable);
        }
    }

    /**
     * 设置全屏锁定
     */
    public void setFullLock() {
        // 加入屏幕锁，则先取消监听
        // unRegisterSensorManager();
        registerSensorManager();
        VDVideoViewController controller = VDVideoViewController.getInstance(mContext);
        if (null!=controller && controller.mLayerContextData.getLayerType() != VDVideoViewLayerContextData.LAYER_COMPLEX_NOVERTICAL) {
            // 在只横不竖的情况下，不做no sensor操作
            VDVideoScreenOrientation.setNoSensor(mContext);
        }
        mIsScreenLock = true;
        if (mGravitySensorManager != null) {
            mGravitySensorManager.setLockScreen(true);
        }
    }

    /**
     * 取消全屏锁定
     */
    public void releaseFullLock() {
        VDVideoScreenOrientation.setSensor(mContext);
        mIsScreenLock = false;
        if (mGravitySensorManager != null) {
            mGravitySensorManager.setLockScreen(false);
        }
    }

}
