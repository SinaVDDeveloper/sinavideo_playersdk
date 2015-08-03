package com.sina.sinavideo.sdk.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.sina.sinavideo.sdk.VDVideoViewController;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnResolutionContainerListener;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnScreenTouchListener;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnShowHideControllerListener;
import com.sina.video_playersdkv2.R;

/**
 * 清晰度选择菜单
 * 
 * @author liuqun
 * 
 */
public class VDVideoResolutionList extends LinearLayout implements VDBaseWidget, OnShowHideControllerListener,
        OnScreenTouchListener, OnResolutionContainerListener {

    String tag = "VDVideoResolutionList";

    public VDVideoResolutionList(Context context) {
        super(context);
        init();
    }

    public VDVideoResolutionList(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ResolutionBackGround);
        a.recycle();
    }

    private void init() {
        setBackgroundResource(R.drawable.definition_select_bg);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    public void reset() {
        VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
        if (controller != null) {
            // controller.addOnResolutionListener(this);
            controller.addOnResolutionContainerListener(this);
            controller.addOnScreenTouchListener(this);
            controller.addOnShowHideControllerListener(this);
        }
    }

    @Override
    public void hide() {
        this.setVisibility(View.GONE);
        VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
        if (controller != null) {
            // controller.removeOnResolutionListener(this);
            controller.removeOnResolutionContainerListener(this);
            controller.removeOnScreenTouchListener(this);
            controller.removeOnShowHideControllerListener(this);
        }
    }

    // public void setResolutionList(VDResolutionData resolutionData) {
    // Log.i(tag, "setResolutionList --> " + resolutionData.toString());
    // if (resolutionData != null) {
    //
    // }
    // }

    @Override
    public void onSingleTouch(MotionEvent ev) {
        setVisibility(GONE);
    }

    // @Override
    // public void onResolutionSelect(String resolution) {
    // Log.i(tag, "onResolutionSelect -> " + resolution);
    // if (resolution != null) {
    //
    // }
    // }

    // @Override
    // public void onParseResolution(VDResolutionData list) {
    // setResolutionList(list);
    // }

    // @Override
    // public void hideResolution() {
    // setVisibility(GONE);
    // }

    public void focusFirstView() {
        // mResolutionButtonBase.requestFocus();
    }

    @Override
    public void doNotHideControllerBar() {

    }

    @Override
    public void hideControllerBar(long delay) {
    }

    @Override
    public void showControllerBar(boolean delayHide) {

    }

    @Override
    public void onPostHide() {
        setVisibility(View.GONE);
    }

    @Override
    public void onPostShow() {

    }

    @Override
    public void onPreHide() {
        setVisibility(View.GONE);
    }

    @Override
    public void onPreShow() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onResolutionContainerVisible(boolean isVisible) {
        // TODO Auto-generated method stub
        if (isVisible) {
            setVisibility(View.VISIBLE);
        } else {
            setVisibility(View.GONE);
        }
    }

}
