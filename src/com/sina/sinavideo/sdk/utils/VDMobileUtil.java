
package com.sina.sinavideo.sdk.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class VDMobileUtil {

    public final static int PROVIDER_UNKNOWN = 0;
    public final static int CHINA_MOBILE = 1;
    public final static int CHINA_UNICOM = 2;
    public final static int CHINA_TELECOM = 3;

    public final static int NONE = 0;
    public final static int MOBILE_2G = 1;
    public final static int MOBILE_3G = 2;
    public final static int MOBILE_4G = 3;
    public final static int MOBILE_UNKNOWN = 4;
    public final static int WIFI = 5;

    public static final int NETWORK_TYPE_GPRS = 1;
    public static final int NETWORK_TYPE_EDGE = 2;
    public static final int NETWORK_TYPE_UMTS = 3;
    public static final int NETWORK_TYPE_CDMA = 4;
    public static final int NETWORK_TYPE_EVDO_0 = 5;
    public static final int NETWORK_TYPE_EVDO_A = 6;
    public static final int NETWORK_TYPE_1xRTT = 7;
    public static final int NETWORK_TYPE_HSDPA = 8;
    public static final int NETWORK_TYPE_HSUPA = 9;
    public static final int NETWORK_TYPE_HSPA = 10;
    public static final int NETWORK_TYPE_IDEN = 11;
    public static final int NETWORK_TYPE_EVDO_B = 12;
    public static final int NETWORK_TYPE_LTE = 13;
    public static final int NETWORK_TYPE_EHRPD = 14;
    public static final int NETWORK_TYPE_HSPAP = 15;

    public static int getProvider(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String IMSI = telephonyManager.getSubscriberId();
        if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
            return CHINA_MOBILE;
        } else if (IMSI.startsWith("46001")) {
            return CHINA_UNICOM;
        } else if (IMSI.startsWith("46003")) {
            return CHINA_TELECOM;
        }
        return PROVIDER_UNKNOWN;
    }

    public static int getMobileDetail(Context context) {
        ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conn == null) {
            return NONE;
        }
        NetworkInfo networkInfo = conn.getActiveNetworkInfo();
        if (networkInfo == null) {
            // 网络不可用
            return NONE;
        }
        if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return WIFI;
        } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return telephonyManager.getNetworkType();
        }
        return NONE;
    }

    public static int getMobileType(Context context) {
        int type = getMobileDetail(context);
        int ret = NONE;
        switch (type) {
            case NETWORK_TYPE_GPRS:
            case NETWORK_TYPE_EDGE:
            case NETWORK_TYPE_CDMA:
            case NETWORK_TYPE_1xRTT:
            case NETWORK_TYPE_IDEN:
                ret = MOBILE_2G;
                break;
            case NETWORK_TYPE_UMTS:
            case NETWORK_TYPE_EVDO_0:
            case NETWORK_TYPE_EVDO_A:
            case NETWORK_TYPE_EVDO_B:
            case NETWORK_TYPE_HSPA:
            case NETWORK_TYPE_HSDPA:
            case NETWORK_TYPE_HSPAP:
            case NETWORK_TYPE_HSUPA:
            case NETWORK_TYPE_EHRPD:
                ret = MOBILE_3G;
                break;
            case NETWORK_TYPE_LTE:
                ret = MOBILE_4G;
                break;
            default:
                ret = MOBILE_UNKNOWN;
        }
        return ret;
    }
}
