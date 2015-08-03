package com.sina.sinavideo.sdk.data;

import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;

import com.sina.sinavideo.sdk.utils.VDApplication;
import com.sina.sinavideo.sdk.utils.VDResolutionManager;
import java.util.HashMap;
import java.util.Map;

/**
 * 视频状态信息类
 * 
 * @author sunxiao1@staff.sina.com.cn
 */
public class VDVideoInfo {

	public static final String SOURCE_TYPE_FAKE_LIVE = "3";

	// only url
	public static final int VIDEO_TYPE_URL = 0;
	// sina:vid/vms
	public static final int VIDEO_TYPE_VID = 2;
	// offline
	public static final int VIDEO_TYPE_UNLINE = 1;
	// vid+adv system
	public static final int VIDEO_TYPE_VID_ADV = 3;
	// 纯粹的adv方式，用来解析非sina数据
	public static final int VIDEO_TYPE_ADV = 4;

	/**
	 * 类型选择<br>
	 * 1、直接给出URL进行播放:VIDEO_TYPE_URL<br>
	 * 2、使用VID转换后进行播放:VIDEO_TYPE_VID<br>
	 * 3、使用离线缓冲里面的视频进行播放:VIDEO_TYPE_UNLINE<br>
	 * 4、使用VID+SDK调用广告系统:VIDEO_TYPE_VID_ADV
	 */
	public int mVideoInfoType = VIDEO_TYPE_URL;

	// --------状态相关----------

	// --------视频相关----------

	/**
	 * 构造函数
	 */
	public VDVideoInfo() {
	}

	/**
	 * 构造函数
	 * 
	 * @param url
	 *            视频地址
	 */
	public VDVideoInfo(String url) {
		mPlayUrl = url;
	}

	/**
	 * 视频id
	 */
	public String mVideoId;
	/**
	 * 名称
	 */
	public String mTitle;

	/**
	 * 描述
	 */
	public String mDescription;

	/**
	 * 视频播放地址
	 */
	public String mPlayUrl;

	/**
	 * 播放器有播放本地离线缓存的需求，在这种情况下，用这个变量保存实际的网络地址，否则DLNA是拿不到视频源的。<br>
	 * 视频网络地址,DLNA用<br>
	 * NOTE:重构点，后期考虑使用单独的path来保存离线地址，而PlayerUrl一直都存在，一致是原始的URL地址
	 */
	private String mNetUrl;

	/**
	 * 视频信息Url
	 */
	public String mVideoInfoUrl;

	/**
	 * 图标地址
	 */
	public String mThumbnailUrl;

	/**
	 * 视频播放总时长
	 */
	public long mVideoDuration;

	/**
	 * 当前播放位置
	 */
	public long mVideoPosition;

	/**
	 * 是否被收藏
	 */
	public boolean mIsFavorited = false;

	/**
	 * 是否已下载
	 */
	public boolean mIsDownloaded = false;

	public boolean mIsParsed = false;

	/**
	 * 是否直播
	 */
	public boolean mIsLive = false;

	/**
	 * 是否广告？？？
	 */
	public boolean mIsInsertAD = false;

	/**
	 * 前贴片广告的秒数
	 */
	public int mInsertADSecNum = 0;

	/**
	 * 资源类型
	 */
	public String mSourceType = "";

	/**
     * 
     */
	private long mServerTime;

	/**
     * 
     */
	private long mStartTime;

	/**
     * 
     */
	private long mLocalTime;

	/**
	 * 解析后的直播地址
	 */
	public Map<String, Object> mAdvReqData = new HashMap<String, Object>();

	/**
	 * 伪直播时变换清晰度需要seekto到上一次播放的位置 是否需要 seekto
	 */
	public boolean mNeedSeekTo = false;

	/**
	 * 当前视频支持的清晰度信息，key是清晰度索引，value是对应的播放地址
	 */
	// public HashMap<String, String> mVMSDefinitionInfos;
	// public VDResolutionData mResolutionData = null;

	/**
	 * 当前视频对应的清晰度索引值
	 */
	public String mCurVMSDefinitionKey;

	/**
	 * 重定向后的播放地址
	 */
	public String mRedirectUrl;

	/**
	 * 获取视频支持的清晰度信息
	 * 
	 * @return
	 */
	public HashMap<String, String> getVMSDefinitionInfo() {
		Context context = VDApplication.getInstance().getContext();
		if (context != null) {
			return VDResolutionManager.getInstance(context).getResolutionData()
					.getResolution();
		}
		return null;
	}

	/**
	 * 设置视频支持的清晰度信息[不再支持]
	 * 
	 * @param definitionInfos
	 */
	// public void setVMSDefinitionInfo(HashMap<String, String> definitionInfos)
	// {
	// this.mResolutionData.setResolutionWithMap(definitionInfos);
	// }

	// public void setVMSDefinitionInfo(VDResolutionData resolutionData) {
	// mResolutionData = resolutionData;
	// }

	/**
	 * @param serverTime
	 * @param startTime
	 */
	public void setLiveTime(long serverTime, long startTime) {
		mServerTime = serverTime;
		mStartTime = startTime;
		mLocalTime = SystemClock.elapsedRealtime();
	}

	/**
	 * @param st
	 */
	public void setSourceType(String st) {
		mSourceType = st;
	}

