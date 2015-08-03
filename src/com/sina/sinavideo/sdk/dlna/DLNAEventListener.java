package com.sina.sinavideo.sdk.dlna;

import java.util.HashSet;
import java.util.Set;

import android.os.Handler;
import android.util.Log;

/**
 * DLNA事件处理回调类
 * 
 * @author sina
 *
 */
public class DLNAEventListener {

    public DLNAEventListener() {
        mHandler = new Handler();
    }

    private Handler mHandler;

    private static DLNAEventListener mDLNAEventListener;

    public static DLNAEventListener getInstance() {
        if (mDLNAEventListener == null) {
            mDLNAEventListener = new DLNAEventListener();
        }
        return mDLNAEventListener;
    }

    // -- Listener

    /**
     * DLNA开关回调
     * 
     * @author liuqun1
     * 
     */
    public interface OnDLNAListSwitchListener {

        void setUp();

        void onDLNAListSwitch(boolean open);

        void toggle();
        
        void hide();
    }

    /**
     * DLNA设备发现或消失事件
     * 
     * @author liuqun1
     * 
     */
    public interface OnMediaRenderNumChangedListener {

        void onMediaRenderAdded(String uuid, String name);

        void onMediaRenderRemoved(String uuid, String name);
    }

    /**
     * DLNA设备状态改变回调
     * 
     * @author liuqun1
     * 
     */
    public interface OnMediaRenderStateChangeListener {

        void onMediaRenderStateChanged(String name, String value);
    }

    /**
     * DLNA设备连接成功回调
     * 
     * @author liuqun1
     * 
     */
    public interface OnDLNASelectedListener {

        void onMediaRenderSelect(String name, String value);

        void onMediaRenderOpened(boolean opened);
    }

    /**
     * DLNA占位图显示/隐藏回调
     * 
     * @author liuqun1
     * 
     */
    public interface OnDLNAVisibleChangeListener {

        void onDLNAIndicaterVisibleChange(boolean visible);
    }

    /**
     * MediaController的 UI回调
     * 
     * @author liuqun1
     * 
     */
    public interface OnMediaControllerListener extends OnDLNASelectedListener {

        @Override
        void onMediaRenderOpened(boolean opened);

        /**
         * DLNA进度更新事件
         * 
         * @param position
         * @param duration
         */
        void onProgressUpdate(long position, long duration);

        void onDLNASwitch(boolean open);

        /**
         * DLNA设备状态改变回调
         * 
         * @param name
         * @param value
         */
        void onMediaRenderStateChanged(String name, String value);

        void onGetVolume(int vol);

        void onPlayStateChanged(boolean isPlaying);

        void onPreOpenDLNA();

        void onGetDuration(long dur);
    }

    /**
     * DLNA开关
     * 
     * @author liuqun1
     * 
     */
    public interface OnDLNASwitchListener {

        void onDLNASwitch(boolean open);
    }

    public void addOnDLNAListSwitchListener(OnDLNAListSwitchListener l) {
        mOnDLNAListSwitchListener.add(l);
    }

    public void removeOnDLNAListSwitchListener(OnDLNAListSwitchListener l) {
        mOnDLNAListSwitchListener.remove(l);
    }

    public void addOnMediaRenderNumChangedListener(OnMediaRenderNumChangedListener l) {
        mOnMediaRenderNumChangedListener.add(l);
    }

    public void removeOnMediaRenderNumChangedListener(OnMediaRenderNumChangedListener l) {
        mOnMediaRenderNumChangedListener.remove(l);
    }

    public void addOnMediaRenderStateChangeListener(OnMediaRenderStateChangeListener l) {
        mOnMediaRenderStateChangeListener.add(l);
    }

    public void removeOnMediaRenderStateChangeListener(OnMediaRenderStateChangeListener l) {
        mOnMediaRenderStateChangeListener.remove(l);
    }

    public void addOnDLNASelectedListener(OnDLNASelectedListener l) {
        mOnDLNASelectedListener.add(l);
    }

    public void removeOnDLNASelectedListener(OnDLNASelectedListener l) {
        mOnDLNASelectedListener.remove(l);
    }

    public void addOnDLNAVisiableChangeListener(OnDLNAVisibleChangeListener l) {
        mOnDLNAVisibleChangeListener.add(l);
    }

    public void removeOnDLNAVisiableChangeListener(OnDLNAVisibleChangeListener l) {
        mOnDLNAVisibleChangeListener.remove(l);
    }

