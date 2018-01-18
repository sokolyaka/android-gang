package com.sokolov.gang.core.domain.wifiDirect.client;

import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.util.Log;

import com.sokolov.gang.core.domain.interaction.ClosablePingPongerCallback;
import com.sokolov.gang.core.domain.interaction.PingPonger;
import com.sokolov.gang.core.domain.interaction.socket.GangSocketConnectionWithServer;
import com.sokolov.gang.core.entity.Device;
import com.sokolov.gang.core.entity.IGangClient;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

import static com.sokolov.gang.core.domain.Constants.MESSAGE_PING;
import static com.sokolov.gang.core.domain.interaction.socket.Ports.SERVER_PING_PONG_PORT;

public class WiFiP2pGangClientReceiverCallback implements WiFiP2pGangClientReceiver.Callback {
    private static final String TAG = "WiFiP2pReceiverAC";

    private final IGangClient gangClient;
    private final IGangClient.IConnectionListener connectionRequestListener;
    private final ExecutorService executor;

    public WiFiP2pGangClientReceiverCallback(
            IGangClient gangClient,
            IGangClient.IConnectionListener connectionRequestListener,
            ExecutorService executor) {
        this.gangClient = gangClient;
        this.connectionRequestListener = connectionRequestListener;
        this.executor = executor;
    }

    @Override
    public void processConnectionChangedAction(NetworkInfo networkInfo, WifiP2pInfo wifiP2pInfo, WifiP2pGroup group) {
        if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
            try {
                gangClient.stopDiscover();
                Device server =
                        new Device(
                                wifiP2pInfo.groupOwnerAddress.getHostAddress(),
                                new GangSocketConnectionWithServer(wifiP2pInfo.groupOwnerAddress)
                                        .establish());
                executor.submit(
                        new PingPonger(
                                server.sockets().get(SERVER_PING_PONG_PORT),
                                5000,
                                MESSAGE_PING,
                                new ClosablePingPongerCallback(
                                        server.sockets().values())));

                connectionRequestListener.onConnected(server);
            } catch (IOException e) {
                Log.e(TAG, "processConnectionChangedAction: ", e);
                gangClient.startDiscovering();
            }
        }
    }

    @Override
    public void processThisDeviceChangedAction(WifiP2pDevice device) {
        //nop
    }


    @Override
    public void processStateChangedAction(int state) {
        //nop
    }

}
