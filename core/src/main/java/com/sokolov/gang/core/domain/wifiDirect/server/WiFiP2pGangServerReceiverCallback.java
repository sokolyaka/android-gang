package com.sokolov.gang.core.domain.wifiDirect.server;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.text.TextUtils;
import android.util.Log;

import com.sokolov.gang.core.data.IDevicesRepository;
import com.sokolov.gang.core.domain.interaction.PingPonger;
import com.sokolov.gang.core.domain.interaction.socket.GangSocketConnectionWithClient;
import com.sokolov.gang.core.entity.Device;
import com.sokolov.gang.core.entity.IDevice;
import com.sokolov.gang.core.entity.SafeClosableDevice;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_STATE_DISABLED;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_STATE_ENABLED;
import static com.sokolov.gang.core.domain.Constants.MESSAGE_PING;
import static com.sokolov.gang.core.domain.interaction.socket.Ports.SERVER_PING_PONG_PORT;

public class WiFiP2pGangServerReceiverCallback implements WiFiP2pGangServerReceiver.Callback {
    private static final String TAG = "WiFiP2pReceiverDC";

    private final IWiFiP2pGangServer wiFiP2pGangServer;
    private final ExecutorService executor;
    private final IDevicesRepository devicesRep;

    private int state = 0;
    private int prevState = 0;

    public WiFiP2pGangServerReceiverCallback(IWiFiP2pGangServer wiFiP2pGangServer, ExecutorService executor, IDevicesRepository devicesRep) {
        this.wiFiP2pGangServer = wiFiP2pGangServer;
        this.executor = executor;
        this.devicesRep = devicesRep;
    }

    @Override
    public void processDiscoveryChangedAction(int state) {
        //nop
    }

    @Override
    public void processStateChangedAction(int state) {
        prevState = this.state;
        this.state = state;
    }

    @Override
    public void processPeersChangedAction(WifiP2pDeviceList deviceList) {
        //nop
    }

    @Override
    public void processThisDeviceChangedAction(WifiP2pDevice device) {
        //nop
    }

    @Override
    public void processConnectionChangedAction(WifiP2pGroup group) {
        if (TextUtils.isEmpty(group.getNetworkName())
                && state == WIFI_P2P_STATE_ENABLED
                && prevState == WIFI_P2P_STATE_DISABLED) {
            wiFiP2pGangServer.preClearServices();
        }

        if (group.getClientList() != null && group.isGroupOwner()) {
            for (WifiP2pDevice wifiP2pDevice : group.getClientList()) {
                if (!devicesRep.contains(wifiP2pDevice.deviceAddress)) {
                    try {
                        final IDevice device =
                                new Device(
                                        wifiP2pDevice.deviceAddress,
                                        new GangSocketConnectionWithClient()
                                                .establish());
                        executor.execute(
                                new PingPonger(
                                        device.sockets().get(SERVER_PING_PONG_PORT),
                                        5000,
                                        MESSAGE_PING,
                                        new PingPonger.IPingPongerCallback() {
                                            @Override
                                            public void onException(Exception e) {
                                                new SafeClosableDevice(
                                                        devicesRep.delete(
                                                                device.address()))
                                                        .closeConnections();
                                            }
                                        }));

                        devicesRep.save(device);
                    } catch (IOException e) {
                        Log.e(TAG, "processConnectionChangedAction: ", e);
                    }
                }
            }
        }
    }
}
