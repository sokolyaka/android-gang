package com.sokolov.gang.core.domain.wifiDirect.client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import static android.net.wifi.p2p.WifiP2pManager.EXTRA_NETWORK_INFO;
import static android.net.wifi.p2p.WifiP2pManager.EXTRA_WIFI_P2P_DEVICE;
import static android.net.wifi.p2p.WifiP2pManager.EXTRA_WIFI_P2P_GROUP;
import static android.net.wifi.p2p.WifiP2pManager.EXTRA_WIFI_P2P_INFO;
import static android.net.wifi.p2p.WifiP2pManager.EXTRA_WIFI_STATE;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION;

public class WiFiP2pGangClientReceiver extends BroadcastReceiver {
    private static final String TAG = "WiFiP2pGangClientReceiv";

    private final Callback callback;
    private volatile boolean isRegistered;

    public WiFiP2pGangClientReceiver(Callback callback) {
        super();
        this.callback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            callback
                    .processConnectionChangedAction(
                            (NetworkInfo) intent.getParcelableExtra(EXTRA_NETWORK_INFO),
                            (WifiP2pInfo) intent.getParcelableExtra(EXTRA_WIFI_P2P_INFO),
                            (WifiP2pGroup) intent.getParcelableExtra(EXTRA_WIFI_P2P_GROUP));

        } else if (WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            callback
                    .processThisDeviceChangedAction(
                            (WifiP2pDevice) intent.getParcelableExtra(EXTRA_WIFI_P2P_DEVICE));

        } else if (WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            callback
                    .processStateChangedAction(
                            intent.getIntExtra(EXTRA_WIFI_STATE, 0));
        }
    }


    public void register(Context context) {
        try {
            context.registerReceiver(this, getIntentFilter());
            isRegistered = true;
        } catch (Exception e) {
            Log.e(TAG, "error unregistering receiver");
        }
    }

    private IntentFilter getIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        return intentFilter;
    }

    public void unregister(Context context) {
        try {
            context.unregisterReceiver(this);
            isRegistered = false;
        } catch (Exception e) {
            Log.e(TAG, "error unregistering receiver");
        }
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    public interface Callback {
        void processConnectionChangedAction(NetworkInfo networkInfo, WifiP2pInfo wifiP2pInfo, WifiP2pGroup group);

        void processThisDeviceChangedAction(WifiP2pDevice device);

        void processStateChangedAction(int state);
    }
}

