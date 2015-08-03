package com.sina.sinavideo.sdk.widgets.playlist;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.sina.sinavideo.sdk.VDVideoViewController;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnVideoListVisibleChangeListener;
import com.sina.sinavideo.sdk.widgets.VDBaseWidget;
import com.sina.video_playersdkv2.R;

/**
 * 播放列表弹出界面，容器部分
 * 
 * @author liuqun
 */
public class VDVideoPlayListContainer extends LinearLayout implements VDBaseWidget, OnVideoListVisibleChangeListener {

    public interface OnPlayListItemClick {

        public void onItemClick(int position);
    }

    private Animation mShowAnim;
    private Animation mHideAnim;

    private Context mContext = null;

    /**
     * 容器的适配器，因为不知道当前适配的界面，所以，ViewHolder没法弄了，短列表姑且认为没问题吧
     * 
     * @author sunxiao
     * 
     */

    public VDVideoPlayListContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // setVisibility(GONE);
                removeCallbacks(mHideAction);
                post(mHideAction);
            }
        });
        // 如果有自定义属性，加载
        // int itemID = -1;
        // TypedArray typedArr = context.obtainStyledAttributes(attrs,
        // R.styleable.VDVideoPlaylistContainer);
        // for (int i = 0; i < typedArr.getIndexCount(); i++) {
        // switch (typedArr.getIndex(i)) {
        // default :
        // break;
        // case R.styleable.VDVideoPlaylistContainer_listItem :
        // itemID =
        // typedArr.getResourceId(R.styleable.VDVideoPlaylistContainer_listItem,
        // -1);
        // break;
        // }
        // }
        // typedArr.recycle();

        // 设置adapter
        // VDVideoViewController.getInstance().addOnVideoListListener(this);
        VDVideoViewController controller = VDVideoViewController.getInstance(context);
        if(null!=controller)controller.addOnVideoListVisibleChangeListener(this);

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
                //VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
                //if(null!=controller)controller.notifyShowBottomControllerBar();
            }
        });
    }

    @Override
    public void reset() {

    }

    @Override
    public void hide() {
        setVisibility(GONE);
    }

    @Override
    public void toogle() {
        if (getAnimation() != null) {
            return;
        }
        if (getVisibility() == VISIBLE) {
            removeCallbacks(mHideAction);
            hidePlayList();
            // TODO 什么意思？？
            VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
            if(null!=controller)controller.notifyShowBottomControllerBar();
        } else {
            setVisibility(VISIBLE);
            showPlayList();
        }

    }

    public Runnable mHideAction = new Runnable() {

        @Override
        public void run() {
            if (getVisibility() == VISIBLE && getAnimation() == null) {
                // setVisibility(GONE);
            	//VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
                //if(null!=controller)controller.notifyHideControllerBar(0);
                startAnimation(mHideAnim);
            }
        }
    };

    @Override
    public void showPlayList() {
        startAnimation(mShowAnim);
        VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
        if(null!=controller)controller.notifyNotHideControllerBar();
        postDelayed(mHideAction, VDVideoViewController.DEFAULT_DELAY);
        // VDVideoViewController.getInstance().notifyHideBottomControllerBar();
    }

    @Override
    public void hidePlayList() {
    	removeCallbacks(mHideAction);
        if (getVisibility() == VISIBLE) {
            // VDVideoViewController.getInstance().notifyHideControllerBar(VDVideoViewController.DEFAULT_DELAY);
            // startAnimation(mHideAnim);
            post(mHideAction);

            VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
            if(null!=controller)controller.notifyHideTopControllerBar();
            if(null!=controller)controller.notifyHideBottomControllerBar();
            // setVisibility(GONE);
        }
    }

    @Override
    public void removeAndHideDelay() {
        removeCallbacks(mHideAction);
        postDelayed(mHideAction, VDVideoViewController.DEFAULT_DELAY);
    }
}
