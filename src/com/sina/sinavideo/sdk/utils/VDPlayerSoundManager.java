package com.sina.sinavideo.sdk.utils;

import com.sina.sinavideo.sdk.VDVideoViewController;
import com.sina.sinavideo.sdk.dlna.DLNAController;

import android.app.Service;
import android.content.Context;
import android.media.AudioManager;

/**
 * 音量控制类，简单点处理，直接用静态方法来代替了。可能单例会更好一点
 * 
 * @author alexsun
 * 
 */
public class VDPlayerSoundManager {

	private static int mCurrSoundNum = -1;

	private static AudioManager getAudioManager(Context context) {
		if (context == null) {
			return null;
		}
		return (AudioManager) context.getSystemService(Service.AUDIO_SERVICE);
	}

	/**
	 * 清理相关静态变量等的资源
	 */
	public static void clear() {
		mCurrSoundNum = -1;
	}

	/**
	 * 调节音量
	 * 
	 * @param currVolume
	 *            当前音量值
	 * @param notify
	 *            是否有通知
	 */
	public static void dragSoundSeekTo(Context context, int currVolume,
			boolean notify) {
		if (DLNAController.mIsDLNA) {
			DLNAController.getInstance(context).mVolume = currVolume;
			DLNAController.getInstance(context).setVolume(currVolume);
		} else {
			if (getAudioManager(context) != null) {
				getAudioManager(context).setStreamVolume(
						AudioManager.STREAM_MUSIC, currVolume, 0);
			}
		}
		VDVideoViewController controller = VDVideoViewController
				.getInstance(context);
		if (null != controller) {
			controller.mVDPlayerInfo.mCurVolume = currVolume;
			if (notify) {
				controller.notifySoundChanged(currVolume);
			}
		}
	}

	/**
	 * 得到当前最大音量
	 * 
	 * @return
	 */
	public static int getMaxSoundVolume(Context context) {
		if (getAudioManager(context) != null) {
			return getAudioManager(context).getStreamMaxVolume(
					AudioManager.STREAM_MUSIC);
		}
		return 0;
	}

	/**
	 * 得到当前音量
	 * 
	 * @return
	 */
	public static int getCurrSoundVolume(Context context) {
		if (getAudioManager(context) != null) {
			return getAudioManager(context).getStreamVolume(
					AudioManager.STREAM_MUSIC);
		}
		return 0;
	}

	/**
	 * 获取是否静音
	 * 
	 * @return
	 */
	public static boolean isMuted(Context context) {
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Service.AUDIO_SERVICE);
		int currSoundNum = -1;
		if (audioManager != null) {
			currSoundNum = audioManager
					.getStreamVolume(AudioManager.STREAM_MUSIC);
		}
		return (currSoundNum == 0);
	}

	/**
	 * 设置静音
	 * 
	 * @param isMuted
	 */
	public static void setMute(Context context, boolean isMuted,
			boolean needNotify) {
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Service.AUDIO_SERVICE);
		if (audioManager == null) {
			return;
		}
		if (isMuted) {
			mCurrSoundNum = audioManager
					.getStreamVolume(AudioManager.STREAM_MUSIC);
			dragSoundSeekTo(context, 0, needNotify);
		} else {
			if (mCurrSoundNum != -1) {
				dragSoundSeekTo(context, mCurrSoundNum, needNotify);
			}
		}
	}
}
