/**
 * 播放时间
 * 
 * @author liuqun
 */
package com.sina.sinavideo.sdk.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.TextView;

import com.sina.sinavideo.sdk.VDVideoViewController;
import com.sina.sinavideo.sdk.VDVideoViewListeners;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnShowHideControllerListener;
import com.sina.sinavideo.sdk.data.VDVideoInfo;
import com.sina.sinavideo.sdk.utils.VDUtility;

public final class VDVideoLastTimeTextView extends TextView implements
		VDBaseWidget, VDVideoViewListeners.OnProgressUpdateListener,
		OnShowHideControllerListener {

	public VDVideoLastTimeTextView(Context context) {
		super(context);
		setTextColor(Color.WHITE);
	}

	public VDVideoLastTimeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);

		// 读取颜色属性，如果已经设置就使用系统的，否则用默认的
		TypedArray typedArr = context.obtainStyledAttributes(attrs,
				new int[] { android.R.attr.textColor });

		if (typedArr != null) {
			int resouceID = typedArr.getResourceId(0, -1);
			if (resouceID == -1) {
				setTextColor(Color.WHITE);
			}
			typedArr.recycle();
		} else {
			setTextColor(Color.WHITE);
		}
		VDVideoViewController controller = VDVideoViewController
				.getInstance(this.getContext());
		if (controller != null)
			controller.addOnShowHideControllerListener(this);
	}

	@Override
	public void onProgressUpdate(long current, long duration) {
		if (current > duration) {
			current = duration;
		}
		setText(VDUtility.generatePlayTime(duration - current));
	}

	@Override
	public void reset() {
		VDVideoViewController controller = VDVideoViewController
				.getInstance(this.getContext());
		if (controller == null)
			return;
		VDVideoInfo info = controller.getCurrentVideo();
		if (info != null) {
			onProgressUpdate(info.mVideoPosition, info.mVideoDuration);
		}
		controller.addOnProgressUpdateListener(this);
	}

	@Override
	public void hide() {
		VDVideoViewController controller = VDVideoViewController
				.getInstance(this.getContext());
		if (controller != null)
			controller.removeOnProgressUpdateListener(this);
	}

	@Override
	public void onDragProgess(long progress, long duration) {
		onProgressUpdate(progress, duration);
	}

	@Override
	public void doNotHideControllerBar() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hideControllerBar(long delay) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showControllerBar(boolean delayHide) {

	}

	@Override
	public void onPostHide() {
		setVisibility(VISIBLE);
	}

	@Override
	public void onPostShow() {
		setVisibility(GONE);
	}

	@Override
	public void onPreHide() {

	}

	@Override
	public void onPreShow() {
		setVisibility(GONE);
	}

}