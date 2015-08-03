package com.sina.sinavideo.sdk.data;

public class VDPlayerErrorInfo {

    // 从splayer里面的player.h中搬过来的
    // NOTE:后期考虑是否需要优化并将错误码合并起来，BTW，错误码实在太乱了
    // 主错误
    public final static int MEDIA_ERROR_WHAT_UNKNOWN = 0; // MEDIA_ERROR_UNKNOWN:1
    public final static int MEDIA_ERROR_WHAT_SERVER_DIED = 2; // MEDIA_ERROR_SERVER_DIED:100
    public final static int MEDIA_ERROR_WHAT_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK = 3; // MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:200
    public final static int MEDIA_ERROR_WHAT_M3U8_PARSER = 4; // 自己加的，表示m3u8解析的错误
    public final static int MEDIA_ERROR_WHAT_VMS_PARSER = 5; // 自己加的，表示vms网络或者解析错误
    // 子错误
    public final static int MEDIA_ERROR_EXTRA_PLAYER_UNKNOWN = 0;
    public final static int MEDIA_ERROR_EXTRA_PLAYER_IO = 1; // MEDIA_ERROR_IO:-1004
    public final static int MEDIA_ERROR_EXTRA_PLAYER_MALFORMED = 2; // MEDIA_ERROR_MALFORMED:-1007
    public final static int MEDIA_ERROR_EXTRA_PLAYER_UNSUPPORTED = 3; // MEDIA_ERROR_UNSUPPORTED:-1010
    public final static int MEDIA_ERROR_EXTRA_PLAYER_TIMED_OUT = 4; // MEDIA_ERROR_TIMED_OUT:-110
    public final static int MEDIA_ERROR_EXTRA_PLAYER_DECODER_FAIL = 5; // MEDIA_ERROR_DECODER_FAIL:-2000
    public static final int MEDIA_ERROR_EXTRA_M3U8_NO_CONTENT = 6; // M3u8ContentParser:ERROR_NO_CONTENT:1
    public static final int MEDIA_ERROR_EXTRA_M3U8_PARSE = 7; // M3u8ContentParser:ERROR_PARSE:2
    public static final int MEDIA_ERROR_EXTRA_VMS_NETWORK_DISABLED = 8; // vms网络错误
    public static final int MEDIA_ERROR_EXTRA_VMS_REQUEST_ERROR = 9; // vms网络错误
    public static final int MEDIA_ERROR_EXTRA_VMS_RESPONSE_ERROR = 10; // vms网络返回结果错误
    public static final int MEDIA_ERROR_EXTRA_VMS_PARSE = 11; // vms_json解析错误
    // INFO部分
    public static final int MEDIA_INFO_WHAT_UNKNOWN = 0; // MEDIA_INFO_UNKNOWN
    public static final int MEDIA_INFO_WHAT_VIDEO_TRACK_LAGGING = 1;// MEDIA_INFO_VIDEO_TRACK_LAGGING
    public static final int MEDIA_INFO_WHAT_BUFFERING_START = 2;// MEDIA_INFO_BUFFERING_START
    public static final int MEDIA_INFO_WHAT_BUFFERING_END = 3;// MEDIA_INFO_BUFFERING_END
    public static final int MEDIA_INFO_WHAT_BAD_INTERLEAVING = 4;// MEDIA_INFO_BAD_INTERLEAVING
    public static final int MEDIA_INFO_WHAT_NOT_SEEKABLE = 5;// MEDIA_INFO_NOT_SEEKABLE
    public static final int MEDIA_INFO_WHAT_METADATA_UPDATE = 6;// MEDIA_INFO_METADATA_UPDATE
    // 贴片广告部分
    public static final int MEDIA_INSERTAD_SUCCESS = 0;
    public static final int MEDIA_INSERTAD_ERROR_STEPOUT = 1;
    public static final int MEDIA_INSERTAD_ERROR_UNKNOWN = 2;
}
