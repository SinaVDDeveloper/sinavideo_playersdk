package com.sina.sinavideo.sdk;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.graphics.PointF;
import android.media.TimedText;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;

import com.sina.sinavideo.coreplayer.ISinaVideoView;
import com.sina.sinavideo.sdk.data.VDPlayerInfo;
import com.sina.sinavideo.sdk.data.VDResolutionData;
import com.sina.sinavideo.sdk.data.VDVideoInfo;
import com.sina.sinavideo.sdk.data.VDVideoListInfo;
import com.sina.sinavideo.sdk.dlna.DLNAController;
import com.sina.sinavideo.sdk.utils.VDApplication;
import com.sina.sinavideo.sdk.utils.VDLog;
import com.sina.sinavideo.sdk.utils.VDPlayerSoundManager;
import com.sina.sinavideo.sdk.utils.VDVideoFullModeController;

/**
 * 管理控制类中所有回调消息
 * 
 * @author haifeng9
 * 
 */
public class VDVideoViewListeners {

	private final static String TAG = "VDVideoViewListeners";
	private Context mContext;

	// 日志相关
	// private LogPushManager mLogPushManager;

	/**
	 * 所有的事件的定义
	 */
	public interface OnBufferingUpdateListener {

		abstract void onBufferingUpdate(int percent);
	}

	public interface OnCompletionListener {

		abstract void onCompletion();
	}

	public interface OnErrorListener {

		abstract boolean onError(int what, int extra);
	}

	public interface OnPauseListener {

		public void onPause();
	}

	public interface OnInfoListener {

		abstract boolean onInfo(int what, int extra);
	}

	public interface OnPreparedListener {

		abstract void onPrepared();
	}

	public interface OnSeekCompleteListener {

		abstract void onSeekComplete();
	}

	public interface OnVideoSizeChangedListener {

		abstract void onVideoSizeChanged(int width, int height);
	}

	public interface OnTimedTextListener {

		public void onTimedText(TimedText text);
	}

	public interface OnProgressUpdateListener {

		public void onProgressUpdate(long current, long duration);

		public void onDragProgess(long progress, long duration);
	}

	public interface OnVideoOpenedListener {

		public void onVideoOpened();
	}

	public interface OnVideoUIRefreshListener {

		public void onVideoUIRefresh();
	}

	public interface OnPlayVideoListener {

		public void onVideoInfo(VDVideoInfo info);

		public void onShowLoading(boolean show);

		public void onVideoPrepared(boolean prepare);

		public void onPlayStateChanged();
	}

	// public interface OnLiveVideoListener {
	//
	// /**
	// * 如果有分辨率的信息，就有list，如果没有list为null
	// */
	// public void onResolutionInfo(VDResolutionData list);
	//
	// public void onResolutionIndex(int index);
	// }

	/**
	 * 全屏事件，转屏时候，会发送此通知
	 * 
	 * @author sunxiao
	 * 
	 */
	public interface OnFullScreenListener {

		public void onFullScreen(boolean isFullScreen, boolean isFromHand);
	}

	/**
	 * 音量改变后的回调
	 * 
	 * @author liuqun1
	 * 
	 */
	public interface OnSoundChangedListener {

		public void onSoundChanged(int currVolume);
	}

	/**
	 * 点击按钮，播放或暂停操作
	 * 
	 * @author liuqun
	 * 
	 */
	public interface OnClickPlayListener {

		public void onClickPlay();
	}

	/**
	 * 音量调节空间显示
	 * 
	 * @author sunxiao
	 * 
	 */
	public interface OnSoundVisibleListener {

		public void onSoundVisible(boolean isVisible);

		public void onSoundSeekBarVisible(boolean isVisible);
	}

	/**
	 * 清晰度控件显示
	 * 
	 * @author GengHongchao
	 * 
	 */
	public interface OnVMSResolutionListener {

		public void onVMSResolutionContainerVisible(boolean isVisible);

		public void onVMSResolutionChanged();
	}

	public enum eSingleTouchListener {
		eTouchListenerSingleTouchStart, eTouchListenerSingleTouch, eTouchListenerSingleTouchEnd,
	}

	public enum eDoubleTouchListener {
		eTouchListenerDoubleTouchStart, eTouchListenerDoubleTouch, eTouchListenerDoubleTouchEnd,
	}

	public enum eVerticalScrollTouchListener {
		eTouchListenerVerticalScrollStart, eTouchListenerVerticalScroll, eTouchListenerVerticalScrollSound, eTouchListenerVerticalScrollLighting, eTouchListenerVerticalScrollEnd,
	}

	public enum eHorizonScrollTouchListener {
		eTouchListenerHorizonScrollStart, eTouchListenerHorizonScroll, eTouchListenerHorizonScrollEnd,
	}

	/**
	 * 触屏相关的消息
	 * 
	 * @author sunxiao
	 * 
	 */
	public interface OnScreenTouchListener {

		/**
		 * 单击，隐藏、显示
		 * 
		 * @param ev
		 */
		public void onSingleTouch(MotionEvent ev);
	}

	/**
	 * 亮度调节
	 * 
	 * @author sunxiao
	 * 
	 */
	public interface OnLightingChangeListener {

		public void onLightingChange(float curr);
	}

	/**
	 * 亮度调节显示
	 * 
	 * @author sunxiao
	 * 
	 */
	public interface OnLightingVisibleListener {

		public void onLightingVisible(boolean isVisible);
	}

	/**
	 * 清晰度解析完回调，以及选择清晰度的回调
	 * 
	 * @author liuqun1
	 * 
	 */
	// public interface OnResolutionListener {
	//
	// void onResolutionSelect(String resolution);
	//
	// void onParseResolution(VDResolutionData list);
	//
	// void hideResolution();
	// }

	/**
	 * 清晰度改变
	 * 
	 * @author sunxiao
	 * 
	 */
	public interface OnResolutionListener {

		/**
		 * 清晰度按钮点击后回传
		 * 
		 * @param tag
		 */
		public void onResolutionChanged(String tag);

		/**
		 * 清晰度解析完毕时候回传
		 * 
		 * @param list
		 */
		public void onResolutionParsed(VDResolutionData list);
	}

	/**
	 * 清晰度按钮容器之类的隐藏显示
	 * 
	 * @author alexsun
	 * 
	 */
	public interface OnResolutionContainerListener {

		public void onResolutionContainerVisible(boolean isVisible);
	}

	/**
	 * 在遥控器等TV项目中，在进入container焦点的时候，触发的第一个清晰度按钮
	 * 
	 * @author alexsun
	 * 
	 */
	public interface OnResolutionListButtonListener {

		public void onResolutionListButtonFocusFirst();
	}

	/**
	 * 清晰度选择按钮，显示或隐藏清晰度列表
	 * 
	 * @author liuqun1
	 * 
	 */
	// public interface OnResolutionVisibleChangeListener {
	//
	// void toogle();
	// }

	/**
	 * 提示通知
	 * 
	 * @author liuqun1
	 * 
	 */
	public interface OnTipListener {

		void onTip(String tip);

		void onTip(int tipResId);

		void hideTip();
	}

	/**
	 * 加载进度条回调
	 * 
	 * @author liuqun1
	 * 
	 */
	public interface OnLoadingListener {

		void showLoading();

		void hideLoading();
	}

	/**
	 * 新手引导页部分
	 * 
	 * @author sunxiao
	 * 
	 */
	public interface OnVideoGuideTipsListener {

		public void onVisible(boolean isVisible);
	}

	/**
	 * 静帧广告部分，弄起来很麻烦，可能有问题 [点击暂停的时候，出现此东东]
	 * 
	 * @author sunxiao
	 * 
	 */
	public interface OnVideoFrameADListener {

		/**
		 * 广告显示开始
		 */
		public void onVideoFrameADBegin();

		/**
		 * 广告播放时长结束
		 */
		public void onVideoFrameADEnd();
	}

	public interface OnVideoInsertADListener {

		/**
		 * 插入式广告开始
		 */
		public void onVideoInsertADBegin();

		/**
		 * 插入式广告的跳秒信号
		 */
		public void onVideoInsertADTicker();

