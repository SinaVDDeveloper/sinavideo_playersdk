package com.sina.sinavideo.sdk.dlna;

import java.util.ArrayList;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.sina.sinavideo.dlna.SinaDLNA;
import com.sina.sinavideo.dlna.SinaDLNA.SinaDLNAListener;
import com.sina.sinavideo.sdk.utils.VDVideoFullModeController;

/**
 * DLNA逻辑处理类
 * 
 * @author sina
 *
 */
public class DLNAController {

    // private static final int DLNA_PLAYSTATE_INIT = 0;
    private static final int DLNA_PLAYSTATE_OPEN = 1; // 设置播放URL
    private static final int DLNA_PLAYSTATE_PLAY = 2; // 播放中
    private static final int DLNA_PLAYSTATE_PAUSE = 3; // 暂停播放
    private static final int DLNA_PLAYSTATE_STOP = 4; // 停止播放
    public ArrayList<MRContent> mData;
    private ArrayList<String> mList;
    public String mPlayUrl;
    public long mSeekPosition;
    public long mPosition;
    public static long mTmpPosition;
    public int mVolume;
    public boolean mIsLive = false;
    public boolean mDoSeek = false;
    public boolean mStoped = false;
    private boolean mGetPositioning = false;
    private boolean mGetVolume = false;
    private SinaDLNA mDLNA;
    public boolean mPlaying;
    private Context mContext;
    private boolean mIsInit = false;
    private int mPlaystate;
    private MySinaDLNAListener mMySinaDLNAListener;
    public int mDuration; // msec
    private String mTransportState; // 播放设备当前状态
    public float mProgressRate;

