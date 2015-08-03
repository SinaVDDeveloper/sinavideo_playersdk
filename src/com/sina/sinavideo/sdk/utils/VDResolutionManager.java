package com.sina.sinavideo.sdk.utils;

import java.util.List;

import com.sina.sinavideo.sdk.data.VDResolutionData;
import com.sina.sinavideo.sdk.data.VDResolutionData.VDResolution;

import android.annotation.SuppressLint;
import android.content.Context;

/**
 * 清晰度部分逻辑太复杂，单独做了一个类出来处理<br />
 * <br />
 * 清晰度方案1:用户方式<br />
 * 1、查找sp里面，如果有上次用户点击的清晰度，那么就按照用户选择的来<br/>
 * 2、如果没有找到，那么就从默认最低的清晰度开始搞<br/>
 * 3、在解析情绪度回来的时候，检查当前所有的清晰度，如果命中，那么就直接选出，如果没有命中，那么就往下寻找一个最接近当前清晰度的来播放<br/>
 * 4、如果是在3G环境中，那么123都无效，按照最小的来<br />
 * <br />
 * 清晰度方案2：暴力方式<br />
 * 直接选择最低一档的清晰度，不再记录用户行为<br />
 * 
 * @author sunxiao
 * 
 */
public class VDResolutionManager {

	public final static int RESOLUTION_SOLUTION_OBTAIN_USER = 0;
	public final static int RESOLUTION_SOLUTION_NONE = 1;

	private String mResolutionTag = VDResolutionData.TYPE_DEFINITION_SD;
	private VDResolutionData mResolutionData = null;
	@SuppressLint("unused")
	private Context mContext = null;
	private int mResolutionSolutionType = RESOLUTION_SOLUTION_OBTAIN_USER;

	private static class VDResolutionManagerINSTANCE {

		private static VDResolutionManager instance = new VDResolutionManager();
	}

	public static VDResolutionManager getInstance(Context context) {
		VDResolutionManager instance = VDResolutionManagerINSTANCE.instance;
		instance.mContext = context.getApplicationContext();
		instance.initResolutionTag(context);
		return instance;
	}

	/**
	 * 初始化代码，用于外部调用来设置当前的清晰度获取方式
	 * 
	 * @param solutionType
	 */
	public void init(int solutionType) {
		setSolution(solutionType);
	}

	/**
	 * 改变当前默认的清晰度逻辑，默认为用户方式
	 * 
	 * @param solutionType
	 */
	private void setSolution(int solutionType) {
		if (solutionType < 0) {
			return;
		}
		boolean isOnlyMobileNet = VDUtility.isOnlyMobileType(mContext);
		if (isOnlyMobileNet) {
			// 在只有移动环境时候，只用标清方式
			mResolutionTag = VDResolutionData.TYPE_DEFINITION_SD;
			return;
		}
		mResolutionSolutionType = solutionType;
		initResolutionTag(mContext);
	}

	/**
	 * 初始化清晰度tag，按照已经设定的解决方案来处理
	 */
	private void initResolutionTag(Context context) {
		if (mResolutionSolutionType == RESOLUTION_SOLUTION_OBTAIN_USER) {
			String savedResolutionTag = VDSharedPreferencesUtil
					.getCurResolution(context);
			if (savedResolutionTag != null) {
				mResolutionTag = savedResolutionTag;
			}
		} else if (mResolutionSolutionType == RESOLUTION_SOLUTION_NONE) {
			mResolutionTag = VDResolutionData.TYPE_DEFINITION_SD;
		}
	}

	/**
	 * 对输入的数据进行过滤以及重新排序
	 * 
	 * @param resolutionData
	 */
	private VDResolutionData resortResolutionData(
			VDResolutionData resolutionData) {
		VDResolutionData newResolutionData = new VDResolutionData();
		List<String> tagList = VDResolutionData.getDefDescTagList();
		for (String value : tagList) {
			VDResolution resolution = resolutionData
					.getResolutionWithTag(value);
			if (resolution != null) {
				newResolutionData.addResolution(resolution);
			}
		}
		return newResolutionData;
	}

	/**
	 * 校对当前的清晰度tag，如果resolutionData中没有当前的tag，那么找一个最接近的来
	 * 
	 * NOTE 这个要求resolutionData按照从小到大分辨率的顺序来
	 */
	private String nearResolutionTag(VDResolutionData resolutionData,
			String currTag) {
		if (resolutionData == null || resolutionData.getResolutionSize() == 0) {
			return currTag;
		}

		// step1:如果只有一个清晰度，那么直接返回
		if (resolutionData.getResolutionSize() == 1) {
			return resolutionData.getResolutionList().get(0).getTag();
		}

		// step2:如果有多个，那么按照倒排序方式进行resolutionData的遍历，然后得到与当前currTag最接近的值
		String tag = currTag;
		List<String> tagList = VDResolutionData.getDefDescTagList();
		int currTagIndex = tagList.indexOf(currTag);
		List<String> currTagList = resolutionData.getTagList();
		int num = currTagList.size() - 1;
		int base = 100;
		for (int i = num; i >= 0; i--) {
			int absCurrSplit = Math.abs(i - currTagIndex);
			if (absCurrSplit <= base) {
				base = absCurrSplit;
				tag = currTagList.get(i);
			}
		}

		return tag;
	}

	public boolean isParsed() {
		return (mResolutionData != null && mResolutionData.getResolutionSize() > 0) ? true
				: false;
	}

	/**
	 * 得到当前的清晰度数据
	 * 
	 * @return
	 */
	public VDResolutionData getResolutionData() {
		return mResolutionData;
	}

	/**
	 * 得到当前的清晰度
	 * 
	 * @return
	 */
	public VDResolution getCurrResolution() {
		return mResolutionData.getResolutionWithTag(mResolutionTag);
	}

	/**
	 * 得到当前的清晰度tag
	 * 
	 * @return
	 */
	public String getCurrResolutionTag() {
		return mResolutionTag;
	}

	/**
	 * 解析完毕后，设置清晰度列表
	 * 
	 * @param resolutionData
	 */
	public void setResolutionData(VDResolutionData resolutionData) {
		if (resolutionData == null) {
			return;
		}

		resolutionData = resortResolutionData(resolutionData);
		mResolutionData = resolutionData;
		mResolutionTag = nearResolutionTag(resolutionData, mResolutionTag);
	}

	/**
	 * 清晰度改变，设置一个新的可选择tag
	 * 
	 * @param resolutionTag
	 */
	public void setResolutionTag(String resolutionTag) {
		if (resolutionTag == null) {
			return;
		}
		if (mResolutionData.isContainTag(resolutionTag)) {
			mResolutionTag = resolutionTag;
			VDSharedPreferencesUtil.setResolution(mContext, resolutionTag);
		}
	}

	/**
	 * 因为是个单例，所以用来清理资源
	 */
	public void release() {
		mResolutionData = null;
		mResolutionTag = null;
		mContext = null;
	}

}
