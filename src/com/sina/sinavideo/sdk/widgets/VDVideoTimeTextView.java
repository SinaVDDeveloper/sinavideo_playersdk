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
import com.sina.sinavideo.sdk.data.VDVideoInfo;
import com.sina.sinavideo.sdk.utils.VDUtility;
import com.sina.video_playersdkv2.R;

public final class VDVideoTimeTextView extends TextView implements
		VDBaseWidget, VDVideoViewListeners.OnProgressUpdateListener {

	public static final int TIME_FORMAT_PROGRESSDURAION = 0;
	public static final int TIME_FORMAT_PROGRESSONLY = 1;
	public static final int TIME_FORMAT_CUTDOWNPROGRESS = 2;
	public static final int TIME_FORMAT_DURAIONONLY = 3;

	private int mTimeFormat = TIME_FORMAT_PROGRESSDURAION;

	public VDVideoTimeTextView(Context context) {
		super(context);
		setTextColor(Color.WHITE);
	}

	public VDVideoTimeTextView(Context context, AttributeSet attrs) {
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

		// 读取事件的显示格式
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.TimeTextView);
		mTimeFormat = a.getInteger(R.styleable.TimeTextView_TimeFormat,
				TIME_FORMAT_PROGRESSDURAION);
		a.recycle();
	}

	@Override
	public void onProgressUpdate(long current, long duration) {
		if (current > duration) {
			current = duration;
		}
		switch (mTimeFormat) {
		case TIME_FORMAT_PROGRESSDURAION:
			setText(VDUtility.generatePlayTime(current) + "/"
					+ VDUtility.generatePlayTime(duration));
			break;
		case TIME_FORMAT_PROGRESSONLY:
			setText(VDUtility.generatePlayTime(current));
			break;
		case TIME_FORMAT_CUTDOWNPROGRESS:
			setText(VDUtility.generatePlayTime(duration - current));
			break;
		case TIME_FORMAT_DURAIONONLY:
			setText(VDUtility.generatePlayTime(duration));
			break;
		}
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
}