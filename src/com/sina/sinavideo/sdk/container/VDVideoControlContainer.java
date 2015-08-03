package com.sina.sinavideo.sdk.container;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.sina.sinavideo.sdk.VDVideoViewController;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnScreenTouchListener;
import com.sina.sinavideo.sdk.widgets.VDBaseWidget;

/**
 * 控制条容器类，单击隐藏等操作
 * 
 * @author seven
 */
public class VDVideoControlContainer extends LinearLayout implements VDBaseWidget, OnScreenTouchListener {

    public VDVideoControlContainer(Context context) {
        super(context);
    }

    public VDVideoControlContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onSingleTouch(MotionEvent ev) {
        if (getVisibility() == View.GONE) {
            setVisibility(View.VISIBLE);
        } else {
            setVisibility(View.GONE);
        }
    }

    @Override
    public void reset() {
    	VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
    	if(controller!=null)controller.addOnScreenTouchListener(this);
    }

    @Override
    public void hide() {
        setVisibility(GONE);
        VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
    	if(controller!=null)controller.removeOnScreenTouchListener(this);
    }

}
