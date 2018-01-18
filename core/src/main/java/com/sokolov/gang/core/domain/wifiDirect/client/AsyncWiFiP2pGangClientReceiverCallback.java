package com.sokolov.gang.core.domain.wifiDirect.client;

import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;

import java.util.concurrent.ExecutorService;

public class AsyncWiFiP2pGangClientReceiverCallback implements WiFiP2pGangClientReceiver.Callback {
    private final WiFiP2pGangClientReceiver.Callback origin;
    private final ExecutorService executorService;

    public AsyncWiFiP2pGangClientReceiverCallback(WiFiP2pGangClientReceiver.Callback origin, ExecutorService executorService) {
        this.origin = origin;
        this.executorService = executorService;
    }

    @Override
    public void processConnectionChangedAction(final NetworkInfo networkInfo, final WifiP2pInfo wifiP2pInfo, final WifiP2pGroup group) {
        executorService
                .execute(
                        new Runnable() {
                            @Override
                            public void run() {
                                origin.processConnectionChangedAction(networkInfo, wifiP2pInfo, group);
                            }
                        });
    }

    @Override
    public void processThisDeviceChangedAction(final WifiP2pDevice device) {
        executorService
                .execute(
                        new Runnable() {
                            @Override
                            public void run() {
                                origin.processThisDeviceChangedAction(device);
                            }
                        });
    }

    @Override
    public void processStateChangedAction(final int state) {
        executorService
                .execute(
                        new Runnable() {
                            @Override
                            public void run() {
                                origin.processStateChangedAction(state);
                            }
                        });
    }
}
