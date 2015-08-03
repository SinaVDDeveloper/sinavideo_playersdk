package com.sina.sinavideo.sdk.container;

import com.sina.sinavideo.sdk.VDVideoViewController;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnShowHideADContainerListener;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnVideoInsertADListener;
import com.sina.sinavideo.sdk.widgets.VDBaseWidget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

public class VDVideoADBottomRelativeContainer extends RelativeLayout implements VDBaseWidget, OnVideoInsertADListener,
        OnShowHideADContainerListener {
    @SuppressLint("nouse")
    private Context mContext = null;

    public VDVideoADBottomRelativeContainer(Context context) {
        super(context);
        init(context);
    }

    public VDVideoADBottomRelativeContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        setVisibility(View.GONE);
        // TODO Auto-generated constructor stub
    }

    private void init(Context context) {
        mContext = context;
    }

    @Override
    public void hideADContainerBar() {
        // TODO Auto-generated method stub
        setVisibility(View.GONE);
    }

    @Override
    public void showADContainerBar() {
        // TODO Auto-generated method stub
        setVisibility(View.VISIBLE);
    }

    @Override
    public void onVideoInsertADBegin() {
        // TODO Auto-generated method stub
        setVisibility(View.VISIBLE);
    }

    @Override
    public void onVideoInsertADTicker() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onVideoInsertADEnd() {
        // TODO Auto-generated method stub
        setVisibility(View.GONE);
    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub
    	VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
    	if(controller!=null)controller.addOnVideoInsertADListener(this);
    	if(controller!=null)controller.addOnShowHideADContainerListener(this);
    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub
    	VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
    	if(controller!=null)controller.removeOnVideoInsertADListener(this);
    	if(controller!=null)controller.removeOnShowHideADContainerListener(this);
    }

}
