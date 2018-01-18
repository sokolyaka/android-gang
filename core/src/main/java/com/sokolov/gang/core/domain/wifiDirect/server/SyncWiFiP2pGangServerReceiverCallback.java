package com.sokolov.gang.core.domain.wifiDirect.server;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;

public class SyncWiFiP2pGangServerReceiverCallback implements WiFiP2pGangServerReceiver.Callback {
    private final WiFiP2pGangServerReceiver.Callback origin;
    private final Object syncObj;

    public SyncWiFiP2pGangServerReceiverCallback(WiFiP2pGangServerReceiver.Callback origin) {
        this.origin = origin;
        syncObj = new Object();
    }

    @Override
    public void processConnectionChangedAction(WifiP2pGroup group) {
        synchronized (syncObj) {
            origin.processConnectionChangedAction(group);
        }
    }

    @Override
    public void processThisDeviceChangedAction(WifiP2pDevice device) {
        synchronized (syncObj) {
            origin.processThisDeviceChangedAction(device);
        }
    }

    @Override
    public void processPeersChangedAction(WifiP2pDeviceList deviceList) {
        synchronized (syncObj) {
            origin.processPeersChangedAction(deviceList);
        }
    }

    @Override
    public void processStateChangedAction(int state) {
        synchronized (syncObj) {
            origin.processStateChangedAction(state);
        }
    }

    @Override
    public void processDiscoveryChangedAction(int state) {
        synchronized (syncObj) {
            origin.processDiscoveryChangedAction(state);
        }
    }
}
