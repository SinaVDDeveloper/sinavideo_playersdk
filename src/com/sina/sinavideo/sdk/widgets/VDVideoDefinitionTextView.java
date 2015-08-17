package com.sina.sinavideo.sdk.widgets;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.sina.sinavideo.sdk.VDVideoViewController;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnVMSResolutionListener;
import com.sina.sinavideo.sdk.data.VDResolutionData;
import com.sina.sinavideo.sdk.utils.VDResolutionManager;
import com.sina.video_playersdkv2.R;

/**
 * 清晰度选择按钮部分，点击打开清晰度选择窗口
 * 
 * @deprecated 使用VDVideoResolutionListButton代替
 * @author seven
 */
public final class VDVideoDefinitionTextView extends TextView implements
		VDBaseWidget, OnVMSResolutionListener {

	private Context mContext = null;
	private int mContainerID = -1;

	public VDVideoDefinitionTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		mContext = context;

		// 指定相应的音量控制容器类
		TypedArray typedArr = context.obtainStyledAttributes(attrs,
				R.styleable.VDVideoDefinitionTextView);
		if (typedArr != null) {
			for (int i = 0; i < typedArr.getIndexCount(); i++) {
				if (typedArr.getIndex(i) == R.styleable.VDVideoDefinitionTextView_definitionContainer) {
					mContainerID = typedArr.getResourceId(i, -1);
				}
			}
			typedArr.recycle();
		}
		updateText();
		registerListeners();
	}

	@Override
	public void reset() {
		updateText();
	}

	@Override
	public void hide() {
	}

	/**
	 * 为自己注册事件
	 */
	private void registerListeners() {

		// click事件
		this.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mContainerID == -1) {
					return;
				}
				View container = ((Activity) mContext)
						.findViewById(mContainerID);
				if (container != null) {
					boolean isVisible = container.getVisibility() == View.GONE;
					VDVideoViewController controller = VDVideoViewController
							.getInstance(VDVideoDefinitionTextView.this
									.getContext());
					if (controller != null)
						controller.notifyDefinitionContainerVisible(isVisible);
				}
			}

		});

	}

	@Override
	public void onVMSResolutionContainerVisible(boolean isVisible) {
		if (isVisible) {
			updateText();
			setPressed(true);
		} else {
			setPressed(false);
		}
	}

	@Override
	public void onVMSResolutionChanged() {
		updateText();
	}

	/**
	 * 更新分辨率控制区的文字显示内容
	 */
	private void updateText() {
		VDVideoViewController controller = VDVideoViewController
				.getInstance(this.getContext());
		if (null == controller)
			return;
		VDResolutionManager resolutionManager = VDResolutionManager
				.getInstance(controller.getContext());
		if (resolutionManager != null) {
			String tag = resolutionManager.getCurrResolutionTag();
			setText(VDResolutionData.mDefDesc.get(tag));
		}
	}
}
