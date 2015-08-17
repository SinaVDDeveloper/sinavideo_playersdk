package com.sina.sinavideo.sdk.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sina.sinavideo.sdk.VDVideoViewController;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnResolutionListener;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnShowHideBottomControllerListener;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnShowHideControllerListener;
import com.sina.sinavideo.sdk.data.VDResolutionData;
import com.sina.sinavideo.sdk.utils.VDLog;
import com.sina.sinavideo.sdk.utils.VDResolutionManager;
import com.sina.video_playersdkv2.R;

/**
 * 清晰度选择按钮
 * 
 * TODO 遥控器部分的响应事件没有做过测试，如果以后要用的话，要重新测试一下
 * 
 * @author liuqun
 * 
 */
public final class VDVideoResolutionButton extends TextView implements
		VDBaseWidget, OnResolutionListener, OnShowHideControllerListener,
		OnShowHideBottomControllerListener {

	// private VDVideoResolutionList mVDVideoResolutionList;
	private boolean mIsListVisible = false;
	private boolean mAlignCenter = true;
	private Context mContext = null;

	// private String mResolutionTag = VDResolutionData.TYPE_DEFINITION_SD;
	// private boolean mIsParsedResolution = false;

	public VDVideoResolutionButton(Context context) {
		super(context);
		init(context);
	}

	public VDVideoResolutionButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.ResolutionBackGround);
		mAlignCenter = a.getBoolean(
				R.styleable.ResolutionBackGround_alignCenter, true);
		a.recycle();
		registerListeners();
		init(context);
	}

	public VDVideoResolutionButton(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.ResolutionBackGround);
		mAlignCenter = a.getBoolean(
				R.styleable.ResolutionBackGround_alignCenter, true);
		a.recycle();
		registerListeners();
		init(context);
	}

	private void init(Context context) {
		mContext = context;
		if (getBackground() == null) {
			setBackgroundResource(R.drawable.quality_bg);
		}
		final float scale = getResources().getDisplayMetrics().density;
		setTextSize(TypedValue.COMPLEX_UNIT_PX, 12 * scale);
		setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					VDVideoViewController.getInstance(mContext)
							.notifyResolutionContainerVisible(false);
				}
			}
		});
		VDVideoViewController controller = VDVideoViewController
				.getInstance(this.getContext());
		if (controller != null) {
			controller.addOnShowHideControllerListener(this);
		}
		if (controller != null) {
			controller.addOnShowHideBottomControllerListener(this);
		}
		setVisibility(View.GONE);
	}

	private void setListButtonVisible(boolean isVisible) {
		VDVideoViewController controller = VDVideoViewController
				.getInstance(VDVideoResolutionButton.this.getContext());
		if (mAlignCenter) {
			updateResolutionPos();
		}
		if (controller != null) {
			controller.notifyResolutionContainerVisible(isVisible);
			mIsListVisible = isVisible;
		}
	}

	/**
	 * 为自己注册事件
	 */
	private void registerListeners() {
		// click事件
		this.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 如果没有解析出清晰度，那么当前点击无效
				VDResolutionManager resolutionManager = VDResolutionManager
						.getInstance(mContext);
				if (!resolutionManager.isParsed()) {
					return;
				}
				VDVideoViewController controller = VDVideoViewController
						.getInstance(VDVideoResolutionButton.this.getContext());
				if (controller != null && controller.isBottomPannelHiding()) {
					return;
				}

				if (mAlignCenter) {
					updateResolutionPos();
				}
				controller.notifyResolutionContainerVisible(!mIsListVisible);
				mIsListVisible = !mIsListVisible;
			}
		});

	}

	@Override
	public void reset() {
		VDVideoViewController controller = VDVideoViewController
				.getInstance(this.getContext());
		if (controller != null)
			controller.addOnResolutionListener(this);
	}

	@Override
	public void hide() {
		// this.setVisibility(View.GONE);
	}

	private int[] loc;

	private void updateResolutionPos() {
		if (loc == null)
			loc = new int[2];
		getLocationOnScreen(loc);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		VDLog.e("VDVideoResolutionButton", " onKeyUp keyCode = " + keyCode);
		VDVideoViewController controller = VDVideoViewController
				.getInstance(mContext);
		if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			VDLog.e("VDVideoResolutionButton",
					" onKeyUp KEYCODE_DPAD_UP 111111111");
			if (controller == null) {
				return super.onKeyUp(keyCode, event);
			}
			// mVDVideoResolutionList.focusFirstView();
			controller.notifyResolutionListButtonFocusFirst();
			int id = getNextFocusUpId();
			View v = ((ViewGroup) getParent()).findViewById(id);
			if (v != null) {
				VDLog.e("VDVideoResolutionButton", " onKeyUp KEYCODE_DPAD_UP");
				v.requestFocus();
			}
			// return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public void hideBottomControllerBar() {
		// TODO Auto-generated method stub
		setListButtonVisible(false);
	}

	@Override
	public void showBottomControllerBar() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doNotHideControllerBar() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hideControllerBar(long delay) {
		// TODO Auto-generated method stub
		setListButtonVisible(false);
	}

	@Override
	public void showControllerBar(boolean delayHide) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPostHide() {
		// TODO Auto-generated method stub
		setListButtonVisible(false);
	}

	@Override
	public void onPostShow() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPreHide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPreShow() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResolutionChanged(String tag) {
		// TODO Auto-generated method stub
		if (VDResolutionData.TYPE_DEFINITION_FHD.equals(tag)) {
			setText(R.string.super_definition);
		} else if (VDResolutionData.TYPE_DEFINITION_HD.equals(tag)) {
			setText(R.string.high_definition);
		} else if (VDResolutionData.TYPE_DEFINITION_SD.equals(tag)) {
			setText(R.string.base_definition);
		} else if (VDResolutionData.TYPE_DEFINITION_3D.equals(tag)) {
			setText(R.string.threed_definition);
		} else if (VDResolutionData.TYPE_DEFINITION_CIF.equals(tag)) {
			setText(R.string.cif_definition);
		}
		setVisibility(VISIBLE);

	}

	@Override
	public void onResolutionParsed(VDResolutionData list) {
		// TODO Auto-generated method stub
		if (list != null && list.getResolutionSize() > 0) {
			String tag = VDResolutionManager.getInstance(mContext)
					.getCurrResolutionTag();
			if (tag != null) {
				if (VDResolutionData.TYPE_DEFINITION_FHD.equals(tag)) {
					setText(R.string.super_definition);
				} else if (VDResolutionData.TYPE_DEFINITION_HD.equals(tag)) {
					setText(R.string.high_definition);
				} else if (VDResolutionData.TYPE_DEFINITION_SD.equals(tag)) {
					setText(R.string.base_definition);
				} else if (VDResolutionData.TYPE_DEFINITION_3D.equals(tag)) {
					setText(R.string.threed_definition);
				} else if (VDResolutionData.TYPE_DEFINITION_CIF.equals(tag)) {
					setText(R.string.cif_definition);
				}
			}
			setVisibility(VISIBLE);
		}
	}
}
