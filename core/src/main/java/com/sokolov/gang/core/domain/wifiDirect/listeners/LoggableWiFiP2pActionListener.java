package com.sokolov.gang.core.domain.wifiDirect.listeners;

import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;


public class LoggableWiFiP2pActionListener implements WifiP2pManager.ActionListener {
    private static final String TAG = "LoggableWiFiP2pAL";
    private final WifiP2pManager.ActionListener origin;
    private final String methodName;

    public LoggableWiFiP2pActionListener(String methodName) {
        this.methodName = methodName;
        origin = null;
    }

    public LoggableWiFiP2pActionListener(WifiP2pManager.ActionListener origin, String methodName) {
        this.origin = origin;
        this.methodName = methodName;
    }

    @Override
    public void onSuccess() {
        Log.d(TAG, methodName + ".onSuccess() called");
        if (origin != null) {
            origin.onSuccess();
        }
    }

    @Override
    public void onFailure(int reason) {
        Log.d(TAG, methodName + ".onFailure() called with: reason = [" + reason + "]");
        if (origin != null) {
            origin.onFailure(reason);
        }
    }
}
