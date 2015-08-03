package com.sina.sinavideo.sdk;

import java.util.ArrayList;
import java.util.Hashtable;

import android.view.View;
import android.view.ViewGroup;

import com.sina.sinavideo.sdk.widgets.VDBaseWidget;
import com.sina.sinavideo.sdk.widgets.VDVideoSoundSeekBar;
import com.sina.sinavideo.sdk.widgets.VDVideoSoundSeekPercentView;

/**
 * 表示一页，复杂模式：一次两页，一个横屏一个竖屏，简单模式：就一个竖屏
 * 
 * @author sunxiao1@staff.sina.com.cn
 */
public class VDVideoViewLayerContext {
	public boolean mIsComplexLayerType = false;
	public ArrayList<VDVideoViewLayer> mComplexLayer = new ArrayList<VDVideoViewLayer>();
	public VDVideoViewLayer mSimpleLayer = null;
	// 当前是否需要显示
	private boolean mIsGone = false;
	// 当前层是否是广告层
	public boolean mIsInsertADLayer = false;
	private boolean mIsFullMode = false;

	private Hashtable<VDVideoViewLayer, ArrayList<VDBaseWidget>> mWidgetList = new Hashtable<VDVideoViewLayer, ArrayList<VDBaseWidget>>();

	public boolean isCanShow(boolean isAD) {
		// 条件：在isAD是广告且当前是广告层或者当前是非广告视频且不是广告层，并且没有被隐藏的时候显示
		return mIsInsertADLayer == isAD && !mIsGone;
	}

	public VDVideoViewLayerContext() {
		super();
	}

