package com.sina.sinavideo.sdk.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.sina.sinavideo.sdk.VDVideoViewController;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnLoadingListener;

/**
 * 中间加载进度条
 * 
 * @author liuqun
 * 
 */
public final class VDVideoLoadingProgress extends ProgressBar implements
		VDBaseWidget, OnLoadingListener {

	public VDVideoLoadingProgress(Context context) {
		super(context);
		init();
	}

	public VDVideoLoadingProgress(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public VDVideoLoadingProgress(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		registerListeners();
	}

	@Override
	public void reset() {
	}

	@Override
	public void hide() {
	}

	/**
	 * 为自己注册事件
	 */
	private void registerListeners() {
		VDVideoViewController controller = VDVideoViewController
				.getInstance(this.getContext());
		if (controller != null)
			controller.addOnLoadingListener(this);
	}

	@Override
	public void showLoading() {
		setVisibility(VISIBLE);
	}

	@Override
	public void hideLoading() {
		setVisibility(GONE);
	}

}
