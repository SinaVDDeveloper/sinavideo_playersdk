package com.sina.sinavideo.sdk.container;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.sina.sinavideo.sdk.VDVideoViewController;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnVideoFrameADListener;
import com.sina.sinavideo.sdk.widgets.VDBaseWidget;
import com.sina.video_playersdkv2.R;

/**
 * 静帧广告容器，仅用于暂停、滑动等情况时候
 */
public class VDVideoADFrameContainer extends RelativeLayout implements
		VDBaseWidget, OnVideoFrameADListener {
	public VDVideoADFrameContainer(Context context) {
		super(context);
	}

	public VDVideoADFrameContainer(Context context, AttributeSet attrs) {
		super(context, attrs);

		int adEnum = 1;
		TypedArray typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.VDVideoControlPanelContainer);
		for (int i = 0; i < typedArray.getIndexCount(); i++) {
			if (typedArray.getIndex(i) == R.styleable.VDVideoADFrameContainer_adConfig) {
				adEnum = typedArray.getInt(i, 1);
			}
		}
		VDVideoViewController controller = VDVideoViewController.getInstance(context);
		if(null!=controller)controller.mADConfigEnum = adEnum;
		typedArray.recycle();
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
		if(controller!=null)controller.addOnVideoFrameADListener(this);
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
		if(controller!=null)controller.removeOnVideoFrameADListener(this);
	}

	@Override
	public void onVideoFrameADBegin() {
		// TODO Auto-generated method stub
		setVisibility(View.VISIBLE);
	}

	@Override
	public void onVideoFrameADEnd() {
		// TODO Auto-generated method stub
		setVisibility(View.GONE);
	}

}
