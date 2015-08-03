package com.sina.sinavideo.sdk;

import android.content.Context;
import android.os.Handler;

import com.sina.sinavideo.sdk.data.VDVideoInfo;

/**
 * 外部引用用的listerner文件，用于通知外部的activity等等
 * 
 * @author sunxiao
 */
public class VDVideoExtListeners {

    public final static String TAG = "VDVideoExtListeners";

    private Handler mHandler = new Handler();
    private Context mContext;

    /**
     * 视频插入广告部分,有点击，换图等事件回调
     * 
     * @author sunxiao
     */
    public interface OnVDVideoInsertADListener {

        /**
         * 当前视频插入广告，每次点击都触发此方法，用来通知外部activity做广告跳转
         */
        public void onInsertADClick(VDVideoInfo info);

        /**
         * 当前视频插入广告，每次点击『跳过广告』会触发此方法，用来通知外部，做会员注册登录等用途
         */
        public void onInsertADStepOutClick(VDVideoInfo info);

    }

    public interface OnVDVideoInsertADInitListener {

    }

    /**
     * 插入贴片广告全部播放完毕，会回调此接口
     * 
     * @author alexsun
     * 
     */
    public interface OnVDVideoInsertADEndListener {

        public void onInsertADEnd(VDVideoInfo info, int status);
    }

    /**
     * 静帧广告，在点击暂停时候等情况会出现，这个接口用来给client端准备图片之类的素材用
     * 
     * @author sunxiao
     */
    public interface OnVDVideoFrameADListener {

        /**
         * 每次触发广告前，都会执行这个方法，用来给帧间广告换图之类的用途
         */
        public void onFrameADPrepared(VDVideoInfo info);
    }

    /**
     * 播放列表部分，这个接口用来当播放列表中的某一项被点中的时候，client端接受并初始化视频之用
     * 
     * @author sunxiao
     */
    public interface OnVDVideoPlaylistListener {

        /**
         * 返回当前点击的播放列表的返回接口
         * 
         * @param info
         * @param p
         */
        public void onPlaylistClick(VDVideoInfo info, int p);
    }

    /**
     * 播放列表切换
     * 
     * @author sunxiao
     */
    // TODO 这个接口需要合并
    public interface OnVDPlayerTypeSwitchListener {

        public void OnVDVideoPlayerTypeSwitch(VDVideoInfo info, int p);
    }

    /**
     * 当前视频的状态变化
     * 
     * @author sunxiao
     */
    public interface OnVDVideoCompletionListener {

        /**
         * 视频播放结束会调用此接口
         * 
         * @param info
         * @param status
         *            一直是0
         */
        public void onVDVideoCompletion(VDVideoInfo info, int status);
    }

    /**
     * 软硬解切换通知
     * 
     * @author sunxiao
     * 
     */
    public interface OnVDVideoPlayerChangeListener {

        public void OnVDVideoPlayerChangeSwitch(int index, long position);
    }

    /**
     * 当前视频发生错误
     * 
     * @author sunxiao
     */
    public interface OnVDVideoErrorListener {

        /**
         * 发生错误的时候调用
         * 
         * @param info
         * @param errWhat
         *            MEDIA_PLAYER_ERROR_WHAT_*
         * @param errExtra
         *            MEDIA_PLAYER_ERROR_EXTRA_*
         */
        public void onVDVideoError(VDVideoInfo info, int errWhat, int errExtra);
    }

    /**
     * 当前视频有信息返回的时候，回调
     * 
     * @author sunxiao
     */
    public interface OnVDVideoInfoListener {

        /**
         * 当前视频有信息返回的时候，回调
         * 
         * @param info
         * @param infWhat
         *            MEDIA_INFO_WHAT_*
         */
        public void onVDVideoInfo(VDVideoInfo info, int infWhat);
    }

    /**
     * prepared回调
     * 
     * @author sunxiao
     */
    public interface OnVDVideoPreparedListener {

        /**
         * 当前有视频prepared信息的时候，进行回调
         * 
         * @param info
         */
        public void onVDVideoPrepared(VDVideoInfo info);
    }

    private OnVDVideoFrameADListener mFrameADListener;
    private OnVDVideoInsertADListener mInsertADListener;
    public OnVDVideoPlaylistListener mPlaylistListener;
    public OnVDPlayerTypeSwitchListener mOnVDPlayerTypeSwitchListener;
    private OnVDVideoCompletionListener mOnVDVideoCompletionListener;
    private OnVDVideoErrorListener mOnVDVideoErrorListener;
    private OnVDVideoInfoListener mOnVDVideoInfoListener;
    private OnVDVideoPreparedListener mOnVDVideoPreparedListener;
    private OnVDVideoInsertADEndListener mOnVDVideoInsertADEndListener;
    private OnVDVideoPlayerChangeListener mOnVDVideoPlayerChangeListener;

    public void setOnVDVideoPlayerChangeListener(OnVDVideoPlayerChangeListener l) {
        mOnVDVideoPlayerChangeListener = l;
    }

    public void setOnVDVideoInsertADEndListener(OnVDVideoInsertADEndListener l) {
        mOnVDVideoInsertADEndListener = l;
    }

    public void setOnVDVideoPreparedListener(OnVDVideoPreparedListener l) {
        mOnVDVideoPreparedListener = l;
    }

    /**
     * 设置广告监听接口
     * 
     * @param l
     */
    public void setFrameADListener(OnVDVideoFrameADListener l) {
        mFrameADListener = l;
    }

