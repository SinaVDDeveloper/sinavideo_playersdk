/**
 * 音量控制组件的百分比方式显示
 * 
 * @author seven
 */
package com.sina.sinavideo.sdk.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.TextView;

import com.sina.sinavideo.sdk.VDVideoViewController;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnSoundChangedListener;
import com.sina.sinavideo.sdk.dlna.DLNAController;
import com.sina.sinavideo.sdk.utils.VDPlayerSoundManager;

public final class VDVideoSoundSeekPercentView extends TextView implements
		VDBaseWidget, OnSoundChangedListener {

	private Context mContext = null;

	public VDVideoSoundSeekPercentView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mContext = context;
		TypedArray typedArr = context.obtainStyledAttributes(attrs, new int[] {
				android.R.attr.textSize, android.R.attr.textColor });
		if (typedArr != null) {
			float textSize = typedArr.getDimension(0, -1);
			if (textSize == -1) {
				setTextSize(20);
			}

			int textColor = typedArr.getColor(1, -1);
			if (textColor == -1) {
				setTextColor(Color.WHITE);
			}
			typedArr.recycle();
		}

		getPaint().setFakeBoldText(true);
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		VDVideoViewController controller = VDVideoViewController
				.getInstance(this.getContext());
		if (controller != null)
			controller.addOnSoundChangedListener(this);
		float currPercent = ((float) VDPlayerSoundManager
				.getCurrSoundVolume(mContext) / VDPlayerSoundManager
				.getMaxSoundVolume(mContext));
		setSoundText(currPercent);
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		VDVideoViewController controller = VDVideoViewController
				.getInstance(this.getContext());
		if (controller != null)
			controller.removeOnSoundChangedListener(this);
	}

	@Override
	public void onSoundChanged(int currVolume) {
		int maxVolume = 0;
		if (DLNAController.mIsDLNA) {
			VDVideoViewController controller = VDVideoViewController
					.getInstance(this.getContext());
			if (controller != null)
				maxVolume = DLNAController.getInstance(controller.getContext())
						.getVolumeMax();
		} else {
			maxVolume = VDPlayerSoundManager.getMaxSoundVolume(mContext);
		}
		if (maxVolume != 0) {
			float currPercent = (float) currVolume / maxVolume;
			setSoundText(currPercent);
		}
	}

	private void setSoundText(float currPercent) {
		if (currPercent > 100) {
			currPercent = 100;
		}
		setText(String.format("%.0f%%", currPercent * 100));
	}

}
