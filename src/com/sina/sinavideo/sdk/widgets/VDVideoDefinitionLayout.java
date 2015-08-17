package com.sina.sinavideo.sdk.widgets;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sina.sinavideo.sdk.VDVideoViewController;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnVMSResolutionListener;
import com.sina.sinavideo.sdk.data.VDResolutionData;

/**
 * 清晰度选择窗口
 * 
 * @deprecated 用VDVideoResolutionList代替
 * @author seven
 * 
 */
public final class VDVideoDefinitionLayout extends LinearLayout implements
		VDBaseWidget, OnVMSResolutionListener {

	private Context mContext = null;

	public VDVideoDefinitionLayout(Context context) {
		super(context);
		init(context, null);
	}

	public VDVideoDefinitionLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		mContext = context;
		setOrientation(LinearLayout.VERTICAL);
		registerListener();
	}

	private void registerListener() {
		VDVideoViewController controller = VDVideoViewController
				.getInstance(this.getContext());
		if (null != controller)
			controller.addOnVMSResolutionListener(this);
	}

	@Override
	public void reset() {
	}

	@Override
	public void hide() {
		removeAllViews();
	}

	@Override
	public void onVMSResolutionContainerVisible(boolean isVisible) {
		VDVideoViewController controller = VDVideoViewController
				.getInstance(this.getContext());
		if (controller == null)
			return;
		HashMap<String, String> vmsResolutionInfo = controller
				.getCurrVMSResolutionInfo();
		if (vmsResolutionInfo != null) {
			removeAllViews();
			Set<String> keys = vmsResolutionInfo.keySet();
			addItems(keys);
		}
		onSizeChanged(getWidth(), getHeight(), 0, 0);
	}

	@Override
	public void onVMSResolutionChanged() {
		onSizeChanged(getWidth(), getHeight(), 0, 0);
	}

	private void addItems(Set<String> keys) {
		// LinkedHashMap<String, String> desc = VDVMSVideoRequest.mDefDesc;
		LinkedHashMap<String, String> desc = VDResolutionData.mDefDesc;
		for (Iterator<String> iterator = desc.keySet().iterator(); iterator
				.hasNext();) {
			final String key = iterator.next();
			if (keys.contains(key)) {
				TextView view = new TextView(mContext);
				view.setText(desc.get(key));
				view.setTextSize(24);
				LayoutParams params = new LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				view.setPadding(0, 20, 20, 0);
				view.setLayoutParams(params);
				view.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						VDVideoViewController controller = VDVideoViewController
								.getInstance(VDVideoDefinitionLayout.this
										.getContext());
						if (controller == null) {
							return;
						}
						long sec = controller.getCurrentPosition();
						controller.changeResolution(sec, key);
					}
				});
				addView(view);
			}
		}

	}
}
