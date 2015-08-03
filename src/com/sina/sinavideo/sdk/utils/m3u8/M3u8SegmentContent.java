package com.sina.sinavideo.sdk.utils.m3u8;


public class M3u8SegmentContent extends M3u8Content {

    public M3u8SegmentContent() {
        mType = M3U8_TYPE_SEGMENT;
    }

//    private int mTargetDuration;
//    private List<M3u8Segment> mM3u8SegmentList;

    public static class M3u8Segment {

        public int mDuration;
        public String mUrl;
    }
}