		/**
		 * 插入式广告结束
		 */
		public void onVideoInsertADEnd();
	}

	/**
	 * 视频列表通知
	 * 
	 * @author liuqun1
	 * 
	 */
	public interface OnVideoListListener {

		// /**
		// * @deprecated 不再使用，用这个：onVideoList(VDVideoListInfo infoList)
		// * @param videoList
		// */
		// void onVideoList(ArrayList<VDVideoInfo> videoList);

		public void onVideoList(VDVideoListInfo infoList);
	}

	/**
	 * 视频列表切换显示隐藏通知
	 * 
	 * @author liuqun1
	 * 
	 */
	public interface OnVideoListVisibleChangeListener {

		void toogle();

		void showPlayList();

		void hidePlayList();

		void removeAndHideDelay();
	}

	/**
	 * 更多操作面板切换显示隐藏通知
	 * 
	 * @author liuqun1
	 * 
	 */
	public interface OnMoreOprationVisibleChangeListener {

		void showPanel();

		void hidePanel();

		void removeAndHideDelay();
	}

	/**
	 * 双击播放/暂停动画view的通知
	 * 
	 * @author liuqun1
	 * 
	 */
	public interface OnVideoDoubleTapListener {

		void onDoubleTouch();
	}

	/**
	 * 解码器部分的触发
	 * 
	 * @author sunxiao
	 * 
	 */
	public interface OnDecodingTypeListener {

		public void onChange(boolean isFFMpeg);
	}

	/**
	 * 屏幕方向发生改变
	 * 
	 * @author liuqun
	 * 
	 */
	public interface OnScreenOrientationChangeListener {

		void onScreenOrientationVertical();

		void onScreenOrientationHorizontal();
	}

	/**
	 * 显示或隐藏控制栏
	 * 
	 * @author liuqun
	 * 
	 */
	public interface OnShowHideControllerListener {

		void doNotHideControllerBar();

		void hideControllerBar(long delay);

		void showControllerBar(boolean delayHide);

		void onPostHide();

		void onPostShow();

		void onPreHide();

		void onPreShow();
	}

	/**
	 * 显示或隐藏底部控制栏
	 * 
	 * @author liuqun
	 * 
	 */
	public interface OnShowHideBottomControllerListener {

		void hideBottomControllerBar();

		void showBottomControllerBar();

	}

	/**
	 * 显示或者隐藏顶部的控制栏
	 * 
	 * @author alexsun
	 * 
	 */
	public interface OnShowHideTopContainerListener {

		public void hideTopControllerBar();

		public void showTopControllerBar();
	}

	/**
	 * 显示或者隐藏前贴片部分控制容器的事件
	 * 
	 * @author alexsun
	 * 
	 */
	public interface OnShowHideADContainerListener {

		public void hideADContainerBar();

		public void showADContainerBar();
	}

	/**
	 * 手势调整进度
	 * 
	 * @author liuqun
	 * 
	 */
	public interface OnProgressViewVisibleListener {

		public void onProgressVisible(boolean isVisible);
	}

	/**
	 * 方向键改变进度
	 * 
	 * @author liuqun
	 * 
	 */
	public interface OnKeyChangeProgressListener {

		public void onKeyDown(boolean keyLeft);
	}

	/**
	 * TV 键盘事件
	 * 
	 * @author liuqun
	 * 
	 */
	public interface OnKeyEventListener {

		/**
		 * 单击，隐藏、显示
		 */
		public void onKeyEvent();

		/**
		 * 单击，隐藏、显示
		 */
		public void onKeyLeftRight();
	}

	/**
	 * 音量设置的回调,用于DLNA
	 * 
	 * @author liuqun1
	 * 
	 */
	public interface OnSetSoundListener {

		public void onSetCurVolume(int currVolume);

		public void onSetMaxVolume(int maxVolume);
	}

	/**
	 * VDVideoView注册DLNAListener
	 * 
	 * @author liuqun1
	 * 
	 */
	public interface OnRegisterDLNAListener {

		public void register();
	}

	/**
	 * DLNA显示/隐藏Listener
	 * 
	 * @author liuqun1
	 * 
	 */
	public interface OnDLNALinearLayoutListener {

		public void setLayoutVisiable(boolean visiable);
	}

	/**
	 * 横竖屏切换Listener
	 * 
	 * @author liuqun1
	 * 
	 */
	public interface OnScreenOrientationSwitchListener {

		public void onScreenOrientationSwitch(boolean fullScreen);
	}

	/**
	 * 点击错误重试按钮Listener
	 * 
	 * @author liuqun1
	 * 
	 */
	public interface OnClickRetryListener {

		public void onClickRetry();
	}

