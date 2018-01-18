package com.sokolov.gang.core.domain.wifiDirect.server;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import java.util.Map;

public class LoggableDnsSdTxtRecordListener implements WifiP2pManager.DnsSdTxtRecordListener {
    private static final String TAG = "LoggableDnsSdTxtRecordL";
    private final WifiP2pManager.DnsSdTxtRecordListener origin;

    public LoggableDnsSdTxtRecordListener(WifiP2pManager.DnsSdTxtRecordListener origin) {
        this.origin = origin;
    }

    @Override
    public void onDnsSdTxtRecordAvailable(String fullDomainName, Map<String, String> txtRecordMap, WifiP2pDevice srcDevice) {
        Log.d(TAG, "onDnsSdTxtRecordAvailable() called with: fullDomainName = [" + fullDomainName + "], txtRecordMap = [" + txtRecordMap + "], srcDevice = [" + srcDevice + "]");
        origin.onDnsSdTxtRecordAvailable(fullDomainName, txtRecordMap, srcDevice);
    }
}