    private int POSITION_TIMER = 1000;
    private Handler mHandler = new Handler();
    private Handler mTimerhandler = new Handler(Looper.getMainLooper());
    private Runnable mTimerRunnable = new Runnable() {

        @Override
        public void run() {
            try {
                if (mPlaying) {
                    getPosition();
                    getVolume();
                    if (mDuration <= 0) {
                        getDuration();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public static boolean mIsDLNA = false;

    private static DLNAController mDLNAWrapper;

    private DLNAController(Context contxt) {
        mContext = contxt.getApplicationContext();
        mDLNA = new SinaDLNA(mContext);
        mData = new ArrayList<MRContent>();
        mList = new ArrayList<String>();
    }

    private SinaDLNA getDLNA() {
        if (mDLNA == null) {
            mDLNA = new SinaDLNA(mContext);
        }
        return mDLNA;
    }

    public static DLNAController getInstance(Context contxt) {
        if (mDLNAWrapper == null) {
            mDLNAWrapper = new DLNAController(contxt);
        }
        return mDLNAWrapper;
    }

    public void setUp() {
        if (mDLNA != null) {
            if (!mIsInit) {
                Log.i("DLNA", "SinaDLNA setUp ");
                mMySinaDLNAListener = new MySinaDLNAListener();
                mDLNA.setSinaDLNAListener(mMySinaDLNAListener);
                mDLNA.setup();
                mIsInit = true;
            }
        }
    }

    public int setMediaRender(String uuid) {
        Log.d("DLNA", "setMediaRender: " + uuid);
        return getDLNA().setMediaRender(uuid);
    }

    public void onClickPlay() {
        Log.d("DLNA", "onClickPlay mPlaying = " + mPlaying);
        if (mPlaying) {
            pause();
        } else {
            play();
        }
    }

    public void open(String url) {
        if (url != null) {
            mStoped = false;
            mPosition = 0;
            mDuration = -1;
            mPlayUrl = url;
            getDLNA().stop();
            Log.d("DLNA", "open() open:" + mPlayUrl + " seek : " + mPosition);
            // String dlnaUrl = changeUrl(mPlayUrl);
            getDLNA().open(mPlayUrl, SinaDLNA.buildDIDL(mPlayUrl));
            // getDLNA().seek((int) mPosition);
            getDLNA().getVolume();
        } else {
            Log.d("DLNA", "open() open mPlayUrl null");
        }

    }

    //
    public String changeUrl(String url) {
        if (url != null) {
            if (url.contains("edge.v.iask.com")) {
                int index = url.indexOf("mp4");
                String ret = url.substring(0, index + 3);
                return ret;
            } else {
                return url;
            }
        }

        return null;
    }

    public void open() {
        open(mPlayUrl);
    }

    private void HandleOnOpen() {
        if (mPlaystate != DLNA_PLAYSTATE_OPEN) {
            mPlaystate = DLNA_PLAYSTATE_OPEN;
            play();
        }
    }

    public void pause() {
        Log.d("DLNA", "pause() play:" + mPlayUrl);
        getDLNA().pause();
    }

    private void play() {
        Log.d("DLNA", "play() play:" + mPlayUrl);
        getDLNA().play();
    }

    private void HandleOnPlay() {
        if (mPlaystate != DLNA_PLAYSTATE_PLAY) {
            mPlaying = true;
            // mBtnPlay.setImageDrawable(getResources().getDrawable(R.drawable.stop));
            DLNAEventListener.getInstance().notifyPlayStateChanged(true);
            mPlaystate = DLNA_PLAYSTATE_PLAY;
            if (!mIsLive) {
                getDuration();
                getPosition();
                mTimerhandler.postDelayed(mTimerRunnable, POSITION_TIMER);
            }
        }
    }

    private void HandleOnPause() {
        if (mPlaystate != DLNA_PLAYSTATE_PAUSE) {
            mPlaying = false;
            // mBtnPlay.setImageDrawable(getResources().getDrawable(R.drawable.play));
            DLNAEventListener.getInstance().notifyPlayStateChanged(false);
            if (!mIsLive)
                mTimerhandler.removeCallbacks(mTimerRunnable);
            mPlaystate = DLNA_PLAYSTATE_PAUSE;
        }
    }

    private void HandleOnStop() {
        if (mPlaystate != DLNA_PLAYSTATE_STOP) {
            mPlaying = false;
            // mBtnPlay.setImageDrawable(getResources().getDrawable(R.drawable.play));
            if (!mIsLive)
                mTimerhandler.removeCallbacks(mTimerRunnable);
            mPlaystate = DLNA_PLAYSTATE_STOP;
        }
    }

    public void seek(final long pos) {
        Log.d("DLNA", "seek :" + pos);
        getDLNA().seek((int) pos);
    }

    public void stop() {
        Log.d("DLNA", "stop ");
        mStoped = true;
        mTimerhandler.removeCallbacks(mTimerRunnable);
        getDLNA().stop();
    }

    public void release() {
        if (mDLNA != null) {
            // mDLNA.release();
            mTimerhandler.removeCallbacks(mTimerRunnable);
            // mDLNA = null;
            // mDLNAWrapper = null;
            mIsDLNA = false;
        }
    }

    public void setVolume(final int vol) {
        Log.d("DLNA", "setVolume vol : " + vol);
        getDLNA().setVolume(vol);
    }

    private void getDuration() {
        Log.d("DLNA", "getDuration:");
        getDLNA().getDuration();
    }

    private void getPosition() {
        Log.d("DLNA", "getPosition:");
        if (!mGetPositioning) {
            mGetPositioning = true;
        }
        getDLNA().getPosition();
    }

    public int getVolumeMax() {
        if (mDLNA != null) {
            Log.d("DLNA", "getVolumeMax vol:" + mDLNA.getVolumeMax());
            return mDLNA.getVolumeMax();
        }
        return -1;
    }

    public void getVolume() {
        if (!mGetVolume) {
            getDLNA().getVolume();
            mGetVolume = true;
        }
    }

    public int getVolumeMin() {
        if (mDLNA != null) {
            Log.d("DLNA", "getVolumeMin vol:" + mDLNA.getVolumeMin());
            return mDLNA.getVolumeMin();
        }
        return -1;
    }

    private void setProgressRate() {
        boolean isPortrait = VDVideoFullModeController.getInstance().getIsPortrait();
        long duration = mDuration;
        if (duration < 10 * 60 * 1000) {
            if (isPortrait) {
                mProgressRate = 60 * 1000f / duration;
            } else {
                mProgressRate = 90 * 1000f / duration;
            }

        } else if (duration < 20 * 60 * 1000) {
            if (isPortrait) {
                mProgressRate = 2 * 60 * 1000f / duration;
            } else {
                mProgressRate = 150 * 1000f / duration;
            }
        } else {
            if (isPortrait) {
                mProgressRate = 5 * 60 * 1000f / duration;
            } else {
                mProgressRate = 460 * 1000f / duration;
            }
        }
    }

    public class MySinaDLNAListener implements SinaDLNAListener {

        @Override
        public void onMediaRenderAdded(final String uuid, final String name) {
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    if (!mList.contains(uuid)) {
                        MRContent mr = new MRContent();
                        mr.setUuid(uuid);
                        mr.setName(name);
                        mData.add(mr);
                        mList.add(uuid);
                        DLNAEventListener.getInstance().notifyMediaRenderAdded(uuid, name);
                    }
                }
            });

        }

        @Override
        public void onMediaRenderRemoved(final String uuid, final String name) {
            Log.i("DLNA", "controller onMediaRenderRemoved : uuid = " + uuid + " , name = " + name);
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    for (int i = 0; i < mData.size(); i++) {
                        if (mData.get(i) != null && mData.get(i).getUuid().equals(uuid)) {
                            mData.remove(mData.get(i));
                            break;
                        }
                    }
                    mList.remove(uuid);
                    DLNAEventListener.getInstance().notifyMediaRenderRemoved(uuid, name);
                }
            });
        }

