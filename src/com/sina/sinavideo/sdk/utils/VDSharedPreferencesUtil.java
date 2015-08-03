package com.sina.sinavideo.sdk.utils;

import com.sina.sinavideo.sdk.data.VDResolutionData;

import android.content.Context;
import android.content.SharedPreferences;

public class VDSharedPreferencesUtil {

	public static final String PREFERENCES_PLAY_RESOLUTION = "play_resolution";
	public static final String KEY_PLAY_RESOLUTION = "key_play_resolution";
	public static final String PREFERENCES_FIRST_IN = "app_first_in";
	public static final String KEY_FIRST_FULL_SCREEN = "is_first_full_screen";
	public static final String PREFERENCES_DECODING_TYPE = "app_decoding_type";
	public static final String KEY_DECODING_TYPE = "is_decoding_type_ffmpeg";

	public static String getCurResolution(Context ctt) {
		String resolution = VDResolutionData.TYPE_DEFINITION_SD;
		if (ctt != null) {
			SharedPreferences sp = ctt.getSharedPreferences(
					PREFERENCES_PLAY_RESOLUTION, Context.MODE_PRIVATE);
			resolution = sp.getString(KEY_PLAY_RESOLUTION,
					VDResolutionData.TYPE_DEFINITION_SD);
		}
		return resolution;
	}

	public static void setResolution(Context ctt, String resolution) {
		if (ctt != null) {
			SharedPreferences sp = ctt.getSharedPreferences(
					PREFERENCES_PLAY_RESOLUTION, Context.MODE_PRIVATE);
			sp.edit().putString(KEY_PLAY_RESOLUTION, resolution).commit();
		}
	}

	// public static int getCurResolution(Context ctt) {
	// if (ctt != null) {
	// SharedPreferences sp =
	// ctt.getSharedPreferences(PREFERENCES_PLAY_RESOLUTION,
	// Context.MODE_PRIVATE);
	// return sp.getInt(KEY_PLAY_RESOLUTION,
	// M3u8Resolution.M3U8_RESOLUTION_TYPE_SD);
	// }
	// return M3u8Resolution.M3U8_RESOLUTION_TYPE_SD;
	// }

	// public static void setResolution(Context ctt, int resolution) {
	// if (ctt != null) {
	// SharedPreferences sp =
	// ctt.getSharedPreferences(PREFERENCES_PLAY_RESOLUTION,
	// Context.MODE_PRIVATE);
	// sp.edit().putInt(KEY_PLAY_RESOLUTION, resolution).commit();
	// }
	// }

	public static boolean isFirstFullScreen(Context ctt) {
		if (ctt != null) {
			SharedPreferences sp = ctt.getSharedPreferences(
					PREFERENCES_FIRST_IN, Context.MODE_PRIVATE);
			return sp.getBoolean(KEY_FIRST_FULL_SCREEN, true);
		}
		return false;
	}

	public static void setFirstFullScreen(Context ctt, boolean first) {
		if (ctt != null) {
			SharedPreferences sp = ctt.getSharedPreferences(
					PREFERENCES_FIRST_IN, Context.MODE_PRIVATE);
			sp.edit().putBoolean(KEY_FIRST_FULL_SCREEN, first).commit();
		}
	}

	/**
	 * 设置当前播放器使用的解码类型
	 * 
	 * @param context
	 *            上下文
	 * @param isFFMpeg
	 *            true：表示软解码；false：表示硬解码
	 */
	public static void setDecodingType(Context context, boolean isFFMpeg) {
		if (context != null) {
			SharedPreferences sp = context.getSharedPreferences(
					PREFERENCES_DECODING_TYPE, Context.MODE_PRIVATE);
			sp.edit().putBoolean(KEY_DECODING_TYPE, isFFMpeg).commit();
		}
	}

	/**
	 * 得到当前保存的播放器类型，如果没有设置，那么默认就是软解
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isDecodingTypeFFMpeg(Context context) {
		if (context != null) {
			SharedPreferences sp = context.getSharedPreferences(
					PREFERENCES_DECODING_TYPE, Context.MODE_PRIVATE);
			if (!sp.contains(KEY_DECODING_TYPE) && VDUtility.isXiaomi3()) {
				// 对于默认情况下，小米3为硬解
				return false;
			}
			return sp.getBoolean(KEY_DECODING_TYPE, true);
		}
		return true;
	}
}
