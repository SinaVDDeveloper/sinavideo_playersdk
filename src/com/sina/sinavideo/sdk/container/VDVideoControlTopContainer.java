
package com.sina.sinavideo.sdk.container;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.sina.sinavideo.sdk.VDVideoViewController;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnKeyEventListener;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnScreenTouchListener;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnShowHideControllerListener;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnShowHideTopContainerListener;
import com.sina.sinavideo.sdk.utils.VDLog;
import com.sina.sinavideo.sdk.utils.VDUtility;
import com.sina.sinavideo.sdk.utils.VDVideoScreenOrientation;
import com.sina.sinavideo.sdk.widgets.VDBaseWidget;
import com.sina.video_playersdkv2.R;

/**
 * 控制条容器类，单击隐藏等操作
 * 
 * @author liuqun
 */
public class VDVideoControlTopContainer extends VDVideoControlContainer implements VDBaseWidget, OnScreenTouchListener,
        OnShowHideControllerListener, OnKeyEventListener, OnShowHideTopContainerListener {

    private int mStateBarHeight = 0;
    private boolean mUseStatusBar = true;
    private Animation mShowAnim;
    private Animation mHideAnim;

    // private boolean isAnimating;
    public VDVideoControlTopContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.VDVideoControlTopContainer);
        mUseStatusBar = a.getBoolean(R.styleable.VDVideoControlTopContainer_useStatusBar, true);
        a.recycle();
        if (!mUseStatusBar) {
            mStateBarHeight = 0;
        }
        VDVideoViewController controller = VDVideoViewController.getInstance(context);
        if (controller != null)
            controller.addOnShowHideControllerListener(this);
    }

    public VDVideoControlTopContainer(Context context) {
        super(context);
        init(context);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        return true;
    }

    private void init(Context ctt) {
        VDLog.d("VDVideoControlTopContainer", "context ctt=" + ctt);
        VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
        if (controller != null)
            controller.addOnKeyEventListener(this);
        mShowAnim = AnimationUtils.loadAnimation(ctt, R.anim.up_to_down_translate);
        mShowAnim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                clearAnimation();
            }
        });

        mHideAnim = AnimationUtils.loadAnimation(ctt, R.anim.down_to_up_translate2);
        mHideAnim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                clearAnimation();
            }
        });

        mStateBarHeight = VDVideoScreenOrientation.getStatusBarHeight(ctt);
    }

    @Override
    public void hide() {
        super.hide();
        removeCallbacks(hideRun);
        VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
        if (controller != null)
            controller.removeOnShowHideControllerListener(this);
        if (controller != null)
            controller.removeOnShowHideTopControllerListener(this);
    }

    @Override
    public void reset() {
        super.reset();
        VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
        if (controller != null)
            controller.addOnShowHideControllerListener(this);
        if (controller != null)
            controller.addOnShowHideTopControllerListener(this);
    }

    @Override
    public void onSingleTouch(MotionEvent ev) {
        // Log.i("VDVideoControlTopContainer",
        // "VDVideoControlTopContainer onSingleTouch " + getVisibility());
        if (getVisibility() == VISIBLE) {
            removeCallbacks(hideRun);
            hideController();
        } else {
            removeCallbacks(hideRun);
            showController();
            postDelayed(hideRun, VDVideoViewController.DEFAULT_DELAY);
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
    public void onKeyEvent() {
        if (getVisibility() == VISIBLE) {
            removeCallbacks(hideRun);
            hideController();
        } else {
            removeCallbacks(hideRun);
            showController();
            // postDelayed(hideRun, VDVideoViewController.DEFAULT_DELAY);
        }
    }

    @Override
    public void onKeyLeftRight() {
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
    public void hideTopControllerBar() {
        // TODO Auto-generated method stub
        hideController();
    }

    @Override
    public void showTopControllerBar() {
        // TODO Auto-generated method stub
        showController();
    }

    /**
     * 隐藏当前容器的私有方法，集中调用这个方法来实现<br />
     * TODO 后期考虑将此方法重构，放入统一的helper类中
     * 
     * @param delay 延迟隐藏的毫秒数，立即执行，填写0
     */
    private void hideControllerBarWithDelay(long delay) {
        removeCallbacks(hideRun);
        if (delay <= 0) {
            hideController();
        } else {
            postDelayed(hideRun, delay);
        }
    }

    /**
     * 显示当前容器的私有方法，集中调用这个方法来实现<br />
     * TODO 后期考虑将此方法重构，放入统一的helper类中
     * 
     * @param delay 是否需要自动隐藏
     */
    private void showControllerBarWithDelay(boolean delayHide) {
        showController();
        removeCallbacks(hideRun);
        if (delayHide) {
            postDelayed(hideRun, VDVideoViewController.DEFAULT_DELAY);
        }
    }

    private void hideController() {
        if (getVisibility() != VISIBLE || getAnimation() != null) {
            return;
        }
        android.view.ViewGroup.MarginLayoutParams lp = (MarginLayoutParams) getLayoutParams();
        if (lp != null) {
            lp.topMargin = 0;
        }
        // setVisibility(GONE);
        // TODO 控制栏部分，挪到外部去处理
        if (mUseStatusBar) {
            VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
            if (null != controller)
                controller.hideStatusBar(true);
        }
        startAnimation(mHideAnim);
        if (lp != null) {
            setLayoutParams(lp);
        }
    }

    private void showController() {
        if (getVisibility() == VISIBLE) {
            return;
        }
        android.view.ViewGroup.MarginLayoutParams lp = (MarginLayoutParams) getLayoutParams();
        if (lp != null && !(VDUtility.isMeizu() || VDUtility.isSamsungNoteII())) {
            lp.topMargin = mStateBarHeight;
        }
        // setVisibility(VISIBLE);
        startAnimation(mShowAnim);
        // TODO 控制栏部分，挪到外部去处理
        if (mUseStatusBar) {
            VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
            if (null != controller)
                controller.hideStatusBar(false);
        }
        if (lp != null) {
            VDLog.i("VDVideoControlTopContainer", "padTop = " + mStateBarHeight);
            setLayoutParams(lp);
        }
    }

    private Runnable hideRun = new Runnable() {

        @Override
        public void run() {
            hideController();
        }
    };
}
