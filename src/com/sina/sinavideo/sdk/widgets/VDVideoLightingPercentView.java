/**
 * 百分比方式显示亮度
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
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnLightingChangeListener;
import com.sina.sinavideo.sdk.utils.VDLog;

public final class VDVideoLightingPercentView extends TextView implements
		VDBaseWidget, OnLightingChangeListener {

	public VDVideoLightingPercentView(Context context, AttributeSet attrs) {
		super(context, attrs);

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
		if (controller == null)
			return;
		controller.addOnLightingChangeListener(this);
		setLigtingText(controller.getCurrLightingSetting());
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		VDVideoViewController controller = VDVideoViewController
				.getInstance(this.getContext());
		if (controller != null)
			controller.removeOnLightingChangeListener(this);
	}

	@Override
	public void onLightingChange(float curr) {
		// TODO Auto-generated method stub
		setLigtingText(curr);
	}

	private void setLigtingText(float curr) {
		VDLog.e("TAG", "curr:" + curr);
		setText(String.format("%.0f%%", curr * 100));
	}

}
