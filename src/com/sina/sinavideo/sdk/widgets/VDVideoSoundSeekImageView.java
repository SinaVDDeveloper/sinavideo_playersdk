/**
 * 音量控制，小喇叭标志，在静音状态会显示不同的图标
 * 
 * @author seven
 */
package com.sina.sinavideo.sdk.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.sina.sinavideo.sdk.VDVideoViewController;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnSoundChangedListener;
import com.sina.video_playersdkv2.R;

public class VDVideoSoundSeekImageView extends ImageView implements VDBaseWidget, OnSoundChangedListener {

    private Drawable mMuteDrawable = null;
    private Drawable mSrcDrawable = null;

    public VDVideoSoundSeekImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        TypedArray typedArr = context.obtainStyledAttributes(attrs, R.styleable.VDVideoSoundSeekImageView);
        if (typedArr != null) {
            for (int i = 0; i < typedArr.getIndexCount(); i++) {
                if (typedArr.getIndex(i) == R.styleable.VDVideoSoundSeekImageView_muteSrc) {
                    mMuteDrawable = typedArr.getDrawable(i);
                    if (mMuteDrawable == null) {
                        mMuteDrawable = context.getResources().getDrawable(R.drawable.play_ctrl_sound_gestrue_silent);
                    }
                }
            }
            if (mMuteDrawable == null) {
                mMuteDrawable = context.getResources().getDrawable(R.drawable.play_ctrl_sound_gestrue_silent);
            }
            typedArr.recycle();
        }

        typedArr = context.obtainStyledAttributes(attrs, new int[]{android.R.attr.src});
        if (typedArr != null) {
            mSrcDrawable = typedArr.getDrawable(0);
            if (mSrcDrawable == null) {
                mSrcDrawable = context.getResources().getDrawable(R.drawable.play_ctrl_sound_gestrue);
            }
            typedArr.recycle();
        }
    }

    @Override
    public void onSoundChanged(int currVolume) {
        // TODO Auto-generated method stub
        if (currVolume <= 0) {
            setImageDrawable(mMuteDrawable);
        } else {
            setImageDrawable(mSrcDrawable);
        }
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
    	if(null!=controller)controller.addOnSoundChangedListener(this);
    }

}
