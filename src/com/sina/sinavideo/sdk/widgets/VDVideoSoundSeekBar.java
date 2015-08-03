package com.sina.sinavideo.sdk.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.SeekBar;

import com.sina.sinavideo.sdk.VDVideoViewController;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnSetSoundListener;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnSoundChangedListener;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnSoundVisibleListener;
import com.sina.sinavideo.sdk.utils.VDLog;
import com.sina.sinavideo.sdk.utils.VDPlayerSoundManager;
import com.sina.sinavideo.sdk.utils.VDVerticalSeekBar;
import com.sina.video_playersdkv2.R;

/**
 * 视频音量拖动条
 * 
 * @author seven
 * 
 */
public class VDVideoSoundSeekBar extends VDVerticalSeekBar implements VDBaseWidget, OnSoundChangedListener,
        OnSoundVisibleListener, SeekBar.OnSeekBarChangeListener, OnSetSoundListener {

    private Context mContext = null;
    private final static String TAG = "VDVideoSoundSeekBar";

    public VDVideoSoundSeekBar(Context context) {
        super(context);
        init(context, null);
    }

    public VDVideoSoundSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void initVolume(Context context) {
        int maxVolume = VDPlayerSoundManager.getMaxSoundVolume(context);
        int progress = VDPlayerSoundManager.getCurrSoundVolume(context);
        setMax(maxVolume);
        setProgress(progress);
        VDLog.e(TAG, "max:" + maxVolume + ",progress:" + progress);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        // 滑动条样式
        TypedArray typedArr = context.obtainStyledAttributes(attrs, new int[]{android.R.attr.progressDrawable,
                android.R.attr.thumb});
        if (typedArr != null) {
            Drawable drawable = typedArr.getDrawable(0);
            if (drawable == null) {
                setProgressDrawable(context.getResources().getDrawable(R.drawable.play_soundseekbar_background));
            }
        } else {
            setProgressDrawable(context.getResources().getDrawable(R.drawable.play_soundseekbar_background));
        }

        // 焦点样式
        if (typedArr != null) {
            Drawable drawable = typedArr.getDrawable(1);
            if (drawable == null) {
                setThumb(context.getResources().getDrawable(R.drawable.play_ctrl_sound_ball));
            }
        } else {
            setThumb(context.getResources().getDrawable(R.drawable.play_ctrl_sound_ball));
        }

        if (typedArr != null) {
            typedArr.recycle();
        }

        registerListener();
    }

    private void registerListener() {
        setOnSeekBarChangeListener(this);
        VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
        if(controller!=null)controller.addOnSoundChangedListener(this);
        if(controller!=null)controller.addOnSetSoundListener(this);
        if(controller!=null)controller.addOnSoundVisibleListener(this);
    }

    @Override
    public void reset() {
        // VDVideoViewController.getInstance().addOnSoundChangedListener(this);
        initVolume(mContext);
    }

    @Override
    public void hide() {
        // VDVideoViewController.getInstance().removeOnSoundChangedListener(this);
    }

    @Override
    public void onSoundChanged(int currVolume) {
        // LogS.e(TAG, "onSoundChanged   currVolume:" + currVolume +
        // ",mIsDragging : " + mIsDragging);
        if (!mIsDragging) {
            setProgress(currVolume);
            onSizeChanged(getWidth(), getHeight(), 0, 0);
        }
    }

    @Override
    public void onSoundVisible(boolean isVisible) {
        // LogS.i(TAG, "onSoundVisible : " );
        // if (isVisible) {
        // setVisibility(View.VISIBLE);
        // } else {
        // setVisibility(View.GONE);
        // }
        // setProgress(getProgress());
        onSizeChanged(getWidth(), getHeight(), 0, 0);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        VDLog.e(TAG, "onProgressChanged   progress:" + progress + ",mIsDragging : " + mIsDragging);
        VDPlayerSoundManager.dragSoundSeekTo(mContext, progress, mIsDragging);
        if (mIsDragging) {
        	VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
        	if(controller!=null)controller.notifySoundSeekBarVisible(true);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar arg0) {
    	VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
    	if(controller!=null)controller.notifySoundSeekBarVisible(true);
    }

    @Override
    public void onStopTrackingTouch(SeekBar arg0) {
    }

    @Override
    public void onSoundSeekBarVisible(boolean isVisible) {
        // LogS.i(TAG, "onSoundSeekBarVisible : " );
        // if (isVisible) {
        // setVisibility(View.VISIBLE);
        // } else {
        // setVisibility(View.GONE);
        // }
        // setProgress(getProgress());
        onSizeChanged(getWidth(), getHeight(), 0, 0);
    }

    @Override
    public void onSetCurVolume(int currVolume) {
        // LogS.e(TAG, "DLNA onSetCurVolume   currVolume:" + currVolume );
        setProgress(currVolume);
        onSizeChanged(getWidth(), getHeight(), 0, 0);
    }

    @Override
    public void onSetMaxVolume(int maxVolume) {
        // LogS.e(TAG, "DLNA onSetMaxVolume   maxVolume:" + maxVolume );
        if (getMax() != maxVolume) {
            setMax(maxVolume);
        }
    }

}
