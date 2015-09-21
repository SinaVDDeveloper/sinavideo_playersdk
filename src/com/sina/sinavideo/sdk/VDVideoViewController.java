package com.sina.sinavideo.sdk;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.PointF;
import android.media.TimedText;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.sina.sinavideo.coreplayer.ISinaVideoView;
import com.sina.sinavideo.coreplayer.splayer.MediaPlayer;
import com.sina.sinavideo.coreplayer.splayer.MediaPlayer.OnBufferingUpdateListener;
import com.sina.sinavideo.coreplayer.splayer.MediaPlayer.OnCompletionListener;
import com.sina.sinavideo.coreplayer.splayer.MediaPlayer.OnErrorListener;
import com.sina.sinavideo.coreplayer.splayer.MediaPlayer.OnInfoListener;
import com.sina.sinavideo.coreplayer.splayer.MediaPlayer.OnPreparedListener;
import com.sina.sinavideo.coreplayer.splayer.MediaPlayer.OnSeekCompleteListener;
import com.sina.sinavideo.coreplayer.splayer.MediaPlayer.OnTimedTextListener;
import com.sina.sinavideo.coreplayer.splayer.MediaPlayer.OnVideoOpenedListener;
import com.sina.sinavideo.coreplayer.splayer.MediaPlayer.OnVideoSizeChangedListener;
import com.sina.sinavideo.coreplayer.splayer.VideoView;
import com.sina.sinavideo.coreplayer.splayer.VideoViewHard;
import com.sina.sinavideo.sdk.VDVideoConfig.eVDDecodingType;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnClickPlayListener;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnClickRetryListener;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnDLNALinearLayoutListener;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnDecodingTypeListener;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnKeyChangeProgressListener;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnKeyEventListener;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnLightingVisibleListener;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnLoadingListener;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnMoreOprationVisibleChangeListener;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnProgressViewVisibleListener;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnRegisterDLNAListener;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnResolutionListButtonListener;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnScreenOrientationChangeListener;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnScreenOrientationSwitchListener;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnSetSoundListener;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnShowHideADContainerListener;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnShowHideBottomControllerListener;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnShowHideControllerListener;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnShowHideTopContainerListener;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnSoundVisibleListener;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnTipListener;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnVMSResolutionListener;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnVideoDoubleTapListener;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnVideoFrameADListener;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnVideoGuideTipsListener;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnVideoInsertADListener;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnVideoListListener;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnVideoListVisibleChangeListener;
import com.sina.sinavideo.sdk.data.VDPlayerErrorInfo;
import com.sina.sinavideo.sdk.data.VDPlayerInfo;
import com.sina.sinavideo.sdk.data.VDResolutionData;
import com.sina.sinavideo.sdk.data.VDVideoInfo;
import com.sina.sinavideo.sdk.data.VDVideoListInfo;
import com.sina.sinavideo.sdk.dlna.DLNAController;
import com.sina.sinavideo.sdk.utils.VDApplication;
import com.sina.sinavideo.sdk.utils.VDLog;
import com.sina.sinavideo.sdk.utils.VDNetworkBroadcastReceiver;
import com.sina.sinavideo.sdk.utils.VDPlayerLightingManager;
import com.sina.sinavideo.sdk.utils.VDPlayerSoundManager;
import com.sina.sinavideo.sdk.utils.VDResolutionManager;
import com.sina.sinavideo.sdk.utils.VDSDKConfig;
import com.sina.sinavideo.sdk.utils.VDSharedPreferencesUtil;
import com.sina.sinavideo.sdk.utils.VDUtility;
import com.sina.sinavideo.sdk.utils.VDVideoFullModeController;
import com.sina.sinavideo.sdk.utils.VDVideoScreenOrientation;
import com.sina.sinavideo.sdk.utils.m3u8.M3u8ContentParser;
import com.sina.sinavideo.sdk.utils.m3u8.M3u8ContentParser.M3u8ParserListener;
import com.sina.video_playersdkv2.R;

/**
 * 控制类，使用引用方式调用CorePlayer中的MediaController<br>
 * 注：VDVideoviewController不是传统意义上的Videoviewcontroller<br>
 * VDVideoViewController更像一个使用EventBus模式处理的消息泵<br>
 * 理论上，所有的控制都通过controller来进行<br>
 * 所有调用返回的数据都通过VDVideoViewListeners的内部接口来返回
 * 
 * @hide
 * @author sunxiao
 */
