package com.sokolov.gang.core.domain.wifiDirect.server;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;

import java.util.Map;

public class DisposableDnsSdTxtRecordListener implements WifiP2pManager.DnsSdTxtRecordListener {
    private final WifiP2pManager.DnsSdTxtRecordListener origin;
    private int count;

    public DisposableDnsSdTxtRecordListener(WifiP2pManager.DnsSdTxtRecordListener origin) {
        this.origin = origin;
        count = 0;
    }

    @Override
    public void onDnsSdTxtRecordAvailable(String fullDomainName, Map<String, String> txtRecordMap, WifiP2pDevice srcDevice) {
        if (++count > 1) {
            return;
        }
        origin.onDnsSdTxtRecordAvailable(fullDomainName, txtRecordMap, srcDevice);
    }
}