	/**
	 * @return
	 */
	public long getSeekTo() {
		long delta = SystemClock.elapsedRealtime() - mLocalTime;
		return mServerTime + delta - mStartTime;
	}

	/**
	 * @return
	 */
	public String getVideoId() {
		if ("0".equals(mVideoId) || TextUtils.isEmpty(mVideoId))
			return mPlayUrl;
		return mVideoId;
	}

	public String getVideoUrl(String tag) {
		if (tag == null) {
			return mPlayUrl;
		}
		Context context = VDApplication.getInstance().getContext();
		if (context != null) {
			VDResolutionManager resolutionManager = VDResolutionManager
					.getInstance(context);
			if (resolutionManager != null) {
				if (resolutionManager.getResolutionData().isContainTag(tag)) {
					return resolutionManager.getResolutionData()
							.getResolutionWithTag(tag).getUrl();
				}
			}
		}
		return mPlayUrl;
	}

	// public String getVideoUrl(int type) {
	// if (!mIsLive && !isM3u8()) {
	// return mPlayUrl;
	// }
	// if (mUrlList != null) {
	// for (int i = 0; i < mUrlList.size(); i++) {
	// M3u8Resolution u = mUrlList.get(i);
	// if (u != null && u.mResolutionType == type) {
	// return u.mM3u8Url;
	// }
	// }
	// }
	// return mPlayUrl;
	// }

	/**
	 * 获取VMS系统提供的视频播放地址
	 * 
	 * @param type
	 *            清晰度类型
	 * @return
	 */
	public String getVMSVideoUrl(String type) {
		String playUrl = null;
		Context context = VDApplication.getInstance().getContext();
		if (context != null) {
			VDResolutionManager resolutionManager = VDResolutionManager
					.getInstance(context);
			if (resolutionManager != null) {
				String tag = resolutionManager.getCurrResolutionTag();
				playUrl = resolutionManager.getResolutionData()
						.getResolutionWithTag(tag).getUrl();
			}
		}
		if (playUrl == null) {
			playUrl = mPlayUrl;
		}
		return playUrl;
	}

	// /**
	// * 获取VMS系统提供的视频默认播放地址
	// *
	// * @return
	// */
	// public String getVMSDefaultVideoUrl() {
	// return
	// mResolutionData.getResolutionWithTag(VDResolutionData.TYPE_DEFINITION_SD).getUrl();
	// }

	public String getNetUrl() {
		return mNetUrl;
	}

	public void setNetUrl(String netUrl) {
		this.mNetUrl = netUrl;
	}

	/**
	 * 获取当前视频清晰度关键值 [sd(标清), hd(⾼高清), fhd(超清)]
	 * 
	 * @deprecated 不再保留清晰度字段
	 * @return
	 */
	public String getCurVMSDefinitionKey() {
		Context context = VDApplication.getInstance().getContext();
		return VDResolutionManager.getInstance(context).getCurrResolutionTag();
	}

	// private String getResolutionNear(String tag) {
	// if (mResolutionData != null) {
	// int currCount = VDResolutionData.getDefDescIndexWithTag(tag);
	// if (currCount > 0) {
	// List<String> tagList = VDResolutionData.getDefDescTagList();
	// for (int i = tagList.size() - 1; i > 0; i--) {
	// String currTag = tagList.get(i);
	// VDResolution u = mResolutionData.getResolutionWithTag(currTag);
	// if (u != null && i <= currCount) {
	// return u.getTag();
	// }
	// }
	// }
	// }
	// return VDResolutionData.TYPE_DEFINITION_SD;
	// }

	/**
	 * 必须使用此函数来获取当前的清晰度
	 * 
	 * @return
	 */
	public String getResolution() {
		Context context = VDApplication.getInstance().getContext();
		return VDResolutionManager.getInstance(context).getCurrResolutionTag();
		// String resolutionTag = VDResolutionData.TYPE_DEFINITION_SD;
		// Context context = VDApplication.getInstance().getContext();
		// String savedResolutionTag = null;
		// if (context != null) {
		// savedResolutionTag =
		// VDSharedPreferencesUtil.getCurResolution(context);
		// }
		// if (savedResolutionTag != null) {
		// resolutionTag = getResolutionNear(savedResolutionTag);
		// }
		// return resolutionTag;
	}

	/**
	 * 设置当前视频清晰度关键值 [sd(标清), hd(⾼高清), fhd(超清)]
	 * 
	 * @deprecated 不再保留清晰度字段
	 * @param mCurVMSDefinitionKey
	 */
	public void setCurVMSDefinitionKey(String mCurVMSDefinitionKey) {
		this.mCurVMSDefinitionKey = mCurVMSDefinitionKey;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "vid:" + mVideoId + ",title:" + mTitle + ",url:" + mPlayUrl
				+ ",mVideoDuration：" + mVideoDuration + ",mDescription:"
				+ mDescription;
	}

	/**
	 * 体育APP要求点播能同时播放MP4和带有ENDLIST的m3u8的URL片源，而且要求显示m3u8清晰度. 判断播放URL是不是以m3u8结尾
	 * */
	public boolean isM3u8() {
		if (mRedirectUrl != null) {
			return mRedirectUrl.endsWith(".m3u8");
		} else if (mPlayUrl != null) {
			return mPlayUrl.endsWith(".m3u8");
		} else {
			return false;
		}
	}
}