    /**
     * 设置插入视频广告的回调
     * 
     * @param l
     */
    public void setInsertADListener(OnVDVideoInsertADListener l) {
        mInsertADListener = l;
    }

    /**
     * 设置播放列表监听接口
     * 
     * @param l
     */
    public void setPlaylistListener(OnVDVideoPlaylistListener l) {
        mPlaylistListener = l;
    }

    /**
     * 设置播放类型切换监听接口
     * 
     * @param l
     */
    public void setOnVDPlayerTypeSwitchListener(OnVDPlayerTypeSwitchListener l) {
        mOnVDPlayerTypeSwitchListener = l;
    }

    /**
     * 设置播放结束监听接口
     * 
     * @param l
     */
    public void setOnVDVideoCompletionListener(OnVDVideoCompletionListener l) {
        mOnVDVideoCompletionListener = l;
    }

    /**
     * 设置播放错误监听接口
     * 
     * @param l
     */
    public void setOnVDVideoErrorListener(OnVDVideoErrorListener l) {
        mOnVDVideoErrorListener = l;
    }

    /**
     * 设置播放器信息监听接口 NOTE: info不一定代表错误，具体数据参照文件头部
     * 
     * @param l
     */
    public void setOnVDVideoInfoListener(OnVDVideoInfoListener l) {
        mOnVDVideoInfoListener = l;
    }

    public void notifyPlayerChange(final int index, final long position) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (mOnVDVideoPlayerChangeListener != null) {
                    VDVideoViewController controller = VDVideoViewController.getInstance(mContext);
                    if (controller != null) {
                        mOnVDVideoPlayerChangeListener.OnVDVideoPlayerChangeSwitch(index, position);
                    }
                }
            }
        });
    }

    public void notifyInsertADEnd(final int status) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (mOnVDVideoInsertADEndListener != null) {
                    VDVideoViewController controller = VDVideoViewController.getInstance(mContext);
                    if (controller != null)
                        mOnVDVideoInsertADEndListener.onInsertADEnd(controller.getCurrentVideo(), status);
                }
            }
        });
    }

    public void notifyPreparedListener() {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (mOnVDVideoPreparedListener != null) {
                    VDVideoViewController controller = VDVideoViewController.getInstance(mContext);
                    if (controller != null)
                        mOnVDVideoPreparedListener.onVDVideoPrepared(controller.getCurrentVideo());
                }
            }
        });
    }

    /**
     * 静帧广告，通知执行广告接口回调，用于广告图片的替换
     */
    public void notifyFrameADListenerPrepared() {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (mFrameADListener != null) {
                    VDVideoViewController controller = VDVideoViewController.getInstance(mContext);
                    if (controller != null)
                        mFrameADListener.onFrameADPrepared(controller.getCurrentVideo());
                }
            }
        });
    }

    /**
     * 广告被点击，通知外部事件
     */
    public void notifyInsertADListenerClick() {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (mInsertADListener != null) {
                    VDVideoViewController controller = VDVideoViewController.getInstance(mContext);
                    if (controller != null)
                        mInsertADListener.onInsertADClick(controller.getCurrentVideo());
                }
            }
        });
    }

    /**
     * 跳过广告被点击，通知外部事件
     */
    public void notifyInsertADListenerStepout() {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (mInsertADListener != null) {
                    VDVideoViewController controller = VDVideoViewController.getInstance(mContext);
                    if (controller != null)
                        mInsertADListener.onInsertADStepOutClick(controller.getCurrentVideo());
                }
            }
        });
    }

    /**
     * 通知执行播放列表接口回调
     * 
     * @param info
     *            当前播放的info
     * @param p
     *            正片的序号（不包含贴片广告）
     */
    public void notifyPlaylistListener(final VDVideoInfo info, final int p) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (mPlaylistListener != null) {
                    mPlaylistListener.onPlaylistClick(info, p);
                }
            }
        });
    }

    /**
     * 通知执行播放类型切换接口回调
     * 
     * @param info
     * @param p
     */
    public void notifySwitchPlayerListener(final VDVideoInfo info, final int p) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (mOnVDPlayerTypeSwitchListener != null) {
                    mOnVDPlayerTypeSwitchListener.OnVDVideoPlayerTypeSwitch(info, p);
                }
            }
        });
    }

    public void notifyCompletionListener(final VDVideoInfo info, final int status) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (mOnVDVideoCompletionListener != null) {
                    mOnVDVideoCompletionListener.onVDVideoCompletion(info, status);
                }
            }
        });
    }

    public void ontifyErrorListener(final VDVideoInfo info, final int errWhat, final int errExtra) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (mOnVDVideoErrorListener != null) {
                    mOnVDVideoErrorListener.onVDVideoError(info, errWhat, errExtra);
                }
            }
        });
    }

    public void notifyInfoListener(final VDVideoInfo info, final int infWhat) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (mOnVDVideoInfoListener != null) {
                    mOnVDVideoInfoListener.onVDVideoInfo(info, infWhat);
                }
            }
        });
    }

    public VDVideoExtListeners(Context context) {
        super();
        mContext = context;
    }

    /**
     * 清除所有监听
     */
    public void clear() {
        mHandler.removeCallbacksAndMessages(null);
        mFrameADListener = null;
        mPlaylistListener = null;
        mOnVDPlayerTypeSwitchListener = null;
        mOnVDVideoCompletionListener = null;
        mOnVDVideoErrorListener = null;
        mOnVDVideoPreparedListener = null;
        mOnVDVideoInsertADEndListener = null;
        mOnVDVideoPlayerChangeListener = null;
    }
}
