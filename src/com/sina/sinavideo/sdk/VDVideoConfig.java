package com.sina.sinavideo.sdk;

/**
 * 配置一些常量，用于SDK区别一些特殊状态
 * 
 * @author sunxiao
 */
public class VDVideoConfig {

    /**
     * 是否是DEBUG状态，区别于是否显示整片日志
     */
    public final static boolean mIsDebug = true;
    /**
     * 是否显示屏幕锁
     */
    public final static boolean mIsScreenLockUI = true;

    /**
     * 全屏方式，分别有一下种类： <br>
     * eScreenOrientationTypeOnlySensor:纯粹依靠感应器，这种情况下，任何全屏按钮等都不起作用<br>
     * eScreenOrientationTypeOnlyManual:纯粹手动控制，感应器无效<br>
     * eScreenOrientationTypeBoth:即依靠感应器又依靠手动控制方式[默认]<br>
     * 
     * @author sunxiao
     * 
     */
    public enum eVDScreenOrientationType {
        eScreenOrientationTypeOnlyManual, eScreenOrientationTypeOnlySensor, eScreenOrientationTypeBoth,
    }

    public final static eVDScreenOrientationType mScreenType = eVDScreenOrientationType.eScreenOrientationTypeBoth;

    /**
     * 解码方式选择 <br>
     * eVDDecodingTypeSoft 纯软解方式 <br>
     * eVDDecodingTypeBothAuto 根据系统白名单自动切换[默认] <br>
     * eVDDecodingTypeBothManual 根据VDVideoDecodingButton选择结果处理，默认软解 <br>
     * eVDDecodingTypeBoth 自动与手动方式都进行，如果点击手动开关，那么就关闭自动选择功能
     * 
     * @author sunxiao
     * 
     */
    public enum eVDDecodingType {
        eVDDecodingTypeSoft, eVDDecodingTypeBothAuto, eVDDecodingTypeBothManual, eVDDecodingTypeBoth, eVDDecodingTypeHardWare
    }

    public static eVDDecodingType mDecodingType = eVDDecodingType.eVDDecodingTypeBoth;

    public final static String[] mDecodingPlayerDesc = {"兼容播放器", "高级播放器"};
    public final static String mDecodingPlayerTitle = "切换播放器";
    public final static String mDecodingDesc = "想让画质更清晰、手机更省电？请使用高级播放器。";
    public final static String mDecodingCancelButton = "取消";
}
