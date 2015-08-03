
package com.sina.sinavideo.sdk.container;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.sina.sinavideo.sdk.VDVideoViewController;
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
public class VDVideoControlBottomContainer extends VDVideoControlContainer implements VDBaseWidget,
        OnScreenTouchListener, OnShowHideControllerListener, OnShowHideBottomControllerListener {

    private Animation mShowAnim;
    private Animation mHideAnim;

    // private boolean isAnimating;
    public VDVideoControlBottomContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    public VDVideoControlBottomContainer(Context context) {
        super(context);
        init(context);
    }

    private void init(Context ctt) {
        // VDVideoViewController.getInstance().addOnShowHideControllerListener(this);
        mShowAnim = AnimationUtils.loadAnimation(ctt, R.anim.down_to_up_translate);
        mShowAnim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                VDVideoViewController controller = VDVideoViewController.getInstance(VDVideoControlBottomContainer.this.getContext());
                if (null != controller)
                    controller.setBottomPannelHiding(false);
                setVisibility(View.VISIBLE);
                if (null != controller)
                    controller.notifyHideVideoList();
                if (null != controller)
                    controller.notifyHideMoreOprationPanel();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                VDVideoViewController controller = VDVideoViewController.getInstance(VDVideoControlBottomContainer.this.getContext());
                if (null != controller)
                    controller.notifyControllerBarPostShow();
                clearAnimation();
            }
        });

        mHideAnim = AnimationUtils.loadAnimation(ctt, R.anim.up_to_down_translate2);
        mHideAnim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                VDVideoViewController controller = VDVideoViewController.getInstance(VDVideoControlBottomContainer.this.getContext());
                if (controller != null)
                    controller.setBottomPannelHiding(true);
                setVisibility(GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                VDVideoViewController controller = VDVideoViewController.getInstance(VDVideoControlBottomContainer.this.getContext());
                if (controller != null)
                    controller.notifyControllerBarPostHide();
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
        if (getVisibility() == VISIBLE) {
            hideControllerBarWithDelay(0);
        } else {
            showControllerBarWithDelay(true);
        }
    }

    @Override
    public void doNotHideControllerBar() {
        removeCallbacks(hideRun);
    }

    @Override
    public void hideControllerBar(long delay) {
        hideControllerBarWithDelay(delay);
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
    public void showControllerBar(boolean delayHide) {
        showControllerBarWithDelay(delayHide);
    }

    @Override
    public void onPreShow() {
        // TODO Auto-generated method stub

    }

    @Override
    public void reset() {
        // Exception e = new Exception("this is a log");
        // e.printStackTrace();
        super.reset();
        removeCallbacks(hideRun);
        setVisibility(GONE);
        VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
        if (controller != null)
            controller.addOnShowHideControllerListener(this);
        if (controller != null)
            controller.addOnShowHideBottomControllerListener(this);
    }

    @Override
    public void hide() {
        super.hide();
        removeCallbacks(hideRun);
        setVisibility(GONE);
        VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
        if (controller != null)
            controller.removeOnShowHideControllerListener(this);
        if (controller != null)
            controller.removeOnShowHideBottomControllerListener(this);
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

    /**
     * 显示当前容器的私有方法，集中调用这个方法来实现<br />
     * #NOTE 后期考虑将此方法重构，放入统一的helper类中
     * 
     * @param delay 是否需要自动隐藏
     */
    private void showControllerBarWithDelay(boolean delayHide) {
        if (getVisibility() != VISIBLE) {
            setVisibility(View.VISIBLE);
            startAnimation(mShowAnim);
        }
        removeCallbacks(hideRun);
        if (delayHide) {
            postDelayed(hideRun, VDVideoViewController.DEFAULT_DELAY);
        }
    }

    /**
     * 隐藏当前容器的私有方法，集中调用这个方法来实现<br />
     * #NOTE 后期考虑将此方法重构，放入统一的helper类中
     * 
     * @param delay 延迟隐藏的毫秒数，立即执行，填写0
     */
    private void hideControllerBarWithDelay(long delay) {
        removeCallbacks(hideRun);
        postDelayed(hideRun, delay);
    }

    private Runnable hideRun = new Runnable() {

        @Override
        public void run() {
            if (getVisibility() == VISIBLE && getAnimation() == null) {
                startAnimation(mHideAnim);
                VDVideoViewController controller = VDVideoViewController.getInstance(VDVideoControlBottomContainer.this.getContext());
                if (null != controller)
                    controller.notifyControllerBarPreHide();
            }
        }
    };
}
