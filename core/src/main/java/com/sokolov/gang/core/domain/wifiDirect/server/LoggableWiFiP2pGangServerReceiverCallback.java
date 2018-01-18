package com.sokolov.gang.core.domain.wifiDirect.server;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.util.Log;

public class LoggableWiFiP2pGangServerReceiverCallback implements WiFiP2pGangServerReceiver.Callback {
    private static final String TAG = "LoggableWiFiP2pDRC";
    private final WiFiP2pGangServerReceiver.Callback origin;

    public LoggableWiFiP2pGangServerReceiverCallback(WiFiP2pGangServerReceiver.Callback origin) {
        this.origin = origin;
    }

    @Override
    public void processConnectionChangedAction(WifiP2pGroup group) {
        Log.d(TAG, "processConnectionChangedAction() called with: group = [" + group + "]");
        origin.processConnectionChangedAction(group);
    }

    @Override
    public void processThisDeviceChangedAction(WifiP2pDevice device) {
        Log.d(TAG, "processThisDeviceChangedAction() called with: device = [" + device + "]");
        origin.processThisDeviceChangedAction(device);
    }

    @Override
    public void processPeersChangedAction(WifiP2pDeviceList deviceList) {
        Log.d(TAG, "processPeersChangedAction() called with: deviceList = [" + deviceList + "]");
        origin.processPeersChangedAction(deviceList);
    }

    @Override
    public void processStateChangedAction(int state) {
        Log.d(TAG, "processStateChangedAction() called with: state = [" + state + "]");
        origin.processStateChangedAction(state);
    }

    @Override
    public void processDiscoveryChangedAction(int state) {
        Log.d(TAG, "processDiscoveryChangedAction() called with: state = [" + state + "]");
        origin.processDiscoveryChangedAction(state);
    }
}