    public void addOnMediaControllerListener(final OnMediaControllerListener l) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                mOnMediaControllerListener.add(l);
            }
        });
        Log.i("DLNA", "addOnMediaControllerListener : " + mOnMediaControllerListener.size());
    }

    public void removeOnMediaControllerListener(final OnMediaControllerListener l) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                mOnMediaControllerListener.remove(l);
            }
        });
    }

    public void addOnDLNASwitchListener(OnDLNASwitchListener l) {
        mOnDLNASwitchListener.add(l);
    }

    public void removeOnDLNASwitchListener(OnDLNASwitchListener l) {
        mOnDLNASwitchListener.remove(l);
    }

    // Listeners
    private Set<OnDLNAListSwitchListener> mOnDLNAListSwitchListener = new HashSet<OnDLNAListSwitchListener>();
    private Set<OnMediaRenderNumChangedListener> mOnMediaRenderNumChangedListener = new HashSet<OnMediaRenderNumChangedListener>();
    private Set<OnMediaRenderStateChangeListener> mOnMediaRenderStateChangeListener = new HashSet<OnMediaRenderStateChangeListener>();
    private Set<OnDLNASelectedListener> mOnDLNASelectedListener = new HashSet<OnDLNASelectedListener>();
    private Set<OnDLNAVisibleChangeListener> mOnDLNAVisibleChangeListener = new HashSet<OnDLNAVisibleChangeListener>();
    private Set<OnMediaControllerListener> mOnMediaControllerListener = new HashSet<OnMediaControllerListener>();
    private Set<OnDLNASwitchListener> mOnDLNASwitchListener = new HashSet<OnDLNASwitchListener>();

    // notify method
    public void notifyMediaRenderAdded(String uuid, String name) {
        for (OnMediaRenderNumChangedListener l : mOnMediaRenderNumChangedListener) {
            l.onMediaRenderAdded(uuid, name);
        }
    }

    public void notifyMediaRenderRemoved(String uuid, String name) {
        for (OnMediaRenderNumChangedListener l : mOnMediaRenderNumChangedListener) {
            l.onMediaRenderRemoved(uuid, name);
        }
    }

    public void notifyDLNASetUp() {
        for (OnDLNAListSwitchListener l : mOnDLNAListSwitchListener) {
            l.setUp();
        }
    }

    public void notifyDLNAListSwitch(boolean open) {
        for (OnDLNAListSwitchListener l : mOnDLNAListSwitchListener) {
            l.onDLNAListSwitch(open);
        }
    }

    public void notifyDLNASwitch(boolean open) {
        for (OnDLNASwitchListener l : mOnDLNASwitchListener) {
            l.onDLNASwitch(open);
        }
    }
    
    public void notifyDLNAListToogle() {
    	for (OnDLNAListSwitchListener l : mOnDLNAListSwitchListener) {
    		l.toggle();
    	}
    }

    public void notifyDLNAListHide() {
        for (OnDLNAListSwitchListener l : mOnDLNAListSwitchListener) {
            l.hide();
        }
    }

    public void notifyDLNAProgressUpdate(final long position, final long duration) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                for (OnMediaControllerListener l : mOnMediaControllerListener) {
                    l.onProgressUpdate(position, duration);
                }
            }
        });
    }

    public void notifyDLNADuration(final long duration) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                for (OnMediaControllerListener l : mOnMediaControllerListener) {
                    l.onGetDuration(duration);
                }
            }
        });
    }

    public void notifyDLNAMediaRenderStateChange(final String name, final String value) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                for (OnMediaRenderStateChangeListener l : mOnMediaRenderStateChangeListener) {
                    l.onMediaRenderStateChanged(name, value);
                }
                for (OnMediaControllerListener l : mOnMediaControllerListener) {
                    l.onMediaRenderStateChanged(name, value);
                }
            }
        });
    }

    public void notifyDLNAMediaRenderSelected(String name, String value) {
        for (OnDLNASelectedListener l : mOnDLNASelectedListener) {
            l.onMediaRenderSelect(name, value);
        }
    }

    public void notifyDLNAMediaRenderOpened(boolean opened) {
        for (OnDLNASelectedListener l : mOnDLNASelectedListener) {
            l.onMediaRenderOpened(opened);
        }
    }

    public void notifyPlayStateChanged(final boolean isPlaying) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                for (OnMediaControllerListener l : mOnMediaControllerListener) {
                    l.onPlayStateChanged(isPlaying);
                }
            }
        });
    }

    public void notifyOnPreOpenDLNA() {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                for (OnMediaControllerListener l : mOnMediaControllerListener) {
                    l.onPreOpenDLNA();
                }
            }
        });
    }

    public void notifyDLNAVisibleSwitch(boolean visible) {
        for (OnDLNAVisibleChangeListener l : mOnDLNAVisibleChangeListener) {
            l.onDLNAIndicaterVisibleChange(visible);
        }
    }

    public void notifyDLNAClose() {
        notifyDLNAMediaRenderOpened(false);
        notifyDLNAVisibleSwitch(false);
        notifyDLNAListSwitch(false);
        notifyDLNASwitch(false);
        try {
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    for (OnMediaControllerListener l : mOnMediaControllerListener) {
                        l.onDLNASwitch(false);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void notifyOnGetVolume(final int vol) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                for (OnMediaControllerListener l : mOnMediaControllerListener) {
                    l.onGetVolume(vol);
                }
            }
        });
    }
}
