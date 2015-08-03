package com.sina.sinavideo.sdk.utils;

import com.sina.sinavideo.coreplayer.splayer.SPlayer;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class VDApplication {

	private Context mContext = null;
	public boolean mDebug = true;

	private static final String TAG = "VDApplication";

	private static class VDApplicationINSTANCE {

		static VDApplication instance = new VDApplication();
	}

	public static VDApplication getInstance() {
		return VDApplicationINSTANCE.instance;
	}

	public void initPlayer(Context context) {
		setContext(context);
		SPlayer.initialize(context);
	}

	public void setContext(Context context) {
		VDLog.d(TAG, "setContext:" + context);
		if (mContext == null) {
			mContext = context.getApplicationContext();
		}
	}

	public Context getContext() {
		VDLog.d(TAG, "getContext");
		return mContext;
	}

	public void release() {
		// 使用了新的controller后，不再release，否则，可能导致mContext为null，导致播放器中的延时操作等语句出现NullException
		// VDLog.d(TAG, "release");
		// mContext = null;
	}

	public String getAPPName() {
		if (mContext == null) {
			return "";
		}
		return mContext.getPackageName();
	}

	public String getAPPVersion() {
		if (mContext == null) {
			return "";
		}
		try {
			PackageInfo packInfo = mContext.getPackageManager().getPackageInfo(
					mContext.getPackageName(), 0);
			return packInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}

	public String getDeviceName() {
		return android.os.Build.MODEL;
	}

	public String getAndroidVersion() {
		return android.os.Build.VERSION.RELEASE;
	}

	/**
	 * 网络是否已连接
	 * 
	 * @param context
	 * @return
	 */
	public boolean isNetworkConnected() {
		try {
			ConnectivityManager cm = (ConnectivityManager) mContext
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (cm == null) {
				return false;
			}
			NetworkInfo networkInfo = cm.getActiveNetworkInfo();
			return networkInfo != null && networkInfo.isConnected();
		} catch (NullPointerException e) {
			VDLog.d(TAG, e.getMessage());
			// e.printStackTrace();
			return false;
		}
	}
}
