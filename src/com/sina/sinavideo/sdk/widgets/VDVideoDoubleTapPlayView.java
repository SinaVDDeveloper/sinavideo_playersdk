package com.sina.sinavideo.sdk.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.sina.sinavideo.sdk.VDVideoViewController;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnVideoDoubleTapListener;
import com.sina.video_playersdkv2.R;

/**
 * 双击播放/暂停动画view
 * 
 * @author liuqun
 * 
 */
public class VDVideoDoubleTapPlayView extends ImageView implements VDBaseWidget, OnVideoDoubleTapListener {

    private Context mContext = null;

    public VDVideoDoubleTapPlayView(Context context) {
        super(context);
        mContext = context;
        registerListeners();
    }

    public VDVideoDoubleTapPlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        // 判断是否有新定义的背景图片，没有则使用sina视频的默认图片
        // TypedArray typedArr = context.obtainStyledAttributes(attrs,
        // new int[] { android.R.attr.background });
        // if (typedArr != null) {
        // int resouceID = typedArr.getResourceId(0, -1);
        // if (resouceID == -1) {
        // setBackgroundResource(R.drawable.play_ctrl_back);
        // }
        // typedArr.recycle();
        // } else {
        // setBackgroundResource(R.drawable.play_ctrl_back);
        // }

        registerListeners();
    }

    @Override
    public void reset() {
        Log.i("VDVideoDoubleTapPlayView", "reset");
        setVisibility(GONE);
        VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
        if(null!=controller)controller.addOnVideoDoubleTapListener(this);
    }

    @Override
    public void hide() {
        Log.i("VDVideoDoubleTapPlayView", "hide");
        VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
        if(null!=controller)controller.removeOnVideoDoubleTapListener(this);
    }

    /**
     * 为自己注册事件
     */
    private void registerListeners() {
        // VDVideoViewController.getInstance().addOnVideoDoubleTapListener(this);
    }

    @Override
    public void onDoubleTouch() {
        Log.i("VDVideoDoubleTapPlayView", "onDoubleTouch");
        VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
        if (controller!=null && controller.getIsPlaying()) {
            setBackgroundResource(R.drawable.play_ctrl_play);
        } else {
            setBackgroundResource(R.drawable.play_ctrl_pause);
        }
        Animation a = AnimationUtils.loadAnimation(mContext, R.anim.zoom_out);

        a.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // cancelHide();
                // sendHideDelay(sDefaultTimeout);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // animIsShowing = false;
                clearAnimation();
                setVisibility(GONE);
            }
        });
        setVisibility(View.VISIBLE);
        startAnimation(a);
    }

}
