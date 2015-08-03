package com.sina.sinavideo.sdk.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.provider.Settings;

import com.sina.sinavideo.sdk.VDVideoViewController;

/**
 * 从传感器中，获得相应的值 保证在横竖屏时候，左右摆动10度左右，能解除锁屏限制
 */
public class VDGravitySensorManager {

    public static final int ScreenOreintationX = 1;
    public static final int ScreenOreintationY = 2;
    public static final int ScreenOreintationZ = 3;
    private int lastScreenOreintation = -1;
    private int currentScreenOreintation = 0;
    // private VDVideoViewController mController;

    private boolean mIsScreenLock = false;
    private boolean mIsGravity;

    public void setLockScreen(boolean lock) {
        mIsScreenLock = lock;
    }

    public boolean isScreenLocked() {
        return mIsScreenLock;
    }

    private class VDGravitySensorEventListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (!VDVideoScreenOrientation.getIsNeedSensor()) {
                return;
            }
            mIsGravity = getSystemGravity(mContext);
            if (!mIsGravity) {
                if (!VDVideoScreenOrientation.getIsLandscape(mContext)
                        && !VDVideoFullModeController.getInstance().getIsFromHand()) {
                    VDLog.d(TAG, "VDVideoScreenOrientation.setPortrait");
                    VDVideoScreenOrientation.setPortrait(mContext);
                }
                return;
            }
            // TODO Auto-generated method stub
            float x = event.values[0]; // X轴
            float y = event.values[1]; // Y轴
            float z = event.values[2]; // Z轴
            if (Math.abs(y) > Math.abs(x) && Math.abs(y) > Math.abs(z)) {
                currentScreenOreintation = ScreenOreintationY;
            } else if (Math.abs(y) < Math.abs(x) && Math.abs(y) < Math.abs(x)) {
                currentScreenOreintation = ScreenOreintationX;
            }

            if (Math.abs(x) <= 3F && Math.abs(y) >= 7F && Math.abs(z) <= 6F) {
                if (lastScreenOreintation != currentScreenOreintation) {
                    lastScreenOreintation = currentScreenOreintation;
                    VDVideoViewController controller = VDVideoViewController.getInstance(mContext);
                    if (null != controller)
                        controller.notifyScreenOrientationChange(true);
                    VDLog.e(VDVideoFullModeController.TAG, "onSensorChanged: 竖屏 显示lockview");
                }
            } else if (Math.abs(x) >= 7.2F && Math.abs(y) <= 3.5F && (double) Math.abs(z) <= 6F) {
                if (lastScreenOreintation != currentScreenOreintation) {
                    VDLog.e(VDVideoFullModeController.TAG, "onSensorChanged: 横屏 显示lockview");
                    lastScreenOreintation = currentScreenOreintation;
                    VDVideoViewController controller = VDVideoViewController.getInstance(mContext);
                    if (null != controller)
                        controller.notifyScreenOrientationChange(false);
                }
            }
            if (!mIsScreenLock && !mIsGravity) {
                return;
            }
            if (VDVideoFullModeController.getInstance().getIsPortrait()) {
                // 竖屏
                if (Math.abs(x) <= 3F && Math.abs(y) >= 7F && Math.abs(z) <= 6F) {
                    if (VDVideoScreenOrientation.getOrientationConfig(mContext) == ActivityInfo.SCREEN_ORIENTATION_SENSOR) {
                        return;
                    }
                    if (!mIsScreenLock) {
                        VDVideoScreenOrientation.setSensor(mContext);
                    }

                    // mIsInSensor = true;

                }
            } else {
                if (Math.abs(x) >= 7.2F && Math.abs(y) <= 3.5F && (double) Math.abs(z) <= 6F) {
                    if (VDVideoScreenOrientation.getOrientationConfig(mContext) == ActivityInfo.SCREEN_ORIENTATION_SENSOR) {
                        return;
                    }
                    // 横屏
                    if (!mIsScreenLock) {
                        VDVideoScreenOrientation.setSensor(mContext);
                    }
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub

        }

    }

    private final static String TAG = "VDGravitySensorManager";
    private SensorManager mSensorManager = null;
    private Sensor mSensor = null;
    private VDGravitySensorEventListener mEventListener = null;
    private Context mContext = null;

    public VDGravitySensorManager() {
        super();
        // mController = VDVideoViewController.getInstance();
    }

    /**
     * 注册监听
     */
    public void register(Context context) {
        if (context == null) {
            VDLog.e(TAG, "context is null");
            return;
        }
        mContext = context;
        try {
            if (mEventListener == null) {
                mEventListener = new VDGravitySensorEventListener();
            }
            mSensorManager = (SensorManager) ((Activity) context).getSystemService(Context.SENSOR_SERVICE);
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

            mSensorManager.registerListener(mEventListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } catch (Exception ex) {
            ex.printStackTrace();
            VDLog.e(TAG, ex.getMessage());
        }
        VDLog.d("VDGravitySensorManager", "context ctt=" + mContext);
        // mIsInSensor = false;
    }

    /**
     * 取消监听
     */
    public void release() {
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(mEventListener);
        }
        // mIsInSensor = false;
    }

    public static boolean getSystemGravity(Context context) {
        int rotation;
        try {
            rotation = android.provider.Settings.System.getInt(context.getContentResolver(),
                    Settings.System.ACCELEROMETER_ROTATION);
        } catch (android.provider.Settings.SettingNotFoundException settingnotfoundexception) {
            settingnotfoundexception.printStackTrace();
            return false;
        }
        return rotation != 0;
    }

    public void enableSensor(boolean enable) {
        VDLog.e(VDVideoFullModeController.TAG, "enableSensor: " + enable);
        // mIsInSensor = enable;
    }
}
