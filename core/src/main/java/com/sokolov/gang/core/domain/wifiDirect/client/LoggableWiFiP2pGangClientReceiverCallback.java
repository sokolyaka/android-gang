package com.sokolov.gang.core.domain.wifiDirect.client;

import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.util.Log;

public class LoggableWiFiP2pGangClientReceiverCallback implements WiFiP2pGangClientReceiver.Callback {
    private static final String TAG = "WiFiP2pGangClient";
    private final WiFiP2pGangClientReceiver.Callback origin;

    public LoggableWiFiP2pGangClientReceiverCallback(WiFiP2pGangClientReceiver.Callback origin) {
        this.origin = origin;
    }

    @Override
    public void processConnectionChangedAction(NetworkInfo networkInfo, WifiP2pInfo wifiP2pInfo, WifiP2pGroup group) {
        Log.d(TAG, "processConnectionChangedAction() called with: networkInfo = [" + networkInfo + "], wifiP2pInfo = [" + wifiP2pInfo + "], group = [" + group + "]");
        origin.processConnectionChangedAction(networkInfo, wifiP2pInfo, group);
    }

    @Override
    public void processThisDeviceChangedAction(WifiP2pDevice device) {
        Log.d(TAG, "processThisDeviceChangedAction() called with: device = [" + device + "]");
        origin.processThisDeviceChangedAction(device);
    }

    @Override
    public void processStateChangedAction(int state) {
        Log.d(TAG, "processStateChangedAction() called with: state = [" + state + "]");
        origin.processStateChangedAction(state);
    }
}
