package com.sina.sinavideo.sdk.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

/**
 * 网络更改时候调用，加SDK里面确实很奇葩
 * 
 * @author sunxiao
 * 
 */
public class VDNetworkBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "NetworkBroadcastReceiver";
    private List<NetworkNotifyListener> mNotifyListeners = new ArrayList<VDNetworkBroadcastReceiver.NetworkNotifyListener>();

    public VDNetworkBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        State wifiState = null;
        State mobileState = null;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            VDLog.w(TAG, "onReceive -- ConnectivityManager is null!");
            return;
        }
        NetworkInfo networkInfo = null;
        try {
            networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (networkInfo != null) {
            wifiState = networkInfo.getState();
        }
        try {
            networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (networkInfo != null) {
            mobileState = networkInfo.getState();
        }
        VDLog.d(TAG, "onReceive -- wifiState = " + wifiState + " -- mobileState = " + mobileState);
        if (wifiState != null && mobileState != null && State.CONNECTED != wifiState && State.CONNECTED == mobileState) {
            // 手机网络连接成功
            VDLog.d(TAG, "onReceive -- 手机网络连接成功");
            mobileNotify();
        } else if (wifiState != null && mobileState != null && State.CONNECTED != wifiState
                && State.CONNECTED != mobileState) {
            // 手机没有任何的网络
            VDLog.d(TAG, "onReceive -- 手机没有任何的网络");
            nothingNotify();
        } else if (wifiState != null && State.CONNECTED == wifiState) {
            // 无线网络连接成功
            VDLog.d(TAG, "onReceive -- 无线网络连接成功");
            wifiNotify();
        }

    }

    public void wifiNotify() {
        for (NetworkNotifyListener listener : mNotifyListeners) {
            listener.wifiConnected();
        }
    }

    public void mobileNotify() {
        for (NetworkNotifyListener listener : mNotifyListeners) {
            listener.mobileConnected();
        }
    }

    public void nothingNotify() {
        for (NetworkNotifyListener listener : mNotifyListeners) {
            listener.nothingConnected();
        }
    }

    public void addListener(NetworkNotifyListener listener) {
        mNotifyListeners.add(listener);
    }

    public void removeListener(NetworkNotifyListener listener) {
        mNotifyListeners.remove(listener);
    }

    public void destory() {
        mNotifyListeners.clear();
    }

    public interface NetworkNotifyListener {

        void wifiConnected();

        void mobileConnected();

        void nothingConnected();
    }

}