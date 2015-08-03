package com.sina.sinavideo.sdk.widgets.playlist;

import com.sina.sinavideo.sdk.data.VDVideoInfo;

/**
 * 相关视频列表中数据项的数据接口
 * 
 * @author liuqun
 */
public interface VDVideoPlaylistBase {

    /**
     * 设置当前一条数据
     * 
     * @param info
     */
    public void setData(VDVideoInfo info);

    /**
     * 设置这条数据在列表中哪一个？
     * 
     * @param infoIndex
     */
    public void setVideoInfo(int infoIndex, int curPlayIndex);
}