	/**
	 * 所有的事件保存在此
	 */
	private Set<OnBufferingUpdateListener> mOnBufferingUpdateListener = new HashSet<OnBufferingUpdateListener>();
	private Set<OnCompletionListener> mOnCompletionListener = new HashSet<OnCompletionListener>();
	private Set<OnErrorListener> mOnErrorListener = new HashSet<OnErrorListener>();
	private Set<OnErrorListener> mOnRetryErrorListener = new HashSet<OnErrorListener>();
	private Set<OnInfoListener> mOnInfoListener = new HashSet<OnInfoListener>();
	private Set<OnPreparedListener> mOnPreparedListener = new HashSet<OnPreparedListener>();
	private Set<OnSeekCompleteListener> mOnSeekCompleteListener = new HashSet<OnSeekCompleteListener>();
	private Set<OnVideoSizeChangedListener> mOnVideoSizeChangedListener = new HashSet<OnVideoSizeChangedListener>();
	private Set<OnVideoOpenedListener> mOnVideoOpenedListener = new HashSet<OnVideoOpenedListener>();
	private Set<OnTimedTextListener> mOnTimedTextListener = new HashSet<OnTimedTextListener>();
	private Set<OnProgressUpdateListener> mOnProgressUpdateListener = new HashSet<OnProgressUpdateListener>();
	private Set<OnPlayVideoListener> mOnPlayVideoListener = new HashSet<OnPlayVideoListener>();
	// private Set<OnLiveVideoListener> mOnLiveVideoListener = new
	// HashSet<OnLiveVideoListener>();
	private Set<OnFullScreenListener> mOnFullScreenListener = new HashSet<OnFullScreenListener>();
	private Set<OnSoundChangedListener> mOnSoundChangedListener = new HashSet<OnSoundChangedListener>();
	private Set<OnScreenTouchListener> mOnScreenTouchListener = new HashSet<OnScreenTouchListener>();
	private Set<OnResolutionListener> mOnResolutionListener = new HashSet<OnResolutionListener>();
	private Set<OnResolutionContainerListener> mOnResolutionContainerListener = new HashSet<OnResolutionContainerListener>();
	private Set<OnResolutionListButtonListener> mOnResolutionListButtonListener = new HashSet<OnResolutionListButtonListener>();
	private Set<OnTipListener> mOnTipListener = new HashSet<OnTipListener>();
	private Set<OnLightingChangeListener> mOnLightingChangeListener = new HashSet<VDVideoViewListeners.OnLightingChangeListener>();
	private Set<OnSoundVisibleListener> mOnSoundVisibleListener = new HashSet<VDVideoViewListeners.OnSoundVisibleListener>();
	private Set<OnVMSResolutionListener> mOnVMSResolutionListener = new HashSet<VDVideoViewListeners.OnVMSResolutionListener>();
	private Set<OnLightingVisibleListener> mOnLightingVisibleListener = new HashSet<VDVideoViewListeners.OnLightingVisibleListener>();
	private Set<OnLoadingListener> mOnLoadingListener = new HashSet<VDVideoViewListeners.OnLoadingListener>();
	private Set<OnVideoGuideTipsListener> mOnVideoGuideTipsListener = new HashSet<VDVideoViewListeners.OnVideoGuideTipsListener>();
	private Set<OnPauseListener> mOnPauseListener = new HashSet<VDVideoViewListeners.OnPauseListener>();
	private Set<OnVideoFrameADListener> mOnVideoFrameADListener = new HashSet<VDVideoViewListeners.OnVideoFrameADListener>();
	private Set<OnVideoInsertADListener> mOnVideoInsertADListener = new HashSet<VDVideoViewListeners.OnVideoInsertADListener>();
	private Set<OnVideoListListener> mOnVideoListListener = new HashSet<VDVideoViewListeners.OnVideoListListener>();
	private Set<OnVideoListVisibleChangeListener> mOnVideoListVisibleChangeListener = new HashSet<VDVideoViewListeners.OnVideoListVisibleChangeListener>();
	private Set<OnMoreOprationVisibleChangeListener> mOnMoreOprationVisibleChangeListener = new HashSet<VDVideoViewListeners.OnMoreOprationVisibleChangeListener>();
	// private Set<OnResolutionVisibleChangeListener>
	// mOnResolutionVisibleChangeListener = new
	// HashSet<VDVideoViewListeners.OnResolutionVisibleChangeListener>();
	private Set<OnVideoDoubleTapListener> mOnVideoDoubleTapListener = new HashSet<VDVideoViewListeners.OnVideoDoubleTapListener>();
	private Set<OnDecodingTypeListener> mOnDecodingTypeListener = new HashSet<VDVideoViewListeners.OnDecodingTypeListener>();
	private Set<OnScreenOrientationChangeListener> mOnScreenOrientationChangeListener = new HashSet<VDVideoViewListeners.OnScreenOrientationChangeListener>();
	private Set<OnShowHideControllerListener> mOnShowHideControllerListener = new HashSet<VDVideoViewListeners.OnShowHideControllerListener>();
	private Set<OnShowHideBottomControllerListener> mOnShowHideBottomControllerListener = new HashSet<VDVideoViewListeners.OnShowHideBottomControllerListener>();
	private Set<OnShowHideTopContainerListener> mOnShowHideTopContainerListener = new HashSet<VDVideoViewListeners.OnShowHideTopContainerListener>();
	private Set<OnProgressViewVisibleListener> mOnProgressViewVisibleListener = new HashSet<VDVideoViewListeners.OnProgressViewVisibleListener>();
	private Set<OnClickPlayListener> mOnClickPlayListener = new HashSet<VDVideoViewListeners.OnClickPlayListener>();
	private Set<OnKeyChangeProgressListener> mOnKeyChangeProgressListener = new HashSet<VDVideoViewListeners.OnKeyChangeProgressListener>();
	private Set<OnKeyEventListener> mOnKeyEventListener = new HashSet<VDVideoViewListeners.OnKeyEventListener>();
	private Set<OnSetSoundListener> mOnSetSoundListener = new HashSet<VDVideoViewListeners.OnSetSoundListener>();
	private Set<OnRegisterDLNAListener> mOnRegisterDLNAListener = new HashSet<VDVideoViewListeners.OnRegisterDLNAListener>();
	private Set<OnDLNALinearLayoutListener> mOnDLNALinearLayoutListener = new HashSet<VDVideoViewListeners.OnDLNALinearLayoutListener>();
	private Set<OnScreenOrientationSwitchListener> mOnScreenOrientationSwitchListener = new HashSet<VDVideoViewListeners.OnScreenOrientationSwitchListener>();
	private Set<OnClickRetryListener> mOnClickRetryListener = new HashSet<VDVideoViewListeners.OnClickRetryListener>();
	private Set<OnVideoUIRefreshListener> mOnVideoUIRefreshListener = new HashSet<OnVideoUIRefreshListener>();
	private Set<OnShowHideADContainerListener> mOnShowHideADContainerListener = new HashSet<VDVideoViewListeners.OnShowHideADContainerListener>();

	/**
	 * 通过主线程回调消息到界面
	 */
	private Handler mMainHandler;

	public VDVideoViewListeners(Context context) {
		mMainHandler = new Handler();
		mContext = context;
	}

	public void addOnVideoUIRefreshListener(OnVideoUIRefreshListener l) {
		mOnVideoUIRefreshListener.add(l);
	}

	public void removeOnVideoUIRefreshListener(OnVideoUIRefreshListener l) {
		mOnVideoUIRefreshListener.remove(l);
	}

	void addOnBufferingUpdateListener(OnBufferingUpdateListener l) {
		mOnBufferingUpdateListener.add(l);
	}

	void removeOnBufferingUpdateListener(OnBufferingUpdateListener l) {
		mOnBufferingUpdateListener.remove(l);
	}

	void addOnCompletionListener(OnCompletionListener l) {
		mOnCompletionListener.add(l);
	}

	void removeOnCompletionListener(OnCompletionListener l) {
		mOnCompletionListener.remove(l);
	}

	void addOnErrorListener(OnErrorListener l) {
		mOnErrorListener.add(l);
	}

	void removeOnErrorListener(OnErrorListener l) {
		mOnErrorListener.remove(l);
	}

	void addOnRetryErrorListener(OnErrorListener l) {
		mOnRetryErrorListener.add(l);
	}

	void removeOnRetryErrorListener(OnErrorListener l) {
		mOnRetryErrorListener.remove(l);
	}

	void addOnInfoListener(OnInfoListener l) {
		mOnInfoListener.add(l);
	}

	void removeOnInfoListener(OnInfoListener l) {
		mOnInfoListener.remove(l);
	}

	void addOnPreparedListener(OnPreparedListener l) {
		mOnPreparedListener.add(l);
	}

	void removeOnPreparedListener(OnPreparedListener l) {
		mOnPreparedListener.remove(l);
	}

	void addOnSeekCompleteListener(OnSeekCompleteListener l) {
		mOnSeekCompleteListener.add(l);
	}

	void removeOnSeekCompleteListener(OnSeekCompleteListener l) {
		mOnSeekCompleteListener.remove(l);
	}

	void addOnVideoSizeChangedListener(OnVideoSizeChangedListener l) {
		mOnVideoSizeChangedListener.add(l);
	}

	void removeOnVideoSizeChangedListener(OnVideoSizeChangedListener l) {
		mOnVideoSizeChangedListener.remove(l);
	}

	void addOnVideoOpenedListener(OnVideoOpenedListener l) {
		mOnVideoOpenedListener.add(l);
	}

	void removeOnVideoOpenedListener(OnVideoOpenedListener l) {
		mOnVideoOpenedListener.remove(l);
	}

	public void addOnProgressUpdateListener(OnProgressUpdateListener l) {
		mOnProgressUpdateListener.add(l);
	}

	public void removeOnProgressUpdateListener(OnProgressUpdateListener l) {
		mOnProgressUpdateListener.remove(l);
	}

	public void addOnTimedTextListener(OnTimedTextListener l) {
		mOnTimedTextListener.add(l);
	}

	public void removeOnTimedTextListener(OnTimedTextListener l) {
		mOnTimedTextListener.remove(l);
	}

	public void addOnPlayVideoListener(OnPlayVideoListener l) {
		mOnPlayVideoListener.add(l);
	}

	public void removeOnPlayVideoListener(OnPlayVideoListener l) {
		mOnPlayVideoListener.remove(l);
	}

	// public void addOnLiveVideoListener(OnLiveVideoListener l) {
	// mOnLiveVideoListener.add(l);
	// }
	//
	// public void removeOnLiveVideoListener(OnLiveVideoListener l) {
	// mOnLiveVideoListener.remove(l);
	// }

	public void addOnFullScreenListener(OnFullScreenListener l) {
		mOnFullScreenListener.add(l);
	}

	public void removeOnFullScreenListener(OnFullScreenListener l) {
		mOnFullScreenListener.remove(l);
	}

	public void addOnSoundChangedListener(OnSoundChangedListener l) {
		mOnSoundChangedListener.add(l);
	}

	public void removeOnSoundChangedListener(OnSoundChangedListener l) {
		mOnSoundChangedListener.remove(l);
	}

	public void addOnScreenTouchListener(OnScreenTouchListener l) {
		mOnScreenTouchListener.add(l);
	}

	public void removeOnScreenTouchListener(OnScreenTouchListener l) {
		mOnScreenTouchListener.remove(l);
	}

	public void addOnResolutionListener(OnResolutionListener l) {
		mOnResolutionListener.add(l);
	}

