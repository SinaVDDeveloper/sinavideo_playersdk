package com.sina.sinavideo.sdk.utils.m3u8;

import java.util.ArrayList;
import java.util.List;

public class M3u8ResolutionContent extends M3u8Content {

    public M3u8ResolutionContent() {
        mType = M3U8_TYPE_RESOLUTION;
        mResolutionList = new ArrayList<M3u8ResolutionContent.M3u8Resolution>();
    }

    private List<M3u8Resolution> mResolutionList;

    public static class M3u8Resolution {

        public static final int M3U8_RESOLUTION_TYPE_SD = 0;
        public static final int M3U8_RESOLUTION_TYPE_HD = 1;
        public static final int M3U8_RESOLUTION_TYPE_XHD = 2;
        public static final int M3U8_RESOLUTION_TYPE_3D = 3;

        public int mResolutionType;
        public int mProgramId;
        public int mBandWidth;
        public String mM3u8Url;
    }

    public void setResolutionList(List<M3u8Resolution> list) {
        mResolutionList = list;
    }

    public List<M3u8Resolution> getResolutionList() {
        return mResolutionList;
    }
}