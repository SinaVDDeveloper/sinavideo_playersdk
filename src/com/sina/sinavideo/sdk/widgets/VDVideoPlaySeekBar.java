package com.sina.sinavideo.sdk.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.SeekBar;

import com.sina.sinavideo.sdk.VDVideoViewController;
import com.sina.sinavideo.sdk.VDVideoViewListeners;
import com.sina.sinavideo.sdk.data.VDPlayerInfo;
import com.sina.sinavideo.sdk.data.VDVideoInfo;
import com.sina.sinavideo.sdk.dlna.DLNAController;
import com.sina.video_playersdkv2.R;

/**
 * 视频进度拖动条
 * 
 * @author liuqun
 * 
 */
public final class VDVideoPlaySeekBar extends SeekBar implements VDBaseWidget,
		VDVideoViewListeners.OnProgressUpdateListener,
		SeekBar.OnSeekBarChangeListener,
		VDVideoViewListeners.OnBufferingUpdateListener {

	private long mDuration;

	public VDVideoPlaySeekBar(Context context) {
		super(context);
		setProgressDrawable(getResources().getDrawable(
				R.drawable.play_seekbar_background));
		setThumb(getResources().getDrawable(R.drawable.play_ctrl_sound_ball));
		init();
	}

	public VDVideoPlaySeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray typedArr = context.obtainStyledAttributes(attrs,
				new int[] { android.R.attr.progressDrawable });

		if (typedArr != null) {
			int resouceID = typedArr.getResourceId(0, -1);
			if (resouceID == -1) {
				setProgressDrawable(getResources().getDrawable(
						R.drawable.play_seekbar_background));
				setThumb(getResources().getDrawable(
						R.drawable.play_ctrl_sound_ball));
			}
			typedArr.recycle();
		} else {
			setProgressDrawable(getResources().getDrawable(
					R.drawable.play_seekbar_background));
			setThumb(getResources()
					.getDrawable(R.drawable.play_ctrl_sound_ball));
		}

		init();
	}

	private void init() {
		setOnSeekBarChangeListener(this);
	}

	@Override
	public void reset() {
		VDVideoViewController controller = VDVideoViewController
				.getInstance(this.getContext());
		if (null == controller)
			return;
		VDVideoInfo info = controller.getCurrentVideo();
		if (info != null) {
			onProgressUpdate(info.mVideoPosition, info.mVideoDuration);
		}
		controller.addOnProgressUpdateListener(this);
		controller.addOnBufferingUpdateListener(this);
	}

	@Override
	public void hide() {
		VDVideoViewController controller = VDVideoViewController
				.getInstance(this.getContext());
		if (null != controller)
			controller.removeOnProgressUpdateListener(this);
		if (null != controller)
			controller.removeOnBufferingUpdateListener(this);
	}

	@Override
	public void onProgressUpdate(long current, long duration) {
		if (duration > 0) {
			mDuration = duration;
			if (getMax() != duration && duration > 0) {
				setMax((int) duration);
			}
			setProgress((int) current);
		}

	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (fromUser) {
			VDVideoViewController controller = VDVideoViewController
					.getInstance(this.getContext());
			if (controller != null)
				controller.dragProgressTo((float) progress / mDuration);
			if (controller != null)
				controller
						.notifyHideControllerBar(VDVideoViewController.DEFAULT_DELAY);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		VDVideoViewController controller = VDVideoViewController
				.getInstance(this.getContext());
		if (controller == null) {
			return;
		}
		if (!DLNAController.mIsDLNA) {
			VDPlayerInfo playInfo = controller.getPlayerInfo();
			if (playInfo != null) {
			}
		}
		controller.notifyHideControllerBar(VDVideoViewController.DEFAULT_DELAY);
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		VDVideoViewController controller = VDVideoViewController
				.getInstance(this.getContext());
		if (controller != null)
			controller
					.notifyHideControllerBar(VDVideoViewController.DEFAULT_DELAY);
		if (DLNAController.mIsDLNA) {
			DLNAController.getInstance(getContext()).seek(getProgress());
		} else {
			if (controller != null)
				controller.seekTo(getProgress());
			if (controller != null)
				controller.start();
		}
	}

	@Override
	public void onDragProgess(long progress, long duration) {
		setProgress((int) progress);
	}

	@Override
	public void onBufferingUpdate(int percent) {
		setSecondaryProgress(percent * (getMax() / 100));
	}

}
