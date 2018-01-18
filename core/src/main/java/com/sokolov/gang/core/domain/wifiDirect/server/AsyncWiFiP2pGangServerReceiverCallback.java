package com.sokolov.gang.core.domain.wifiDirect.server;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;

import java.util.concurrent.ExecutorService;

public class AsyncWiFiP2pGangServerReceiverCallback implements WiFiP2pGangServerReceiver.Callback {
    private final WiFiP2pGangServerReceiver.Callback origin;
    private final ExecutorService executorService;

    public AsyncWiFiP2pGangServerReceiverCallback(WiFiP2pGangServerReceiver.Callback origin, ExecutorService executorService) {
        this.origin = origin;
        this.executorService = executorService;
    }

    @Override
    public void processConnectionChangedAction(final WifiP2pGroup group) {
        executorService.execute(
                new Runnable() {
                    @Override
                    public void run() {
                        origin.processConnectionChangedAction(group);
                    }
                }
        );
    }

    @Override
    public void processThisDeviceChangedAction(final WifiP2pDevice device) {
        executorService.execute(
                new Runnable() {
                    @Override
                    public void run() {
                        origin.processThisDeviceChangedAction(device);
                    }
                }
        );
    }

    @Override
    public void processPeersChangedAction(final WifiP2pDeviceList deviceList) {
        executorService.execute(
                new Runnable() {
                    @Override
                    public void run() {
                        origin.processPeersChangedAction(deviceList);
                    }
                }
        );
    }

    @Override
    public void processStateChangedAction(final int state) {
        executorService.execute(
                new Runnable() {
                    @Override
                    public void run() {
                        origin.processStateChangedAction(state);
                    }
                }
        );
    }

    @Override
    public void processDiscoveryChangedAction(final int state) {
        executorService.execute(
                new Runnable() {
                    @Override
                    public void run() {
                        origin.processDiscoveryChangedAction(state);
                    }
                }
        );
    }
}
