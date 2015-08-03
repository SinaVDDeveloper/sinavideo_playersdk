package com.sina.sinavideo.sdk.widgets;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

import com.sina.sinavideo.sdk.VDVideoViewController;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnSoundChangedListener;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnSoundVisibleListener;
import com.sina.sinavideo.sdk.utils.VDPlayerSoundManager;
import com.sina.video_playersdkv2.R;

/**
 * 音量控制按钮部分，点击打开音量控制拖拉条
 * 
 * @author seven
 */
public class VDVideoSoundSeekButton extends ImageButton implements VDBaseWidget, OnSoundChangedListener,
        OnSoundVisibleListener {

    private Context mContext = null;
    private int mContainerID = -1;

    // 普通背景
    private int mResID = -1;
    // 静音情况下的背景
    private int mSilentResID = -1;

    public VDVideoSoundSeekButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        mContext = context;
        // 支持自定义背景
        TypedArray typedArr = context.obtainStyledAttributes(attrs, new int[]{android.R.attr.background});

        mResID = R.drawable.play_ctrl_volume;
        if (typedArr != null) {
            mResID = typedArr.getResourceId(0, -1);
            if (mResID == -1) {
                mResID = R.drawable.play_ctrl_volume;
            }
            typedArr.recycle();
        }
        setBackgroundResource(mResID);

        // 指定相应的音量控制容器类
        typedArr = context.obtainStyledAttributes(attrs, R.styleable.VDVideoSoundSeekButton);
        mSilentResID = R.drawable.ad_silent_selcetor;
        if (typedArr != null) {
            for (int i = 0; i < typedArr.getIndexCount(); i++) {
                if (typedArr.getIndex(i) == R.styleable.VDVideoSoundSeekButton_soundSeekContainer) {
                    mContainerID = typedArr.getResourceId(i, -1);
                } else if (typedArr.getIndex(i) == R.styleable.VDVideoSoundSeekButton_soundSeekSilent) {
                    // 自定义背景的时候，换静音按钮
                    mSilentResID = typedArr.getResourceId(i, -1);
                }
            }
            typedArr.recycle();
        }

        registerListeners();
    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub
        VDVideoViewController.getInstance(this.getContext()).addOnSoundChangedListener(this);
    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub
        // VDVideoViewController.getInstance().removeOnSoundChangedListener(this);
    }

    /**
     * 为自己注册事件
     */
    private void registerListeners() {

        // click事件
        this.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 先测试添加于此
                if (mContainerID == -1) {
                    return;
                }
                View container = ((Activity) mContext).findViewById(mContainerID);
                if (container != null) {
                    if (container.getVisibility() == View.GONE) {
                        // container.setVisibility(View.VISIBLE);
                    	VDVideoViewController controller = VDVideoViewController.getInstance(VDVideoSoundSeekButton.this.getContext());
                    	if(controller!=null)controller.notifySoundSeekBarVisible(true);
                    } else {
                        // container.setVisibility(View.GONE);
                    	VDVideoViewController controller = VDVideoViewController.getInstance(VDVideoSoundSeekButton.this.getContext());
                    	if(controller!=null)controller.notifySoundSeekBarVisible(false);
                    }
                }
            }

        });

    }

    @Override
    public void onSoundChanged(int soundIndex) {
        if (soundIndex <= 0) {
            setBackgroundResource(mSilentResID);
        } else {
            setBackgroundResource(mResID);
        }
    }

    @Override
    public void onSoundVisible(boolean isVisible) {
        int soundIndex = VDPlayerSoundManager.getCurrSoundVolume(mContext);
        if (soundIndex <= 0) {
            setBackgroundResource(mSilentResID);
        } else {
            setBackgroundResource(mResID);
        }
        if (isVisible) {
            setPressed(true);
        } else {
            setPressed(false);
        }
    }

    @Override
    public void onSoundSeekBarVisible(boolean isVisible) {

    }
}
