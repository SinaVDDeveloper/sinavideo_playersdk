package com.sina.sinavideo.sdk.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 清晰度的类 TODO 这个类，后期要在SDK中再放一份。
 * 
 * @author alexsun
 * 
 */
public class VDResolutionData {

	/** 清晰度：流畅 */
	public static final String TYPE_DEFINITION_CIF = "cif";
	/** 清晰度：标清 */
	public static final String TYPE_DEFINITION_SD = "sd";
	/** 清晰度：高清 */
	public static final String TYPE_DEFINITION_HD = "hd";
	/** 清晰度：超清 */
	public static final String TYPE_DEFINITION_FHD = "fhd";
	/**
	 * 3D，这个一般都没有用到
	 */
	public static final String TYPE_DEFINITION_3D = "3d";

	public static LinkedHashMap<String, String> mDefDesc = new LinkedHashMap<String, String>();

	static {
		mDefDesc.put(TYPE_DEFINITION_CIF, "流畅");
		mDefDesc.put(TYPE_DEFINITION_SD, "标清");
		mDefDesc.put(TYPE_DEFINITION_HD, "高清");
		mDefDesc.put(TYPE_DEFINITION_FHD, "超清");
		mDefDesc.put(TYPE_DEFINITION_3D, "3D");
	}

	public static String getDefDescTagWithIndex(int index) {
		Iterator<Map.Entry<String, String>> iter = mDefDesc.entrySet()
				.iterator();
		int step = 0;
		String ret = null;
		while (iter.hasNext()) {
			Map.Entry<String, String> entry = (Map.Entry<String, String>) iter
					.next();
			if (step == index) {
				ret = entry.getKey();
			}
			step++;
		}
		return ret;
	}

	public static String getDefDescTextWithTag(String tag) {
		return mDefDesc.get(tag);
	}

	public static List<String> getDefDescList() {
		ArrayList<String> retList = new ArrayList<String>();
		Iterator<Map.Entry<String, String>> iter = mDefDesc.entrySet()
				.iterator();
		while (iter.hasNext()) {
			Map.Entry<String, String> entry = (Map.Entry<String, String>) iter
					.next();
			retList.add(entry.getValue());
		}
		return retList;
	}

	public static List<String> getDefDescTagList() {
		ArrayList<String> retList = new ArrayList<String>();
		Iterator<Map.Entry<String, String>> iter = mDefDesc.entrySet()
				.iterator();
		while (iter.hasNext()) {
			Map.Entry<String, String> entry = (Map.Entry<String, String>) iter
					.next();
			retList.add(entry.getKey());
		}
		return retList;
	}

	public static int getDefDescIndexWithTag(String tag) {
		int ret = -1;
		Iterator<Map.Entry<String, String>> iter = mDefDesc.entrySet()
				.iterator();
		int count = 0;
		while (iter.hasNext()) {
			Map.Entry<String, String> entry = (Map.Entry<String, String>) iter
					.next();
			if (entry.getKey().equals(tag)) {
				ret = count;
			}
			count++;
		}
		return ret;
	}

	private List<VDResolution> mResolutionList = new ArrayList<VDResolutionData.VDResolution>();

	public VDResolution getFirstResolution() {
		if (mResolutionList != null && mResolutionList.size() != 0) {
			return mResolutionList.get(0);
		}
		return null;
	}

	public List<String> getUrlList() {
		if (!mResolutionList.isEmpty()) {
			ArrayList<String> retUrlList = new ArrayList<String>();
			for (VDResolution resolution : mResolutionList) {
				retUrlList.add(resolution.mUrl);
			}
			return retUrlList;
		}
		return null;
	}

	public List<String> getTagList() {
		if (!mResolutionList.isEmpty()) {
			ArrayList<String> retTagList = new ArrayList<String>();
			for (VDResolution resolution : mResolutionList) {
				retTagList.add(resolution.mTag);
			}
			return retTagList;
		}
		return null;
	}

	/**
	 * 深度copy，效率方面似乎有优化余地，要保持次序
	 * 
	 * @param src
	 */
	public void deepCopy(VDResolutionData src) {
		if (!src.getResolutionList().isEmpty()) {
			mResolutionList.clear();
			for (VDResolution value : src.mResolutionList) {
				mResolutionList.add(value);
			}
		}
	}

	public int getResolutionSize() {
		return mResolutionList.size();
	}

	public List<VDResolution> getResolutionList() {
		return mResolutionList;
	}

	public VDResolution getResolutionWithTag(String tag) {
		for (VDResolution value : mResolutionList) {
			if (value.mTag.equals(tag)) {
				return value;
			}
		}
		return null;
	}

	/**
	 * 这个函数名字有问题，实际上是返回tag:url的组合
	 * 
	 * @return
	 */
	public HashMap<String, String> getResolution() {
		HashMap<String, String> retMap = null;
		if (mResolutionList != null) {
			for (VDResolution value : mResolutionList) {
				if (retMap == null) {
					retMap = new HashMap<String, String>();
				}
				retMap.put(value.mTag, value.mUrl);
			}
		}
		return retMap;
	}

	public boolean isContainTag(String tag) {
		if (mResolutionList != null) {
			for (VDResolution value : mResolutionList) {
				if (value.mTag.equals(tag)) {
					return true;
				}
			}
		}
		return false;
	}

	public void addResolution(VDResolution resolution) {
		if (!isContainTag(resolution.mTag)) {
			mResolutionList.add(resolution);
		}
	}

	/**
	 * @deprecated 不再使用
	 * @param tag
	 * @param resolution
	 */
	public void addResolution(String tag, VDResolution resolution) {
		if (!isContainTag(tag)) {
			mResolutionList.add(resolution);
		}
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		for (VDResolution value : mResolutionList) {
			sb.append(value.toString() + "\n");
			sb.append("-------------\n");
		}
		return sb.toString();
	}

	/**
	 * 清晰度封装
	 * 
	 * @author alexsun
	 * 
	 */
	public static class VDResolution {

		public VDResolution() {
			super();
		}

		public VDResolution(String tag, String url, int programID, int bandwidth) {
			super();
			mTag = tag;
			mUrl = url;
			mProgramID = programID;
			mBandWidth = bandwidth;
		}

		private String mTag = "";
		private String mUrl = "";
		private int mProgramID = 0;
		private int mBandWidth = 0;

		public void setTag(String tag) {
			mTag = tag;
		}

		public void setUrl(String url) {
			mUrl = url;
		}

		public void setProgramID(int programID) {
			mProgramID = programID;
		}

		public void setBandWidth(int bandwidth) {
			mBandWidth = bandwidth;
		}

		public String getTag() {
			return mTag;
		}

		public String getUrl() {
			return mUrl;
		}

		public int getProgramID() {
			return mProgramID;
		}

		public int getBandWidth() {
			return mBandWidth;
		}

		public void deepCopy(VDResolution resolution) {
			mUrl = new String(resolution.getUrl());
			mTag = new String(resolution.getTag());
			mProgramID = resolution.getProgramID();
			mBandWidth = resolution.getBandWidth();
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			StringBuilder sb = new StringBuilder();
			sb.append("mTag" + mTag + "\n");
			sb.append("mUrl" + mUrl + "\n");
			sb.append("mProgramID" + mProgramID + "\n");
			sb.append("mBandWidth" + mBandWidth + "\n");
			return sb.toString();
		}
	}
}
