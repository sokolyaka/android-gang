package com.sokolov.gang.core.domain.wifiDirect.server;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;

import static android.net.wifi.p2p.WifiP2pManager.EXTRA_DISCOVERY_STATE;
import static android.net.wifi.p2p.WifiP2pManager.EXTRA_P2P_DEVICE_LIST;
import static android.net.wifi.p2p.WifiP2pManager.EXTRA_WIFI_P2P_DEVICE;
import static android.net.wifi.p2p.WifiP2pManager.EXTRA_WIFI_P2P_GROUP;
import static android.net.wifi.p2p.WifiP2pManager.EXTRA_WIFI_STATE;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_DISCOVERY_STOPPED;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION;

public class WiFiP2pGangServerReceiver extends BroadcastReceiver {

    private final Callback callback;
    private volatile boolean isRegistered;

    public WiFiP2pGangServerReceiver(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            callback
                    .processConnectionChangedAction(
                            (WifiP2pGroup) intent.getParcelableExtra(EXTRA_WIFI_P2P_GROUP));

        } else if (WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            callback.
                    processThisDeviceChangedAction(
                            (WifiP2pDevice) intent.getParcelableExtra(EXTRA_WIFI_P2P_DEVICE));

        } else if (WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            callback
                    .processPeersChangedAction(
                            (WifiP2pDeviceList) intent.getParcelableExtra(EXTRA_P2P_DEVICE_LIST));

        } else if (WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            callback
                    .processStateChangedAction(
                            intent.getIntExtra(EXTRA_WIFI_STATE, 0));

        } else if (WIFI_P2P_DISCOVERY_CHANGED_ACTION.equals(action)) {
            callback
                    .processDiscoveryChangedAction
                            (intent.getIntExtra(EXTRA_DISCOVERY_STATE, WIFI_P2P_DISCOVERY_STOPPED));
        }
    }

    public void register(Context context) {
        if (!isRegistered) {
            isRegistered = true;
            context.registerReceiver(this, getFilter());
        }

    }

    private static IntentFilter getFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        filter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        filter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        filter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        filter.addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION);
        return filter;
    }

    public void unregister(Context context) {
        if (isRegistered) {
            context.unregisterReceiver(this);
            isRegistered = false;
        }
    }

    public interface Callback {
        void processConnectionChangedAction(WifiP2pGroup group);

        void processThisDeviceChangedAction(WifiP2pDevice device);

        void processPeersChangedAction(WifiP2pDeviceList deviceList);

        void processStateChangedAction(int state);

        void processDiscoveryChangedAction(int state);
    }
}