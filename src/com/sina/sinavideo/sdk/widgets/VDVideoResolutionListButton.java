package com.sina.sinavideo.sdk.widgets;

import com.sina.sinavideo.sdk.VDVideoViewController;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnResolutionListButtonListener;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnResolutionListener;
import com.sina.sinavideo.sdk.data.VDResolutionData;
import com.sina.sinavideo.sdk.data.VDResolutionData.VDResolution;
import com.sina.sinavideo.sdk.data.VDVideoInfo;
import com.sina.sinavideo.sdk.dlna.DLNAController;
import com.sina.sinavideo.sdk.utils.VDResolutionManager;
import com.sina.sinavideo.sdk.utils.VDSharedPreferencesUtil;
import com.sina.video_playersdkv2.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * 清晰度列表中包含的按钮
 * 
 * @author alexsun
 * 
 */
public final class VDVideoResolutionListButton extends TextView implements
		VDBaseWidget, OnResolutionListButtonListener, OnResolutionListener {

	private Context mContext = null;
	private int mResolutionIndex = -1;
	// 当前的button表示的解析度tag
	protected String mResolutionTag = VDResolutionData.TYPE_DEFINITION_SD;
	// 经过处理后，用户当前选择的解析度tag
	// private String mSavedResolutionTag = VDResolutionData.TYPE_DEFINITION_SD;
	private VDResolution mResolution = null;

	// 流畅
	private final static int RESOLUTION_TAG_CIF = 1;
	// 标清
	private final static int RESOLUTION_TAG_SD = 2;
	// 高清
	private final static int RESOLUTION_TAG_HD = 3;
	// 超清
	private final static int RESOLUTION_TAG_FHD = 4;
	// 3D
	private final static int RESOLUTION_TAG_3D = 5;

	public VDVideoResolutionListButton(Context context) {
		super(context);
		init(context);
	}

	public VDVideoResolutionListButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 自定义背景
		TypedArray typedArr = context.obtainStyledAttributes(attrs,
				new int[] { android.R.attr.background });
		int background = R.drawable.quality_bg;
		if (typedArr != null) {
			int res = typedArr.getResourceId(0, -1);
			if (res != -1) {
				background = res;
			}
			typedArr.recycle();
		}
		setBackgroundResource(background);
		// 自定义属性部分
		typedArr = context.obtainStyledAttributes(attrs,
				R.styleable.VDVideoResolutionListButton);
		if (typedArr != null) {
			for (int i = 0; i < typedArr.getIndexCount(); i++) {
				if (typedArr.getIndex(i) == R.styleable.VDVideoResolutionListButton_resolutionTag) {
					mResolutionIndex = typedArr.getInt(i, -1);
				}
			}
			typedArr.recycle();
		}
		init(context);
	}

	private void init(Context context) {
		mContext = context;
		VDVideoViewController controller = VDVideoViewController
				.getInstance(context);
		if (controller == null) {
			return;
		}

		// 只能放这儿，不能放reset/hide里面，因为经常情绪度选择都在横屏，竖屏没有，所以，为了能在任何时间都收到通知，就只能一直常驻
		controller.addOnResolutionListener(this);
		controller.addOnResolutionListButtonListener(this);

		if (mResolutionIndex == RESOLUTION_TAG_CIF) {
			mResolutionTag = VDResolutionData.TYPE_DEFINITION_CIF;
		} else if (mResolutionIndex == RESOLUTION_TAG_SD) {
			mResolutionTag = VDResolutionData.TYPE_DEFINITION_SD;
		} else if (mResolutionIndex == RESOLUTION_TAG_HD) {
			mResolutionTag = VDResolutionData.TYPE_DEFINITION_HD;
		} else if (mResolutionIndex == RESOLUTION_TAG_FHD) {
			mResolutionTag = VDResolutionData.TYPE_DEFINITION_FHD;
		} else if (mResolutionIndex == RESOLUTION_TAG_3D) {
			mResolutionTag = VDResolutionData.TYPE_DEFINITION_3D;
		}

		// mSavedResolutionTag =
		// VDSharedPreferencesUtil.getCurResolution(context);

		registerListeners();
	}

	private void registerListeners() {
		setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				VDResolutionManager resolutionManager = VDResolutionManager
						.getInstance(mContext);
				if (resolutionManager == null) {
					return;
				}
				String currTag = resolutionManager.getCurrResolutionTag();
				if (currTag.equals(mResolutionTag)) {
					return;
				}

				// if (mSavedResolutionTag.equals(mResolutionTag)) {
				// return;
				// }
				VDVideoViewController controller = VDVideoViewController
						.getInstance(mContext);
				if (controller != null) {
					controller.notifyShowLoading();
					VDVideoInfo videoInfo = controller.getCurrentVideo();
					if (videoInfo == null) {
						return;
					}
					if (!videoInfo.mIsLive) {
						videoInfo.mVideoPosition = controller
								.getCurrentPosition();
						videoInfo.mNeedSeekTo = true;
					}
					// controller.setResolution(mResolutionTag);
					if (DLNAController.mIsDLNA) {
						DLNAController.getInstance(mContext).open(
								videoInfo.getVideoUrl(mResolutionTag));
					} else {
						controller.setVideoPath(videoInfo
								.getVideoUrl(mResolutionTag));
					}
					VDSharedPreferencesUtil.setResolution(mContext,
							mResolutionTag);
					controller.notifyResolutionContainerVisible(false);
					controller.notifyResolutionChanged(mResolutionTag);
				}
			}
		});
	}

	public void initResolution() {
		VDVideoViewController controller = VDVideoViewController
				.getInstance(mContext);
		if (controller != null) {
			try {
				VDResolutionManager resolutionManager = VDResolutionManager
						.getInstance(mContext);
				if (resolutionManager != null) {
					mResolution = resolutionManager.getCurrResolution();
					String tag = resolutionManager.getCurrResolutionTag();
					setClicked(tag);
				}
				refreshButtonText();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private void refreshButtonText() {
		if (mResolutionTag != null) {
			String resolutionText = VDResolutionData
					.getDefDescTextWithTag(mResolutionTag);
			if (resolutionText != null) {
				setText(resolutionText);
				setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		if (mResolution != null) {
			setVisibility(View.VISIBLE);
			refreshButtonText();
		} else {
			setVisibility(View.GONE);
		}
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		setVisibility(View.GONE);
	}

	@Override
	public void onResolutionListButtonFocusFirst() {
		// TODO Auto-generated method stub
		// 当遥控器焦点指到container的时候，要求里面的按钮中第一个要显示为focus状态
		VDVideoViewController controller = VDVideoViewController
				.getInstance(mContext);
		if (controller != null) {
			VDResolutionManager resolutionManager = VDResolutionManager
					.getInstance(mContext);
			if (resolutionManager != null) {
				VDResolution firstResolution = resolutionManager
						.getResolutionData().getFirstResolution();
				if (firstResolution != null
						&& firstResolution.getTag().equals(mResolutionTag)) {
					requestFocus();
				}
			}
		}
	}

	private void setClicked(String tag) {
		if (tag.equals(mResolutionTag)) {
			setFocusable(false);
			setPressed(true);
		} else {
			setFocusable(false);
			setPressed(false);
		}
	}

	@Override
	public void onResolutionChanged(String tag) {
		// TODO Auto-generated method stub
		setClicked(tag);
	}

	@Override
	public void onResolutionParsed(VDResolutionData list) {
		// TODO Auto-generated method stub
		initResolution();
	}

}
