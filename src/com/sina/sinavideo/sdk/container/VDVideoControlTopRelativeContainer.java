package com.sina.sinavideo.sdk.container;

import java.lang.reflect.Field;

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
import com.sina.sinavideo.sdk.widgets.VDBaseWidget;
import com.sina.video_playersdkv2.R;

/**
 * 控制条容器类，单击隐藏等操作
 * 
 * @author liuqun
 */
public class VDVideoControlTopRelativeContainer extends VDVideoControlRelativeContainer implements VDBaseWidget,
        OnScreenTouchListener, OnShowHideControllerListener, OnShowHideTopContainerListener, OnKeyEventListener {

    private int mStateBarHeight = 0;
    private boolean mUseStatusBar = true;
    private Animation mShowAnim;
    private Animation mHideAnim;

    // private boolean isAnimating;
    public VDVideoControlTopRelativeContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.VDVideoControlTopContainer);
        mUseStatusBar = a.getBoolean(R.styleable.VDVideoControlTopContainer_useStatusBar, true);
        a.recycle();
        if (!mUseStatusBar) {
            mStateBarHeight = 0;
        }
        VDVideoViewController controller = VDVideoViewController.getInstance(context);
        if(controller!=null)controller.addOnShowHideControllerListener(this);
    }

    public VDVideoControlTopRelativeContainer(Context context) {
        super(context);
        init(context);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        return true;
    }

    private void init(Context ctt) {
    	VDVideoViewController controller = VDVideoViewController.getInstance(ctt);
    	if(controller!=null)controller.addOnKeyEventListener(this);
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
                // isAnimating = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                clearAnimation();
                // isAnimating = false;
            }
        });
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            mStateBarHeight = ctt.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            mStateBarHeight = (int) (0.5D + (ctt.getResources().getDisplayMetrics().densityDpi / 160F) * 25);
            e1.printStackTrace();
        }
    }

    @Override
    public void hide() {
        super.hide();
        removeCallbacks(hideRun);
    }

    @Override
    public void onSingleTouch(MotionEvent ev) {
        // Log.i("VDVideoControlTopContainer",
        // "VDVideoControlTopContainer onSingleTouch " + getVisibility());
        if (getVisibility() == VISIBLE) {
            removeCallbacks(hideRun);
            hideControllerInner();
        } else {
            removeCallbacks(hideRun);
            showControllerInner();
            postDelayed(hideRun, VDVideoViewController.DEFAULT_DELAY);
        }

    }

    private Runnable hideRun = new Runnable() {

        @Override
        public void run() {
            hideControllerInner();
        }
    };

    private void showControllerInner() {
        if (getVisibility() == VISIBLE) {
            return;
        }
        android.view.ViewGroup.MarginLayoutParams lp = (MarginLayoutParams) getLayoutParams();
        if (lp != null && !(VDUtility.isMeizu() || VDUtility.isSamsungNoteII())) {
            lp.topMargin = mStateBarHeight;
        }
        // setVisibility(VISIBLE);
        startAnimation(mShowAnim);
        if (mUseStatusBar) {
        	VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
        	if(null!=controller)
        		controller.hideStatusBar(false);
        }
        if (lp != null) {
            VDLog.i("VDVideoControlTopContainer", "padTop = " + mStateBarHeight);
            setLayoutParams(lp);
        }
    }

    private void hideControllerInner() {
        if (getVisibility() != VISIBLE || getAnimation() != null) {
            return;
        }
        android.view.ViewGroup.MarginLayoutParams lp = (MarginLayoutParams) getLayoutParams();
        if (lp != null) {
            lp.topMargin = 0;
        }
        setVisibility(GONE);
        if (mUseStatusBar) {
        	VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
        	if(null!=controller)controller.hideStatusBar(true);
        }
        startAnimation(mHideAnim);
        if (lp != null) {
            // LogS.i("VDVideoControlTopContainer", "padTop = " +
            // mStateBarHeight);
            setLayoutParams(lp);
        }
    }

    private void hideControllerBarWithDelay(long delay) {
        removeCallbacks(hideRun);
        postDelayed(hideRun, delay);
    }

    private void showControllerBarWithDelay(boolean delayHide) {
        showControllerInner();
        removeCallbacks(hideRun);
        if (delayHide) {
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
    public void showControllerBar(boolean delayHide) {
        showControllerBarWithDelay(delayHide);
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
        // VDVideoViewController controller =
        // VDVideoViewController.getInstance();
        // if(controller.mVDPlayerInfo.mPlayStatus ==
        // VDPlayerInfo.PLAYER_ISPLAYING){
        // if (getVisibility() == VISIBLE) {
        // removeCallbacks(hideRun);
        // hideController();
        // }
        // } else {
        // if (getVisibility() != VISIBLE) {
        // removeCallbacks(hideRun);
        // showController();
        // }
        // }
        if (getVisibility() == VISIBLE) {
            removeCallbacks(hideRun);
            hideControllerInner();
        } else {
            removeCallbacks(hideRun);
            showControllerInner();
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
    public void hideTopControllerBar() {
        // TODO Auto-generated method stub
        hideControllerBarWithDelay(0);
    }

    @Override
    public void showTopControllerBar() {
        // TODO Auto-generated method stub
        showControllerBarWithDelay(false);

    }

}
