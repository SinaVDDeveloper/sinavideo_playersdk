package com.sina.sinavideo.sdk.container;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.sina.sinavideo.sdk.VDVideoViewController;
import com.sina.sinavideo.sdk.VDVideoViewListeners.eDoubleTouchListener;
import com.sina.sinavideo.sdk.VDVideoViewListeners.eHorizonScrollTouchListener;
import com.sina.sinavideo.sdk.VDVideoViewListeners.eSingleTouchListener;
import com.sina.sinavideo.sdk.VDVideoViewListeners.eVerticalScrollTouchListener;
import com.sina.sinavideo.sdk.dlna.DLNAEventListener;
import com.sina.sinavideo.sdk.utils.VDLog;
import com.sina.sinavideo.sdk.widgets.VDBaseWidget;
import com.sina.video_playersdkv2.R;

/**
 * 控制面板显示反馈布局 1. 快进\快退 2. 双击暂停\播放 3. 亮度调整 4. 音量调整
 * 
 * @author seven
 */
public class VDVideoControlPanelContainer extends View implements VDBaseWidget {

	@SuppressWarnings("unused")
	private Context mContext = null;

	private GestureDetector mGestureDetector = null;
	private VDVideoControlPanelGesture mVDVideoControlPanelGesture = null;
	private final static String TAG = "VDVideoControlPanelLayout";
	private int mLevel = -1;
	private boolean mIsVertical = false;
	private boolean mIsHorinzontal = false;
	private boolean mIsScrolling = false;
	private PointF mPrePoint = new PointF();

	private boolean mOperationExecuting = false;
	private eVerticalScrollTouchListener eFlag;

	public final int GESTURELEVELSINGLETAP = 1;
	public final int GESTURELEVELDOUBLETAP = 2;
	public final int GESTURELEVELHORIZONSCROLL = 4;
	public final int GESTURELEVELVERTICALSCROLL = 8;
	public final int GESTURELEVELHORIZONSCROLLLIGHTING = 16;
	public final int GESTURELEVELHORIZONSCROLLSOUND = 32;

	public VDVideoControlPanelContainer(Context context) {
		super(context);
		// 直接使用控件的话，就默认全支持
		init(context,
				(GESTURELEVELVERTICALSCROLL | GESTURELEVELHORIZONSCROLL
						| GESTURELEVELDOUBLETAP | GESTURELEVELSINGLETAP
						| GESTURELEVELHORIZONSCROLLLIGHTING | GESTURELEVELHORIZONSCROLLSOUND));
	}

	public VDVideoControlPanelContainer(Context context, AttributeSet attrs) {
		super(context, attrs);

		int level = -1;
		TypedArray typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.VDVideoControlPanelContainer);
		for (int i = 0; i < typedArray.getIndexCount(); i++) {
			if (typedArray.getIndex(i) == R.styleable.VDVideoControlPanelContainer_gestureLevel) {
				level = typedArray.getInt(i, -1);
			}
		}
		typedArray.recycle();

		// if (level == -1) {
		// level = (GESTURELEVELVERTICALSCROLL | GESTURELEVELHORIZONSCROLL
		// | GESTURELEVELDOUBLETAP | GESTURELEVELSINGLETAP
		// | GESTURELEVELHORIZONSCROLLLIGHTING |
		// GESTURELEVELHORIZONSCROLLSOUND);
		// }

