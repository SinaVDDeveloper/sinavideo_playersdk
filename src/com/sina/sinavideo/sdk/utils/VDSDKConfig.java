package com.sina.sinavideo.sdk.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.sina.sinavideo.coreplayer.util.LogS;
import com.sina.video_playersdkv2.R;

import android.content.Context;

/**
 * 配置管理器，管理raw/config.properties的配置文件信息
 * 
 * @author alexsun
 * 
 */
public class VDSDKConfig {

	private Properties mProperties = null;
	private final static String TAG = "VDSDKConfig";

	private final static String CONFIG_RETRY_TIME = "retrytime";
	private final static String CONFIG_ADV_UNITID = "advUnitID";

	public VDSDKConfig() {
	}

	public synchronized void init(Context context) {
		if (context == null || mProperties != null) {
			VDLog.e(TAG, "VDSDKConfig's init,context is null");
			return;
		}
		mProperties = new Properties();
		try {
			InputStream is = context.getResources().openRawResource(
					R.raw.config);
			mProperties.load(is);
		} catch (IOException ex) {
			LogS.e(TAG, ex.getMessage());
		}
	}

	private static class VDSDKConfigINSTANCE {

		private static VDSDKConfig instance = new VDSDKConfig();
	}

	public static VDSDKConfig getInstance() {
		return VDSDKConfigINSTANCE.instance;
	}

	public static VDSDKConfig getInstance(Context context) {
		VDSDKConfig config = VDSDKConfigINSTANCE.instance;
		config.init(context);
		return config;
	}

	public int getRetryTime() {
		return Integer.valueOf(mProperties.getProperty(CONFIG_RETRY_TIME));
	}

	public String[] getAdvUnitID() {
		String unitID = mProperties.getProperty(CONFIG_ADV_UNITID);
		return unitID.split("\\|");
	}
}
