package com.sina.sinavideo.sdk.utils.m3u8;

/**
 * m3u8实体基础类
 * 
 * @author GengHongchao
 *
 */
public class M3u8Content {

    public static final int M3U8_TYPE_UNKNOWN = 0;
    public static final int M3U8_TYPE_SEGMENT = 1;
    public static final int M3U8_TYPE_RESOLUTION = 2;

    protected int mType;

    public int getType() {
        return mType;
    }
}