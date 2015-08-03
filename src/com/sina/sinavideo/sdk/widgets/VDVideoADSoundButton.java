package com.sina.sinavideo.sdk.widgets;

import com.sina.sinavideo.sdk.VDVideoViewController;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnSoundChangedListener;
import com.sina.sinavideo.sdk.utils.VDPlayerSoundManager;
import com.sina.video_playersdkv2.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

/**
 * 广告用的音量按钮，静音开关方式，点击后或者开启或者关闭声音
 * 
 * @author sunxiao
 * 
 */
public class VDVideoADSoundButton extends ImageButton implements VDBaseWidget, OnSoundChangedListener {

    private int mResID = -1;
    private int mSilentResID = -1;
    private Context mContext = null;

    public VDVideoADSoundButton(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        registerListeners();
        init(context);
    }

    public VDVideoADSoundButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        // 支持自定义背景
        TypedArray typedArr = context.obtainStyledAttributes(attrs, new int[]{android.R.attr.background});
        if (typedArr != null) {
            mResID = typedArr.getResourceId(0, -1);
            if (mResID == -1) {
                mResID = R.drawable.play_ctrl_volume;
            }
            typedArr.recycle();
        }
        // 静音部分的定义
        typedArr = context.obtainStyledAttributes(attrs, R.styleable.VDVideoADSoundButton);
        mSilentResID = R.drawable.ad_silent_selcetor;
        if (typedArr != null) {
            for (int i = 0; i < typedArr.getIndexCount(); i++) {
                if (typedArr.getIndex(i) == R.styleable.VDVideoADSoundButton_adSoundSeekSilent) {
                    // 自定义背景的时候，换静音按钮
                    mSilentResID = typedArr.getResourceId(i, -1);
                }
            }
            typedArr.recycle();
        }

        registerListeners();
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        changeBackground(VDPlayerSoundManager.isMuted(context));
    }

    private void changeBackground(boolean isSilent) {
        if (isSilent)
            setBackgroundResource(mSilentResID);
        else
            setBackgroundResource(mResID);
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
                boolean isMuted = VDPlayerSoundManager.isMuted(mContext);
                isMuted = !isMuted;
                VDPlayerSoundManager.setMute(mContext, isMuted, false);
                changeBackground(isMuted);
            }

        });

    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub
    	VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
    	if(null!=controller)controller.addOnSoundChangedListener(this);
    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub
    	VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
    	if(null!=controller)controller.removeOnSoundChangedListener(this);
    }

    @Override
    public void onSoundChanged(int currVolume) {
        // TODO Auto-generated method stub
        int currSoundNum = VDPlayerSoundManager.getCurrSoundVolume(mContext);
        if (currSoundNum > 0) {
            changeBackground(false);
        } else {
            changeBackground(true);
        }
    }

}