	/**
	 * 看当前组件列表中是否有音量显示组件
	 * 
	 * @return
	 */
	public boolean checkSoundWidget() {
		for (ArrayList<VDBaseWidget> widgetList : mWidgetList.values()) {
			for (VDBaseWidget widget : widgetList) {
				if (widget instanceof VDVideoSoundSeekBar
						|| widget instanceof VDVideoSoundSeekPercentView) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 重新刷新当前层
	 * 
	 * @param isAD
	 */
	public void refresh(boolean isAD) {
		int i = 1;
		if (isCanShow(isAD)) {
			for (VDVideoViewLayer item : mComplexLayer) {
				if ((i % 2 == 0 && mIsFullMode) || (i % 2 == 1 && !mIsFullMode)) {
					// 横屏，并且偶数|竖屏，并且奇数
					item.setVisibility(View.VISIBLE);
					resetWidget(item);
				} else {
					item.setVisibility(View.GONE);
					hideWidget(item);
				}
				i++;
			}
		} else {
			for (VDVideoViewLayer item : mComplexLayer) {
				item.setVisibility(View.GONE);
				hideWidget(item);
			}
		}
	}

	/**
	 * 设置全局模式
	 * 
	 * @param isFullMode
	 */
	public void setFullMode(boolean isFullMode, boolean isAD) {
		int i = 1;
		mIsFullMode = isFullMode;
		if (isCanShow(isAD)) {
			for (VDVideoViewLayer item : mComplexLayer) {
				if ((i % 2 == 0 && mIsFullMode) || (i % 2 == 1 && !mIsFullMode)) {
					// 横屏，并且偶数|竖屏，并且奇数
					item.setVisibility(View.VISIBLE);
					resetWidget(item);
				} else {
					item.setVisibility(View.GONE);
					hideWidget(item);
				}
				i++;
			}
		} else {
			for (VDVideoViewLayer item : mComplexLayer) {
				hideWidget(item);
				item.setVisibility(View.GONE);
			}
		}
	}

	/**
	 * 遍历当前layer里面的widget，调用reset方法
	 * 
	 * @param layer
	 */
	private void resetWidget(VDVideoViewLayer layer) {
		if (layer == null) {
			return;
		}
		for (VDBaseWidget value : mWidgetList.get(layer)) {
			value.reset();
		}
	}

	/**
	 * 遍历当前layer里面的widget，调用reset方法
	 * 
	 * @param layer
	 */
	private void hideWidget(VDVideoViewLayer layer) {
		if (layer == null) {
			return;
		}

		for (VDBaseWidget value : mWidgetList.get(layer)) {
			value.hide();
		}
	}

	/**
	 * 将当前页下所有的widget，重新刷新一遍
	 */
	public void reset() {
		if (mIsComplexLayerType) {
			if (mComplexLayer != null) {
				for (VDVideoViewLayer item : mComplexLayer) {
					resetWidget(item);
				}
			}
		} else {
			resetWidget(mSimpleLayer);
		}
	}

	/**
	 * 查询当前页是否显示
	 * 
	 * @return
	 */
	public boolean isVisibility() {
		return !mIsGone;
	}

	/**
	 * 设置当前页的显示状态 NOTE: 需要考虑到横竖屏的情况
	 * 
	 * @deprecated 后期删除，所有的显示隐藏部分，依靠系统内部来做
	 * @param isGone
	 */
	public void setVisibility(boolean isGone) {
		if (isGone == mIsGone) {
			// 一致，就不做了
			return;
		}
		mIsGone = isGone;
		if (mIsComplexLayerType) {
			for (VDVideoViewLayer layer : mComplexLayer) {
				if (isGone) {
					layer.setVisibility(View.GONE);
				} else {
					layer.setVisibility(View.VISIBLE);
				}
			}
		} else {
			if (mSimpleLayer != null) {
				if (isGone) {
					mSimpleLayer.setVisibility(View.GONE);
				} else {
					mSimpleLayer.setVisibility(View.VISIBLE);
				}
			}
		}
	}

	/**
	 * 将当前layer的树形结构放入缓冲中
	 * 
	 * @param layer
	 * @param list
	 */
	public void initHierarchyVideoViewLayer(ViewGroup layer,
			ArrayList<VDBaseWidget> list) {
		for (int i = 0; i < layer.getChildCount(); i++) {
			View view = layer.getChildAt(i);

			if (view instanceof VDBaseWidget) {
				list.add((VDBaseWidget) view);
			}
			if (view instanceof ViewGroup) {
				initHierarchyVideoViewLayer((ViewGroup) view, list);
			}
		}
	}

	/**
	 * 复杂模式下，添加一页
	 * 
	 * @param layer
	 */
	public void addComplexItem(VDVideoViewLayer layer) {
		mIsComplexLayerType = true;
		if (!mComplexLayer.contains(layer)) {
			mComplexLayer.add(layer);
			ArrayList<VDBaseWidget> widget = new ArrayList<VDBaseWidget>();
			initHierarchyVideoViewLayer(layer, widget);
			mWidgetList.put(layer, widget);

		}
		resetWidget(layer);
	}

	/**
	 * 复杂模式下，删除一页
	 * 
	 * @param layer
	 */
	public void removeComplexItem(VDVideoViewLayer layer) {
		mIsComplexLayerType = true;
		if (mComplexLayer.contains(layer)) {
			mComplexLayer.remove(layer);
			if (mWidgetList.contains(layer)) {
				mWidgetList.remove(layer);
			}
		}
		hideWidget(layer);
	}

	/**
	 * 简单模式下，添加一页
	 * 
	 * @param layer
	 */
	public void addSimpleItem(VDVideoViewLayer layer) {
		mIsComplexLayerType = false;
		mSimpleLayer = layer;
		ArrayList<VDBaseWidget> widget = new ArrayList<VDBaseWidget>();
		initHierarchyVideoViewLayer(layer, widget);
		mWidgetList.put(layer, widget);
		resetWidget(layer);
	}

	/**
	 * 简单模式下，添加一页
	 * 
	 * @param layer
	 */
	public void removeSimpleItem(VDVideoViewLayer layer) {
		mIsComplexLayerType = false;
		mSimpleLayer = null;
		if (mWidgetList.contains(layer)) {
			mWidgetList.remove(layer);
		}
		hideWidget(layer);
	}
}
