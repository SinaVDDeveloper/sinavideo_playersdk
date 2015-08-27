package com.sina.sinavideo.sdk;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.sina.sinavideo.coreplayer.ISinaVideoView;
import com.sina.sinavideo.coreplayer.splayer.VideoView;
import com.sina.sinavideo.coreplayer.splayer.VideoViewHard;
import com.sina.sinavideo.dlna.SinaDLNA;
import com.sina.sinavideo.sdk.VDVideoExtListeners.OnVDPlayerTypeSwitchListener;
import com.sina.sinavideo.sdk.VDVideoExtListeners.OnVDVideoCompletionListener;
import com.sina.sinavideo.sdk.VDVideoExtListeners.OnVDVideoErrorListener;
import com.sina.sinavideo.sdk.VDVideoExtListeners.OnVDVideoFrameADListener;
import com.sina.sinavideo.sdk.VDVideoExtListeners.OnVDVideoInfoListener;
import com.sina.sinavideo.sdk.VDVideoExtListeners.OnVDVideoInsertADEndListener;
import com.sina.sinavideo.sdk.VDVideoExtListeners.OnVDVideoInsertADListener;
import com.sina.sinavideo.sdk.VDVideoExtListeners.OnVDVideoPlayerChangeListener;
import com.sina.sinavideo.sdk.VDVideoExtListeners.OnVDVideoPlaylistListener;
import com.sina.sinavideo.sdk.VDVideoExtListeners.OnVDVideoPreparedListener;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnRegisterDLNAListener;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnVideoUIRefreshListener;
import com.sina.sinavideo.sdk.data.VDPlayerInfo;
import com.sina.sinavideo.sdk.data.VDVideoInfo;
import com.sina.sinavideo.sdk.data.VDVideoListInfo;
import com.sina.sinavideo.sdk.dlna.DLNAController;
import com.sina.sinavideo.sdk.dlna.DLNAEventListener;
import com.sina.sinavideo.sdk.dlna.DLNAEventListener.OnMediaControllerListener;
import com.sina.sinavideo.sdk.utils.VDLog;
import com.sina.sinavideo.sdk.utils.VDApplication;
import com.sina.sinavideo.sdk.utils.VDPlayPauseHelper;
import com.sina.sinavideo.sdk.utils.VDPlayerSoundManager;
import com.sina.sinavideo.sdk.utils.VDSharedPreferencesUtil;
import com.sina.sinavideo.sdk.utils.VDUtility;
import com.sina.sinavideo.sdk.utils.VDVideoFullModeController;
import com.sina.sinavideo.sdk.utils.VDVideoScreenOrientation;
import com.sina.sinavideo.sdk.widgets.VDVideoADTicker;
import com.sina.video_playersdkv2.R;

/**
 * 视频SDK的基本容器类，是一个FrameLayout，一般包含三层，按照从下往上的顺序：<br>
 * 第一层：自定义的VideoView，只是用来播放视频<br>
 * 第二层：广告显示层：广告、Tips、教程等显示<br>
 * 第三层：控制层：一堆按钮集合，在这一层实现所有的控制单元<br>
 * 
 * @author sunxiao1@staff.sina.com.cn
 */
