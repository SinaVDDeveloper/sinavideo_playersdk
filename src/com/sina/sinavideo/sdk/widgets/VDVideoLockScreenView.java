package com.sina.sinavideo.sdk.widgets;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.sina.sinavideo.sdk.VDVideoViewController;
import com.sina.sinavideo.sdk.VDVideoViewLayer;
import com.sina.sinavideo.sdk.VDVideoViewLayerContextData;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnFullScreenListener;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnScreenOrientationChangeListener;
import com.sina.sinavideo.sdk.utils.VDLog;
import com.sina.sinavideo.sdk.utils.VDVideoFullModeController;
import com.sina.video_playersdkv2.R;

/**
 * 锁屏 奇葩的需求，要放到外面的container才能搞定个，否则没办法控制横竖屏时候的显示与隐藏
 * 
 * @author sunxiao
 */
public final class VDVideoLockScreenView extends ImageView implements
		View.OnClickListener, OnFullScreenListener,
		OnScreenOrientationChangeListener, VDBaseWidget {

	// private boolean mRotateLocked = false;
	private boolean mIsShowLockView = true;
	private Activity mActivity;
	private ViewGroup mContainer = null;
	private ViewGroup mPreContainer = null;
	private final static int ORIENTATION_VERTICAL = 1;
	private final static int ORIENTATION_HORIZONTAL = 2;

	private int mOrientation = ORIENTATION_VERTICAL;

	/**
	 * 锁定图标
	 */
	private int mBackgroundCloseID = R.drawable.orientation_lock_close;
	/**
	 * 未锁定图标
	 */
	private int mBackGroundOpenID = R.drawable.orientation_lock_open;

	// 是否是横屏xml加载的控件
	private boolean mIsChecked = false;
	private boolean mIsVertical = false;

	public VDVideoLockScreenView(Context context) {
		super(context);
		setBackgroundResource(R.drawable.orientation_lock_open);
		init(context);
	}

	public VDVideoLockScreenView(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray typedArr = context.obtainStyledAttributes(attrs,
				R.styleable.VDVideoLockScreenView);

		if (typedArr != null) {
			for (int i = 0; i < typedArr.getIndexCount(); i++) {
				int resID = -1;
				if (typedArr.getIndex(i) == R.styleable.VDVideoLockScreenView_LockOpenImg) {
					resID = typedArr.getResourceId(
							R.styleable.VDVideoLockScreenView_LockOpenImg, -1);
					if (resID != -1) {
						mBackGroundOpenID = resID;
					}
				} else if (typedArr.getIndex(i) == R.styleable.VDVideoLockScreenView_LockCloseImg) {
					resID = typedArr.getResourceId(
							R.styleable.VDVideoLockScreenView_LockCloseImg, -1);
					if (resID != -1) {
						mBackgroundCloseID = resID;
					}
				} else if (typedArr.getIndex(i) == R.styleable.VDVideoLockScreenView_orientation) {
					int flag = typedArr.getInt(i, -1);
					if (flag != -1) {
						mOrientation = flag;
					}
				}
			}
			typedArr.recycle();
		}

		setBackground();

		init(context);
	}

	private void setBackground() {
		if (VDVideoFullModeController.getInstance().getIsScreenLock()) {
			setBackgroundResource(mBackGroundOpenID);
		} else {
			setBackgroundResource(mBackgroundCloseID);
		}
	}

	public VDVideoLockScreenView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		VDVideoViewController controller = VDVideoViewController
				.getInstance(context);
		if (controller != null)
			controller.addOnScreenOrientationChangeListener(this);
		mActivity = (Activity) context;
		setVisibility(GONE);
		registerListener();
	}

	private void registerListener() {
		setOnClickListener(this);
		VDVideoViewController controller = VDVideoViewController
				.getInstance(this.getContext());
		if (controller != null)
			controller.addOnFullScreenListener(this);
	}

	@Override
	public void onClick(View v) {

		if (VDVideoFullModeController.getInstance().getIsScreenLock()) {
			VDVideoFullModeController.getInstance().releaseFullLock();
		} else {
			VDVideoFullModeController.getInstance().setFullLock();
		}
		setBackground();
	}

	/**
	 * 显示
	 */
	private void showLockOreintationView() {
		VDVideoViewController controller = VDVideoViewController
				.getInstance(this.getContext());
		if (controller != null
				&& controller.getLayerContextData().getLayerType() == VDVideoViewLayerContextData.LAYER_COMPLEX_NOVERTICAL) {
			// 只横不竖的时候，不显示当前视图
			setVisibility(View.GONE);
			return;
		}
		setBackground();
		removeCallbacks(hideLockOreintationView);
		// if (mIsShowLockView) {
		if (mOrientation == ORIENTATION_VERTICAL && mContainer != null) {
			mContainer.removeView(this);
			mContainer.addView(this);
		}
		setVisibility(View.VISIBLE);
		postDelayed(hideLockOreintationView, 2500);
		// }
	}

	public void setShowLockView(boolean show) {
		this.mIsShowLockView = show;
	}

	public boolean isShowLockView() {
		return mIsShowLockView;
	}

	private Runnable hideLockOreintationView = new Runnable() {

		@Override
		public void run() {
			hideLockOreintationView();
		}
	};

	public void hideLockOreintationView() {
		setVisibility(View.GONE);
	}

	@Override
	public void onFullScreen(boolean isFullScreen, boolean isFromHand) {
		if (!isFullScreen && mOrientation == ORIENTATION_HORIZONTAL) {
			return;
		} else if (isFullScreen && mOrientation == ORIENTATION_VERTICAL) {
			return;
		}
		VDLog.i("screen_oreintation", "onFullScreen isFullScreen "
				+ isFullScreen + " , isFromHand = " + isFromHand
				+ " , mOreintation = " + mOrientation);

		if (isFromHand) {
			// 手动转屏，直接退出
			return;
		}
		if ((mIsVertical && !isFullScreen) || (!mIsVertical && isFullScreen)) {
			// 竖屏组件、竖屏时候
			// 横屏组件，横屏时候
			// 显示
			showLockOreintationView();
		} else {
			hideLockOreintationView();
		}
	}

	public void checkIsVerticalFromContainer(View view) {
		mIsChecked = true;
		ViewParent vp = view.getParent();
		if (vp == null) {
			return;
		}
		if (vp instanceof VDVideoViewLayer) {
			VDVideoViewLayer layer = (VDVideoViewLayer) vp;
			mIsVertical = layer.mIsVertical;
			return;
		}
		checkIsVerticalFromContainer((View) vp);
	}

	/**
	 * 将当前的这个屏幕锁，挪到最顶上的容器中，但如果这个容器不是relativelayout，就没法实现居中显示了
	 * 
	 * @param view
	 */
	public void changeToRoot(View view) {
		ViewGroup vg = (ViewGroup) mActivity.findViewById(android.R.id.content);
		if (vg != null) {
			try {
				mContainer = vg/* .getChildAt(0) */;
				if (mContainer != null) {
					mPreContainer.removeView(this);
					// ViewGroup.LayoutParams params = getLayoutParams();
					// setLayoutParams(null);
					ViewGroup.LayoutParams params = new FrameLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT, -2, Gravity.CENTER);
					mContainer.addView(this, params);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		// VDVideoViewController.getInstance().addOnFullScreenListener(this);
		if (!mIsChecked) {
			checkIsVerticalFromContainer(this);
		}

		// 测试一下，竖屏下面怎么获得上级view
		if (mIsVertical && mContainer == null) {
			mPreContainer = (ViewGroup) getParent();
			changeToRoot(this);
		}
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		// VDVideoViewController.getInstance().removeOnFullScreenListener(this);
	}

	@Override
	public void onScreenOrientationVertical() {
		VDLog.i("VDVideoLockScreenView", "onScreenOrientationVertical--> "
				+ mOrientation);
		boolean portrait = VDVideoFullModeController.getInstance()
				.getIsPortrait();
		boolean lock = VDVideoFullModeController.getInstance()
				.getIsScreenLock();
		if (mOrientation == ORIENTATION_HORIZONTAL) {
			if (!portrait && lock) {
				showLockOreintationView();
			}
			return;
		}
		if (portrait && lock)
			return;
		VDLog.i("VDVideoLockScreenView", "onScreenOrientationVertical-->");
		showLockOreintationView();
	}

	@Override
	public void onScreenOrientationHorizontal() {
		VDLog.i("VDVideoLockScreenView", "onScreenOrientationHorizontal--> "
				+ mOrientation);
		boolean portrait = VDVideoFullModeController.getInstance()
				.getIsPortrait();
		boolean lock = VDVideoFullModeController.getInstance()
				.getIsScreenLock();
		if (mOrientation == ORIENTATION_VERTICAL) {
			if (portrait && lock) {
				showLockOreintationView();
			}
			return;
		}
		if (!portrait && lock)
			return;
		VDLog.i("VDVideoLockScreenView", "onScreenOrientationHorizontal-->");
		showLockOreintationView();
	}

}