	public void removeOnResolutionListener(OnResolutionListener l) {
		mOnResolutionListener.remove(l);
	}

	public void addOnResolutionContainerListener(OnResolutionContainerListener l) {
		mOnResolutionContainerListener.add(l);
	}

	public void removeOnResolutionContainerListener(
			OnResolutionContainerListener l) {
		mOnResolutionContainerListener.remove(l);
	}

	public void addOnResolutionListButtonListener(
			OnResolutionListButtonListener l) {
		mOnResolutionListButtonListener.add(l);
	}

	public void removeOnResolutionListButtonListener(
			OnResolutionListButtonListener l) {
		mOnResolutionListButtonListener.remove(l);
	}

	public void addOnTipListener(OnTipListener l) {
		mOnTipListener.add(l);
	}

	public void removeOnTipListener(OnTipListener l) {
		mOnTipListener.remove(l);
	}

	public void addOnLightingChangeListener(OnLightingChangeListener l) {
		mOnLightingChangeListener.add(l);
	}

	public void removeOnLightingChangeListener(OnLightingChangeListener l) {
		mOnLightingChangeListener.remove(l);
	}

	public void addOnLightingVisibleListener(OnLightingVisibleListener l) {
		mOnLightingVisibleListener.add(l);
	}

	public void removeOnLightingVisibleListener(OnLightingVisibleListener l) {
		mOnLightingVisibleListener.remove(l);
	}

	public void addOnSoundVisibleListener(OnSoundVisibleListener l) {
		mOnSoundVisibleListener.add(l);
	}

	public void removeOnSoundVisibleListener(OnSoundVisibleListener l) {
		mOnSoundVisibleListener.remove(l);
	}

	public void addOnVMSResolutionListener(OnVMSResolutionListener l) {
		mOnVMSResolutionListener.add(l);
	}

	public void removeOnVMSResolutionListener(OnVMSResolutionListener l) {
		mOnVMSResolutionListener.remove(l);
	}

	public void addOnLoadingListener(OnLoadingListener l) {
		mOnLoadingListener.add(l);
	}

	public void removeOnLoadingListener(OnLoadingListener l) {
		mOnLoadingListener.remove(l);
	}

	public void addOnVideoGuideTipsListener(OnVideoGuideTipsListener l) {
		mOnVideoGuideTipsListener.add(l);
	}

	public void removeOnVideoGuideTipsListener(OnVideoGuideTipsListener l) {
		mOnVideoGuideTipsListener.remove(l);
	}

	public void addOnPauseListener(OnPauseListener l) {
		mOnPauseListener.add(l);
	}

	public void removeOnPauseListener(OnPauseListener l) {
		mOnPauseListener.remove(l);
	}

	public void addOnVideoADListener(OnVideoFrameADListener l) {
		mOnVideoFrameADListener.add(l);
	}

	public void removeOnVideoAdListener(OnVideoFrameADListener l) {
		mOnVideoFrameADListener.remove(l);
	}

	public void addOnVideoInsertADListener(OnVideoInsertADListener l) {
		mOnVideoInsertADListener.add(l);
	}

	public void removeOnVideoInsertADListener(OnVideoInsertADListener l) {
		mOnVideoInsertADListener.remove(l);
	}

	public void addOnVideoListListener(OnVideoListListener l) {
		mOnVideoListListener.add(l);
	}

	public void removeOnVideoListListener(OnVideoListListener l) {
		mOnVideoListListener.remove(l);
	}

	public void addOnVideoListVisibleChangeListener(
			OnVideoListVisibleChangeListener l) {
		mOnVideoListVisibleChangeListener.add(l);
	}

	public void removeOnVideoListVisibleChangeListener(
			OnVideoListVisibleChangeListener l) {
		mOnVideoListVisibleChangeListener.remove(l);
	}

	public void addOnMoreOprationVisibleChangeListener(
			OnMoreOprationVisibleChangeListener l) {
		mOnMoreOprationVisibleChangeListener.add(l);
	}

	public void removeOnMoreOprationVisibleChangeListener(
			OnMoreOprationVisibleChangeListener l) {
		mOnMoreOprationVisibleChangeListener.remove(l);
	}

	// public void
	// addOnResolutionVisibleChangeListener(OnResolutionVisibleChangeListener l)
	// {
	// mOnResolutionVisibleChangeListener.add(l);
	// }
	//
	// public void
	// removeOnResolutionVisibleChangeListener(OnResolutionVisibleChangeListener
	// l) {
	// mOnResolutionVisibleChangeListener.remove(l);
	// }

	public void addOnVideoDoubleTapListener(OnVideoDoubleTapListener l) {
		mOnVideoDoubleTapListener.add(l);
	}

	public void removeOnVideoDoubleTapListener(OnVideoDoubleTapListener l) {
		mOnVideoDoubleTapListener.remove(l);
	}

	public void addOnDecodingTypeListener(OnDecodingTypeListener l) {
		mOnDecodingTypeListener.add(l);
	}

	public void removeOnDecodingTypeListener(OnDecodingTypeListener l) {
		mOnDecodingTypeListener.remove(l);
	}

	public void addOnScreenOrientationChangeListener(
			OnScreenOrientationChangeListener l) {
		mOnScreenOrientationChangeListener.add(l);
	}

	public void removeOnScreenOrientationChangeListener(
			OnScreenOrientationChangeListener l) {
		mOnScreenOrientationChangeListener.remove(l);
	}

	public void addOnShowHideControllerListener(OnShowHideControllerListener l) {
		mOnShowHideControllerListener.add(l);
	}

	public void removeOnShowHideControllerListener(
			OnShowHideControllerListener l) {
		mOnShowHideControllerListener.remove(l);
	}

	public void addOnShowHideBottomControllerListener(
			OnShowHideBottomControllerListener l) {
		mOnShowHideBottomControllerListener.add(l);
	}

	public void removeOnShowHideBottomControllerListener(
			OnShowHideBottomControllerListener l) {
		mOnShowHideBottomControllerListener.remove(l);
	}

	public void addOnShowHideTopContainerListener(
			OnShowHideTopContainerListener l) {
		mOnShowHideTopContainerListener.add(l);
	}

	public void removeOnShowHideTopContainerListener(
			OnShowHideTopContainerListener l) {
		mOnShowHideTopContainerListener.remove(l);
	}

	public void addOnProgressViewVisibleListener(OnProgressViewVisibleListener l) {
		mOnProgressViewVisibleListener.add(l);
	}

	public void removeOnProgressViewVisibleListener(
			OnProgressViewVisibleListener l) {
		mOnProgressViewVisibleListener.remove(l);
	}

	public void addOnClickPlayListener(OnClickPlayListener l) {
		mOnClickPlayListener.add(l);
	}

	public void removeOnClickPlayListener(OnClickPlayListener l) {
		mOnClickPlayListener.remove(l);
	}

	public void addOnKeyChangeProgressListener(OnKeyChangeProgressListener l) {
		mOnKeyChangeProgressListener.add(l);
	}

	public void removeOnKeyChangeProgressListener(OnKeyChangeProgressListener l) {
		mOnKeyChangeProgressListener.remove(l);
	}

	public void addOnKeyEventListener(OnKeyEventListener l) {
		mOnKeyEventListener.add(l);
	}

	public void removeOnKeyEventListener(OnKeyEventListener l) {
		mOnKeyEventListener.remove(l);
	}

	public void addOnSetSoundListener(OnSetSoundListener l) {
		mOnSetSoundListener.add(l);
	}

	public void removeOnSetSoundListener(OnSetSoundListener l) {
		mOnSetSoundListener.remove(l);
	}

	public void addOnRegisterDLNAListener(OnRegisterDLNAListener l) {
		mOnRegisterDLNAListener.add(l);
	}

	public void removeOnRegisterDLNAListener(OnRegisterDLNAListener l) {
		mOnRegisterDLNAListener.remove(l);
	}

	public void addOnDLNALinearLayoutListener(OnDLNALinearLayoutListener l) {
		mOnDLNALinearLayoutListener.add(l);
	}