        @Override
        public void onMediaRenderStateChanged(String name, String value) {
            Log.e("DLNA", "onMediaRenderStateChanged name : " + name + " , value : " + value);
            DLNAEventListener.getInstance().notifyDLNAMediaRenderStateChange(name, value);
            if (SinaDLNA.TRANSPORT_STATE.equalsIgnoreCase(name)) {
                if (SinaDLNA.TRANSPORT_STATE_PLAYING.equalsIgnoreCase(value)) {
                    mPlaying = true;
                    HandleOnPlay();
                } else if (SinaDLNA.TRANSPORT_STATE_PAUSED_PLAYBACK.equalsIgnoreCase(value)) {
                    mPlaying = false;
                    HandleOnPause();
                } else if (SinaDLNA.TRANSPORT_STATE_STOPPED.equalsIgnoreCase(value)) {
                    mPlaying = false;
                    HandleOnStop();
                }
                if (mPlaystate != DLNA_PLAYSTATE_PLAY && mPlaystate != DLNA_PLAYSTATE_PAUSE
                        && mPlaystate != DLNA_PLAYSTATE_STOP) {
                    // log("not in play state");
                    return;
                }
                if (SinaDLNA.TRANSPORT_STATE_UNKNOWN.equals(mTransportState)
                        && !SinaDLNA.TRANSPORT_STATE_PLAYING.equalsIgnoreCase(value)) {
                    // log("transport state not equal TRANSPORT_STATE_PLAYING");
                    return;
                }
                if (value.equalsIgnoreCase(mTransportState)) {
                    // log("mr transport state unchanged , cur state=" +
                    // mTransportState +", notify state="+value.toUpperCase());
                } else {
                    // log("mr transport state changed , cur state=" +
                    // mTransportState +", notify state="+value.toUpperCase());
                    mTransportState = value;
                    if (SinaDLNA.TRANSPORT_STATE_TRANSITIONIN.equalsIgnoreCase(mTransportState)) {
                        // 不做任何动作
                    } else if (SinaDLNA.TRANSPORT_STATE_PLAYING.equalsIgnoreCase(mTransportState)) {
                        if (mPlaystate == DLNA_PLAYSTATE_PAUSE) { // 播放暂停时，设备上继续播放
                            // HandleOnPlay();
                        }
                    } else if (SinaDLNA.TRANSPORT_STATE_PAUSED_PLAYBACK.equalsIgnoreCase(mTransportState)) {
                        if (mPlaystate == DLNA_PLAYSTATE_PLAY) { // 播放中时，设备上暂停播放
                            // HandleOnPause();
                        }
                    } else if (SinaDLNA.TRANSPORT_STATE_STOPPED.equals(mTransportState)) {
                        if (mPlaystate == DLNA_PLAYSTATE_PLAY || mPlaystate == DLNA_PLAYSTATE_PAUSE) {
                            // HandleOnStop();
                        }
                    } else if (SinaDLNA.TRANSPORT_STATE_NO_MEDIA_PRESENT.equals(mTransportState)) {
                        // 不做任何动作
                    } else if (SinaDLNA.TRANSPORT_STATE_CUSTOM.equals(mTransportState)) {
                        // 不做任何动作
                    }
                }
            } else if (SinaDLNA.CURRENTMEDIADURATION.equalsIgnoreCase(name)
                    || SinaDLNA.CURRENTTRACKDURATION.equalsIgnoreCase(name)) {
                // log("dutation state changed,mDuration="+mDuration);
                if (!mIsLive) {
                    if (mDuration == 0) { // 未取得时长
                        // mDuration = formatToMSec(value);
                        // if(mDuration>0){
                        // mDurationStr = formatFromMSec(mDuration);
                        // mPositionStr = formatFromMSec(mPosition);
                        // mCurTimeView.setText(mPositionStr + "/" +
                        // mDurationStr);
                        // mSeekBar.setMax((int)(mDuration / 1000) - 1);
                        // }
                        // log("mDuration =" + mDuration +
                        // ",mDurationStr="+mDurationStr);
                    }
                }
            }
        }