public class VDVideoView extends FrameLayout implements OnRegisterDLNAListener,
		OnVideoUIRefreshListener {

	private final String TAG = "VDVideoView";
	private Context mContext = null;
	private VDVideoViewLayerContextData mVDVideoViewLayerData = new VDVideoViewLayerContextData();
	private ISinaVideoView mVideoView = null;
	/** 全屏的时候 VDVideoView 的直接父亲 */
	private LinearLayout mVideoFullScreenContainer = null;
	private ViewGroup.LayoutParams mVideoViewParams = null;
	/** 小屏时VDVideoView 的直接父亲 */
	private ViewGroup mVDVideoViewContainer = null;

	private VDPlayPauseHelper mVDPlayPauseHelper;

	private ViewGroup mExternalFullScreenContainer;
	// 单独拎出来一个广告跳数组件，如果没有这个玩意儿，估计会出问题
	@SuppressLint("nouse")
	private VDVideoADTicker mTickerView = null;

	/**
	 * 是否允许使用popupwindow方式播放视频
	 */
	private final static int POPWINDOW_RIGHTBOTTOM = 0;
	@SuppressLint("nouse")
	private final static int POPWINDOW_LEFTBOTTOM = 1;
	@SuppressLint("nouse")
	private final static int POPWINDOW_CENTERBOTTOM = 2;
	@SuppressLint("nouse")
	private int mPopupWindowType = POPWINDOW_RIGHTBOTTOM;
	private boolean mIsCanPopupWindow = false;
	private int mPopupWindowWidth = 0;
	private int mPopupWindowHeight = 0;
	private PopupWindow mPopupWindow = null;

	/**
	 * 构造函数，这种情况下，无法自动加载xml<br />
	 * 适用于手动加载的情况下，new完毕后，使用：setLayers 函数来加载响应的layer
	 * 
	 * @param context
	 */
	public VDVideoView(Context context) {
		super(context);
		VDApplication.getInstance().setContext(context);
		mContext = context;
		setBackgroundColor(0x0);

		registerController(context);
		init();

		if (mVideoView == null) {
			mVideoView = VDVideoViewController.create(context);
			VDVideoViewController controller = VDVideoViewController
					.getInstance(context);
			if (controller != null)
				controller.setContext(context);
			initVideo();
		}

		if (((View) mVideoView).getParent() == null) {
			addVideoView(mVideoView);
		}
	}

	/**
	 * 构造函数
	 * 
	 * @param context
	 * @param attrs
	 */
	public VDVideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		VDApplication.getInstance().setContext(context);
		mContext = context;
		setBackgroundColor(0x0);
		registerController(context);
		TypedArray typedArr = context.obtainStyledAttributes(attrs,
				R.styleable.VDVideoView);

		initLayer(typedArr);
		typedArr.recycle();
		init();

		if (mVideoView == null) {
			mVideoView = VDVideoViewController.create(context);
			VDVideoViewController controller = VDVideoViewController
					.getInstance(context);
			if (controller != null)
				controller.setContext(context);
			initVideo();
		}

		if (((View) mVideoView).getParent() == null) {
			addVideoView(mVideoView);
		}
	}

	/**
	 * 构造函数
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public VDVideoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		VDApplication.getInstance().setContext(context);
		mContext = context;
		setBackgroundColor(0x0);

		registerController(context);

		TypedArray typedArr = context.obtainStyledAttributes(attrs,
				R.styleable.VDVideoView, defStyle, 0);
		initLayer(typedArr);
		typedArr.recycle();
		init();

		if (mVideoView == null) {
			mVideoView = VDVideoViewController.create(context);
			VDVideoViewController cnotroller = VDVideoViewController
					.getInstance(context);
			if (cnotroller != null)
				cnotroller.setContext(context);
			initVideo();
		}

		if (((View) mVideoView).getParent() == null) {
			addVideoView(mVideoView);
		}

	}

	/**
	 * 生成一个新的controller并注册到VDVideoViewController静态MAP中去
	 */
	private void registerController(Context context) {
		VDVideoViewController controller = VDVideoViewController
				.getInstance(context);
		if (controller == null) {
			controller = new VDVideoViewController(context);
			VDVideoViewController.register(context, controller);
		}
	}

	/**
	 * 从VDVideoViewController静态MAP中去掉一个已经注册的controller
	 */
	private void unRegisterController(Context context) {
		VDVideoViewController.unRegister(context);
	}

	/**
	 * 初始化数据
	 */
	public void init() {
		mVideoFullScreenContainer = new LinearLayout(mContext);
		mVideoFullScreenContainer.setOrientation(LinearLayout.HORIZONTAL);

		mVideoFullScreenContainer.setBackgroundColor(0x000000);
		mVideoFullScreenContainer.setVisibility(View.GONE);
		mVDPlayPauseHelper = new VDPlayPauseHelper(getContext());
		VDVideoViewController controller = VDVideoViewController
				.getInstance(mContext);
		if (controller != null)
			controller.addOnRegisterDLNAListener(this);
		if (controller != null)
			controller.addOnVideoUIRefreshListener(this);

		// 初始化popupwindow
		if (mIsCanPopupWindow) {
			mPopupWindow = new PopupWindow(this, mPopupWindowWidth,
					mPopupWindowHeight, true);
			mPopupWindow.setFocusable(true);
		}
	}

	/**
	 * 初始化屏幕部分，保证横屏进入正常
	 */
	public void initVideo() {
		// 全屏容器
		boolean isFullScreen = false;
		// @sunxiao1 modify ，加入只横不竖方式，直接横屏设置过去
		if (VDVideoScreenOrientation.getIsLandscape(mContext)
				|| mVDVideoViewLayerData.getLayerType() == VDVideoViewLayerContextData.LAYER_COMPLEX_NOVERTICAL) {
			isFullScreen = true;
		}
		setIsFullScreen(isFullScreen, true);
		VDVideoFullModeController.getInstance().setIsFullScreen(isFullScreen);
		// 设置音量部分
		for (VDVideoViewLayerContext context : mVDVideoViewLayerData
				.getLayerList()) {
			if (context.checkSoundWidget()) {
				VDVideoViewController controller = VDVideoViewController
						.getInstance(mContext);
				if (controller != null)
					controller.mIsHasSoundWidget = true;
				break;
			}
		}
	}

	/**
	 * 设置当前vdvideoview的父类容器，[只能手动指定，否则，再第一次进入即为横屏时候，无法处理]
	 * 
	 * @param container
	 */
	public void setVDVideoViewContainer(ViewGroup container) {
		mVDVideoViewContainer = container;
	}

	/**
	 * 设置帧间广告回调
	 * 
	 * @param l
	 */
	public void setFrameADListener(OnVDVideoFrameADListener l) {
		VDVideoViewController controller = VDVideoViewController
				.getInstance(mContext);
		if (controller != null && controller.getExtListener() != null) {
			controller.getExtListener().setFrameADListener(l);
		}
	}

	/**
	 * 设置前贴片广告（插入广告）回调
	 * 
	 * @param l
	 */
	public void setInsertADListener(OnVDVideoInsertADListener l) {
		VDVideoViewController controller = VDVideoViewController
				.getInstance(mContext);
		if (controller != null && controller.getExtListener() != null) {
			controller.getExtListener().setInsertADListener(l);
		}
	}

	/**
	 * 设置播放列表回调
	 * 
	 * @param l
	 */
	public void setPlaylistListener(OnVDVideoPlaylistListener l) {
		VDVideoViewController controller = VDVideoViewController
				.getInstance(mContext);
		if (controller != null)
			controller.getExtListener().setPlaylistListener(l);
	}

	public void setPlayerChangeListener(OnVDVideoPlayerChangeListener l) {
		VDVideoViewController controller = VDVideoViewController
				.getInstance(mContext);
		if (controller != null) {
			controller.getExtListener().setOnVDVideoPlayerChangeListener(l);
		}
	}

	public void setOnVDPlayerTypeSwitchListener(OnVDPlayerTypeSwitchListener l) {
		VDVideoViewController controller = VDVideoViewController
				.getInstance(mContext);
		if (controller != null)
			controller.getExtListener().setOnVDPlayerTypeSwitchListener(l);
	}

	public void setCompletionListener(OnVDVideoCompletionListener l) {
		VDVideoViewController controller = VDVideoViewController
				.getInstance(mContext);
		if (controller != null)
			controller.getExtListener().setOnVDVideoCompletionListener(l);
	}

	public void setPreparedListener(OnVDVideoPreparedListener l) {
		VDVideoViewController controller = VDVideoViewController
				.getInstance(mContext);
		if (controller != null)
			controller.getExtListener().setOnVDVideoPreparedListener(l);
	}

	public void setErrorListener(OnVDVideoErrorListener l) {
		VDVideoViewController controller = VDVideoViewController
				.getInstance(mContext);
		if (controller != null)
			controller.getExtListener().setOnVDVideoErrorListener(l);
	}

	public void setInfoListener(OnVDVideoInfoListener l) {
		VDVideoViewController controller = VDVideoViewController
				.getInstance(mContext);
		if (controller != null)
			controller.getExtListener().setOnVDVideoInfoListener(l);
	}

	public void setInsertADEndListener(OnVDVideoInsertADEndListener l) {
		VDVideoViewController controller = VDVideoViewController
				.getInstance(mContext);
		if (controller != null)
			controller.getExtListener().setOnVDVideoInsertADEndListener(l);
	}

	/**
	 * 建立视频，使用VDVideoListInfo方式
	 * 
	 * @param context
	 * @param infoList
	 */
	public void open(Context context, VDVideoListInfo infoList) {
		if (mVideoView == null) {
			mVideoView = VDVideoViewController.create(context);
			VDVideoViewController controller = VDVideoViewController
					.getInstance(context);
			if (controller != null) {
				controller.setContext(context);
			}
		}
		initVideo();
		VDVideoViewController cnotroller = VDVideoViewController
				.getInstance(context);
		if (cnotroller != null)
			cnotroller.setVideoList(infoList);
		if (((View) mVideoView).getParent() == null) {
			addVideoView(mVideoView);
		}
	}

	/**
	 * 建立视频
	 * 
	 * @param context
	 * @param path
	 */
	public void open(Context context, VDVideoInfo path) {
		if (mVideoView == null) {
			mVideoView = VDVideoViewController.create(context);
			VDVideoViewController controller = VDVideoViewController
					.getInstance(context);
			if (controller != null)
				controller.setContext(context);
		}
		initVideo();
		VDVideoListInfo infoList = new VDVideoListInfo();
		infoList.addVideoInfo(path);
		VDVideoViewController controller = VDVideoViewController
				.getInstance(context);
		if (controller != null)
			controller.setVideoList(infoList);
		if (((View) mVideoView).getParent() == null) {
			addVideoView(mVideoView);
		}
	}

	/**
	 * 开始播放视频
	 * 
	 * @param index
	 */
	public void play(int index) {
		VDVideoViewController controller = VDVideoViewController
				.getInstance(mContext);
		if (controller != null && controller.mVDVideoListInfo == null) {
			return;
		}
		initVideo();

		if (controller != null)
			controller.playVideo(index);
	}

	/**
	 * 按照给定的position来播放视频，适合历史记录等情况下
	 * 
	 * @param index
	 * @param position
	 */
	public void play(int index, long position) {
		if (position > 0) {
			VDVideoViewController controller = VDVideoViewController
					.getInstance(mContext);
			if (controller != null)
				controller.getCurrentVideo().mNeedSeekTo = true;
			if (controller != null)
				controller.getCurrentVideo().mVideoPosition = position;
		}
		play(index);
	}

	/**
	 * VDVideoListInfo的VDVideoview包装版本
	 * 
	 * @param insertADList
	 * @param currInfo
	 */
	public void refreshInsertADList(List<VDVideoInfo> insertADList,
			VDVideoInfo currInfo) {
		VDVideoViewController controller = VDVideoViewController
				.getInstance(mContext);
		if (controller != null)
			controller.refreshInsertADList(insertADList, currInfo);
	}

	/**
	 * 释放资源
	 * 
	 * @param isOnlyReloadVideo
	 */
	public void release(boolean isOnlyReloadVideo) {
		removeView((View) mVideoView);
		VDVideoViewController controller = VDVideoViewController
				.getInstance(mContext);
		if (isOnlyReloadVideo) {
			if (controller != null)
				controller.release2();
			mVideoView = null;
		} else {
			if (controller != null)
				controller.release();
		}
		DLNAController.mIsDLNA = false;
		unregisterOnDLNAMediaControllerListener();
		if (controller != null)
			controller.removeOnRegisterDLNAListener(this);
		if (controller != null)
			controller.removeOnVideoUIRefreshListener(this);
		if (false == isOnlyReloadVideo) {
			unRegisterController(mContext);
		}
	}

	/**
	 * 释放转屏部分
	 */
	public void unRegisterSensorManager() {
		if (VDVideoFullModeController.getInstance() != null) {
			VDVideoFullModeController.getInstance().unRegisterSensorManager();
		}
	}

	/**
	 * 手动结束
	 */
	public void stop() {
		VDVideoViewController controller = VDVideoViewController
				.getInstance(mContext);
		if (null != controller) {
			controller.pause();
			controller.stop();
		}

	}

	/**
	 * 是否使用自定义音量滑块<br>
	 * 注：如果不实用音量滑块组件，则默认为系统提供的音量提示
	 * 
	 * @return
	 */
	public boolean hasSoundWidget() {
		return true;
	}

	/**
	 * 设置父容器，用来做全屏转换<br />
	 * 有时候，播放器会嵌入到很奇怪的vg中，比如：fragment等<br/>
	 * 默认，SDK会自己寻找decoreView为父类容器<br />
	 * 但在特殊情况下，找到的decoreView不是正确的父容器，这时候需要手工指定
	 * 
	 * @param vg
	 */
	public void setExternalFullContainer(ViewGroup vg) {
		mExternalFullScreenContainer = vg;
	}

	/**
	 * 手工加载layerAttrs指向的TypedArray
	 * 
	 * @param resourceID
	 *            resources-array的name值
	 */
	public void setLayers(int resourceID) {
		if (resourceID <= 0) {
			throw new IllegalArgumentException("setLayers's resourceID<=0");
		}

		readLayerAttrs(resourceID);
	}

	/**
	 * 得到层的类型：<br/>
	 * 复杂模式，横竖全/只横不竖 <br/>
	 * 简单模式，只竖不横
	 */
	public int getLayerType() {
		return VDVideoViewLayerContextData.LAYER_COMPLEX_ALL;
	}

	/**
	 * 将当前给定的容器，提升到activity的顶层容器中
	 * 
	 * @param view
	 */
	private void changeToRoot(View view) {
		if (mContext == null || view == null) {
			return;
		}
		Activity activity = (Activity) mContext;
		ViewGroup vgRoot = null;
		if (mExternalFullScreenContainer == null) {
			vgRoot = (ViewGroup) activity.findViewById(android.R.id.content);
		} else {
			vgRoot = mExternalFullScreenContainer;
			mExternalFullScreenContainer.setVisibility(View.VISIBLE);
		}
		if (vgRoot != null) {
			try {
				ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT);
				vgRoot.addView(view, lp);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * 具体的转屏执行函数--使用父窗口方式
	 * 
	 * @param container
	 * @param isFullScreen
	 */
	private void setIsFullModeUsingContainer(ViewGroup container,
			boolean isFullScreen) {
		if (container == null) {
			VDLog.e(VDVideoFullModeController.TAG,
					"videoview---setIsFullMode---container--return null");
			throw new IllegalArgumentException("container is null");
		}
		if (mVideoViewParams == null) {
			VDLog.e(VDVideoFullModeController.TAG,
					"videoview---setIsFullMode---mVideoViewParams--return null");
			mVideoViewParams = getLayoutParams();
		}
		mVideoView.beginChangeParentView();

		if (mVideoFullScreenContainer != null) {
			mVideoFullScreenContainer.removeAllViews();
		}

		if (mVideoFullScreenContainer.getParent() == null) {
			changeToRoot(mVideoFullScreenContainer);
		}
		VDVideoViewController controller = VDVideoViewController
				.getInstance(mContext);
		if (controller != null) {
			controller.notifyScreenOrientationSwitch(isFullScreen);
			if (mVideoView.isPlaying()) {
				controller.notifyOnShowHideADContainer(true);
			}
		}
		if (isFullScreen) {
			if (mExternalFullScreenContainer != null) {
				mExternalFullScreenContainer.setVisibility(VISIBLE);
			}
			// 横屏
			VDVideoScreenOrientation.setStatusBarVisible(mContext, true);
			mVideoFullScreenContainer.setVisibility(View.VISIBLE);

			container.removeView(this);
			mVideoFullScreenContainer.addView(this,
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT);
		} else {
			if (mExternalFullScreenContainer != null) {
				mExternalFullScreenContainer.setVisibility(GONE);
			}
			VDVideoScreenOrientation.setStatusBarVisible(mContext, false);
			mVideoFullScreenContainer.setVisibility(View.GONE);
			// 竖屏
			if (getParent() == null) {
				container.addView(this, mVideoViewParams);
			}
		}
		for (VDVideoViewLayerContext item : mVDVideoViewLayerData
				.getLayerList()) {
			if (item.mIsComplexLayerType) {
				// 有复杂模式才进行横竖屏转换
				if (controller != null)
					item.setFullMode(isFullScreen, controller.isInsertAD());
			}
		}
		mVideoView.endChangeParentView();
		mVideoView.requestVideoLayout();
	}

	/**
	 * 增加一个私有函数，用来做initVideo时候的调用专用
	 * 
	 * @param isFullScreen
	 * @param isInited
	 */
	private void setIsFullScreen(boolean isFullScreen, boolean isInited) {
		if (mVDVideoViewContainer == null) {
			VDLog.e(VDVideoFullModeController.TAG,
					"videoview---setIsFullScreen---mVDVideoViewContainer--return null");
			return;
		}

		if (mVDVideoViewLayerData.getLayerType() == VDVideoViewLayerContextData.LAYER_COMPLEX_NOVERTICAL) {
			// 只横不竖方式，不允许转屏
			VDVideoFullModeController.getInstance().setFullLock();
			VDVideoScreenOrientation.setOnlyLandscape(mContext);
			isFullScreen = true;
		}

		// 设置当前的转屏方式
		setIsFullModeUsingContainer(mVDVideoViewContainer, isFullScreen);
		if (VDVideoFullModeController.getInstance().mInHandNum == 1) {
			VDVideoFullModeController.getInstance().setIsManual(false);
		}
		if (VDVideoFullModeController.getInstance().getIsFromHand()) {
			VDVideoFullModeController.getInstance().mInHandNum++;
		}
		VDVideoFullModeController.getInstance().setIsFullScreen(isFullScreen);

		if (!isInited) {
			// 回调接口，实现相应的横竖屏转换通知
			VDVideoViewController cnotroller = VDVideoViewController
					.getInstance(mContext);
			if (cnotroller != null)
				cnotroller
						.notifyFullScreen(isFullScreen,
								VDVideoFullModeController.getInstance()
										.getIsFromHand());
		}
	}

	/**
	 * 横竖屏切换，从onConfigurationChanged入口进来的
	 * 
	 * @param isFullScreen
	 *            true 表全屏；false 表非全屏
	 */
	public void setIsFullScreen(boolean isFullScreen) {
		setIsFullScreen(isFullScreen, false);
	}

	/**
	 * 当前所有的layer显示与关闭
	 * 
	 * @deprecated 不要再使用，后期会关闭此方法
	 * @param isGone
	 */
	public void setLayersVisiblity(boolean isGone) {
		for (VDVideoViewLayerContext item : mVDVideoViewLayerData
				.getLayerList()) {
			item.setVisibility(isGone);
		}
	}

	// ---------------私有函数---------------

	/**
	 * 添加视图控件
	 * 
	 * @param vv
	 */
	private void addVideoView(ISinaVideoView vv) {
		if (vv == null) {
			return;
		}
		setBackgroundColor(Color.BLACK);
		if (vv instanceof VideoViewHard) {
			FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(-1, -1);
			lp.gravity = Gravity.CENTER;
			addView((VideoViewHard) vv, 0, lp);
		} else {
			FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(-1, -1);
			lp.gravity = Gravity.CENTER;
			addView((VideoView) vv, 0, lp);
		}

	}

	/**
	 * 从TypedArray里面抽取需要的resourceID
	 * 
	 * @param typedArr
	 */
	private void initLayer(TypedArray typedArr) {
		removeAllViews();
		for (int i = 0; i < typedArr.getIndexCount(); i++) {
			if (typedArr.getIndex(i) == R.styleable.VDVideoView_layerAttrs) {
				int resID = -1;
				// 1.layerAttrs中格式一般为@array/sv_videoview_layers之类的格式
				// 先读出其resourceID来，准备加载TypedArray
				resID = typedArr.getResourceId(
						R.styleable.VDVideoView_layerAttrs, -1);
				if (resID == -1) {
					if (typedArr != null)
						typedArr.recycle();
					throw new IllegalArgumentException("resID=-1");
				}

				readLayerAttrs(resID);
			} else if (typedArr.getIndex(i) == R.styleable.VDVideoView_canPopupWindow) {
				int popWindowType = typedArr.getInt(
						R.styleable.VDVideoView_canPopupWindow, -1);
				if (popWindowType != -1) {
					mIsCanPopupWindow = true;
					mPopupWindowType = popWindowType;
				}
			} else if (typedArr.getIndex(i) == R.styleable.VDVideoView_popWindowWidth) {
				float width = typedArr.getDimension(
						R.styleable.VDVideoView_popWindowWidth, -1);
				if (width != -1) {
					mPopupWindowWidth = (int) width;
				}
			} else if (typedArr.getIndex(i) == R.styleable.VDVideoView_popWindowHeight) {
				float height = typedArr.getDimension(
						R.styleable.VDVideoView_popWindowHeight, -1);
				if (height != -1) {
					mPopupWindowHeight = (int) height;
				}
			}
		}
	}

	/**
	 * 设置弹出窗口的相关属性，用于手动加载VDVideoview控件的时候
	 * 
	 * @param type
	 * @param width
	 * @param height
	 */
	public void setPopWindowStyle(int type, int width, int height) {
		mIsCanPopupWindow = true;
		mPopupWindowType = type;
		mPopupWindowWidth = width;
		mPopupWindowHeight = height;
	}

	/**
	 * 根据现有的转屏方向确定那些layout应该开着，那些应该gone 奇数为竖屏，偶数为横屏
	 * 
	 * @return
	 */
	private boolean getIsFullMode(int seq) {
		boolean ret = true;
		if (seq % 2 == 0) {
			ret = false;
		}
		return ret;
	}

	/**
	 * 从layerAttrs属性中加载相应的layer，支持两种格式： 1、直接使用@layer方式组装数组 2、使用@array方式做二维数组方式
	 * 
	 * @param context
	 * @param attrs
	 */
	private void readLayerAttrs(int resID) {
		// 将resourceID指向的数组加载到内存中，一般为：
		// 复杂模式：
		// <array name="sv_videoview_layers">
		// <item>@array/sv_videoview_layer_adlayer</item>
		// <item>@array/sv_videoview_layer_controllayer</item>
		// </array>
		// 或者，简单模式：
		// <array name="sina_video_videoview_layers">
		// <item>@layout/ad_layer</item>
		// <item>@layout/control_layer</item>
		// </array>
		// 格式
		TypedArray layerTypedArr = getResources().obtainTypedArray(resID);
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// obtainStyledAttributes而来的数组使用getIndexCount方法取得数量
		// obtainTypedArray得到的数组使用length来得到数量
		int complexNoHorizonLayerNum = 0;
		for (int j = 0; j < layerTypedArr.length(); j++) {
			int resID2 = -1;
			resID2 = layerTypedArr.getResourceId(j, -1);

			if (resID2 == -1) {
				if (layerTypedArr != null)
					layerTypedArr.recycle();
				throw new IllegalArgumentException("resID2=-1");
			}

			String type = getResources().getResourceTypeName(resID2);
			if (type.equals("layout")) {
				// 简单模式
				VDVideoViewLayer layer = (VDVideoViewLayer) inflater.inflate(
						resID2, null);
				// 添加进入当前容器
				addView(layer, false);
				// 保存引用
				VDVideoViewLayerContext item = new VDVideoViewLayerContext();
				// 默认就是竖屏的
				layer.mIsVertical = true;
				item.addSimpleItem(layer);
				mVDVideoViewLayerData.addLayerContext(item);

			} else if (type.equals("array")) {
				// 复杂模式
				TypedArray orientationLayerArr = getResources()
						.obtainTypedArray(resID2);
				if (orientationLayerArr.length() != 2) {
					if (layerTypedArr != null)
						layerTypedArr.recycle();
					if (orientationLayerArr != null) {
						orientationLayerArr.recycle();
					}
					throw new IllegalArgumentException(
							"使用布局数组的情况下，一个数组只能容纳两个layer");
				}

				VDVideoViewLayerContext item = new VDVideoViewLayerContext();
				for (int k = 0; k < orientationLayerArr.length(); k++) {
					int resID3 = orientationLayerArr.getResourceId(k, -1);
					// 复杂模式下，无论何种情况，都要求有两个默认的layer在其中
					VDVideoViewLayer layer = new VDVideoViewLayer(mContext);
					if (resID3 == -1) {
						if (k == 1) {
							// 只允许只横不竖，那么只有竖屏，也就是k=0的时候，可以为-1，其他情况判错
							if (layerTypedArr != null)
								layerTypedArr.recycle();
							if (orientationLayerArr != null) {
								orientationLayerArr.recycle();
							}
							throw new IllegalArgumentException("resID3=-1");
						}
						complexNoHorizonLayerNum++;
					} else {
						layer = (VDVideoViewLayer) inflater.inflate(resID3,
								this, false);
					}
					boolean isGone = getIsFullMode(k);
					// 添加进入当前容器
					layer.mIsVertical = !isGone;
					addView(layer, isGone);
					// 保存引用
					item.addComplexItem(layer);
					// 判断是否是广告层
					if (layer instanceof VDVideoADLayer) {
						item.mIsInsertADLayer = true;
					}
				}
				mVDVideoViewLayerData.addLayerContext(item);
				orientationLayerArr.recycle();
			} else {
				throw new IllegalArgumentException("加入的类型错误");
			}
		}
		layerTypedArr.recycle();

		int simpleLayerNum = 0;
		int complexLayerNum = 0;

		for (VDVideoViewLayerContext value : mVDVideoViewLayerData
				.getLayerList()) {
			if (value.mComplexLayer.size() != 0) {
				complexLayerNum++;
			}
			if (value.mSimpleLayer != null) {
				simpleLayerNum++;
			}
		}
		if (simpleLayerNum == 0 && complexLayerNum == 0) {
			// 空的，直接返回错误
			throw new IllegalArgumentException("layout为空");
		} else if (simpleLayerNum != 0 && complexLayerNum != 0) {
			throw new IllegalArgumentException("简单模式、复杂模式只能二选一");
		} else if (simpleLayerNum != 0) {
			mVDVideoViewLayerData
					.setLayerType(VDVideoViewLayerContextData.LAYER_SIMPLE);
		} else if (complexLayerNum != 0) {
			if (complexNoHorizonLayerNum == complexLayerNum) {
				mVDVideoViewLayerData
						.setLayerType(VDVideoViewLayerContextData.LAYER_COMPLEX_NOVERTICAL);
			} else {
				mVDVideoViewLayerData
						.setLayerType(VDVideoViewLayerContextData.LAYER_COMPLEX_ALL);
			}
		}
		VDVideoViewController controller = VDVideoViewController
				.getInstance(mContext);
		if (controller != null)
			controller.setLayerContextData(mVDVideoViewLayerData);
	}

	/**
	 * 在当前容器中，添加一页
	 * 
	 * @param layer
	 * @param isGone
	 */
	private void addView(VDVideoViewLayer layer, boolean isGone) {
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		if (isGone) {
			layer.setVisibility(View.GONE);
		} else {
			layer.setVisibility(View.VISIBLE);
		}

		if (layer != null) {
			addView(layer, params);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i(TAG, "onKeyDown keyCode = " + keyCode);
		VDVideoViewController controller = VDVideoViewController
				.getInstance(mContext);
		VDVideoInfo info = null;
		if (controller != null)
			info = controller.getCurrentVideo();
		if (info == null) {
			return super.onKeyDown(keyCode, event);
		}
		if (info.mIsLive) {

		} else {

		}
		if ((keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER)) {
			mVDPlayPauseHelper.doClick();
			if (controller.mVDPlayerInfo.mIsPlaying) {
				controller.notifyHideControllerBar(0);
			} else {
				controller.notifyShowControllerBar(false);
			}
			// VDVideoViewController.getInstance().notifyKeyEvent();
			controller.notifyPlayOrPause();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
			// VDVideoViewController.getInstance().notifyPlayOrPause();
			controller.notifyKeyLeftRightEvent();
			Log.i(TAG, "onKeyDown KEYCODE_DPAD_LEFT = " + keyCode);
			if (info.mIsLive) {

			} else {
				controller.notifyKeyChangeProgress(true);
			}
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			controller.notifyKeyLeftRightEvent();
			Log.i(TAG, "onKeyDown KEYCODE_DPAD_RIGHT  = " + keyCode);
			if (info.mIsLive) {

			} else {
				controller.notifyKeyChangeProgress(false);
			}
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (info.mIsLive) {
				if (getFocusedChild() == null) {
					return false;
				}
			} else {
				Log.i(TAG, "onKeyDown getFocusedChild() == null = "
						+ (getFocusedChild() == null));
				if (getFocusedChild() == null) {
					controller.notifyHideControllerBar(0);
					return false;
				}
			}
			controller.notifyHideControllerBar(0);
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_UP
				|| keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			// Log.i(TAG, "onKeyDown KEYCODE_DPAD_UP  = " + keyCode);
			if (info.mIsLive) {

			} else {
				controller.notifyShowControllerBar(true);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// --- DLNA

	private void registerOnDLNAMediaControllerListener() {
		DLNAEventListener.getInstance().addOnDLNASelectedListener(
				mOnMediaControllerListener);
		DLNAEventListener.getInstance().addOnMediaControllerListener(
				mOnMediaControllerListener);
	}

	private void unregisterOnDLNAMediaControllerListener() {
		DLNAEventListener.getInstance().removeOnMediaControllerListener(
				mOnMediaControllerListener);
		DLNAEventListener.getInstance().removeOnDLNASelectedListener(
				mOnMediaControllerListener);
	}

	private OnMediaControllerListener mOnMediaControllerListener = new OnMediaControllerListener() {

		@Override
		public void onMediaRenderOpened(boolean opened) {
			VDVideoViewController controller = VDVideoViewController
					.getInstance(mContext);
			if (controller != null)
				controller.notifyHideLoading();
			if (mVideoView != null) {
				if (controller != null)
					controller.pause();
				mVideoView.stopPlayback();
				((View) mVideoView).setVisibility(GONE);
			}
			DLNAController dlnaController = DLNAController
					.getInstance(mContext);
			int max_stream_music_level = dlnaController.getVolumeMax()
					- dlnaController.getVolumeMin();
			if (controller != null)
				controller.notifySetMaxVolume(max_stream_music_level);
		}

		@Override
		public void onProgressUpdate(long position, long duration) {
			VDVideoViewController controller = VDVideoViewController
					.getInstance(mContext);
			if (controller != null)
				controller.notifyProgressUpdate(position, duration);
		}

		@Override
		public void onDLNASwitch(boolean open) {
			closeDLNA();
		}

		@Override
		public void onMediaRenderStateChanged(String name, String value) {
			if (SinaDLNA.TRANSPORT_STATE.equalsIgnoreCase(name)) {
				VDVideoViewController controller = VDVideoViewController
						.getInstance(mContext);
				if (null == controller) {
					return;
				}
				if (SinaDLNA.TRANSPORT_STATE_PLAYING.equalsIgnoreCase(value)) {
					controller.mVDPlayerInfo.mIsPlaying = true;
					controller.mVDPlayerInfo.mPlayStatus = VDPlayerInfo.PLAYER_STARTED;
					controller.notifyPlayStateChanged();
				} else if (SinaDLNA.TRANSPORT_STATE_PAUSED_PLAYBACK
						.equalsIgnoreCase(value)) {
					controller.mVDPlayerInfo.mPlayStatus = VDPlayerInfo.PLAYER_PAUSE;
					controller.mVDPlayerInfo.mIsPlaying = false;
					controller.notifyPlayStateChanged();
				} else if (SinaDLNA.TRANSPORT_STATE_STOPPED
						.equalsIgnoreCase(value)) {
					controller.mVDPlayerInfo.mPlayStatus = VDPlayerInfo.PLAYER_PAUSE;
					controller.mVDPlayerInfo.mIsPlaying = false;
					controller.notifyPlayStateChanged();
				}
			}
		}

		@Override
		public void onGetVolume(int vol) {
			VDVideoViewController controller = VDVideoViewController
					.getInstance(mContext);
			if (controller != null)
				controller.notifySetCurVolume(vol);
		}

		@Override
		public void onMediaRenderSelect(String name, String value) {
			DLNAController.getInstance(mContext).mSeekPosition = mVideoView
					.getCurrentPosition();
			DLNAController.getInstance(getContext()).mDoSeek = true;
		}

		@Override
		public void onPlayStateChanged(boolean isPlaying) {
			// updatePausePlay(isPlaying); deprecated
			VDVideoViewController controller = VDVideoViewController
					.getInstance(mContext);
			if (null == controller)
				return;
			controller.mVDPlayerInfo.mPlayStatus = isPlaying
			// ? VDPlayerInfo.PLAYER_ISPLAYING
			? VDPlayerInfo.PLAYER_STARTED
					: VDPlayerInfo.PLAYER_PAUSE;
			controller.mVDPlayerInfo.mIsPlaying = isPlaying;
			controller.notifyPlayStateChanged();
		}

		@Override
		public void onPreOpenDLNA() {
			VDVideoViewController contoller = VDVideoViewController
					.getInstance(mContext);
			VDVideoInfo mVideoInfo = null;
			if (contoller != null)
				mVideoInfo = contoller.getCurrentVideo();
			VDLog.i("DLNA", "onPreOpenDLNA " + mVideoInfo.mTitle);
			if (mVideoInfo != null) {
				if (mVideoInfo.mIsLive) {
					DLNAController.getInstance(getContext()).mPlayUrl = mVideoInfo
							.getVideoUrl(VDSharedPreferencesUtil
									.getCurResolution(mContext));
				} else {
					if (VDUtility.isLocalUrl(mVideoInfo.mPlayUrl)) {// 解析
						DLNAController.getInstance(getContext()).mPlayUrl = mVideoInfo
								.getNetUrl();
					} else {
						DLNAController.getInstance(getContext()).mPlayUrl = (mVideoInfo.mRedirectUrl == null ? mVideoInfo.mPlayUrl
								: mVideoInfo.mRedirectUrl);
					}
				}
				DLNAController.getInstance(getContext()).open();
			}
		}

		@Override
		public void onGetDuration(long dur) {

		}

	};

	/**
	 * 关闭DLNA
	 */
	private void closeDLNA() {
		VDVideoViewController controller = VDVideoViewController
				.getInstance(mContext);
		VDVideoInfo mVideoInfo = null;
		if (controller != null) {
			mVideoInfo = controller.getCurrentVideo();
		}
		unregisterOnDLNAMediaControllerListener();
		((View) mVideoView).setVisibility(VISIBLE);
		DLNAController.mIsDLNA = false;

		int progress = VDPlayerSoundManager.getCurrSoundVolume(mContext);
		int maxVolume = VDPlayerSoundManager.getMaxSoundVolume(mContext);
		if (controller != null)
			controller.notifySetCurVolume(progress);
		if (controller != null)
			controller.notifySetMaxVolume(maxVolume);
		if (VDUtility.isLocalUrl(mVideoInfo.mPlayUrl)) {
			mVideoView.setVideoPath(mVideoInfo.mPlayUrl);
		} else {
			mVideoView
					.setVideoPath(DLNAController.getInstance(mContext).mPlayUrl);
		}
		if (!mVideoInfo.mIsLive) {
			Log.i("DLNA",
					"back_seek  : "
							+ DLNAController.getInstance(mContext).mPosition);
			mVideoView.seekTo(DLNAController.getInstance(mContext).mPosition);
		}
		mVideoView.start();
		if (controller != null)
			controller.notifyShowLoading();
	}

	@Override
	public void register() {
		registerOnDLNAMediaControllerListener();
	}

	@Override
	public void onVideoUIRefresh() {
		// TODO Auto-generated method stub
		VDVideoViewController controller = VDVideoViewController
				.getInstance(mContext);
		if (controller == null) {
			return;
		}
		for (VDVideoViewLayerContext layerContext : mVDVideoViewLayerData
				.getLayerList()) {
			layerContext.refresh(controller.isInsertAD());
		}
	}

	/**
	 * activity的onStart回调
	 */
	public void onStart() {
		VDVideoViewController controller = VDVideoViewController
				.getInstance(mContext);
		if (controller != null) {
			controller.onStart();
		}
	}

	/**
	 * activity的onStop回调
	 */
	public void onStop() {
		VDVideoViewController controller = VDVideoViewController
				.getInstance(mContext);
		if (controller != null) {
			controller.onStop();
		}
	}

	/**
	 * activity的onResume回调
	 */
	public void onResume() {
		VDVideoViewController controller = VDVideoViewController
				.getInstance(mContext);
		if (controller != null) {
			controller.onResume();
		}
	}

	/**
	 * activity的onPause回调
	 */
	public void onPause() {
		VDVideoViewController controller = VDVideoViewController
				.getInstance(mContext);
		if (controller != null) {
			controller.onPause();
		}
	}

	/**
	 * activity的onRestart回调
	 */
	public void onRestart() {
	}

	/**
	 * activity的onKeyEvent回调
	 * 
	 * @param keyCode
	 * @param event
	 * @return
	 */
	public boolean onVDKeyDown(int keyCode, KeyEvent event) {
		VDVideoViewController controller = VDVideoViewController
				.getInstance(mContext);
		if (controller != null) {
			return controller.onKeyEvent(event);
		}
		return false;
	}

	/**
	 * 是否播放中
	 * 
	 * @return
	 */
	public boolean getIsPlaying() {
		VDVideoViewController controller = VDVideoViewController
				.getInstance(mContext);
		if (controller != null) {
			return controller.getIsPlaying();
		}
		return false;
	}

	/**
	 * 播放器当前状态
	 * 
	 * @return
	 */
	public int getPlayerStatus() {
		VDVideoViewController controller = VDVideoViewController
				.getInstance(mContext);
		if (controller != null) {
			return controller.getPlayerStatus();
		}
		return VDPlayerInfo.PLAYER_UNKNOWN;
	}

	/**
	 * 数据部分
	 * 
	 * @return
	 */
	public VDVideoListInfo getListInfo() {
		VDVideoViewController controller = VDVideoViewController
				.getInstance(mContext);
		if (controller != null) {
			return controller.getVideoList();
		}
		return null;
	}

}
