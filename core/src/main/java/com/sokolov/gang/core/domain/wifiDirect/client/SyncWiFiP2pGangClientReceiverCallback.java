package com.sokolov.gang.core.domain.wifiDirect.client;

import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;

public class SyncWiFiP2pGangClientReceiverCallback implements WiFiP2pGangClientReceiver.Callback {
    private final WiFiP2pGangClientReceiver.Callback origin;
    private final Object syncObj;

    public SyncWiFiP2pGangClientReceiverCallback(WiFiP2pGangClientReceiver.Callback origin) {
        this.origin = origin;
        syncObj = new Object();
    }

    @Override
    public void processConnectionChangedAction(NetworkInfo networkInfo, WifiP2pInfo wifiP2pInfo, WifiP2pGroup group) {
        synchronized (syncObj) {
            origin.processConnectionChangedAction(networkInfo, wifiP2pInfo, group);
        }
    }

    @Override
    public void processThisDeviceChangedAction(WifiP2pDevice device) {
        synchronized (syncObj) {
            origin.processThisDeviceChangedAction(device);
        }
    }

    @Override
    public void processStateChangedAction(int state) {
        synchronized (syncObj) {
            origin.processStateChangedAction(state);
        }
    }
}
