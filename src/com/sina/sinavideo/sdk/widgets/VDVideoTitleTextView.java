package com.sina.sinavideo.sdk.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.sina.sinavideo.sdk.VDVideoViewController;
import com.sina.sinavideo.sdk.VDVideoViewListeners;
import com.sina.sinavideo.sdk.data.VDVideoInfo;

/**
 * 视频标题TextView
 * 
 * @author liuqun
 * 
 */
public final class VDVideoTitleTextView extends TextView implements
		VDBaseWidget, VDVideoViewListeners.OnPlayVideoListener {

	public VDVideoTitleTextView(Context context) {
		super(context);
	}

	public VDVideoTitleTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		VDVideoViewController controller = VDVideoViewController
				.getInstance(context);
		if (controller != null)
			controller.addOnPlayVideoListener(this);
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		// VDVideoViewController.getInstance().removeOnPlayVideoListener(this);
	}

	@Override
	public void onVideoInfo(VDVideoInfo info) {
		// TODO Auto-generated method stub
		setText(info.mTitle);
	}

	@Override
	public void onShowLoading(boolean show) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onVideoPrepared(boolean prepare) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPlayStateChanged() {
		// TODO Auto-generated method stub

	}

}