		init(context, level);
	}

	public void mergeLevel(int level) {
		mLevel |= level;
	}

	private boolean checkLevel(int level) {
		if (mLevel < 0) {
			return false;
		}
		return ((mLevel & level) == level);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		return super.dispatchKeyEvent(event);
	}

	private void init(Context context, int level) {
		mContext = context;
		mLevel = level;

		mVDVideoControlPanelGesture = new VDVideoControlPanelGesture(context,
				level);
		mGestureDetector = new GestureDetector(context,
				mVDVideoControlPanelGesture);
		mGestureDetector.setIsLongpressEnabled(false);
	}

	private class VDVideoControlPanelGesture extends SimpleOnGestureListener {

		private Context mContext;

		public VDVideoControlPanelGesture(Context context, int level) {
			mContext = context;
		}

		@Override
		public boolean onDown(MotionEvent e) {
			VDLog.e(TAG, "onDown");
			mPrePoint = new PointF();
			// mPrePoint.set(e.getRawX(), e.getRawY());
			return true;
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			handleDoubleTap(e);
			VDLog.e(TAG, "onDoubleTap");
			return super.onDoubleTap(e);
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			handleSingleTap(e);
			VDLog.e(TAG, "onSingleTapConfirmed");
			DLNAEventListener.getInstance().notifyDLNAListHide();
			return super.onSingleTapConfirmed(e);
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			if (mPrePoint.equals(0.f, 0.f)) {
				mPrePoint.set(e1.getRawX(), e1.getRawY());
			}
			if (!mOperationExecuting) { // 滑动操作正在执行锁定其方向判断
				int fromDownX = (int) (e2.getX() - e1.getX());
				int fromDownY = (int) (e1.getY() - e2.getY());
				if (Math.abs(fromDownY) > 10F
						&& Math.abs(fromDownY) > Math.abs(fromDownX)) {
					mIsVertical = true;
					mOperationExecuting = true;
					if (checkLevel(GESTURELEVELVERTICALSCROLL)) {
						VDVideoViewController controller = VDVideoViewController
								.getInstance(mContext);
						if (controller != null)
							controller
									.touchScreenVerticalScrollEvent(
											new PointF(mPrePoint.x, mPrePoint.y),
											new PointF(e2.getRawX(), e2
													.getRawY()),
											new PointF(e1.getRawX(), e1
													.getRawY()),
											eVerticalScrollTouchListener.eTouchListenerVerticalScrollStart,
											distanceY);
					}
					if (checkLevel(GESTURELEVELVERTICALSCROLL)) {
						// 竖直滑动
						eFlag = eVerticalScrollTouchListener.eTouchListenerVerticalScroll;
					} else if (checkLevel(GESTURELEVELHORIZONSCROLLLIGHTING)) {
						// 亮度调整
						eFlag = eVerticalScrollTouchListener.eTouchListenerVerticalScrollLighting;
					} else if (checkLevel(GESTURELEVELHORIZONSCROLLSOUND)) {
						// 声音调整
						eFlag = eVerticalScrollTouchListener.eTouchListenerVerticalScrollSound;
					}
				} else if (Math.abs(fromDownX) > 10F
						&& Math.abs(fromDownX) > Math.abs(fromDownY)) {
					mIsHorinzontal = true;
					mOperationExecuting = true;
					if (checkLevel(GESTURELEVELHORIZONSCROLL)) {
						VDVideoViewController controller = VDVideoViewController
								.getInstance(mContext);
						if (controller != null)
							controller
									.touchScreenHorizonScrollEvent(
											new PointF(mPrePoint.x, mPrePoint.y),
											new PointF(e2.getRawX(), e2
													.getRawY()),
											new PointF(e1.getRawX(), e1
													.getRawY()),
											eHorizonScrollTouchListener.eTouchListenerHorizonScrollStart);
					}
				}
			} else if (mIsVertical) {
				handleVerticalScroll(new PointF(mPrePoint.x, mPrePoint.y),
						new PointF(e2.getRawX(), e2.getRawY()),
						new PointF(e1.getRawX(), e1.getRawY()), distanceY);
			} else if (mIsHorinzontal) {
				handleHorizonScroll(new PointF(mPrePoint.x, mPrePoint.y),
						new PointF(e2.getRawX(), e2.getRawY()),
						new PointF(e1.getRawX(), e1.getRawY()));
			}
			mIsScrolling = true;
			mPrePoint.set(e2.getRawX(), e2.getRawY());

			return super.onScroll(e1, e2, distanceX, distanceY);
		}
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (mGestureDetector.onTouchEvent(event)) {
			return true;
		}
		if (event.getAction() == MotionEvent.ACTION_UP
				|| event.getAction() == MotionEvent.ACTION_CANCEL) {
			if (mIsScrolling) {
				if (mIsVertical) {
					handleVerticalScrollFinish(null, null,
							new PointF(event.getRawX(), event.getRawY()), 0);
				} else if (mIsHorinzontal) {
					handleHorizonScrollFinish(new PointF(mPrePoint.x,
							mPrePoint.y),
							new PointF(event.getRawX(), event.getRawY()),
							new PointF(event.getRawX(), event.getRawY()));
				}
				mIsScrolling = false;
				VDLog.e(TAG, "ACTION_UP");
			}
			mIsVertical = false;
			mIsHorinzontal = false;
			mOperationExecuting = false;

		}
		return false;
	}

	private void handleSingleTap(MotionEvent e) {
		VDVideoViewController controller = VDVideoViewController
				.getInstance(this.getContext());
		if (controller == null)
			return;
		if (checkLevel(GESTURELEVELSINGLETAP)) {
			controller.touchScreenSingleEvent(e,
					eSingleTouchListener.eTouchListenerSingleTouchStart);
		}
		if (checkLevel(GESTURELEVELSINGLETAP)) {
			controller.touchScreenSingleEvent(e,
					eSingleTouchListener.eTouchListenerSingleTouch);
		}
		if (checkLevel(GESTURELEVELSINGLETAP)) {
			controller.touchScreenSingleEvent(e,
					eSingleTouchListener.eTouchListenerSingleTouchEnd);
		}
	}

	private void handleDoubleTap(MotionEvent e) {
		VDVideoViewController controller = VDVideoViewController
				.getInstance(this.getContext());
		if (controller == null) {
			return;
		}
		if (checkLevel(GESTURELEVELDOUBLETAP)) {
			controller.touchScreenDoubleEvent(e,
					eDoubleTouchListener.eTouchListenerDoubleTouchStart);
		}
		if (checkLevel(GESTURELEVELDOUBLETAP)) {
			controller.touchScreenDoubleEvent(e,
					eDoubleTouchListener.eTouchListenerDoubleTouch);
		}
		if (checkLevel(GESTURELEVELDOUBLETAP)) {
			controller.touchScreenDoubleEvent(e,
					eDoubleTouchListener.eTouchListenerDoubleTouchEnd);
		}
	}

	private void handleVerticalScroll(final PointF point1, final PointF point2,
			final PointF beginPoint, float distansY) {
		if (checkLevel(GESTURELEVELVERTICALSCROLL)) {
			VDVideoViewController controller = VDVideoViewController
					.getInstance(this.getContext());
			if (controller != null)
				controller.touchScreenVerticalScrollEvent(point1, point2,
						beginPoint, eFlag, distansY);
		}
	}

	private void handleVerticalScrollFinish(final PointF point1,
			final PointF point2, final PointF beginPoint, float distansY) {
		if (checkLevel(GESTURELEVELVERTICALSCROLL)) {
			VDVideoViewController controller = VDVideoViewController
					.getInstance(this.getContext());
			if (controller != null)
				controller
						.touchScreenVerticalScrollEvent(
								point1,
								point2,
								beginPoint,
								eVerticalScrollTouchListener.eTouchListenerVerticalScrollEnd,
								distansY);
		}
	}

	private void handleHorizonScroll(final PointF point1, final PointF point2,
			final PointF beginPoint) {
		if (checkLevel(GESTURELEVELHORIZONSCROLL)) {
			VDVideoViewController controller = VDVideoViewController
					.getInstance(this.getContext());
			if (controller != null)
				controller
						.touchScreenHorizonScrollEvent(
								point1,
								point2,
								beginPoint,
								eHorizonScrollTouchListener.eTouchListenerHorizonScroll);
		}
	}

	private void handleHorizonScrollFinish(final PointF point1,
			final PointF point2, final PointF beginPoint) {
		if (checkLevel(GESTURELEVELHORIZONSCROLL)) {
			VDVideoViewController controller = VDVideoViewController
					.getInstance(this.getContext());
			if (controller != null)
				controller
						.touchScreenHorizonScrollEvent(
								point1,
								point2,
								beginPoint,
								eHorizonScrollTouchListener.eTouchListenerHorizonScrollEnd);
		}
	}

}
