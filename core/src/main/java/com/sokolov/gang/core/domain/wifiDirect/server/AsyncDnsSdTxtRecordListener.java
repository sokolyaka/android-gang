package com.sokolov.gang.core.domain.wifiDirect.server;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;

import java.util.Map;
import java.util.concurrent.ExecutorService;

public class AsyncDnsSdTxtRecordListener implements WifiP2pManager.DnsSdTxtRecordListener {
    private final WifiP2pManager.DnsSdTxtRecordListener origin;
    private final ExecutorService executorService;

    public AsyncDnsSdTxtRecordListener(WifiP2pManager.DnsSdTxtRecordListener origin, ExecutorService executorService) {
        this.origin = origin;
        this.executorService = executorService;
    }

    @Override
    public void onDnsSdTxtRecordAvailable(String fullDomainName, Map<String, String> txtRecordMap, WifiP2pDevice srcDevice) {
        executorService.execute(() ->
                origin.onDnsSdTxtRecordAvailable(fullDomainName, txtRecordMap, srcDevice));
    }
}
