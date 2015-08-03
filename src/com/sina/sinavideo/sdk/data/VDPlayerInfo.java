package com.sina.sinavideo.sdk.data;

/**
 * 播放器状态信息类
 * 
 * @author seven
 */
public class VDPlayerInfo {

	// 播放器状态列表
	public static final int PLAYER_UNKNOWN = 0;
	// 状态循环，从preparing->prepared->starting->started->finishing->finished
	// 事件状态,resume/pause/stoped/error
	public static final int PLAYER_PREPARING = 1;
	public static final int PLAYER_PREPARED = 2;
	public static final int PLAYER_STARTING = 3;
	public static final int PLAYER_STARTED = 4;
	public static final int PLAYER_STOPED = 5;
	public static final int PLAYER_RESUME = 6;
	public static final int PLAYER_PAUSE = 7;
	public static final int PLAYER_FINISHED = 8;
	public static final int PLAYER_FINISHING = 9;
	public static final int PLAYER_ERROR = 10;

	public boolean isCanScroll() {
		return (mPlayStatus == PLAYER_STARTED || mPlayStatus == PLAYER_PAUSE);
	}

	/**
	 * 视频播放状态 0 : playing, 1 : pause
	 */
	public int mPlayStatus = PLAYER_UNKNOWN;

	public boolean isPlaying() {
		return (mPlayStatus >= PLAYER_PREPARING && mPlayStatus <= PLAYER_STARTED);
	}

	public boolean mIsPlaying = false;

	/**
	 * 当前播放第几个
	 */
	// public int mIndex = 0;

	/**
	 * 点播正在解析(获取通过重定向后的)真正url的索引
	 */
	public int mParseIndex = 0;

	public void init() {
		// mIndex = 0;
		mParseIndex = 0;
	}

	/**
	 * 时长
	 */
	public long mDuration = 0L;
	/**
	 * 当前时刻
	 */
	public long mCurrent = 0L;
	/**
	 * 当前播放器的清晰度
	 */
	public String mCurResolution = VDResolutionData.TYPE_DEFINITION_SD;
	public float mCurLighting = 0.f;
	public int mCurVolume = 0;

}
