package com.sina.sinavideo.sdk.widgets.playlist;

import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import com.sina.sinavideo.sdk.data.VDVideoListInfo;

/**
 * 相关视频列表适配器父类
 * 
 * @author sina
 */
public abstract class VDVideoPlayListAdapter extends BaseAdapter {

    protected LayoutInflater mInflater = null;
    protected int mItemID = -1;
    protected int mCurPlayIndex;

    public void setCurPlayIndex(int index) {
        if (mCurPlayIndex == index)
            return;
        this.mCurPlayIndex = index;
        notifyDataSetChanged();
    }

    public int getCurPlayIndex() {
        return mCurPlayIndex;
    }

    protected VDVideoListInfo mVideoList = new VDVideoListInfo();

    public void setVideoList(VDVideoListInfo list) {
        if (list != null) {
            mVideoList = list;
        }
        notifyDataSetChanged();
    }

    public VDVideoPlayListAdapter() {

    }

}