public class VDVideoViewController implements OnVideoOpenedListener,
		OnVideoSizeChangedListener, OnTimedTextListener,
		OnBufferingUpdateListener, OnCompletionListener, OnErrorListener,
		OnInfoListener, OnPreparedListener, OnSeekCompleteListener {

	public static final int MESSAGE_UPDATE_PROGRESS = 0;
	public static int DEFAULT_DELAY = 5000;
	/**
	 * 播放列表
	 */
	public VDVideoListInfo mVDVideoListInfo = new VDVideoListInfo();
	/**
	 * 播放器状态信息
	 */
	public VDPlayerInfo mVDPlayerInfo = new VDPlayerInfo();

	/**
	 * Core层的VideoView
	 */
	private ISinaVideoView mVideoView = null;
	/**
	 * 外界的事件处理类
	 */
	private VDVideoViewListeners mListeners = null;
	/**
	 * 是否有声音提示组件
	 */
	public boolean mIsHasSoundWidget = false;
	/**
	 * 是否底部控制区执行隐藏动画状态
	 */
	private boolean mIsBottomPannelHiding = false;

	/**
	 * 当前播放进度（100%）
	 */
	public float mProgressRate;
	/**
	 * 网络监控
	 */
	public VDNetworkBroadcastReceiver mReciever = new VDNetworkBroadcastReceiver();

	/**
	 * layer数据
	 */
	public VDVideoViewLayerContextData mLayerContextData = null;

	// 解析m3u8类
	private M3u8ContentParser mParser = null;

	/**
	 * 得到外部事件处理
	 * 
	 * @return
	 */
	public VDVideoExtListeners getExtListener() {
		return mExtListeners;
	}

	private VDVideoExtListeners mExtListeners = null;
	private static Map<Context, VDVideoViewController> mControllers = new HashMap<Context, VDVideoViewController>();
	private final static String TAG = "VDVideoViewController";

	private Context mContext = null;

	private boolean mIsUpdateProgress = false;

	private int mRetryTimes = 0;
	private boolean mIsPlayed = false; // 是否播放过
	private NetChangeListener mNetChangeListener;

	// 广告部分变量
	/**
	 * 广告跳秒的变量
	 */
	private int mADTickerSecNum = -1000;

	private int[] mTmpArr = new int[] { 0, 0 };

	// ------------- end 日志
	/**
	 * 静帧广告控制，使用或方式进行，对应于attrs.xml-VDVideoAdContainer-adConfig
	 */
	public boolean mADIsFromStart = false;
	public int mADConfigEnum = 0;
	private Handler mInsertADHandler = new Handler();
	private Runnable mInsertADRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				if (getCurrentVideo().mIsInsertAD) {
					if (getADTickerSec() > 0) {
						notifyInsertADTicker();
						mInsertADHandler.postDelayed(mInsertADRunnable, 1000);
					} else if (getADTickerSec() == 0) {
						if (mVDVideoListInfo != null
								&& mVDVideoListInfo.isInsertADEnd()) {
							mExtListeners
									.notifyInsertADEnd(VDPlayerErrorInfo.MEDIA_INSERTAD_ERROR_STEPOUT);
						}
						playNext();
					}
				}
			} catch (Exception ex) {

			}
		}
	};

	private int mWhereTopause = 0;
	private Handler mMainHander = new Handler();
	private Runnable mSoundDisapperRunnable = new Runnable() {

		@Override
		public void run() {
			notifySoundSeekBarVisible(false);
			mListeners.notifySoundVisible(false);
		}
	};

	/**
	 * 事件泵
	 */
	private Handler mMessageHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_UPDATE_PROGRESS:
				if (mVideoView == null || getCurrentVideo() == null) {
					return false;
				}
				VDVideoInfo info = getCurrentVideo();
				// 伪直播做点播时候需要seekto到上一次停止时间,所以不能获取真正的当前时间,一般会是0.
				// mNeedSeekTo在非直播且清晰度发生切换时标记
				long position = 0;
				// if (info != null && info.mNeedSeekTo &&
				// info.mVideoPosition >= 0) {
				// position = info.mVideoPosition;
				// } else {
				position = mVideoView.getCurrentPosition();
				// }
				long duration = mVideoView.getDuration();
				if (info != null) {
					info.mVideoPosition = position;
					info.mVideoDuration = duration;
					if (!info.mIsLive) {
						mListeners.notifyProgressUpdate(position, duration);
					}
					info.mVideoPosition = position;
				}
				if (mIsUpdateProgress) {
					long delay = 1000 - (position % 1000);
					if (delay < 50) {
						delay += 1000;
					}
					mMessageHandler.sendEmptyMessageDelayed(
							MESSAGE_UPDATE_PROGRESS, delay);
				}
				break;
			}
			return false;
		}
	});

	public void setLayerContextData(VDVideoViewLayerContextData layerContextData) {
		mLayerContextData = layerContextData;
	}

	public VDVideoViewLayerContextData getLayerContextData() {
		return mLayerContextData;
	}

	public void setScreenOrientationPause(boolean isPause) {
		if (isPause) {
			mWhereTopause = 2;
		} else {
			mWhereTopause = 0;
		}
	}

	public void setSeekPause(boolean isPause) {
		if (isPause) {
			mWhereTopause = 1;
		} else {
			mWhereTopause = 0;
		}
	}

	public void setBeginPause(boolean isPause) {
		if (isPause) {
			mWhereTopause = 4;
		} else {
			mWhereTopause = 0;
		}
	}

	/**
	 * 设置当前底部控制区处于执行隐藏动画状态
	 * 
	 * @param anim
	 */
	public void setBottomPannelHiding(boolean anim) {
		mIsBottomPannelHiding = anim;
	}

	/**
	 * 判定当前底部控制区是否处于执行隐藏动画状态，底部控制区执行隐藏动画屏蔽控制区功能按钮点击操作
	 * 
	 * @return
	 */
	public boolean isBottomPannelHiding() {
		return mIsBottomPannelHiding;
	}

	public VDVideoViewController(Context context) {
		mContext = context;
		mListeners = new VDVideoViewListeners(context);
		mExtListeners = new VDVideoExtListeners(context);
	}

	// public LogPushManager getLogPushManager(){
	// return mLogPushManager;
	// }

	/**
	 * 截获音量键后触发
	 * 
	 * @param keyCode
	 */
	private void handleVolumeKey(int keyCode) {
		int curr = VDPlayerSoundManager.getCurrSoundVolume(mContext);
		int max = VDPlayerSoundManager.getMaxSoundVolume(mContext);
		// float currFloat = (float) curr / max;
		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
			// currFloat -= 0.1f;
			curr--;
		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			// currFloat += 0.1f;
			curr++;
		}
		// if (currFloat < 0) {
		// currFloat = 0;
		// } else if (currFloat > 1) {
		// currFloat = 1.f;
		// }
		if (curr < 0) {
			curr = 0;
		} else if (curr > max) {
			curr = max;
		}
		// notifySoundSeekBarVisible(true);
		mListeners.notifySoundVisible(true);
		// TODO DLNA
		notifySetMaxVolume(VDPlayerSoundManager.getMaxSoundVolume(mContext));
		notifySetCurVolume(VDPlayerSoundManager.getCurrSoundVolume(mContext));

		mMainHander.removeCallbacks(mSoundDisapperRunnable);
		mMainHander.postDelayed(mSoundDisapperRunnable, 1000);
		dragSoundSeekTo(curr);
	}

	/**
	 * 用来提供给activity捕获key的时候使用
	 * 
	 * @param event
	 * @return
	 */
	public boolean onKeyEvent(KeyEvent event) {
		boolean flag = false;
		if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN
				|| event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				// VDVideoViewController.getInstance().notifySoundVisible(true);
				handleVolumeKey(event.getKeyCode());
				VDLog.e(TAG,
						"dispatchKeyEvent截获KEYCODE_VOLUME_DOWN|KEYCODE_VOLUME_UP");
			}
			if (mIsHasSoundWidget) {
				flag = true;
			}
		} else if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
				&& mLayerContextData.getLayerType() != VDVideoViewLayerContextData.LAYER_COMPLEX_NOVERTICAL) {
			if (!VDVideoFullModeController.getInstance().getIsPortrait()) {
				setIsFullScreen(false);
				flag = true;
			}
		}
		return flag;
	}

	/**
	 * 设置清晰度
	 * 
	 * @deprecated 不允许从外部直接设置清晰度
	 * @param resolution
	 */
	// public void setCurResolution(String resolution) {
	// mCurResolution = resolution;
	// }

	/**
	 * 设置是否锁定屏幕方向-不转屏
	 * 
	 * @param isLockScreen
	 */
	public void setIsLockScreen(boolean isLockScreen) {
		VDVideoFullModeController.getInstance().setFullLock();
	}

	/**
	 * 设置横屏，手动控制调用这里
	 */
	public void setIsFullScreen(boolean isFullScreen) {
		if (mContext == null) {
			VDLog.e(VDVideoFullModeController.TAG,
					"controller---setIsFullScreen---mContext--return null");
			return;
		}

		if (VDVideoFullModeController.getInstance().getIsFullScreen() == isFullScreen) {
			return;
		}

		if (mLayerContextData.getLayerType() == VDVideoViewLayerContextData.LAYER_COMPLEX_NOVERTICAL) {
			if (!isFullScreen) {
				// 只横不竖的时候，且转到竖屏，那么直接关闭当前activity
				((Activity) mContext).finish();
			} else {
				// 转到横屏，什么都不做
			}
			return;
		}

		VDVideoFullModeController.getInstance().setIsManual(true);
		VDVideoFullModeController.getInstance().mInHandNum = 0;
		// 发送转屏指令
		if (isFullScreen) {
			VDVideoScreenOrientation.setLandscape(mContext);
		} else {
			VDVideoScreenOrientation.setPortrait(mContext);
		}
	}

	/**
	 * 返回VideoView的引用，用来装载
	 * 
	 * @return
	 */
	public ISinaVideoView getVideoView() {
		return mVideoView;
	}

	/**
	 * 获得播放器当前状态
	 * 
	 * @return
	 */
	public int getPlayerStatus() {
		return mVDPlayerInfo.mPlayStatus;
	}

	/**
	 * 当前是否播放状态
	 * 
	 * @return
	 */
	public boolean getIsPlaying() {
		if (mVideoView == null) {
			return false;
		}
		// NOTE: 后期修改为mPlayerstatus方式
		return mVideoView.isPlaying();
	}

	/**
	 * 增加一个重置首屏GuideTips的地方
	 * 
	 * @param context
	 * @param isFirst
	 *            为true，则重置为首次打开，false则为已经打开过了
	 */
	public void setFirstFullScreen(Context context, boolean isFirst) {
		VDSharedPreferencesUtil.setFirstFullScreen(context, isFirst);
	}

	/**
	 * 单例启动方法，只能在主线程中调用
	 * 
	 * @return
	 */
	public static void register(Context context,
			VDVideoViewController controller) {
		if (Looper.myLooper() != Looper.getMainLooper()) {
			throw new IllegalStateException(TAG + " create not in main thread.");
		}
		mControllers.put(context, controller);
	}

	public static void unRegister(Context context) {
		if (Looper.myLooper() != Looper.getMainLooper()) {
			throw new IllegalStateException(TAG + " create not in main thread.");
		}
		mControllers.remove(context);
	}

	public static VDVideoViewController getInstance(Context context) {
		if (Looper.myLooper() != Looper.getMainLooper()) {
			throw new IllegalStateException(TAG + " create not in main thread.");
		}
		// if (mVDMediaController == null) {
		// mVDMediaController = new VDVideoViewController();
		// }
		return mControllers.get(context);
	}

	/**
	 * 设置上下文<br>
	 * 注：用activity来填充
	 * 
	 * @param context
	 */
	public void setContext(Context context) {
		if (context == null) {
			return;
		}
		mContext = context;
		VDApplication.getInstance().setContext(context);
	}

	/**
	 * 得到上下文<br>
	 * 注：可直接转为容器的activity
	 * 
	 * @return
	 */
	public Context getContext() {
		return mContext;
	}

	/**
	 * 创建方法，每次切换视频，重新建立一个新的
	 * 
	 * @param context
	 *            上下文
	 */
	public static ISinaVideoView create(Context context) {
		if (Looper.myLooper() != Looper.getMainLooper()) {
			throw new IllegalStateException(TAG + " create not in main thread.");
		}
		if (context == null) {
			return null;
		}
		VDVideoViewController controller = VDVideoViewController
				.getInstance(context);
		if (controller == null) {
			controller = new VDVideoViewController(context);
			VDVideoViewController.register(context, controller);
		}
		controller.mContext = context;
		// 监听网络变化
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

		context.registerReceiver(controller.mReciever, intentFilter);
		if (controller.mNetChangeListener == null) {
			controller.mNetChangeListener = new NetChangeListener(context);
			controller.mReciever.addListener(controller.mNetChangeListener);
		}

		// 初始化转屏部分
		VDVideoFullModeController.getInstance().init(context);
		if (VDVideoConfig.mDecodingType == eVDDecodingType.eVDDecodingTypeSoft) {
			controller.mVideoView = new VideoView(context);
		} else if (VDVideoConfig.mDecodingType == eVDDecodingType.eVDDecodingTypeHardWare) {
			controller.mVideoView = new VideoViewHard(context);
		} else {
			if (VDSharedPreferencesUtil.isDecodingTypeFFMpeg(context)) {
				controller.mVideoView = new VideoView(context);
			} else {
				controller.mVideoView = new VideoViewHard(context);
			}
		}

		// 事件监听
		controller.mVideoView.setOnCompletionListener(controller);
		controller.mVideoView.setOnPreparedListener(controller);
		controller.mVideoView.setOnInfoListener(controller);
		controller.mVideoView.setOnErrorListener(controller);
		controller.mVideoView.setOnSeekCompleteListener(controller);
		controller.mVideoView.setOnBufferingUpdateListener(controller);

		// 对于guidetips做一下初始化
		if (VDSharedPreferencesUtil.isFirstFullScreen(context)) {
			// 是首次打开
			controller.notifyGuideTips(true);
			VDSharedPreferencesUtil.setFirstFullScreen(context, false);
		} else {
			controller.notifyGuideTips(false);
		}

		return controller.mVideoView;
	}

	public VDVideoListInfo getVideoList() {
		return mVDVideoListInfo;
	}

	// --------控制方法区begin-----------//

	/**
	 * 设置播放列表
	 * 
	 * @param infoList
	 */
	public void setVideoList(VDVideoListInfo infoList) {
		if (infoList == null) {
			return;
		}

		mVDVideoListInfo = infoList;
		if (mListeners == null) {
			mListeners = new VDVideoViewListeners(mContext);
		}
		mListeners.notifyVideoList(infoList);
	}

	/**
	 * 获得当前广告的秒数
	 * 
	 * @return
	 */
	public int getADTickerSec() {
		return getCurrentVideo().mInsertADSecNum;
	}

	/**
	 * 返回处理过的广告跳秒数
	 * 
	 * @return
	 */
	public synchronized int refreshADTickerSec() {
		if (mADTickerSecNum > 0) {
			mADTickerSecNum--;
		}

		if (getCurrentVideo().mInsertADSecNum > 0) {
			getCurrentVideo().mInsertADSecNum--;
		}

		return mADTickerSecNum;
	}

	/**
	 * VDVideoListInfo的controller包装版本<br />
	 * 尽量使用VDVideoView来调用
	 * 
	 * @param insertADList
	 * @param currInfo
	 */
	public int refreshInsertADList(List<VDVideoInfo> insertADList,
			VDVideoInfo currInfo) {
		return mVDVideoListInfo.refreshInsertADList(insertADList, currInfo);
	}

	private boolean playVideoOnInfoKey(int index) {
		if (mVideoView == null || mVDVideoListInfo == null || index < 0
				|| index >= mVDVideoListInfo.getVideoListSize()) {
			return false;
		}
		stop();

		notifyVideoUIRefresh();
		boolean same = mVDVideoListInfo.mIndex == index;
		mVDVideoListInfo.mIndex = index;

		// 设置广告的跳秒数
		try {
			if (!mVDVideoListInfo.isCanPlay()) {
			}
		} catch (IllegalArgumentException ex) {
			VDLog.e(TAG, ex.getMessage());
			return false;
		}
		VDVideoInfo currInfo = mVDVideoListInfo.getCurrInfo();
		if (currInfo == null) {
			throw new IllegalArgumentException("url为null");
		}
		if (mVDVideoListInfo.getVideoInfo(index).mIsInsertAD
				&& mVDVideoListInfo.mInsertADSecNum == 0
				&& mVDVideoListInfo.getADNum() > 1
				&& mVDVideoListInfo.mIsSetInsertADTime) {
			// 如果是多流多广告类型，那么就需要手动设置广告时间，如果为0，那么要提示错误。
			throw new IllegalArgumentException(
					"如果未设置mIsSetInsertADTime的多流多广告类型，那么就需要手动设置广告时间，不能为0。");
		}
		if (mVDVideoListInfo.mInsertADType != VDVideoListInfo.INSERTAD_TYPE_NONE) {
			if (mVDVideoListInfo.mInsertADSecNum != 0) {
				mADTickerSecNum = mVDVideoListInfo.mInsertADSecNum;
				notifyInsertAD(true);
			}
		}
		// 开始具体的播放逻辑
		final VDVideoInfo videoInfo = mVDVideoListInfo
				.getVideoInfo(mVDVideoListInfo.mIndex);
		mListeners.notifyVideoInfo(videoInfo);
		VIDEO_TIME_OUT = videoInfo.mIsLive ? VIDEO_LIVE_TIME_OUT
				: VIDEO_NORMAL_TIME_OUT;
		notifyShowLoading();
		if (videoInfo.mIsLive || videoInfo.isM3u8()) {
			playLiveVideo(videoInfo);
		} else {
			playOnDemandVideo(videoInfo);
			if (!same) {
				mRetryTimes = 0;
			}
		}
		notifyClickRetry();
		return true;
	}

	/**
	 * 播放视频
	 * 
	 * @param index
	 *            正片列表数组中的下标
	 * @return
	 */
	public boolean playVideo(int index) {
		index = mVDVideoListInfo.getCurrKeyFromRealInfo(index);
		// 日志上报的start部分，从这儿开始
		return playVideoOnInfoKey(index);
	}

	private void startM3u8ContentParser(VDVideoInfo info) {
		mParser = new M3u8ContentParser(new MyM3u8ParserListener(info),
				info.mVideoId, mContext);
		mParser.startParserM3u8(info.mPlayUrl);
	}

	/**
	 * 播放直播视频
	 * 
	 * @param info
	 */
	private void playLiveVideo(VDVideoInfo info) {
		if (!info.mIsParsed) {
			startM3u8ContentParser(info);
		} else {
			String url = null;
			mListeners.notifyResolutionChanged(VDResolutionManager.getInstance(
					mContext).getCurrResolutionTag());
			setVideoPath(url);
			mListeners.notifyShowLoading(true);
			mListeners.notifyVideoPrepared(false);
		}
	}

	/**
	 * 播放点播视频
	 * 
	 * @param videoInfo
	 */
	public void playOnDemandVideo(VDVideoInfo videoInfo) {
		if (DLNAController.mIsDLNA) {
			DLNAController.getInstance(mContext).open(videoInfo.mRedirectUrl);
		} else {
			playVideoWithInfo(videoInfo);
		}
	}

	/**
	 * 播放点播视频
	 * 
	 * @param videoInfo
	 */
	public void playVideoWithInfo(VDVideoInfo videoInfo) {
		if (VDUtility.isLocalUrl(videoInfo.mPlayUrl)) {
			videoInfo.mRedirectUrl = videoInfo.mPlayUrl;
		}
		String url = videoInfo.mRedirectUrl == null ? videoInfo.mPlayUrl
				: videoInfo.mRedirectUrl;
		setVideoPath(url);
		mListeners.notifyShowLoading(true);
		mListeners.notifyVideoPrepared(false);
		mIsPlayed = false;
		notifyClickRetry();
	}

	/**
	 * 设置视频地址
	 * 
	 * @param url
	 */
	public void setVideoPath(String url) {
		getCurrentVideo().mIsParsed = true;
		if (url != null) {
			mVideoView.setVideoPath(url);
		}
	}

	private void updatePlayState() {
		if (mVDPlayerInfo != null && mVideoView != null) {
			mVDPlayerInfo.mPlayStatus = mVideoView.isPlaying() ? VDPlayerInfo.PLAYER_STARTED
					: VDPlayerInfo.PLAYER_PAUSE;
			mVDPlayerInfo.mIsPlaying = mVideoView.isPlaying();
		}
		if (mListeners != null) {
			mListeners.notifyPlayStateChanged();
		}
	}

	/**
	 * 通知清晰度容器是否显示
	 * 
	 * @param isVisible
	 */
	public void notifyResolutionContainerVisible(boolean isVisible) {
		if (mListeners != null) {
			mListeners.notifyResolutionVisible(isVisible);
		}
	}

	/**
	 * 清晰度变更
	 * 
	 * @param tag
	 */
	public void notifyResolutionChanged(String tag) {
		if (mListeners != null) {
			mListeners.notifyResolutionChanged(tag);
		}
	}

	/**
	 * 清晰度解析完毕后回传
	 * 
	 * @param resolutionData
	 */
	public void notifyResolutionParsed(VDResolutionData resolutionData) {
		if (mListeners != null) {
			mListeners.notifyResolutionParsed(resolutionData);
		}
	}

	/**
	 * 状态变更入口
	 */
	public void notifyPlayStateChanged() {
		if (mListeners != null) {
			mListeners.notifyPlayStateChanged();
		}
	}

	/**
	 * 表明当前是否处在播放视频状态中
	 * 
	 * @return
	 */
	public boolean isInsertAD() {
		try {
			return getCurrentVideo().mIsInsertAD;
		} catch (Exception ex) {

		}
		return false;
	}

	/**
	 * 当前视频信息
	 */
	public VDVideoInfo getCurrentVideo() {
		try {
			return mVDVideoListInfo.getVideoInfo(mVDVideoListInfo.mIndex);
		} catch (Exception ex) {
			VDLog.e(TAG, ex.getMessage());
		}
		return null;
	}

	/**
	 * 当前视频清晰度集合对应信息
	 * 
	 * @return
	 */
	public HashMap<String, String> getCurrVMSResolutionInfo() {
		if (mVDVideoListInfo.mIndex >= 0
				&& mVDVideoListInfo.mIndex < mVDVideoListInfo
						.getVideoListSize()) {
			return mVDVideoListInfo.getVideoInfo(mVDVideoListInfo.mIndex)
					.getVMSDefinitionInfo();
		}
		return null;
	}

	/**
	 * 得到播放列表数量
	 * 
	 * @return
	 */
	public int getVideoInfoNum() {
		try {
			return mVDVideoListInfo.getRealVideoListSize();
		} catch (Exception ex) {
			return 0;
		}
	}

	/**
	 * 得到播放器信息【重构的时候，看是否去掉，这个结构可以放到controller里面】
	 * 
	 * @return
	 */
	public VDPlayerInfo getPlayerInfo() {
		return mVDPlayerInfo;
	}

	/**
	 * 得到当前进度
	 * 
	 * @return
	 */
	public long getCurrentPosition() {
		if (mVideoView != null) {
			return mVideoView.getCurrentPosition();
		}
		return 0;
	}

	/**
	 * 开始
	 */
	public void start() {
		if (mVideoView != null) {
			mVideoView.start();
			updatePlayState();
			mListeners.notifyVideoFrameADEnd();
		}
	}

	/**
	 * 是否可以显示广告
	 * 
	 * @return
	 */
	public boolean isCanShowFrameAD() {
		// 1、用户点击暂停
		if (mWhereTopause == 0 && ((mADConfigEnum & 1) == 1))
			return true;
		// 2、用户滑动视频引起的暂停
		if (mWhereTopause == 1 && ((mADConfigEnum & 2) == 2))
			return true;
		// 3、转屏引起的暂停
		if (mWhereTopause == 2 && ((mADConfigEnum & 4) == 4))
			return true;
		// 4、视频开始的时候，仅限于正片
		if (mWhereTopause == 4 && ((mADConfigEnum & 8) == 8))
			return true;

		return false;
	}

	/**
	 * 暂停
	 */
	public void pause() {
		if (mVideoView != null) {
			mVideoView.pause();
		}
		stopUpdateMessage();
		mTimeOutHandler.removeMessages(NET_TIME_OUT);
		mTimeOutHandler.removeMessages(CHECK_LIVE_TIME_OUT);
		updatePlayState();

		pauseInsertAD();

		// if (isCanShowFrameAD()) {
		// mListeners.notifyVideoFrameADBegin();
		// }
	}

	private void pauseInsertAD() {
		try {
			if (getCurrentVideo().mIsInsertAD) {
				mInsertADHandler.removeCallbacks(mInsertADRunnable);
			}
		} catch (Exception ex) {
			VDLog.e(TAG, ex.getMessage());
		}
	}

	private void resumeInsertAD() {
		try {
			if (getCurrentVideo().mIsInsertAD) {
				mInsertADHandler.postDelayed(mInsertADRunnable, 1000);
			}
		} catch (Exception ex) {
			VDLog.e(TAG, ex.getMessage());
		}
	}

	/**
	 * 继续
	 */
	public void resume() {
		resume(true);
	}

	/**
	 * 继续
	 * 
	 * @param updateUI
	 *            是否更新当前UI
	 */
	public void resume(boolean updateUI) {
		if (mVideoView != null) {
			mVideoView.resume();
		}
		if (updateUI) {
			startUpdateMessage();
		}
		updatePlayState();
		VDVideoFullModeController.getInstance().enableSensor(true);

		resumeInsertAD();

		mListeners.notifyVideoFrameADEnd();
		mVDPlayerInfo.mPlayStatus = VDPlayerInfo.PLAYER_RESUME;
	}

	/**
	 * 进度条拖动
	 */
	public void dragProgressTo(float curr) {
		dragProgressTo(curr, false, false);
	}

	/**
	 * 进度条拖动
	 * 
	 * @param curr
	 *            当前
	 * @param seek
	 *            拖动到
	 * @param isgesture
	 *            TODO 此处需要修改
	 */
	public void dragProgressTo(float curr, boolean seek, boolean isgesture) {
		long duration = 0;
		if (DLNAController.mIsDLNA) {
			VDVideoViewController controller = VDVideoViewController
					.getInstance(mContext);
			if (controller != null) {
				duration = DLNAController.getInstance(controller.getContext()).mDuration;
			}
		} else {
			duration = mVDPlayerInfo.mDuration;
		}
		long seekTo = (long) (curr * duration);
		VDLog.i("VDVideoViewListeners", "seek to : curr = " + curr + " , "
				+ seekTo);
		if (seek) {
			if (DLNAController.mIsDLNA) {
				DLNAController.getInstance(mContext).seek(seekTo);
			} else {
				mVideoView.seekTo(seekTo);
			}
		}
		if (DLNAController.mIsDLNA) {
			DLNAController.mTmpPosition = seekTo;
		} else {
			mVDPlayerInfo.mCurrent = seekTo;
		}
		mListeners.notifyDragTo(seekTo, duration);
	}

	/**
	 * 屏幕宽高
	 * 
	 * @return
	 */
	public int[] getScreen() {
		if (mContext == null) {
			return mTmpArr;
		}
		WindowManager wm = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);

		if (VDUtility.getSDKInt() > 13) {

		}
		DisplayMetrics metrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metrics);
		int[] rect = { metrics.widthPixels, metrics.heightPixels };

		return rect;
	}

	public boolean isPlaying() {
		return mVideoView.isPlaying();
	}

	/**
	 * 调节音量
	 * 
	 * @param currVolume
	 *            音量值
	 */
	public void dragSoundSeekTo(int currVolume) {
		VDPlayerSoundManager.dragSoundSeekTo(mContext, currVolume, true);
	}

	/**
	 * 调整系统亮度到
	 * 
	 * @param curr
	 */
	public void dragLightingTo(float curr, boolean notify) {

		VDPlayerLightingManager.getInstance().dragLightingTo(mContext, curr,
				notify);
		mVDPlayerInfo.mCurLighting = curr;
		Settings.System.putInt(this.mContext.getContentResolver(),
				Settings.System.SCREEN_BRIGHTNESS, (int) (curr * 255));
		if (notify) {
			mListeners.notifyLightingSetting(curr);
		}
	}

	/**
	 * 得到当前亮度
	 * 
	 * @return
	 */
	public float getCurrLightingSetting() {
		// if (mCurrLightingNum < 0 && mContext != null) {
		// mCurrLightingNum =
		// Settings.System.getInt(this.mContext.getContentResolver(),
		// Settings.System.SCREEN_BRIGHTNESS, 125);
		// mCurrLightingNum = mCurrLightingNum / 255;
		// }
		// return mCurrLightingNum;
		return VDPlayerLightingManager.getInstance().getCurrLightingSetting(
				mContext);
	}

	/**
	 * 得到系统当前是否是自动调节亮度
	 * 
	 * @return
	 */
	public boolean getIsAutoLightingSetting() {
		// boolean ret = false;
		// try {
		// ret = Settings.System.getInt(mContext.getContentResolver(),
		// Settings.System.SCREEN_BRIGHTNESS_MODE) ==
		// Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
		// } catch (Exception ex) {
		// ex.printStackTrace();
		// VDLog.e(TAG, ex.getMessage());
		// }
		// return ret;
		return VDPlayerLightingManager.getInstance().getIsAutoLightingSetting(
				mContext);
	}

	/**
	 * 设置是否是自动亮度调节
	 * 
	 * @param isAutoLighting
	 */
	public void setAutoLighting(boolean isAutoLighting) {
		VDPlayerLightingManager.getInstance().setAutoLighting(mContext,
				isAutoLighting);
	}

	/**
	 * 结束
	 */
	public void stop() {
		if (mParser != null) {
			mParser.cancelParserM3U8();
		}

		mTimeOutHandler.removeMessages(CHECK_LIVE_TIME_OUT);
		mTimeOutHandler.removeMessages(NET_TIME_OUT);
		mInsertADHandler.removeCallbacks(mInsertADRunnable);
		if (mVideoView != null) {
			mVideoView.stopPlayback();
			mVideoView.setVideoURI(null);
		}
		updatePlayState();
		mVDPlayerInfo.mPlayStatus = VDPlayerInfo.PLAYER_FINISHING;
	}

	/**
	 * 播放上一个
	 */
	public void playPre() {
		stop();
		int currIndex = mVDVideoListInfo.mIndex - 1;
		if (currIndex < 0) {
			currIndex = getVideoInfoNum() - 1;
		}
		playVideoOnInfoKey(currIndex);
		notifyVideoUIRefresh();
	}

	/**
	 * 播放下一个视频
	 */
	public void playNext() {
		stop();
		if (getCurrentVideo().mIsInsertAD) {
			mVDVideoListInfo.mInsertADSecNum = 0;
			mADTickerSecNum = 0;
		}
		int currIndex = mVDVideoListInfo.mIndex + 1;
		if (currIndex >= mVDVideoListInfo.getVideoListSize()) {
			currIndex = 0;
		}
		playVideoOnInfoKey(currIndex);
		notifyVideoUIRefresh();
	}

	/**
	 * 拖动，已毫秒为单位
	 * 
	 * @param mSec
	 */
	public void seekTo(long mSec) {
		mVideoView.seekTo((int) mSec);
	}

	/**
	 * 重新启动播放器，以匹配使用软硬件解码方式
	 * 
	 * @param sec
	 *            播放进度值
	 */
	public void reset(long sec) {
		VDVideoInfo videoInfo = getCurrentVideo();
		videoInfo.mVideoPosition = sec;
		getExtListener().notifySwitchPlayerListener(videoInfo,
				mVDVideoListInfo.mIndex);
		getExtListener().notifyPlayerChange(mVDVideoListInfo.mIndex, sec);
	}

	/**
	 * 调整当前视频的分辨率
	 * 
	 * @param sec
	 *            播放进度值
	 * @param resolutionTag
	 *            新分辨率对应的数值
	 */
	public void changeResolution(long sec, String resolutionTag) {
		VDResolutionManager.getInstance(mContext).setResolutionTag(
				resolutionTag);
		mListeners.notifyVMSResolutionChanged();
		VDVideoInfo videoInfo = getCurrentVideo();
		videoInfo.mPlayUrl = videoInfo.getVMSDefinitionInfo()
				.get(resolutionTag);
		videoInfo.mVideoPosition = sec;
		// NOTE 这个接口用得有问题，准备清理掉
		getExtListener().notifySwitchPlayerListener(videoInfo,
				mVDVideoListInfo.mIndex);
		getExtListener().notifyPlayerChange(mVDVideoListInfo.mIndex, sec);
	}

	/**
	 * 处理资源清理，切换时候必须调用
	 */
	public void release() {
		if (mReciever != null && mContext != null) {
			mContext.unregisterReceiver(mReciever);
		}
		stop();

		mVideoView = null;
		mListeners.clear();
		mExtListeners.clear();
		VDPlayerSoundManager.clear();
		mVDPlayerInfo.init();
		VDVideoFullModeController.getInstance().release();
	}

	/**
	 * 轻度清理资源，在重新加载视频资源时调用
	 */
	public void release2() {
		if (mReciever != null && mContext != null) {
			mContext.unregisterReceiver(mReciever);
		}
		stop();
		mVideoView = null;
		mVDPlayerInfo.init();
		mContext = null;
	}

	/**
	 * 开始更新播放进度
	 */
	private void startUpdateMessage() {
		if (!DLNAController.mIsDLNA) {
			mIsUpdateProgress = true;
			mMessageHandler.removeMessages(MESSAGE_UPDATE_PROGRESS);
			mMessageHandler.sendEmptyMessage(MESSAGE_UPDATE_PROGRESS);
		}
	}

	/**
	 * 停止更新播放进度
	 */
	private void stopUpdateMessage() {
		mIsUpdateProgress = false;
		mMessageHandler.removeMessages(MESSAGE_UPDATE_PROGRESS);
	}

	/**
	 * 通知播放暂停
	 */
	public void notifyPlayOrPause() {
		mListeners.notifyPlayOrPause();
	}

	/**
	 * 通知屏幕单击
	 * 
	 * @param ev
	 *            点击事件对象
	 * @param flag
	 *            点击的状态
	 */
	public void touchScreenSingleEvent(MotionEvent ev,
			VDVideoViewListeners.eSingleTouchListener flag) {
		mListeners.notifyScreenSingleTouch(ev, flag);
	}

	/**
	 * 通知按键事件
	 */
	public void notifyKeyEvent() {
		mListeners.notifyKeyEvent();
	}

	/**
	 * 通知导航键左右事件
	 */
	public void notifyKeyLeftRightEvent() {
		mListeners.notifyKeyLeftRightEvent();
	}

	/**
	 * 通知屏幕双击
	 * 
	 * @param ev
	 *            双击事件对象
	 * @param flag
	 *            双击的状态
	 */
	public void touchScreenDoubleEvent(MotionEvent ev,
			VDVideoViewListeners.eDoubleTouchListener flag) {
		mListeners.notifyScreenDoubleTouch(ev, flag);
	}

	/**
	 * 通知屏幕横向滑动
	 * 
	 * @param point1
	 *            手指接触屏幕的最开始坐标点
	 * @param point2
	 *            滑动后坐标点
	 * @param beginPoint
	 *            滑动前坐标点
	 * @param flag
	 *            滑动的状态
	 */
	public void touchScreenHorizonScrollEvent(final PointF point1,
			final PointF point2, final PointF beginPoint,
			VDVideoViewListeners.eHorizonScrollTouchListener flag) {
		mListeners.notifyScreenHorizonScrollTouch(point1, point2, beginPoint,
				flag);
	}

	/**
	 * 设置进度显示比例
	 */
	public void setProgressRate() {
		if (mVDPlayerInfo.mDuration <= 0) {
			return;
		}
		boolean isPortrait = VDVideoFullModeController.getInstance()
				.getIsPortrait();
		long duration = mVDPlayerInfo.mDuration;
		if (duration < 10 * 60 * 1000) {
			if (isPortrait) {
				mProgressRate = 60 * 1000f / duration;
			} else {
				mProgressRate = 90 * 1000f / duration;
			}

		} else if (duration < 20 * 60 * 1000) {
			if (isPortrait) {
				mProgressRate = 2 * 60 * 1000f / duration;
			} else {
				mProgressRate = 150 * 1000f / duration;
			}
		} else {
			if (isPortrait) {
				mProgressRate = 5 * 60 * 1000f / duration;
			} else {
				mProgressRate = 460 * 1000f / duration;
			}
		}
	}

	/**
	 * 通知屏幕横向滑动
	 * 
	 * @param point1
	 *            手指接触屏幕的最开始坐标点
	 * @param point2
	 *            滑动后坐标点
	 * @param beginPoint
	 *            滑动前坐标点
	 * @param flag
	 *            滑动的状态
	 * @param distansY
	 *            纵向滑动的距离
	 */
	public void touchScreenVerticalScrollEvent(final PointF point1,
			final PointF point2, final PointF beginPoint,
			final VDVideoViewListeners.eVerticalScrollTouchListener flag,
			float distansY) {
		mListeners.notifyScreenVerticalScrollTouch(point1, point2, beginPoint,
				flag, distansY);
	}

	/**
	 * 设置清晰度，更新清晰度UI
	 * 
	 * @param resolution
	 *            对应数值
	 */
	// public void setResolution(String resolution) {
	// this.mCurResolution = resolution;
	// mListeners.notifyResolutionSelect(resolution);
	// }

	/**
	 * 通知音量图标是否可见
	 * 
	 * @param isVisible
	 *            true 可见；false 不可见
	 */
	public void notifySoundSeekBarVisible(boolean isVisible) {
		mListeners.notifySoundSeekBarVisible(isVisible);
	}

	/**
	 * 通知清晰度选择窗口是否可见
	 * 
	 * @param isVisible
	 *            true 可见；false 不可见
	 */
	public void notifyDefinitionContainerVisible(boolean isVisible) {
		mListeners.notifyVMSResolutionContainerVisible(isVisible);
	}

	/**
	 * 横竖屏转换时候，会调用此方法
	 * 
	 * @param isFullScreen
	 *            true 全屏；false 非全屏
	 * @param isFromHand
	 *            true 手动操作；false 非手动操作
	 */
	public void notifyFullScreen(final boolean isFullScreen,
			final boolean isFromHand) {
		mListeners.notifyFullScreen(isFullScreen, isFromHand);
	}

	/**
	 * guideTips通知
	 * 
	 * @param isVisible
	 *            true 可见；false 不可见
	 */
	public void notifyGuideTips(boolean isVisible) {
		mListeners.notifyGuideTips(isVisible);
	}

	/**
	 * 通知显示加载页面
	 */
	public void notifyShowLoading() {
		mListeners.notifyShowLoading();
	}

	/**
	 * 通知隐藏加载页面
	 */
	public void notifyHideLoading() {
		mListeners.notifyHideLoading();
	}

	/**
	 * 通知显示或隐藏相关视频列表
	 */
	public void notifyToogleVideoList() {
		mListeners.notifyVideoListVisibelChange();
	}

	/**
	 * 通知显示视频列表
	 */
	public void notifyShowVideoList() {
		mListeners.notifyShowVideoList();
	}

	/**
	 * 通知隐藏视频列表
	 */
	public void notifyHideVideoList() {
		mListeners.notifyHideVideoList();
	}

	/**
	 * 显示静帧广告
	 */
	public void notifyVideoFrameADBegin() {
		if (mListeners != null) {
			mListeners.notifyVideoFrameADBegin();
		}
	}

	public void notifyRemoveAndHideDelayVideoList() {
		mListeners.removeAndHideDelayVideoList();
	}

	public void notifyShowMoreOprationPanel() {
		mListeners.notifyShowMoreOprationPanel();
	}

	public void notifyHideMoreOprationPanel() {
		mListeners.notifyHideMoreOprationPanel();
	}

	public void notifyRemoveAndHideDelayMoreOprationPanel() {
		mListeners.notifyRemoveAndHideDelayMoreOprationPanel();
	}

	/**
	 * 通知屏幕旋转
	 * 
	 * @param vertical
	 *            true 垂直；false 水平
	 */
	public void notifyScreenOrientationChange(boolean vertical) {
		mListeners.notifyScreenOrientationChange(vertical);
	}

	/**
	 * 通知隐藏底部控制区
	 */
	public void notifyHideBottomControllerBar() {
		mListeners.notifyHideBottomControllerBar();
	}

	/**
	 * 通知显示底部控制区
	 */
	public void notifyShowBottomControllerBar() {
		mListeners.notifyShowBottomControllerBar();
	}

	/**
	 * 通知隐藏顶部控制区
	 */
	public void notifyHideTopControllerBar() {
		mListeners.notifyHideTopControllerBar();
	}

	/**
	 * 通知显示顶部控制区
	 */
	public void notifyShowTopControllerBar() {
		mListeners.notifyShowTopControllerBar();
	}

	/**
	 * 通知不隐藏控制区
	 */
	public void notifyNotHideControllerBar() {
		mListeners.notifyNotHideControllerBar();
	}

	/**
	 * 通知隐藏控制区
	 * 
	 * @param delay
	 *            延时时间
	 */
	public void notifyHideControllerBar(long delay) {
		mListeners.notifyHideControllerBar(delay);
	}

	/**
	 * 通知显示控制区
	 * 
	 * @param delayHide
	 *            延时时间
	 */
	public void notifyShowControllerBar(boolean delayHide) {
		mListeners.notifyShowControllerBar(delayHide);
	}

	/**
	 * 通知控制区需提前隐藏内容
	 */
	public void notifyControllerBarPreHide() {
		mListeners.notifyControllerBarPreHide();
	}

	/**
	 * 通知控制区需提前显示内容
	 */
	public void notifyControllerBarPreShow() {
		mListeners.notifyControllerBarPreShow();
	}

	/**
	 * 通知控制区需延时隐藏内容
	 */
	public void notifyControllerBarPostHide() {
		mListeners.notifyControllerBarPostHide();
	}

	/**
	 * 通知控制区需延时显示内容
	 */
	public void notifyControllerBarPostShow() {
		mListeners.notifyControllerBarPostShow();
	}

	/**
	 * 通知TV中导航键中左键右键的操作
	 * 
	 * @param keyLeft
	 *            true 表示导航左键；false 表示导航右键
	 */
	public void notifyKeyChangeProgress(boolean keyLeft) {
		mListeners.notifyKeyChangeProgress(keyLeft);
	}

	/**
	 * 通知视频播放进度
	 * 
	 * @param current
	 *            当前播放时间
	 * @param duration
	 *            总时长
	 */
	public void notifyProgressUpdate(long current, long duration) {
		mListeners.notifyProgressUpdate(current, duration);
	}

	public void notifySoundChanged(final int curr) {
		mListeners.notifySoundChanged(curr);
	}

	public void notifyLightingSetting(final float curr) {
		mListeners.notifyLightingSetting(curr);
	}

	public void notifyOnShowHideADContainer(final boolean isShow) {
		mListeners.notifyOnShowHideADContainer(isShow);
	}

	public void notifyResolutionListButtonFocusFirst() {
		mListeners.notifyResolutionListButtonFirstFocus();
	}

	// --------控制方法区end-----------//

	// --------回调方法区begin-----------//

	/**
	 * 添加缓冲更新回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void addOnBufferingUpdateListener(
			VDVideoViewListeners.OnBufferingUpdateListener l) {
		mListeners.addOnBufferingUpdateListener(l);
	}

	/**
	 * 移除缓冲更新回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void removeOnBufferingUpdateListener(
			VDVideoViewListeners.OnBufferingUpdateListener l) {
		mListeners.removeOnBufferingUpdateListener(l);
	}

	/**
	 * 添加加载完成回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void addOnCompletionListener(
			VDVideoViewListeners.OnCompletionListener l) {
		mListeners.addOnCompletionListener(l);
	}

	/**
	 * 移除加载完成回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void removeOnCompletionListener(
			VDVideoViewListeners.OnCompletionListener l) {
		mListeners.removeOnCompletionListener(l);
	}

	/**
	 * 添加错误回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void addOnErrorListener(VDVideoViewListeners.OnErrorListener l) {
		mListeners.addOnErrorListener(l);
	}

	/**
	 * 移除错误回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void removeOnErrorListener(VDVideoViewListeners.OnErrorListener l) {
		mListeners.removeOnErrorListener(l);
	}

	/**
	 * 添加重试错误回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void addOnRetryErrorListener(VDVideoViewListeners.OnErrorListener l) {
		mListeners.addOnRetryErrorListener(l);
	}

	/**
	 * 移除重试错误回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void removeOnRetryErrorListener(
			VDVideoViewListeners.OnErrorListener l) {
		mListeners.removeOnRetryErrorListener(l);
	}

	/**
	 * 添加信息回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void addOnInfoListener(VDVideoViewListeners.OnInfoListener l) {
		mListeners.addOnInfoListener(l);
	}

	/**
	 * 移除信息回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void removeOnInfoListener(VDVideoViewListeners.OnInfoListener l) {
		mListeners.removeOnInfoListener(l);
	}

	/**
	 * 添加界面刷新回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void addOnVideoUIRefreshListener(
			VDVideoViewListeners.OnVideoUIRefreshListener l) {
		mListeners.addOnVideoUIRefreshListener(l);
	}

	/**
	 * 移除界面刷新回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void removeOnVideoUIRefreshListener(
			VDVideoViewListeners.OnVideoUIRefreshListener l) {
		mListeners.removeOnVideoUIRefreshListener(l);
	}

	/**
	 * 添加视频准备就绪回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void addOnPreparedListener(VDVideoViewListeners.OnPreparedListener l) {
		mListeners.addOnPreparedListener(l);
	}

	/**
	 * 移除视频准备就绪回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void removeOnPreparedListener(
			VDVideoViewListeners.OnPreparedListener l) {
		mListeners.removeOnPreparedListener(l);
	}

	/**
	 * 添加调节视频进度完毕回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void addOnSeekCompleteListener(
			VDVideoViewListeners.OnSeekCompleteListener l) {
		mListeners.addOnSeekCompleteListener(l);
	}

	/**
	 * 移除添加视频进度完毕回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void removeOnSeekCompleteListener(
			VDVideoViewListeners.OnSeekCompleteListener l) {
		mListeners.removeOnSeekCompleteListener(l);
	}

	/**
	 * 添加视频大小改变回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void addOnVideoSizeChangedListener(
			VDVideoViewListeners.OnVideoSizeChangedListener l) {
		mListeners.addOnVideoSizeChangedListener(l);
	}

	/**
	 * 移除视频大小改变回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void removeOnVideoSizeChangedListener(
			VDVideoViewListeners.OnVideoSizeChangedListener l) {
		mListeners.removeOnVideoSizeChangedListener(l);
	}

	/**
	 * 添加打开视频回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void addOnVideoOpenedListener(
			VDVideoViewListeners.OnVideoOpenedListener l) {
		mListeners.addOnVideoOpenedListener(l);
	}

	/**
	 * 移除打开视频回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void removeOnVideoOpenedListener(
			VDVideoViewListeners.OnVideoOpenedListener l) {
		mListeners.removeOnVideoOpenedListener(l);
	}

	/**
	 * 添加视频进度更新回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void addOnProgressUpdateListener(
			VDVideoViewListeners.OnProgressUpdateListener l) {
		mListeners.addOnProgressUpdateListener(l);
	}

	/**
	 * 移除视频进度更新回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void removeOnProgressUpdateListener(
			VDVideoViewListeners.OnProgressUpdateListener l) {
		mListeners.removeOnProgressUpdateListener(l);
	}

	/**
	 * 添加时间信息更新回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void addOnTimedTextListener(
			VDVideoViewListeners.OnTimedTextListener l) {
		mListeners.addOnTimedTextListener(l);
	}

	/**
	 * 移除时间信息更新回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void removeOnTimedTextListener(
			VDVideoViewListeners.OnTimedTextListener l) {
		mListeners.removeOnTimedTextListener(l);
	}

	/**
	 * @param l
	 */
	public void addOnPlayVideoListener(
			VDVideoViewListeners.OnPlayVideoListener l) {
		mListeners.addOnPlayVideoListener(l);
	}

	public void removeOnPlayVideoListener(
			VDVideoViewListeners.OnPlayVideoListener l) {
		mListeners.removeOnPlayVideoListener(l);
	}

	// public void
	// addOnLiveVideoListener(VDVideoViewListeners.OnLiveVideoListener l) {
	// mListeners.addOnLiveVideoListener(l);
	// }
	//
	// public void
	// removeOnLiveVideoListener(VDVideoViewListeners.OnLiveVideoListener l) {
	// mListeners.removeOnLiveVideoListener(l);
	// }

	/**
	 * 添加全屏回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void addOnFullScreenListener(
			VDVideoViewListeners.OnFullScreenListener l) {
		mListeners.addOnFullScreenListener(l);
	}

	/**
	 * 移除全屏回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void removeOnFullScreenListener(
			VDVideoViewListeners.OnFullScreenListener l) {
		mListeners.removeOnFullScreenListener(l);
	}

	/**
	 * 添加音量回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void addOnSoundChangedListener(
			VDVideoViewListeners.OnSoundChangedListener l) {
		mListeners.addOnSoundChangedListener(l);
	}

	/**
	 * 移除音量回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void removeOnSoundChangedListener(
			VDVideoViewListeners.OnSoundChangedListener l) {
		mListeners.removeOnSoundChangedListener(l);
	}

	/**
	 * 添加触屏回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void addOnScreenTouchListener(
			VDVideoViewListeners.OnScreenTouchListener l) {
		mListeners.addOnScreenTouchListener(l);
	}

	/**
	 * 移除触屏回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void removeOnScreenTouchListener(
			VDVideoViewListeners.OnScreenTouchListener l) {
		mListeners.removeOnScreenTouchListener(l);
	}

	/**
	 * 添加清晰度选择回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void addOnResolutionListener(
			VDVideoViewListeners.OnResolutionListener l) {
		mListeners.addOnResolutionListener(l);
	}

	/**
	 * 移除清晰度选择回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void removeOnResolutionContainerListener(
			VDVideoViewListeners.OnResolutionContainerListener l) {
		mListeners.removeOnResolutionContainerListener(l);
	}

	/**
	 * 添加清晰度容器回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void addOnResolutionContainerListener(
			VDVideoViewListeners.OnResolutionContainerListener l) {
		mListeners.addOnResolutionContainerListener(l);
	}

	/**
	 * 移除清晰度容器回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void removeOnResolutionListener(
			VDVideoViewListeners.OnResolutionListener l) {
		mListeners.removeOnResolutionListener(l);
	}

	/**
	 * 添加调节亮度消息
	 * 
	 * @param l
	 *            回调接口
	 */
	public void addOnLightingChangeListener(
			VDVideoViewListeners.OnLightingChangeListener l) {
		mListeners.addOnLightingChangeListener(l);
	}

	/**
	 * 移除调节亮度消息
	 * 
	 * @param l
	 *            回调接口
	 */
	public void removeOnLightingChangeListener(
			VDVideoViewListeners.OnLightingChangeListener l) {
		mListeners.removeOnLightingChangeListener(l);
	}

	/**
	 * 添加tip通知
	 * 
	 * @param l
	 *            回调接口
	 */
	public void addOnTipListener(OnTipListener l) {
		mListeners.addOnTipListener(l);
	}

	/**
	 * 移除tip通知
	 * 
	 * @param l
	 *            回调接口
	 */
	public void removeOnTipListener(OnTipListener l) {
		mListeners.removeOnTipListener(l);
	}

	/**
	 * 添加亮度调节回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void addOnLightingVisibleListener(OnLightingVisibleListener l) {
		mListeners.addOnLightingVisibleListener(l);
	}

	/**
	 * 移除亮度添加回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void removeOnLightingVisibleListener(OnLightingVisibleListener l) {
		mListeners.removeOnLightingVisibleListener(l);
	}

	/**
	 * 添加进度条控件显示回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void addOnProgressViewVisibleListener(OnProgressViewVisibleListener l) {
		mListeners.addOnProgressViewVisibleListener(l);
	}

	/**
	 * 移除进度条控件显示回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void removeOnProgressViewVisibleListener(
			OnProgressViewVisibleListener l) {
		mListeners.removeOnProgressViewVisibleListener(l);
	}

	/**
	 * 添加声音控件显示回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void addOnSoundVisibleListener(OnSoundVisibleListener l) {
		mListeners.addOnSoundVisibleListener(l);
	}

	/**
	 * 移除声音控件显示回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void removeOnSoundVisibleListener(OnSoundVisibleListener l) {
		mListeners.removeOnSoundVisibleListener(l);
	}

	/**
	 * 添加清晰度控件回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void addOnVMSResolutionListener(OnVMSResolutionListener l) {
		mListeners.addOnVMSResolutionListener(l);
	}

	/**
	 * 移除清晰度控件回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void removeOnVMSResolutionListener(OnVMSResolutionListener l) {
		mListeners.removeOnVMSResolutionListener(l);
	}

	/**
	 * 添加加载中回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void addOnLoadingListener(OnLoadingListener l) {
		mListeners.addOnLoadingListener(l);
	}

	/**
	 * 移除加载中回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void removeOnLoadingListener(OnLoadingListener l) {
		mListeners.removeOnLoadingListener(l);
	}

	/**
	 * 添加视频引导页回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void addOnVideoGuideTipsListener(OnVideoGuideTipsListener l) {
		mListeners.addOnVideoGuideTipsListener(l);
	}

	/**
	 * 移除视频引导页回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void removeOnVideoGuideTipsListener(OnVideoGuideTipsListener l) {
		mListeners.removeOnVideoGuideTipsListener(l);
	}

	/**
	 * 添加插入视频广告回调
	 * 
	 * @param l
	 */
	public void addOnVideoInsertADListener(OnVideoInsertADListener l) {
		mListeners.addOnVideoInsertADListener(l);
	}

	/**
	 * 移除插入视频广告回调
	 * 
	 * @param l
	 */
	public void removeOnVideoInsertADListener(OnVideoInsertADListener l) {
		mListeners.removeOnVideoInsertADListener(l);
	}

	/**
	 * 添加帧间视频广告回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void addOnVideoFrameADListener(OnVideoFrameADListener l) {
		mListeners.addOnVideoADListener(l);
	}

	/**
	 * 移除帧间视频广告回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void removeOnVideoFrameADListener(OnVideoFrameADListener l) {
		mListeners.removeOnVideoAdListener(l);
	}

	/**
	 * 添加视频列表回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void addOnVideoListListener(OnVideoListListener l) {
		mListeners.addOnVideoListListener(l);
	}

	/**
	 * 移除视频列表回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void removeOnVideoListListener(OnVideoListListener l) {
		mListeners.removeOnVideoListListener(l);
	}

	/**
	 * 添加视频列表可见状态改变回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void addOnVideoListVisibleChangeListener(
			OnVideoListVisibleChangeListener l) {
		mListeners.addOnVideoListVisibleChangeListener(l);
	}

	/**
	 * 移除视频列表可见状态改变回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void removeOnVideoListVisibleChangeListener(
			OnVideoListVisibleChangeListener l) {
		mListeners.removeOnVideoListVisibleChangeListener(l);
	}

	public void addOnMoreOprationVisibleChangeListener(
			OnMoreOprationVisibleChangeListener l) {
		mListeners.addOnMoreOprationVisibleChangeListener(l);
	}

	public void removeOnMoreOprationVisibleChangeListener(
			OnMoreOprationVisibleChangeListener l) {
		mListeners.removeOnMoreOprationVisibleChangeListener(l);
	}

	// public void
	// addOnResolutionVisibleChangeListener(OnResolutionVisibleChangeListener l)
	// {
	// mListeners.addOnResolutionVisibleChangeListener(l);
	// }
	//
	// public void
	// removeOnResolutionVisibleChangeListener(OnResolutionVisibleChangeListener
	// l) {
	// mListeners.removeOnResolutionVisibleChangeListener(l);
	// }

	/**
	 * 添加双击回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void addOnVideoDoubleTapListener(OnVideoDoubleTapListener l) {
		mListeners.addOnVideoDoubleTapListener(l);
	}

	/**
	 * 移除双加回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void removeOnVideoDoubleTapListener(OnVideoDoubleTapListener l) {
		mListeners.removeOnVideoDoubleTapListener(l);
	}

	/**
	 * 添加解码类型改变回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void addOnDecodingTypeListener(OnDecodingTypeListener l) {
		mListeners.addOnDecodingTypeListener(l);
	}

	/**
	 * 移除解码类型改变回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void removeOnDecodingTypeListener(OnDecodingTypeListener l) {
		mListeners.removeOnDecodingTypeListener(l);
	}

	/**
	 * 添加屏幕方向改变回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void addOnScreenOrientationChangeListener(
			OnScreenOrientationChangeListener l) {
		mListeners.addOnScreenOrientationChangeListener(l);
	}

	/**
	 * 移除屏幕方向改变回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void removeOnScreenOrientationChangeListener(
			OnScreenOrientationChangeListener l) {
		mListeners.removeOnScreenOrientationChangeListener(l);
	}

	/**
	 * 添加控制区显示状态改变回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void addOnShowHideControllerListener(OnShowHideControllerListener l) {
		mListeners.addOnShowHideControllerListener(l);
	}

	/**
	 * 移除控制区显示状态改变回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void removeOnShowHideControllerListener(
			OnShowHideControllerListener l) {
		mListeners.removeOnShowHideControllerListener(l);
	}

	/**
	 * 添加底部控制区显示状态改变回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void addOnShowHideBottomControllerListener(
			OnShowHideBottomControllerListener l) {
		mListeners.addOnShowHideBottomControllerListener(l);
	}

	/**
	 * 移除底部控制区显示状态改变回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void removeOnShowHideTopControllerListener(
			OnShowHideTopContainerListener l) {
		mListeners.removeOnShowHideTopContainerListener(l);
	}

	/**
	 * 添加底部控制区显示状态改变回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void addOnShowHideTopControllerListener(
			OnShowHideTopContainerListener l) {
		mListeners.addOnShowHideTopContainerListener(l);
	}

	/**
	 * 移除底部控制区显示状态改变回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void removeOnShowHideBottomControllerListener(
			OnShowHideBottomControllerListener l) {
		mListeners.removeOnShowHideBottomControllerListener(l);
	}

	/**
	 * 添加点击播放按钮回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void addOnClickPlayListener(OnClickPlayListener l) {
		mListeners.addOnClickPlayListener(l);
	}

	/**
	 * 移除点击播放按钮回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void removeOnClickPlayListener(OnClickPlayListener l) {
		mListeners.removeOnClickPlayListener(l);
	}

	/**
	 * 添加导航键改变播放进度回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void addOnKeyChangeProgressListener(OnKeyChangeProgressListener l) {
		mListeners.addOnKeyChangeProgressListener(l);
	}

	/**
	 * 移除导航键改变播放进度回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void removeOnKeyChangeProgressListener(OnKeyChangeProgressListener l) {
		mListeners.removeOnKeyChangeProgressListener(l);
	}

	/**
	 * 添加按键事件回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void addOnKeyEventListener(OnKeyEventListener l) {
		mListeners.addOnKeyEventListener(l);
	}

	/**
	 * 移除按键事件回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void removeOnKeyEventListener(OnKeyEventListener l) {
		mListeners.removeOnKeyEventListener(l);
	}

	/**
	 * 添加DLNA布局回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void addOnDLNALinearLayoutListener(OnDLNALinearLayoutListener l) {
		mListeners.addOnDLNALinearLayoutListener(l);
	}

	/**
	 * 移除DLNA布局回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void removeOnDLNALinearLayoutListener(OnDLNALinearLayoutListener l) {
		mListeners.removeOnDLNALinearLayoutListener(l);
	}

	/**
	 * 添加设置音量回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void addOnSetSoundListener(OnSetSoundListener l) {
		mListeners.addOnSetSoundListener(l);
	}

	/**
	 * 移除设置音量回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void removeOnSetSoundListener(OnSetSoundListener l) {
		mListeners.removeOnSetSoundListener(l);
	}

	/**
	 * 添加注册DLNA回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void addOnRegisterDLNAListener(OnRegisterDLNAListener l) {
		mListeners.addOnRegisterDLNAListener(l);
	}

	/**
	 * 移除注册DLNA回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void removeOnRegisterDLNAListener(OnRegisterDLNAListener l) {
		mListeners.removeOnRegisterDLNAListener(l);
	}

	/**
	 * 添加切换屏幕方向回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void addOnScreenOrientationSwitchListener(
			OnScreenOrientationSwitchListener l) {
		mListeners.addOnScreenOrientationSwitchListener(l);
	}

	/**
	 * 移除切换屏幕方向回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void removeOnScreenOrientationSwitchListener(
			OnScreenOrientationSwitchListener l) {
		mListeners.removeOnScreenOrientationSwitchListener(l);
	}

	/**
	 * 添加点击重试回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void addOnClickRetryListener(OnClickRetryListener l) {
		mListeners.addOnClickRetryListener(l);
	}

	/**
	 * 移除贴片广告的容器显示隐藏类
	 * 
	 * @param l
	 *            回调接口
	 */
	public void removeOnShowHideADContainerListener(
			OnShowHideADContainerListener l) {
		mListeners.removeOnShowHideADContainerListener(l);
	}

	/**
	 * 添加贴片广告的容器显示隐藏类
	 * 
	 * @param l
	 *            回调接口
	 */
	public void addOnShowHideADContainerListener(OnShowHideADContainerListener l) {
		mListeners.addOnShowHideADContainerListener(l);
	}

	/**
	 * 移除点击重试回调
	 * 
	 * @param l
	 *            回调接口
	 */
	public void removeOnClickRetryListener(OnClickRetryListener l) {
		mListeners.removeOnClickRetryListener(l);
	}

	public void addOnResolutionListButtonListener(
			OnResolutionListButtonListener l) {
		mListeners.addOnResolutionListButtonListener(l);
	}

	public void removeOnResolutionListButtonListener(
			OnResolutionListButtonListener l) {
		mListeners.removeOnResolutionListButtonListener(l);
	}

	// --------回调方法区end-----------//

	/**
	 * 发送页面刷新通知，尤其用于广告层里面
	 */
	public void notifyVideoUIRefresh() {
		mListeners.notifyVideoUIRefreshListener();
	}

	/**
	 * 点击重试通知
	 */
	public void notifyClickRetry() {
		mListeners.notifyClickRetry();
	}

	/**
	 * 屏幕方向改变通知
	 * 
	 * @param fullScreen
	 *            true 全屏；false 非全屏
	 */
	public void notifyScreenOrientationSwitch(boolean fullScreen) {
		mListeners.notifyScreenOrientationSwitch(fullScreen);
	}

	/**
	 * DLNA视图可见通知
	 * 
	 * @param visiable
	 *            true 可见；false 不可见
	 */
	public void notifySetDLNALayoutVisible(boolean visiable) {
		mListeners.notifySetDLNALayoutVisible(visiable);
	}

	/**
	 * DLNA注册通知
	 */
	public void notifyRegisterDLNAListener() {
		mListeners.notifyRegisterDLNAListener();
	}

	/**
	 * 设置音量通知
	 * 
	 * @param currVolume
	 *            当前音量
	 */
	public void notifySetCurVolume(int currVolume) {
		mListeners.notifySetCurVolume(currVolume);
	}

	/**
	 * 设置最大音量通知
	 * 
	 * @param maxVolume
	 *            最大音量
	 */
	public void notifySetMaxVolume(int maxVolume) {
		mListeners.notifySetMaxVolume(maxVolume);
	}

	/**
	 * 通知前贴片广告的开始与结束
	 * 
	 * @param isBegin
	 */
	public void notifyInsertAD(boolean isBegin) {
		if (isBegin) {
			mListeners.notifyVideoInsertADBegin();
		} else {
			mListeners.notifyVideoInsertADEnd();
		}
	}

	/**
	 * 前贴片跳秒
	 */
	public void notifyInsertADTicker() {
		mListeners.notifyVideoInsertADTicker();
	}

	/**
	 * 浮框通知
	 * 
	 * @param tip
	 */
	public void notifyTip(String tip) {
		mListeners.notifyTip(tip);
	}

	/**
	 * 隐藏浮框通知
	 */
	public void notifyHideTip() {
		mListeners.notifyHideTip();
	}

	@Override
	public void onVideoOpened(MediaPlayer mp) {
		mListeners.notifyVideoOpened();
	}

	@Override
	public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
		mListeners.notifyVideoSizeChanged(width, height);
	}

	@Override
	public void onTimedText(MediaPlayer mp, TimedText text) {
		mListeners.notifyTimedText(text);
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		mVDPlayerInfo.mPlayStatus = VDPlayerInfo.PLAYER_PREPARED;
		if (mVideoView == null) {
			return;
		}
		mVDPlayerInfo.mDuration = mVideoView.getDuration();
		VDVideoViewController controller = VDVideoViewController
				.getInstance(this.mContext);
		if (controller != null)
			controller.notifyShowControllerBar(true);
		if (controller != null)
			controller.notifyOnShowHideADContainer(true);

		startUpdateMessage();

		VDVideoInfo info = getCurrentVideo();

		if (info != null) {
			info.mVideoDuration = mp.getDuration();
			// 补全当前广告部分的时长，只要秒就可以了
			if (info.mIsInsertAD) {
				// 广告的 buffer size 应当很小
				// mp.setBufferSize(1000); //有影响反而卡顿了
				mADIsFromStart = true;
				int duration = (int) Math
						.floor((double) mp.getDuration() / (1000));
				if (!mVDVideoListInfo.mIsSetInsertADTime) {
					// 只有单广告流的时候，才重置当前时间，如果是多流情况，需要手动设置
					mVDVideoListInfo.mInsertADSecNum = duration;
					mADTickerSecNum = mVDVideoListInfo.mInsertADSecNum;
				}
				getCurrentVideo().mInsertADSecNum = duration;
				notifyInsertAD(true);
			} else {
				// 正片部分，将音量调回正常来
				boolean isCanNormalSound = false;
				int key = mVDVideoListInfo.getVideoInfoKey(info) - 1;
				try {
					if (key >= 0
							&& mVDVideoListInfo.getVideoInfo(key).mIsInsertAD) {
						isCanNormalSound = true;
					}
				} catch (Exception ex) {
					VDLog.e(TAG, ex.getMessage());
				}
				if (VDPlayerSoundManager.isMuted(mContext) && isCanNormalSound) {
					VDPlayerSoundManager.setMute(mContext, false, false);
				}
			}
			// 帧间广告部分
			if (isCanShowFrameAD()) {
				mExtListeners.notifyFrameADListenerPrepared();
			}
		}
		mListeners.notifyPrepared();
		mListeners.notifyVideoPrepared(true);
		mExtListeners.notifyPreparedListener();
		mVideoView.start();
		updatePlayState();
		mIsPlayed = false;

		if (info != null && info.mIsLive
				&& VDVideoInfo.SOURCE_TYPE_FAKE_LIVE.equals(info.mSourceType)) {
			// m3u8伪直播充当真直播时，变换清晰度时，需跳转到服务器端的当前直播位置。
			long seekTo = info.getSeekTo();
			if (seekTo > mp.getDuration()) {
				seekTo = 0L;
			}
			seekTo(seekTo);
		} else if (info != null && info.mNeedSeekTo) {
			info.mNeedSeekTo = false;
			seekTo(info.mVideoPosition);
		}
	}

	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		mListeners.notifyInfo(what, extra);
		if (getCurrentVideo() != null) {
			mExtListeners.notifyInfoListener(getCurrentVideo(), what);
		}

		VDLog.i("demo", "onInfo --> what :" + what + " , extra : " + extra);

		if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) { // MediaPlayer暂停播放等待缓冲更多数据。
			// NOTE: video playing quickly bug if pause added here ,by lyh

			notifyShowLoading();
			VDLog.i("demo", "onInfo --> MEDIA_INFO_BUFFERING_START");
			mIsPlayed = true;
			mTimeOutHandler.removeMessages(NET_TIME_OUT);
			if (VDApplication.getInstance().isNetworkConnected()) {// 有网
				mTimeOutHandler.sendEmptyMessageDelayed(NET_TIME_OUT,
						VIDEO_TIME_OUT);
			} else {// 无网
			}
		} else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) { // MediaPlayer在缓冲完后继续播放。
			VDLog.i("demo", "onInfo --> MEDIA_INFO_BUFFERING_END");
			mVDPlayerInfo.mPlayStatus = VDPlayerInfo.PLAYER_STARTED;
			mTimeOutHandler.removeMessages(NET_TIME_OUT);
			notifyHideLoading();
			mListeners.notifyHideLoading();
			mListeners.notifyHideTip();
			// 将前贴片广告的启动点放置到这儿，首帧部分，以规避踹你的计时问题
			if (getCurrentVideo().mIsInsertAD && mADIsFromStart) {
				mInsertADHandler.postDelayed(mInsertADRunnable, 1000);
				mADIsFromStart = false;
			}

		} else if (what == MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING) { // 视频过于复杂，无法解码：不能快速解码帧。此时可能只能正常播放音频。
		} else if (what == MediaPlayer.MEDIA_ERROR_UNKNOWN) { // 播放错误，未知错误。
		}

		return false;
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		VDLog.e(TAG, "onError:errorCode1:" + what + ",errorCodeExtra:" + extra);
		if (getCurrentVideo() != null) {
			if (getCurrentVideo().mIsInsertAD) {
				mInsertADHandler.removeCallbacks(mInsertADRunnable);
			}
		}
		mVDPlayerInfo.mPlayStatus = VDPlayerInfo.PLAYER_ERROR;
		if (!mIsPlayed) {
			mRetryTimes++;
			if (mRetryTimes > VDSDKConfig.getInstance().getRetryTime()) {// 重试两次
				mRetryTimes = 0;
				if (getCurrentVideo() != null) {
					// TODO 这个有点问题，只能记录最后一次的
					if (mVDVideoListInfo.isInsertADEnd()) {
						mExtListeners
								.notifyInsertADEnd(VDPlayerErrorInfo.MEDIA_INSERTAD_ERROR_UNKNOWN);
					}
					if (getCurrentVideo().mIsInsertAD) {
						playNext();
					} else {
						mListeners.notifyRetryError(what, extra);
					}
					mExtListeners.ontifyErrorListener(getCurrentVideo(), what,
							extra);
				}
			} else {
				mMessageHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						playVideoOnInfoKey(mVDVideoListInfo.mIndex);
						VDLog.e(TAG, "avformat_open_input  视频错误2秒重试");
					}
				}, 2000);
			}
		} else {
			mListeners.notifyError(what, extra);
			if (getCurrentVideo() != null) {
				mExtListeners.ontifyErrorListener(getCurrentVideo(), what,
						extra);
			}
			mRetryTimes = 0;
		}

		return false;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		mVDPlayerInfo.mPlayStatus = VDPlayerInfo.PLAYER_FINISHED;
		if (mVDVideoListInfo != null && mVDVideoListInfo.isInsertADEnd()) {
			mExtListeners
					.notifyInsertADEnd(VDPlayerErrorInfo.MEDIA_INSERTAD_SUCCESS);
		}
		VDVideoViewController controller = VDVideoViewController
				.getInstance(this.mContext);
		VDPlayerInfo playerInfo = null;
		if (controller != null)
			playerInfo = controller.getPlayerInfo();
		if (playerInfo != null) {
			playerInfo.mCurrent = playerInfo.mDuration;
		}
		if (!getCurrentVideo().mIsInsertAD) {
			mListeners.notifyCompletion();
			mExtListeners.notifyCompletionListener(getCurrentVideo(), 0);
		} else {
			notifyInsertAD(false);
			if (mVDVideoListInfo.getADNumOfRemain() > 0) {
				// 当前流中还有广告，那么暂停一下，继续开始
				mInsertADHandler.removeCallbacks(mInsertADRunnable);
			}
		}
		VDVideoInfo info = getCurrentVideo();
		if (info != null) {
			info.mVideoPosition = 0;
		}
		// mVDPlayerInfo.mDuration = 0;
		long position = mVideoView.getCurrentPosition();
		long duration = mVideoView.getDuration();
		mListeners.notifyProgressUpdate(position, duration);
		stopUpdateMessage();
		updatePlayState();
		if (getCurrentVideo().mIsInsertAD) {
			playNext();
		}
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		VDLog.d("onBufferingUpdate", "percent:" + percent);
		mListeners.notifyBufferingUpdate(percent);
		if (mLastPercent != percent) {
			mLastPercentTime = System.currentTimeMillis();
			mLastPercent = percent;
			mTimeOutHandler.removeMessages(CHECK_LIVE_TIME_OUT);
		} else {
			try {
				if (getCurrentVideo().mIsLive && mVideoView.isBuffering()) {
					mTimeOutHandler.sendEmptyMessageDelayed(
							CHECK_LIVE_TIME_OUT, CHECK_LIVE_TIME_GAP);
				}
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * m3u8解析回调类
	 * 
	 * @author sina
	 */
	class MyM3u8ParserListener implements M3u8ParserListener {

		private VDVideoInfo mVideoInfo;

		public MyM3u8ParserListener(VDVideoInfo _vi) {
			mVideoInfo = _vi;
		}

		@Override
		public void onParcelResult(String url, VDResolutionData resolutionData) {
			if (resolutionData != null) {
				VDResolutionManager resolutionManager = VDResolutionManager
						.getInstance(mContext);
				if (resolutionManager != null) {
					notifyResolutionParsed(resolutionData);
					resolutionManager.setResolutionData(resolutionData);
					String tag = resolutionManager.getCurrResolutionTag();
					setVideoPath(mVideoInfo.getVideoUrl(tag));
					if (mListeners != null) {
						mListeners.notifyResolutionParsed(resolutionData);
					}
				} else {
					setVideoPath(mVideoInfo
							.getVideoUrl(VDResolutionData.TYPE_DEFINITION_SD));
				}
			} else {
				if (mVideoView != null) {
					setVideoPath(mVideoInfo.mPlayUrl);
				}
			}
			if (mVideoView != null) {
				setVideoViewVisible(View.VISIBLE);
			}
			notifyShowLoading();
		}

		@Override
		public void onError(int error_msg) {
			mListeners.notifyRetryError(0, 0);
			if (getCurrentVideo() != null) {
				mExtListeners.ontifyErrorListener(getCurrentVideo(),
						VDPlayerErrorInfo.MEDIA_ERROR_WHAT_M3U8_PARSER,
						error_msg);
			}
		}

		@Override
		public void updateVideoPlayUrl(String playUrl) {
		}

		@Override
		public void updateVideoID(String videoId) {
			// @sunxiao1
			// 看了一下M3u8ParserListener的实现，发现，updateVideoID在构造后直接丢回来，那么这个操作有毛用？
			// VDSDKLogData.getInstance().mLogVideoInfo.mVideoId = videoId;
		}

	}

	/**
	 * 设置视频控件是否可见
	 * 
	 * @param isVisible
	 *            eg:VISIBLE, INVISIBLE, or GONE.
	 */
	private void setVideoViewVisible(int isVisible) {
		if (mVideoView != null) {
			if (mVideoView instanceof VideoViewHard) {
				((VideoViewHard) mVideoView).setVisibility(isVisible);
			} else {
				((VideoView) mVideoView).setVisibility(isVisible);
			}
		}
	}

	private static long VIDEO_TIME_OUT = 30 * 1000;
	private static final long VIDEO_NORMAL_TIME_OUT = 30 * 1000;
	private static final long VIDEO_LIVE_TIME_OUT = 40 * 1000;
	private static final long CHECK_LIVE_TIME_GAP = 30 * 1000;
	private static final int NET_TIME_OUT = 1;
	private static final int CHECK_LIVE_TIME_OUT = 3;
	private int mBufferPercent;
	private int mLastPercent;
	private long mLastPercentTime;

	/**
	 * 处理时间设置的Handler
	 */
	private static class TimeOutHandler extends Handler {

		WeakReference<VDVideoViewController> controller = null;

		public TimeOutHandler(VDVideoViewController instance) {
			super(Looper.getMainLooper());
			controller = new WeakReference<VDVideoViewController>(instance);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (controller == null) {
				return;
			}
			switch (msg.what) {
			case NET_TIME_OUT:
				int curPercent = controller.get().mVideoView
						.getBufferPercentage();
				long now = System.currentTimeMillis();
				if (Math.abs(curPercent - controller.get().mBufferPercent) < 1
						|| (Math.abs(curPercent - controller.get().mLastPercent) < 1 && (now
								- controller.get().mLastPercentTime >= VIDEO_TIME_OUT))) {
					controller.get().mListeners
							.notifyTip(R.string.net_exp_check_and_retry);

				} else if (curPercent < 100 /* && !mVideoView.isPlaying() */
						&& controller.get().mVideoView.isBuffering()) {
					if (controller.get().getCurrentVideo().mIsLive) {
						controller.get().mListeners
								.notifyTip(R.string.net_no_good_retry);
						VDResolutionManager resolutionManager = VDResolutionManager
								.getInstance(controller.get().mContext);
						if (resolutionManager != null) {
							controller
									.get()
									.setVideoPath(
											controller
													.get()
													.getCurrentVideo()
													.getVideoUrl(
															resolutionManager
																	.getCurrResolutionTag()));
						}
					} else {
						controller.get().mListeners
								.notifyTip(R.string.your_net_is_no_good);
						sendEmptyMessageDelayed(NET_TIME_OUT, VIDEO_TIME_OUT);
					}
					controller.get().mBufferPercent = controller.get().mVideoView
							.getBufferPercentage();
				}
				break;
			case CHECK_LIVE_TIME_OUT:
				controller.get().mListeners
						.notifyTip(R.string.net_exp_check_and_retry);
				break;
			default:
				break;
			}
		}
	}

	private TimeOutHandler mTimeOutHandler = new TimeOutHandler(this);

	/**
	 * 隐藏状态栏
	 * 
	 * @param flag
	 *            true ：隐藏 ；false： 不隐藏
	 */
	public void hideStatusBar(boolean flag) {
		if (mContext == null) {
			return;
		}
		if (VDUtility.isMeizu() || VDUtility.isSamsungNoteII()) {
			return;
		}
		Window window = ((Activity) mContext).getWindow();
		android.view.WindowManager.LayoutParams layoutparams = window
				.getAttributes();
		if (flag)
			layoutparams.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN
					| layoutparams.flags;
		else
			layoutparams.flags = ~WindowManager.LayoutParams.FLAG_FULLSCREEN
					& layoutparams.flags;
		window.setAttributes(layoutparams);
	}

	@Override
	public void onSeekComplete(MediaPlayer mp) {
		startUpdateMessage();
	}

	public void onResume() {
		resume();
	}

	public void onPause() {
		pause();
		notifyHideControllerBar(0);
	}

	public void onStart() {
		if (getCurrentVideo() != null) {
			if (getCurrentVideo().mIsInsertAD) {
				start();
			}
		}
		VDVideoFullModeController.getInstance().enableSensor(true);
	}

	public void onStartWithVideoResume() {
		start();
		VDVideoFullModeController.getInstance().enableSensor(true);
	}

	public void onStop() {
		VDVideoFullModeController.getInstance().enableSensor(false);
	}

	public static class NetChangeListener
			implements
			com.sina.sinavideo.sdk.utils.VDNetworkBroadcastReceiver.NetworkNotifyListener {

		private Context mContext = null;

		public NetChangeListener(Context context) {
			mContext = context;
		}

		@Override
		public void wifiConnected() {
			VDVideoViewController controller = VDVideoViewController
					.getInstance(mContext);
			if (controller == null) {
				return;
			}
		}

		@Override
		public void mobileConnected() {
			VDVideoViewController controller = VDVideoViewController
					.getInstance(mContext);
			if (controller == null) {
				return;
			}
		}

		@Override
		public void nothingConnected() {
			VDVideoViewController controller = VDVideoViewController
					.getInstance(mContext);
			if (controller == null) {
				return;
			}
			File file = Environment.getExternalStorageDirectory();
			VDVideoInfo mVideoInfo = controller.getCurrentVideo();
			if (mVideoInfo != null && mVideoInfo.mPlayUrl != null
					&& VDUtility.isSdcardReady() && file != null
					&& mVideoInfo.mPlayUrl.startsWith(file.getAbsolutePath())) {
				return;
			}
			controller.notifyTip("网络异常，请您检查网络后重试");
		}

	}
}
