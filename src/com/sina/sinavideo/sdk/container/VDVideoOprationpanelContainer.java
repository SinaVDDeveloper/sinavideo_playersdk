package com.sina.sinavideo.sdk.container;

/**
 * 操作面板弹出界面，容器部分
 * 
 * @author liuqun
 */
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.sina.sinavideo.sdk.VDVideoViewController;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnMoreOprationVisibleChangeListener;
import com.sina.sinavideo.sdk.widgets.VDBaseWidget;
import com.sina.video_playersdkv2.R;

public class VDVideoOprationpanelContainer extends LinearLayout
        implements
            VDBaseWidget,
            OnMoreOprationVisibleChangeListener {

    public interface OnPlayListItemClick {

        public void onItemClick(int position);
    }

    private Animation mShowAnim;
    private Animation mHideAnim;

    private Context mContext = null;

    /**
     * 容器的适配器，因为不知道当前适配的界面，所以，ViewHolder没法弄了，短列表姑且认为没问题吧
     * 
     * @author liuqun
     * 
     */

    public VDVideoOprationpanelContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        setOnClickListener(mClickListener);
        VDVideoViewController controller = VDVideoViewController.getInstance(context);
        if(controller!=null)controller.addOnMoreOprationVisibleChangeListener(this);

        mShowAnim = AnimationUtils.loadAnimation(mContext, R.anim.video_list_from_right_in);
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

        mHideAnim = AnimationUtils.loadAnimation(mContext, R.anim.video_list_fade_from_right);
        mHideAnim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setVisibility(View.GONE);
                clearAnimation();
            }
        });

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        getChildAt(0).setOnClickListener(null);
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            removeCallbacks(mHideAction);
            post(mHideAction);
        }
    };

    @Override
    public void reset() {

    }

    @Override
    public void hide() {
        setVisibility(GONE);
    }

    public Runnable mHideAction = new Runnable() {

        @Override
        public void run() {
            if (getVisibility() == VISIBLE && getAnimation() == null) {
                 startAnimation(mHideAnim);
            }
        }
    };

    @Override
    public void showPanel() {
        startAnimation(mShowAnim);
        removeCallbacks(mHideAction);
        postDelayed(mHideAction, VDVideoViewController.DEFAULT_DELAY);
    }

    @Override
    public void hidePanel() {
        removeCallbacks(mHideAction);
        post(mHideAction);
    }
    
    @Override
    public void removeAndHideDelay() {
        removeCallbacks(mHideAction);
        postDelayed(mHideAction, VDVideoViewController.DEFAULT_DELAY);
    }
}
