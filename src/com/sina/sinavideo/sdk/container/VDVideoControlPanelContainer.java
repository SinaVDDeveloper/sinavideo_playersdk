
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

    public VDVideoControlPanelContainer(Context context) {
        super(context);
        init(context, (8 | 4 | 2 | 1));
    }

    public VDVideoControlPanelContainer(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.VDVideoControlPanelContainer);
        for (int i = 0; i < typedArray.getIndexCount(); i++) {
            if (typedArray.getIndex(i) == R.styleable.VDVideoControlPanelContainer_gestureLevel) {
                mLevel = typedArray.getInt(i, -1);
            }
            // switch (typedArray.getIndex(i)) {
            // default :
            // break;
            // case R.styleable.VDVideoControlPanelContainer_gestureLevel :
            // mLevel = typedArray.getInt(i, -1);
            // break;
            // }
        }
        typedArray.recycle();

        init(context, mLevel);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // TODO Auto-generated method stub
        return super.dispatchKeyEvent(event);
    }

    private void init(Context context, int level) {
        mContext = context;
        mVDVideoControlPanelGesture = new VDVideoControlPanelGesture(context, level);
        mGestureDetector = new GestureDetector(context, mVDVideoControlPanelGesture);
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
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (mPrePoint.equals(0.f, 0.f)) {
                mPrePoint.set(e1.getRawX(), e1.getRawY());
            }
            if (!mOperationExecuting) { // 滑动操作正在执行锁定其方向判断
                int fromDownX = (int) (e2.getX() - e1.getX());
                int fromDownY = (int) (e1.getY() - e2.getY());
                if (Math.abs(fromDownY) > 10F && Math.abs(fromDownY) > Math.abs(fromDownX)) {
                    mIsVertical = true;
                    mOperationExecuting = true;
                    if (mLevel > 0) {
                        VDVideoViewController controller = VDVideoViewController.getInstance(mContext);
                        if (controller != null)
                            controller.touchScreenVerticalScrollEvent(new PointF(mPrePoint.x, mPrePoint.y), new PointF(
                                    e2.getRawX(), e2.getRawY()), new PointF(e1.getRawX(), e1.getRawY()),
                                    eVerticalScrollTouchListener.eTouchListenerVerticalScrollStart, distanceY);
                    }
                    if ((mLevel & 8) == 8) {
                        // 竖直滑动
                        eFlag = eVerticalScrollTouchListener.eTouchListenerVerticalScroll;
                    } else if ((mLevel & 16) == 16) {
                        eFlag = eVerticalScrollTouchListener.eTouchListenerVerticalScrollLighting;
                    } else if ((mLevel & 32) == 32) {
                        eFlag = eVerticalScrollTouchListener.eTouchListenerVerticalScrollSound;
                    }
                } else if (Math.abs(fromDownX) > 10F && Math.abs(fromDownX) > Math.abs(fromDownY)) {
                    mIsHorinzontal = true;
                    mOperationExecuting = true;
                    if (mLevel > 0) {
                        VDVideoViewController controller = VDVideoViewController.getInstance(mContext);
                        if (controller != null)
                            controller.touchScreenHorizonScrollEvent(new PointF(mPrePoint.x, mPrePoint.y), new PointF(
                                    e2.getRawX(), e2.getRawY()), new PointF(e1.getRawX(), e1.getRawY()),
                                    eHorizonScrollTouchListener.eTouchListenerHorizonScrollStart);
                    }
                }
            } else if (mIsVertical) {
                handleVerticalScroll(new PointF(mPrePoint.x, mPrePoint.y), new PointF(e2.getRawX(), e2.getRawY()),
                        new PointF(e1.getRawX(), e1.getRawY()), distanceY);
            } else if (mIsHorinzontal) {
                handleHorizonScroll(new PointF(mPrePoint.x, mPrePoint.y), new PointF(e2.getRawX(), e2.getRawY()),
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
        if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            if (mIsScrolling) {
                if (mIsVertical) {
                    handleVerticalScrollFinish(null, null, new PointF(event.getRawX(), event.getRawY()), 0);
                } else if (mIsHorinzontal) {
                    handleHorizonScrollFinish(new PointF(mPrePoint.x, mPrePoint.y),
                            new PointF(event.getRawX(), event.getRawY()), new PointF(event.getRawX(), event.getRawY()));
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
        VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
        if (controller == null)
            return;
        if (mLevel >= 0) {
            controller.touchScreenSingleEvent(e, eSingleTouchListener.eTouchListenerSingleTouchStart);
        }
        if ((mLevel & 1) == 1) {
            controller.touchScreenSingleEvent(e, eSingleTouchListener.eTouchListenerSingleTouch);
        }
        if (mLevel > 0) {
            controller.touchScreenSingleEvent(e, eSingleTouchListener.eTouchListenerSingleTouchEnd);
        }
    }

    private void handleDoubleTap(MotionEvent e) {
        VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
        if (controller == null) {
            return;
        }
        if (mLevel >= 0) {
            controller.touchScreenDoubleEvent(e, eDoubleTouchListener.eTouchListenerDoubleTouchStart);
        }
        if ((mLevel & 2) == 2) {
            controller.touchScreenDoubleEvent(e, eDoubleTouchListener.eTouchListenerDoubleTouch);
        }
        if (mLevel > 0) {
            controller.touchScreenDoubleEvent(e, eDoubleTouchListener.eTouchListenerDoubleTouchEnd);
        }
    }

    private void handleVerticalScroll(final PointF point1, final PointF point2, final PointF beginPoint, float distansY) {
        if (eFlag != null) {
            VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
            if (controller != null)
                controller.touchScreenVerticalScrollEvent(point1, point2, beginPoint, eFlag, distansY);
        }
    }

    private void handleVerticalScrollFinish(final PointF point1, final PointF point2, final PointF beginPoint,
            float distansY) {
        if (mLevel > 0) {
            VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
            if (controller != null)
                controller.touchScreenVerticalScrollEvent(point1, point2, beginPoint,
                        eVerticalScrollTouchListener.eTouchListenerVerticalScrollEnd, distansY);
        }
    }

    private void handleHorizonScroll(final PointF point1, final PointF point2, final PointF beginPoint) {
        if ((mLevel & 4) == 4) {
            VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
            if (controller != null)
                controller.touchScreenHorizonScrollEvent(point1, point2, beginPoint,
                        eHorizonScrollTouchListener.eTouchListenerHorizonScroll);
        }
    }

    private void handleHorizonScrollFinish(final PointF point1, final PointF point2, final PointF beginPoint) {
        if (mLevel > 0) {
            VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
            if (controller != null)
                controller.touchScreenHorizonScrollEvent(point1, point2, beginPoint,
                        eHorizonScrollTouchListener.eTouchListenerHorizonScrollEnd);
        }
    }

}
