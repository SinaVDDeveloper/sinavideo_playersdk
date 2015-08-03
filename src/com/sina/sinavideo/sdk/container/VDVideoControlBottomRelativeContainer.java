package com.sina.sinavideo.sdk.container;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.sina.sinavideo.sdk.VDVideoViewController;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnKeyEventListener;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnScreenTouchListener;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnShowHideBottomControllerListener;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnShowHideControllerListener;
import com.sina.sinavideo.sdk.widgets.VDBaseWidget;
import com.sina.video_playersdkv2.R;

/**
 * 控制条容器类，单击隐藏等操作
 * 
 * @author liuqun
 */
public class VDVideoControlBottomRelativeContainer extends VDVideoControlRelativeContainer implements VDBaseWidget,
        OnScreenTouchListener, OnShowHideControllerListener, OnKeyEventListener, OnShowHideBottomControllerListener {

    private Animation mShowAnim;
    private Animation mHideAnim;

    // private boolean isAnimating;
    public VDVideoControlBottomRelativeContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    public VDVideoControlBottomRelativeContainer(Context context) {
        super(context);
        init(context);
    }

    private void init(Context ctt) {
    	VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
    	if(null!=controller)controller.addOnKeyEventListener(this);
    	if(null!=controller)controller.addOnShowHideControllerListener(this);
        mShowAnim = AnimationUtils.loadAnimation(ctt, R.anim.down_to_up_translate);
        mShowAnim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                setVisibility(View.VISIBLE);
                VDVideoViewController controller = VDVideoViewController.getInstance(VDVideoControlBottomRelativeContainer.this.getContext());
                if(null!=controller){
	                controller.setBottomPannelHiding(false);
	                controller.notifyHideVideoList();
	                controller.notifyHideMoreOprationPanel();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
            	VDVideoViewController controller = VDVideoViewController.getInstance(VDVideoControlBottomRelativeContainer.this.getContext());
            	if(null!=controller)controller.notifyControllerBarPostShow();
                clearAnimation();
            }
        });

        mHideAnim = AnimationUtils.loadAnimation(ctt, R.anim.up_to_down_translate2);
        mHideAnim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            	VDVideoViewController controller = VDVideoViewController.getInstance(VDVideoControlBottomRelativeContainer.this.getContext());
                if(null!=controller)controller.setBottomPannelHiding(true);
                setVisibility(GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
            	VDVideoViewController controller = VDVideoViewController.getInstance(VDVideoControlBottomRelativeContainer.this.getContext());
            	if(null!=controller)controller.notifyControllerBarPostHide();
                clearAnimation();
                // isAnimating = false;
            }
        });
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public void onSingleTouch(MotionEvent ev) {
        Log.i("VDVideoControlBottomRelativeContainer", "onSingleTouch getVisibility() = " + getVisibility());
        // super.onSingleTouch(ev);
        if (getVisibility() == VISIBLE) {
            removeCallbacks(hideRun);
            startAnimation(mHideAnim);
            VDVideoViewController controller =  VDVideoViewController.getInstance(this.getContext());
            if(controller!=null)controller.notifyControllerBarPreHide();
        } else {
            setVisibility(View.VISIBLE);
            removeCallbacks(hideRun);
            startAnimation(mShowAnim);
            VDVideoViewController controller =  VDVideoViewController.getInstance(this.getContext());
            if(controller!=null)controller.notifyControllerBarPreShow();
            postDelayed(hideRun, VDVideoViewController.DEFAULT_DELAY);
        }

    }

    private Runnable hideRun = new Runnable() {

        @Override
        public void run() {
            if (getVisibility() == VISIBLE && getAnimation() == null) {
                removeCallbacks(hideRun);
                startAnimation(mHideAnim);
                VDVideoViewController controller = VDVideoViewController.getInstance(VDVideoControlBottomRelativeContainer.this.getContext());
                if(null!=controller)controller.notifyControllerBarPreHide();
            }
        }
    };

    @Override
    public void doNotHideControllerBar() {
        removeCallbacks(hideRun);
    }

    @Override
    public void hideControllerBar(long delay) {
        removeCallbacks(hideRun);
        postDelayed(hideRun, delay);
    }

    @Override
    public void showControllerBar(boolean delayHide) {
        if (getVisibility() != VISIBLE) {
            setVisibility(View.VISIBLE);
            startAnimation(mShowAnim);
        }
        removeCallbacks(hideRun);
        if (delayHide) {
            postDelayed(hideRun, VDVideoViewController.DEFAULT_DELAY);
        }
    }

    @Override
    public void onPostHide() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPostShow() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPreHide() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onKeyEvent() {
        // VDVideoViewController controller = VDVideoViewController.getInstance();
        // if(controller.mVDPlayerInfo.mPlayStatus ==
        // VDPlayerInfo.PLAYER_ISPLAYING){
        // if (getVisibility() == VISIBLE) {
        // removeCallbacks(hideRun);
        // startAnimation(mHideAnim);
        // }
        // } else {
        // if (getVisibility() != VISIBLE) {
        // removeCallbacks(hideRun);
        // startAnimation(mShowAnim);
        // }
        // }
        if (getVisibility() == VISIBLE) {
            removeCallbacks(hideRun);
            startAnimation(mHideAnim);
        } else {
            setVisibility(View.VISIBLE);
            removeCallbacks(hideRun);
            startAnimation(mShowAnim);
            // postDelayed(hideRun, VDVideoViewController.DEFAULT_DELAY);
        }
    }

    @Override
    public void onKeyLeftRight() {
        if (getVisibility() != VISIBLE) {
            setVisibility(View.VISIBLE);
            removeCallbacks(hideRun);
            startAnimation(mShowAnim);
            postDelayed(hideRun, VDVideoViewController.DEFAULT_DELAY);
        }
    }

    @Override
    public void onPreShow() {
        // TODO Auto-generated method stub

    }

    @Override
    public void reset() {
        super.reset();
        removeCallbacks(hideRun);
        setVisibility(GONE);
        VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
        if(controller!=null)controller.addOnShowHideControllerListener(this);
        if(controller!=null)controller.addOnShowHideBottomControllerListener(this);
    }

    @Override
    public void hide() {
        super.hide();
        removeCallbacks(hideRun);
        setVisibility(GONE);
        Log.i("VDVideoControlBottomContainer", "hide  " + " , this = " + this.hashCode());
        VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
        if(controller!=null)controller.removeOnShowHideControllerListener(this);
        if(controller!=null)controller.removeOnShowHideBottomControllerListener(this);
    }

    @Override
    public void hideBottomControllerBar() {
        if (getVisibility() == VISIBLE && getAnimation() == null) {
            startAnimation(mHideAnim);
        }
    }

    @Override
    public void showBottomControllerBar() {
        if (getVisibility() != VISIBLE && getAnimation() == null) {
            startAnimation(mShowAnim);
        }
    }

}
