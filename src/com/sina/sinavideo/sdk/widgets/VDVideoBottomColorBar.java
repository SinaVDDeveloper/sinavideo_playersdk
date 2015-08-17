package com.sina.sinavideo.sdk.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.SeekBar;

import com.sina.sinavideo.sdk.VDVideoViewController;
import com.sina.sinavideo.sdk.VDVideoViewListeners;
import com.sina.sinavideo.sdk.data.VDVideoInfo;

/**
 * 详情界面视频播放进度条
 * 
 * @author liuqun
 * 
 */
public final class VDVideoBottomColorBar extends SeekBar implements
		VDBaseWidget, VDVideoViewListeners.OnProgressUpdateListener {

	private int progress = 0;
	private int max = 1;
	private Paint mPaint;
	private Rect r = new Rect();

	@Override
	public int getProgress() {
		return progress;
	}

	@Override
	public void setProgress(int progress) {
		this.progress = progress;
		postInvalidate();
	}

	@Override
	public int getMax() {
		return max;
	}

	@Override
	public void setMax(int max) {
		this.max = max;
	}

	public VDVideoBottomColorBar(Context context) {
		super(context);
		init();
	}

	public VDVideoBottomColorBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		mPaint = new Paint();
	}

	@Override
	public void reset() {
		VDVideoViewController controller = VDVideoViewController
				.getInstance(this.getContext());
		if (controller == null) {
			return;
		}
		VDVideoInfo info = controller.getCurrentVideo();
		if (info != null) {
			onProgressUpdate(info.mVideoPosition, info.mVideoDuration);
		}
		controller.addOnProgressUpdateListener(this);
	}

	@Override
	public void hide() {
		VDVideoViewController ctr = VDVideoViewController.getInstance(this
				.getContext());
		if (ctr != null)
			ctr.removeOnProgressUpdateListener(this);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (max != 0) {
			int end = (int) (((float) progress * getMeasuredWidth()) / max);
			mPaint.setColor(Color.parseColor("#0078db"));
			mPaint.setStyle(Style.FILL);
			r.set(0, 0, end, getMeasuredHeight());
			canvas.drawRect(r, mPaint);
		}
	}

	@Override
	public void onProgressUpdate(long current, long duration) {
		setMax((int) duration);
		setProgress((int) current);
	}

	@Override
	public void onDragProgess(long progress, long duration) {
		onProgressUpdate(progress, duration);
	}
}
