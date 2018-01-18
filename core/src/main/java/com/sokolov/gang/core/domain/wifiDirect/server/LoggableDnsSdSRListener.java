package com.sokolov.gang.core.domain.wifiDirect.server;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

public class LoggableDnsSdSRListener implements WifiP2pManager.DnsSdServiceResponseListener {
    private static final String TAG = "LoggableDnsSdSRListener";
    private final WifiP2pManager.DnsSdServiceResponseListener origin;

    public LoggableDnsSdSRListener(WifiP2pManager.DnsSdServiceResponseListener origin) {
        this.origin = origin;
    }

    @Override
    public void onDnsSdServiceAvailable(String instanceName, String registrationType, WifiP2pDevice srcDevice) {
        Log.d(TAG, "onDnsSdServiceAvailable() called with: instanceName = [" + instanceName + "], registrationType = [" + registrationType + "], srcDevice = [" + srcDevice + "]");
        origin.onDnsSdServiceAvailable(instanceName, registrationType, srcDevice);
    }
}