	public void removeOnDLNALinearLayoutListener(OnDLNALinearLayoutListener l) {
		mOnDLNALinearLayoutListener.remove(l);
	}

	public void addOnScreenOrientationSwitchListener(
			OnScreenOrientationSwitchListener l) {
		mOnScreenOrientationSwitchListener.add(l);
	}

	public void removeOnScreenOrientationSwitchListener(
			OnScreenOrientationSwitchListener l) {
		mOnScreenOrientationSwitchListener.remove(l);
	}

	public void addOnClickRetryListener(OnClickRetryListener l) {
		mOnClickRetryListener.add(l);
	}

	public void removeOnClickRetryListener(OnClickRetryListener l) {
		mOnClickRetryListener.remove(l);
	}

	public void addOnShowHideADContainerListener(OnShowHideADContainerListener l) {
		mOnShowHideADContainerListener.add(l);
	}

	public void removeOnShowHideADContainerListener(
			OnShowHideADContainerListener l) {
		mOnShowHideADContainerListener.remove(l);
	}

	public void notifyVideoUIRefreshListener() {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnVideoUIRefreshListener listener : mOnVideoUIRefreshListener) {
					listener.onVideoUIRefresh();
				}
			}
		});
	}

	public void notifyClickRetry() {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnClickRetryListener listener : mOnClickRetryListener) {
					listener.onClickRetry();
				}
			}
		});
	}

	void notifyBufferingUpdate(final int percent) {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnBufferingUpdateListener listener : mOnBufferingUpdateListener) {
					listener.onBufferingUpdate(percent);
				}
			}
		});
	}

	void notifyCompletion() {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnCompletionListener listener : mOnCompletionListener) {
					listener.onCompletion();
				}
			}
		});
	}

	void notifyError(final int what, final int extra) {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnErrorListener listener : mOnErrorListener) {
					listener.onError(what, extra);
				}
			}
		});
	}

	void notifyRetryError(final int what, final int extra) {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnErrorListener listener : mOnRetryErrorListener) {
					listener.onError(what, extra);
				}
			}
		});
	}

	void notifyInfo(final int what, final int extra) {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnInfoListener listener : mOnInfoListener) {
					listener.onInfo(what, extra);
				}
			}
		});
	}

	void notifyPrepared() {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnPreparedListener listener : mOnPreparedListener) {
					listener.onPrepared();
				}
			}
		});
	}

	void notifySeekComplete() {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnSeekCompleteListener listener : mOnSeekCompleteListener) {
					listener.onSeekComplete();
				}
			}
		});
	}

	void notifyVideoSizeChanged(final int width, final int height) {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnVideoSizeChangedListener listener : mOnVideoSizeChangedListener) {
					listener.onVideoSizeChanged(width, height);
				}
			}
		});
	}

	void notifyTimedText(final TimedText text) {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnTimedTextListener listener : mOnTimedTextListener) {
					listener.onTimedText(text);
				}
			}
		});
	}

	void notifyProgressUpdate(final long current, final long duration) {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnProgressUpdateListener listener : mOnProgressUpdateListener) {
					listener.onProgressUpdate(current, duration);
				}
			}
		});
	}

	void notifyVideoOpened() {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnVideoOpenedListener listener : mOnVideoOpenedListener) {
					listener.onVideoOpened();
				}
			}
		});
	}

	void notifyVideoInfo(final VDVideoInfo info) {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnPlayVideoListener listener : mOnPlayVideoListener) {
					listener.onVideoInfo(info);
				}
			}
		});
	}

	void notifyShowLoading(final boolean show) {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnPlayVideoListener listener : mOnPlayVideoListener) {
					listener.onShowLoading(show);
				}
			}
		});
	}

	void notifyVideoPrepared(final boolean prepare) {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnPlayVideoListener listener : mOnPlayVideoListener) {
					listener.onVideoPrepared(prepare);
				}
			}
		});
	}

	void notifyPlayStateChanged() {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnPlayVideoListener listener : mOnPlayVideoListener) {
					listener.onPlayStateChanged();
				}
			}
		});
	}

	void notifyDragTo(final long position, final long duration) {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnProgressUpdateListener listener : mOnProgressUpdateListener) {
					listener.onDragProgess(position, duration);
				}
			}
		});
	}

	// void notifyResolutionInfo(final VDResolutionData list) {
	// mMainHandler.post(new Runnable() {
	//
	// @Override
	// public void run() {
	// for (OnLiveVideoListener listener : mOnLiveVideoListener) {
	// listener.onResolutionInfo(list);
	// }
	// }
	// });
	// }
	//
	// void notifyResolutionIndex(final int index) {
	// mMainHandler.post(new Runnable() {
	//
	// @Override
	// public void run() {
	// for (OnLiveVideoListener listener : mOnLiveVideoListener) {
	// listener.onResolutionIndex(index);
	// }
	// }
	// });
	// }

	void notifyFullScreen(final boolean isFullScreen, final boolean isFromHand) {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnFullScreenListener listener : mOnFullScreenListener) {
					listener.onFullScreen(isFullScreen, isFromHand);
				}
			}
		});
	}

	void notifySoundChanged(final int curr) {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnSoundChangedListener listener : mOnSoundChangedListener) {
					listener.onSoundChanged(curr);
				}
			}
		});
	}

	void notifyScreenSingleTouch(final MotionEvent ev,
			final eSingleTouchListener flag) {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				switch (flag) {
				case eTouchListenerSingleTouchStart:

					break;

				case eTouchListenerSingleTouch:
					for (OnScreenTouchListener listener : mOnScreenTouchListener) {
						listener.onSingleTouch(ev);
					}
					break;

				case eTouchListenerSingleTouchEnd:

					break;

				default:
					break;
				}
			}
		});
	}

	void notifyKeyEvent() {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnKeyEventListener listener : mOnKeyEventListener) {
					listener.onKeyEvent();
				}
			}
		});
	}

	void notifyKeyLeftRightEvent() {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnKeyEventListener listener : mOnKeyEventListener) {
					listener.onKeyLeftRight();
				}
			}
		});
	}

	void notifyScreenDoubleTouch(final MotionEvent ev,
			final eDoubleTouchListener flag) {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				switch (flag) {
				case eTouchListenerDoubleTouchStart:

					break;

				case eTouchListenerDoubleTouch:
					Log.i("VDVideoDoubleTapPlayView",
							"eTouchListenerDoubleTouch");
					notifyDoubleTouch();
					VDVideoViewController controller = VDVideoViewController
							.getInstance(mContext);
					if (controller != null) {
						if (controller.getIsPlaying()) {
							controller.pause();
						} else {
							controller.resume();
							controller.start();
						}
					}
					break;

				case eTouchListenerDoubleTouchEnd:

					break;

				default:
					break;
				}
			}
		});
	}

	/**
	 * 使用坐标点方式得到[0-1]的滑动取值范围
	 * 
	 * @param point1
	 * @param point2
	 * @return
	 */
	private float getCurrTimeFromEvent(final PointF point1, final PointF point2) {
		VDVideoViewController controller = VDVideoViewController
				.getInstance(mContext);
		if (controller == null) {
			return 0;
		}
		DLNAController dlnaController = DLNAController.getInstance(controller
				.getContext());
		int width = controller.getScreen()[0];
		int distance = (int) (point2.x - point1.x);
		long current = 0;
		long duration = 0;
		if (DLNAController.mIsDLNA) {
			current = DLNAController.mTmpPosition;
			duration = dlnaController.mDuration;
		} else {
			current = controller.mVDPlayerInfo.mCurrent;
			duration = controller.mVDPlayerInfo.mDuration;
		}
		// LogS.i(TAG, "getCurrTimeFromEvent  point1.x:" + point1.x +
		// ", point2.x:" + point2.x + ",distance:" + distance);
		float rate = DLNAController.mIsDLNA ? dlnaController.mProgressRate
				: controller.mProgressRate;
		float ret = ((float) current / duration)
				+ ((float) distance / (float) width) * rate;
		VDLog.e(TAG, "current : " + current + ",duration : " + duration
				+ " , distance : " + distance + " , ret = " + ret);
		if (ret < 0) {
			ret = 0;
		} else if (ret > 1) {
			ret = 1;
		}

		return ret;
	}

	private float mTmpStreamLevel;
	private int mScreenHeight;

	/**
	 * 使用坐标点方式得到[0-1]的音量取值范围
	 * 
	 * @param point1
	 * @param point2
	 * @return
	 */
	private float getCurrSoundFromEvent(final PointF point1,
			final PointF point2, float distansY) {
		// float y1 = point1.y;
		// float y2 = point2.y;

		// float distance = y1 - y2;
		VDVideoViewController controller = VDVideoViewController
				.getInstance(mContext);
		if (controller == null) {
			return 0;
		}
		int maxVolume;
		int currVolume;
		if (DLNAController.mIsDLNA) {
			maxVolume = DLNAController.getInstance(controller.getContext())
					.getVolumeMax();
			currVolume = DLNAController.getInstance(controller.getContext()).mVolume;
		} else {
			// Context context = VDApplication.getInstance().getContext();
			maxVolume = VDPlayerSoundManager.getMaxSoundVolume(controller
					.getContext());
			currVolume = VDPlayerSoundManager.getCurrSoundVolume(controller
					.getContext());
		}
		// float degressVolume = (float) currVolume / maxVolume;
		float degree = (float) distansY / mScreenHeight;
		// float currVolumef = currVolume;
		// currVolumef += degree*maxVolume;
		// currVolumef = currVolume/maxVolume;
		Log.i("getCurrSoundFromEvent", "fromDownY = " + distansY
				+ " , currVolume = " + currVolume + " , degree = "

				+ degree + " , tmp_stream_level = " + mTmpStreamLevel
				+ " , maxVolume = " + maxVolume);
		mTmpStreamLevel += (degree * maxVolume);
		// float ret = (currVolume+degree*maxVolume);
		Log.d("getCurrSoundFromEvent", "tmp_stream_level = " + mTmpStreamLevel
				+ " , add = " + (degree * maxVolume));
		// Log.i("getCurrSoundFromEvent", "fromDownY = " + distansY +
		// " , ret = " + ret + " , degree = " + degree + " , y1 = " + y1 +
		// " , cur = " + (currVolume+degree*maxVolume));
		// float ret = degressVolume + degree * 3;
		if (mTmpStreamLevel < 0) {
			mTmpStreamLevel = 0;
		} else if (mTmpStreamLevel > maxVolume) {
			mTmpStreamLevel = maxVolume;
		}

		return mTmpStreamLevel;
	}

	/**
	 * 使用坐标点方式得到[0-1]的亮度取值范围
	 * 
	 * @param point1
	 * @param point2
	 * @return
	 */
	private float getCurrLightingFromEvent(final PointF point1,
			final PointF point2) {
		float ret;
		float y1 = point1.y;
		float y2 = point2.y;
		VDVideoViewController controller = VDVideoViewController
				.getInstance(mContext);
		if (controller == null) {
			ret = 1.0f;
			return ret;
		}
		int height = controller.getScreen()[1];
		float distance = y1 - y2;

		float degree = distance / height;

		float curNum = controller.getCurrLightingSetting();

		ret = curNum + degree;

		if (ret >= 1.0) {
			ret = 1.0f;
		} else if (ret <= 0.1) { // 不能小于10%亮度
			ret = 0.1f;
		}

		return ret;
	}

	private boolean getIsRight(final PointF point) {
		VDVideoViewController controller = VDVideoViewController
				.getInstance(mContext);
		if (null == controller) {
			return false;
		}
		int width = controller.getScreen()[0];
		boolean isRight = false;
		if (point.x > ((float) width / 2)) {
			// 在右边屏幕位置
			isRight = true;
		}

		return isRight;
	}

	void notifyScreenVerticalScrollTouch(final PointF point1,
			final PointF point2, final PointF beginPoint,
			final eVerticalScrollTouchListener flag, final float distansY) {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				float curr = -1;
				boolean isSoundVisible = false;
				if (flag == eVerticalScrollTouchListener.eTouchListenerVerticalScrollEnd) {
					curr = 0;
				} else if (flag == eVerticalScrollTouchListener.eTouchListenerVerticalScrollSound
						|| getIsRight(beginPoint)) {
					// 调节音量
					isSoundVisible = true;
					if (point1 != null && point2 != null) {
						curr = getCurrSoundFromEvent(beginPoint, point2,
								distansY);
					}
				} else if (flag == eVerticalScrollTouchListener.eTouchListenerVerticalScrollLighting
						|| !getIsRight(beginPoint)) {
					// 调节亮度··
					isSoundVisible = false;
					if (point1 != null && point2 != null) {
						curr = getCurrLightingFromEvent(point1, point2);

						VDLog.e("xxoo", curr + "");
					}
				}

				if (curr == -1) {
					return;
				}
				VDVideoViewController controller = VDVideoViewController
						.getInstance(mContext);
				switch (flag) {
				case eTouchListenerVerticalScrollStart:
					if (!VDVideoFullModeController.getInstance()
							.getIsFullScreen()) {
						isSoundVisible = true;
					}
					if (isSoundVisible) {
						notifySoundVisible(true);
						if (DLNAController.mIsDLNA) {

							if (controller != null)
								mTmpStreamLevel = DLNAController
										.getInstance(controller.getContext()).mVolume;
						} else {
							Context context = VDApplication.getInstance()
									.getContext();
							mTmpStreamLevel = VDPlayerSoundManager
									.getCurrSoundVolume(context);
						}
						VDLog.e("getCurrSoundFromEvent", "tmp_stream_level = "
								+ mTmpStreamLevel);
					} else {
						notifyLightingVisible(true);
					}

					if (controller != null)
						mScreenHeight = controller.getScreen()[1];
					break;

				case eTouchListenerVerticalScrollLighting:
					if (controller != null)
						controller.dragLightingTo(curr, true);

					break;

				case eTouchListenerVerticalScrollSound:
					if (controller != null)
						controller.dragSoundSeekTo((int) curr);
					break;

				case eTouchListenerVerticalScroll:
					// 判断是右边还是左边？
					if (getIsRight(beginPoint)) {
						VDLog.e(TAG,
								"notifyScreenVerticalScrollTouch,eTouchListenerVerticalScroll,dragSoundSeekTo curr : "
										+ curr);
						if (controller != null)
							controller.dragSoundSeekTo((int) curr);
					} else {
						VDLog.e(TAG,
								"notifyScreenVerticalScrollTouch,eTouchListenerVerticalScroll,dragLightingTo");
						if (controller != null)
							controller.dragLightingTo(curr, true);
					}
					break;

				case eTouchListenerVerticalScrollEnd:
					notifySoundVisible(false);
					notifyLightingVisible(false);
					break;

				default:
					break;
				}
			}
		});
	}

	void notifyScreenHorizonScrollTouch(final PointF point1,
			final PointF point2, final PointF beginPoint,
			final eHorizonScrollTouchListener flag) {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				// for (OnScreenTouchListener listener : mOnScreenTouchListener)
				// {
				// }
				VDVideoViewController controller = VDVideoViewController
						.getInstance(mContext);
				VDVideoInfo videoInfo = null;
				if (controller != null) {
					VDPlayerInfo playerInfo = controller.getPlayerInfo();
					if (!playerInfo.isCanScroll()) {
						return;
					}
					videoInfo = controller.getCurrentVideo();
				}
				if (videoInfo == null) {
					return;
				}
				switch (flag) {
				case eTouchListenerHorizonScrollStart:
					if (!videoInfo.mIsLive) {
						notifyProgressViewVisible(true);
						if (DLNAController.mIsDLNA) {
							DLNAController dlnaController = DLNAController
									.getInstance(controller.getContext());
							DLNAController.mTmpPosition = dlnaController.mPosition;
						} else {
							controller.mVDPlayerInfo.mCurrent = controller
									.getCurrentPosition();
							controller.setProgressRate();
							controller.pause();
						}
					}
					break;
				case eTouchListenerHorizonScroll:
					if (!videoInfo.mIsLive) {
						float curr = getCurrTimeFromEvent(point1, point2);
						controller.dragProgressTo(curr);
					}
					break;

				case eTouchListenerHorizonScrollEnd:
					if (!videoInfo.mIsLive) {
						notifyProgressViewVisible(false);
						controller.dragProgressTo(
								getCurrTimeFromEvent(point1, point2), true,
								true);
						if (DLNAController.mIsDLNA) {

						} else {
							controller.resume(false);
							ISinaVideoView vv = controller.getVideoView();
							if (!vv.isPlaying()) {
								controller.start();
							}
						}
						notifyPlayStateChanged();
					}
					break;

				default:
					break;
				}
			}
		});
	}

	public void notifyLightingSetting(final float curr) {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnLightingChangeListener listener : mOnLightingChangeListener) {
					listener.onLightingChange(curr);
				}
			}
		});
	}

	/**
	 * 清晰度改变通知
	 * 
	 * @param tag
	 */
	public void notifyResolutionChanged(final String tag) {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				for (OnResolutionListener listener : mOnResolutionListener) {
					listener.onResolutionChanged(tag);
				}
			}
		});
	}

	/**
	 * 清晰度解析完毕的时候回传
	 * 
	 * @param list
	 */
	public void notifyResolutionParsed(final VDResolutionData list) {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				for (OnResolutionListener listener : mOnResolutionListener) {
					listener.onResolutionParsed(list);
				}
			}
		});
	}

	/**
	 * 通知清晰度选择
	 * 
	 * @param mCurResolution
	 */
	// void notifyResolutionSelect(final String mCurResolution) {
	// mMainHandler.post(new Runnable() {
	//
	// @Override
	// public void run() {
	// for (OnResolutionListener listener : mOnResolutionListener) {
	// listener.onResolutionSelect(mCurResolution);
	// }
	// }
	// });
	// }

	/**
	 * 通知清晰度解析完成
	 * 
	 * @param list
	 */
	// void notifyResolutionParsed(final VDResolutionData list) {
	// mMainHandler.post(new Runnable() {
	//
	// @Override
	// public void run() {
	// for (OnResolutionListener listener : mOnResolutionListener) {
	// listener.onParseResolution(list);
	// }
	// }
	// });
	// }

	/**
	 * 隐藏清晰度按钮
	 * 
	 */
	// void notifyHideResolution() {
	// mMainHandler.post(new Runnable() {
	//
	// @Override
	// public void run() {
	// for (OnResolutionListener listener : mOnResolutionListener) {
	// listener.hideResolution();
	// }
	// }
	// });
	// }

	public void notifyResolutionVisible(final boolean isVisible) {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				for (OnResolutionContainerListener listener : mOnResolutionContainerListener) {
					listener.onResolutionContainerVisible(isVisible);
				}
			}
		});
	}

	public void notifyResolutionListButtonFirstFocus() {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				for (OnResolutionListButtonListener l : mOnResolutionListButtonListener) {
					l.onResolutionListButtonFocusFirst();
				}
			}
		});
	}

	void notifyTip(final String tip) {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnTipListener listener : mOnTipListener) {
					listener.onTip(tip);
				}
			}
		});
	}

	void notifyTip(final int tip) {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnTipListener listener : mOnTipListener) {
					listener.onTip(tip);
				}
			}
		});
	}

	void notifyHideTip() {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnTipListener listener : mOnTipListener) {
					listener.hideTip();
				}
			}
		});
	}

	void notifyLightingVisible(final boolean isVisible) {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				for (OnLightingVisibleListener listener : mOnLightingVisibleListener) {
					listener.onLightingVisible(isVisible);
				}
			}
		});
	}

	void notifySoundVisible(final boolean isVisible) {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				for (OnSoundVisibleListener listener : mOnSoundVisibleListener) {
					listener.onSoundVisible(isVisible);
				}
			}
		});
	}

	void notifySoundSeekBarVisible(final boolean isVisible) {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				for (OnSoundVisibleListener listener : mOnSoundVisibleListener) {
					listener.onSoundSeekBarVisible(isVisible);
				}
			}
		});
	}

	void notifyVMSResolutionContainerVisible(final boolean isVisible) {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnVMSResolutionListener listener : mOnVMSResolutionListener) {
					listener.onVMSResolutionContainerVisible(isVisible);
				}
			}
		});
	}

	void notifyVMSResolutionChanged() {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnVMSResolutionListener listener : mOnVMSResolutionListener) {
					listener.onVMSResolutionChanged();
				}
			}
		});
	}

	void notifyShowLoading() {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnLoadingListener listener : mOnLoadingListener) {
					listener.showLoading();
				}
			}
		});
	}

	void notifyHideLoading() {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnLoadingListener listener : mOnLoadingListener) {
					listener.hideLoading();
				}
			}
		});
	}

	void notifyGuideTips(final boolean isVisible) {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnVideoGuideTipsListener listener : mOnVideoGuideTipsListener) {
					listener.onVisible(isVisible);
				}
			}
		});
	}

	void notifyPlayOrPause() {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnClickPlayListener listener : mOnClickPlayListener) {
					listener.onClickPlay();
				}
			}
		});
	}

	void notifyPause() {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnPauseListener listener : mOnPauseListener) {
					listener.onPause();
				}
			}
		});
	}

	void notifyVideoInsertADBegin() {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnVideoInsertADListener listener : mOnVideoInsertADListener) {
					listener.onVideoInsertADBegin();
				}
			}
		});
	}

	void notifyVideoInsertADTicker() {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnVideoInsertADListener listener : mOnVideoInsertADListener) {
					listener.onVideoInsertADTicker();
				}
			}
		});
	}

	void notifyVideoInsertADEnd() {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnVideoInsertADListener listener : mOnVideoInsertADListener) {
					listener.onVideoInsertADEnd();
				}
			}
		});
	}

	void notifyVideoFrameADBegin() {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnVideoFrameADListener listener : mOnVideoFrameADListener) {
					listener.onVideoFrameADBegin();
				}
			}
		});
	}

	void notifyVideoFrameADEnd() {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnVideoFrameADListener listener : mOnVideoFrameADListener) {
					listener.onVideoFrameADEnd();
				}
			}
		});
	}

	// /**
	// * @deprecated 不再用了，用这个：notifyVideoList(final VDVideoListInfo videoList)
	// * @param videoList
	// */
	// void notifyVideoList(final ArrayList<VDVideoInfo> videoList) {
	// mMainHandler.post(new Runnable() {
	//
	// @Override
	// public void run() {
	// for (OnVideoListListener listener : mOnVideoListListener) {
	// listener.onVideoList(videoList);
	// }
	// }
	// });
	// }

	void notifyVideoList(final VDVideoListInfo videoList) {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnVideoListListener listener : mOnVideoListListener) {
					listener.onVideoList(videoList);
				}
			}
		});
	}

	void notifyVideoListVisibelChange() {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnVideoListVisibleChangeListener listener : mOnVideoListVisibleChangeListener) {
					listener.toogle();
				}
			}
		});
	}

	void notifyShowVideoList() {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnVideoListVisibleChangeListener listener : mOnVideoListVisibleChangeListener) {
					listener.showPlayList();
				}
			}
		});
	}

	void notifyHideVideoList() {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnVideoListVisibleChangeListener listener : mOnVideoListVisibleChangeListener) {
					listener.hidePlayList();
				}
			}
		});
	}

	void removeAndHideDelayVideoList() {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnVideoListVisibleChangeListener listener : mOnVideoListVisibleChangeListener) {
					listener.removeAndHideDelay();
				}
			}
		});
	}

	void notifyShowMoreOprationPanel() {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnMoreOprationVisibleChangeListener listener : mOnMoreOprationVisibleChangeListener) {
					listener.showPanel();
				}
			}
		});
	}

	void notifyHideMoreOprationPanel() {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnMoreOprationVisibleChangeListener listener : mOnMoreOprationVisibleChangeListener) {
					listener.hidePanel();
				}
			}
		});
	}

	void notifyRemoveAndHideDelayMoreOprationPanel() {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnMoreOprationVisibleChangeListener listener : mOnMoreOprationVisibleChangeListener) {
					listener.removeAndHideDelay();
				}
			}
		});
	}

	// void notifyResolutionListVisibelChange() {
	// mMainHandler.post(new Runnable() {
	//
	// @Override
	// public void run() {
	// for (OnResolutionVisibleChangeListener listener :
	// mOnResolutionVisibleChangeListener) {
	// listener.toogle();
	// }
	// }
	// });
	// }

	void notifyDoubleTouch() {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnVideoDoubleTapListener listener : mOnVideoDoubleTapListener) {
					listener.onDoubleTouch();
				}
			}
		});
	}

	public void notifyDecodingTypeChange(final boolean isFFMpeg) {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnDecodingTypeListener l : mOnDecodingTypeListener) {
					l.onChange(isFFMpeg);
				}
			}
		});
	}

	public void notifyScreenOrientationChange(final boolean vertical) {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnScreenOrientationChangeListener l : mOnScreenOrientationChangeListener) {
					VDLog.e(VDVideoFullModeController.TAG,
							": OnScreenOrientationChange");
					if (vertical) {
						l.onScreenOrientationVertical();
					} else {
						l.onScreenOrientationHorizontal();
					}
				}
			}
		});
	}

	public void notifyNotHideControllerBar() {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnShowHideControllerListener l : mOnShowHideControllerListener) {
					l.doNotHideControllerBar();
				}
			}
		});
	}

	public void notifyHideBottomControllerBar() {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnShowHideBottomControllerListener l : mOnShowHideBottomControllerListener) {
					l.hideBottomControllerBar();
				}
			}
		});
	}

	public void notifyShowBottomControllerBar() {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnShowHideBottomControllerListener l : mOnShowHideBottomControllerListener) {
					l.showBottomControllerBar();
				}
			}
		});
	}

	public void notifyHideTopControllerBar() {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				for (OnShowHideTopContainerListener l : mOnShowHideTopContainerListener) {
					l.hideTopControllerBar();
				}
			}
		});
	}

	public void notifyShowTopControllerBar() {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				for (OnShowHideTopContainerListener l : mOnShowHideTopContainerListener) {
					l.showTopControllerBar();
				}
			}
		});
	}

	public void notifyControllerBarPreHide() {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnShowHideControllerListener l : mOnShowHideControllerListener) {
					l.onPreHide();
				}
			}
		});
	}

	public void notifyControllerBarPreShow() {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnShowHideControllerListener l : mOnShowHideControllerListener) {
					l.onPreShow();
				}
			}
		});
	}

	public void notifyControllerBarPostHide() {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnShowHideControllerListener l : mOnShowHideControllerListener) {
					l.onPostHide();
				}
			}
		});
	}

	public void notifyControllerBarPostShow() {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnShowHideControllerListener l : mOnShowHideControllerListener) {
					l.onPostShow();
				}
			}
		});
	}

	public void notifyKeyChangeProgress(final boolean keyLeft) {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				VDVideoViewController controller = VDVideoViewController
						.getInstance(mContext);
				VDVideoInfo videoInfo = null;
				if (controller != null)
					videoInfo = controller.getCurrentVideo();
				if (videoInfo != null && !videoInfo.mIsLive) {
					notifyProgressViewVisible(false);
					long current = controller.mVDPlayerInfo.mCurrent;
					long duration = controller.mVDPlayerInfo.mDuration;
					current += keyLeft ? (-7000) : 7000;
					float ret = ((float) current / duration);// ((float)
																// distance /
																// width) *
																// controller.mProgressRate;
					VDLog.e(TAG, "current : " + current + ",duration : "
							+ duration + " , ret = " + ret);
					if (ret < 0) {
						ret = 0;
					} else if (ret > 1) {
						ret = 1;
					}
					controller.dragProgressTo(ret, true, false);
					controller.resume(false);
					ISinaVideoView vv = controller.getVideoView();
					if (!vv.isPlaying()) {
						controller.start();
					}
					notifyPlayStateChanged();
					notifyPlayStateChanged();
				}
				for (OnKeyChangeProgressListener l : mOnKeyChangeProgressListener) {
					l.onKeyDown(keyLeft);
				}
			}
		});
	}

	public void notifyHideControllerBar(final long delay) {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnShowHideControllerListener l : mOnShowHideControllerListener) {
					l.hideControllerBar(delay);
				}
			}
		});
	}

	public void notifyShowControllerBar(final boolean delayHide) {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnShowHideControllerListener l : mOnShowHideControllerListener) {
					l.showControllerBar(delayHide);
				}
				VDVideoViewController controller = VDVideoViewController
						.getInstance(mContext);
				if (controller != null)
					controller.hideStatusBar(!delayHide);
			}
		});
	}

	void notifyProgressViewVisible(final boolean isVisible) {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnProgressViewVisibleListener listener : mOnProgressViewVisibleListener) {
					listener.onProgressVisible(isVisible);
				}
			}
		});
	}

	// DLNA
	public void notifySetCurVolume(final int currVolume) {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnSetSoundListener l : mOnSetSoundListener) {
					l.onSetCurVolume(currVolume);
				}
			}
		});
	}

	public void notifySetMaxVolume(final int maxVolume) {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnSetSoundListener l : mOnSetSoundListener) {
					l.onSetMaxVolume(maxVolume);
				}
			}
		});
	}

	public void notifyRegisterDLNAListener() {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnRegisterDLNAListener l : mOnRegisterDLNAListener) {
					l.register();
				}
			}
		});
	}

	void notifySetDLNALayoutVisible(final boolean visiable) {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnDLNALinearLayoutListener listener : mOnDLNALinearLayoutListener) {
					listener.setLayoutVisiable(visiable);
				}
			}
		});
	}

	void notifyScreenOrientationSwitch(final boolean fullScreen) {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				for (OnScreenOrientationSwitchListener listener : mOnScreenOrientationSwitchListener) {
					listener.onScreenOrientationSwitch(fullScreen);
				}
			}
		});
	}

	public void notifyOnShowHideADContainer(final boolean isShow) {
		mMainHandler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				for (OnShowHideADContainerListener l : mOnShowHideADContainerListener) {
					if (isShow) {
						l.showADContainerBar();
					} else {
						l.hideADContainerBar();
					}
				}
			}
		});
	}

	public void clear() {
		mOnBufferingUpdateListener.clear();
		mOnCompletionListener.clear();
		mOnErrorListener.clear();
		mOnRetryErrorListener.clear();
		mOnInfoListener.clear();
		mOnPreparedListener.clear();
		mOnSeekCompleteListener.clear();
		mOnVideoSizeChangedListener.clear();
		mOnVideoOpenedListener.clear();
		mOnTimedTextListener.clear();
		mOnProgressUpdateListener.clear();
		mOnPlayVideoListener.clear();
		// mOnLiveVideoListener.clear();
		mOnFullScreenListener.clear();
		mOnSoundChangedListener.clear();
		mOnLightingChangeListener.clear();

		mOnScreenTouchListener.clear();
		mOnResolutionListener.clear();
		mOnResolutionContainerListener.clear();
		mOnTipListener.clear();
		mOnLightingVisibleListener.clear();
		mOnLoadingListener.clear();
		mOnSoundVisibleListener.clear();
		mOnVMSResolutionListener.clear();

		mOnVideoGuideTipsListener.clear();
		mOnPauseListener.clear();
		mOnVideoFrameADListener.clear();
		mOnVideoInsertADListener.clear();
		mOnVideoListListener.clear();
		mOnVideoListVisibleChangeListener.clear();
		// mOnResolutionVisibleChangeListener.clear();
		mOnVideoDoubleTapListener.clear();
		mOnDecodingTypeListener.clear();
		mOnScreenOrientationChangeListener.clear();
		mOnShowHideControllerListener.clear();
		mOnSetSoundListener.clear();
		mOnDLNALinearLayoutListener.clear();
		mOnScreenOrientationSwitchListener.clear();
		mOnClickRetryListener.clear();
		mOnVideoUIRefreshListener.clear();
		mOnShowHideADContainerListener.clear();
	}

}
