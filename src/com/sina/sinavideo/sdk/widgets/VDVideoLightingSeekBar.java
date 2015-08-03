/**
 * 调节亮度的seekbar，竖直的 VDVideoLightingSeekBar
 * 
 * @author seven
 */
package com.sina.sinavideo.sdk.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.SeekBar;

import com.sina.sinavideo.sdk.VDVideoViewController;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnLightingChangeListener;
import com.sina.video_playersdkv2.R;

public class VDVideoLightingSeekBar extends SeekBar implements VDBaseWidget, SeekBar.OnSeekBarChangeListener,
        OnLightingChangeListener {

    @SuppressLint("nouse")
    private final static String TAG = "";
    @SuppressLint("nouse")
    private Context mContext = null;
    private boolean mIsDragging = false;

    public VDVideoLightingSeekBar(Context context) {
        super(context);
        init(context);
    }

    public VDVideoLightingSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);

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

        init(context);
    }

    private void init(Context context) {
        mContext = context;
        setMax(1000);
        registerLisetner();

    }

    @Override
    public void reset() {
        VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
        if (null == controller)
            return;
        controller.addOnLightingChangeListener(this);
        float light = controller.getCurrLightingSetting();
        setProgress((int) (light * 1000));
    }

    @Override
    public void hide() {
        // VDVideoViewController.getInstance().removeOnLightingChangeListener(this);
    }

    private void registerLisetner() {
        setOnSeekBarChangeListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
        if (controller == null)
            return;
        controller.dragLightingTo((float) progress / 1000, mIsDragging);
        if (mIsDragging) {
            controller.notifyRemoveAndHideDelayMoreOprationPanel();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar arg0) {
        mIsDragging = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar arg0) {
        mIsDragging = false;
        VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
        if (controller != null)
            controller.notifyRemoveAndHideDelayMoreOprationPanel();
    }

    @Override
    public void onLightingChange(float curr) {
        if (!mIsDragging) {
            setProgress((int) (curr * 1000));
            // onSizeChanged(getWidth(), getHeight(), 0, 0);
        }
    }

}