        @Override
        public void onOpen(int result) {
            Log.d("DLNA", "onOpen result:" + result);
            if (result == 0) {
                HandleOnOpen();
                DLNAController.mIsDLNA = true;
                DLNAEventListener.getInstance().notifyDLNAVisibleSwitch(true);
                DLNAEventListener.getInstance().notifyDLNAListSwitch(false);
                DLNAEventListener.getInstance().notifyDLNAMediaRenderOpened(true);
                // Log.e("DLNA", "onOpen seek:" + mPosition);
                // getDLNA().seek((int) mPosition);
            } else {
                mDoSeek = false;
                DLNAEventListener.getInstance().notifyDLNAListSwitch(false);
                Toast.makeText(mContext, "打开视频失败,继续在手机上播放", Toast.LENGTH_SHORT).show();
                DLNAController.mIsDLNA = false;
            }
        }

        @Override
        public void onPlay(int result) {
            Log.d("DLNA", "onPlay result:" + result);
            if (result == 0) {
                if (mDoSeek) {
                    seek(mSeekPosition);
                    mSeekPosition = 0;
                    mDoSeek = false;
                }
                HandleOnPlay();
            } else {
                Toast.makeText(mContext, "播放视频失败", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onPause(int result) {
            Log.d("DLNA", "onPause result:" + result);
            if (result == 0) {
                HandleOnPause();
            } else {
                // Toast.makeText(mContext, "暂停视频失败",
                // Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onStop(int result) {
            Log.d("DLNA", "onStop result:" + result);
            if (result == 0) {
                HandleOnStop();
            } else {
                // Toast.makeText(mContext, "停止视频失败",
                // Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onSeek(int result) {
            Log.d("DLNA", "onSeek result:" + result);
            if (result == 0) {

            } else {
                // Toast.makeText(mContext, "seek视频失败",
                // Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onSetMute(int result) {
            // TODO Auto-generated method stub
            Log.d("DLNA", "onSetMute result:" + result);
        }

        @Override
        public void onGetMute(int result, boolean mute) {
            // TODO Auto-generated method stub
            Log.d("DLNA", "onGetMute result:" + result);
        }

        @Override
        public void onSetVolume(int result) {
            // TODO Auto-generated method stub
            Log.d("DLNA", "onSetVolume result:" + result);
        }

        @Override
        public void onGetVolume(int result, int vol) {
            Log.d("DLNA", "onGetVolume vol:" + vol);
            mGetVolume = false;
            if (mStoped) {
                return;
            }
            mVolume = vol;
            DLNAEventListener.getInstance().notifyOnGetVolume(vol);
        }

        @Override
        public void onGetDuration(int result, int msec) {
            Log.e("DLNA", "onGetDuration msec :" + msec);
            if (mStoped) {
                return;
            }
            mDuration = msec;
            setProgressRate();
            DLNAEventListener.getInstance().notifyDLNADuration(msec);
        }

        @Override
        public void onGetPosition(int result, int msec) {
            Log.d("DLNA", "onGetPosition result:" + result + " , msec = " + msec + " , mDuration = " + mDuration);
            mGetPositioning = false;
            if (result == 0) {
                if (!mIsLive) {
                    if (mStoped) {
                        return;
                    }
                    mPosition = msec + 1000; // 会慢一秒实时
                    // mPositionStr = formatFromMSec(mPosition);
                    // mCurTimeView.setText(mPositionStr + "/" + mDurationStr);
                    mTimerhandler.postDelayed(mTimerRunnable, POSITION_TIMER);
                    if (mDuration > 0) {
                        DLNAEventListener.getInstance().notifyDLNAProgressUpdate(msec + 1000, mDuration);
                    }
                    // mSeekBar.setProgress((int)(mPosition/1000));
                }
            } else {
                // Toast.makeText(mContext, "获得当前位置失败",
                // Toast.LENGTH_SHORT).show();
            }
        }

    }

}
