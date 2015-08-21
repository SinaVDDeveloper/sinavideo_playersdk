package com.sina.sinavideo.sdk.utils;

/**
 * 配置管理器，管理raw/config.properties的配置文件信息<br />
 * 
 * @sunxiao 注解：<br />
 *          不读了。就剩一个配置选项了。直接写死在里面，要是调整的话，直接改常数就行了。
 * 
 * @author alexsun
 * 
 */
public class VDSDKConfig {

	private final static int mRetryTime = 2;

	public VDSDKConfig() {
	}

	private static class VDSDKConfigINSTANCE {

		private static VDSDKConfig instance = new VDSDKConfig();
	}

	public static VDSDKConfig getInstance() {
		return VDSDKConfigINSTANCE.instance;
	}

	public int getRetryTime() {
		return mRetryTime;
	}
}
