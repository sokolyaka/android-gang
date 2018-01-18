package com.sokolov.gang.core.domain.wifiDirect.client;

import android.content.Context;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.util.Log;

import com.sokolov.gang.core.domain.wifiDirect.WifiDirectCore;
import com.sokolov.gang.core.domain.wifiDirect.listeners.ChannelListener;
import com.sokolov.gang.core.domain.wifiDirect.listeners.LoggableWiFiP2pActionListener;
import com.sokolov.gang.core.domain.wifiDirect.listeners.OnSuccessWifiP2pManagerActionListener;
import com.sokolov.gang.core.domain.wifiDirect.server.AsyncDnsSdTxtRecordListener;
import com.sokolov.gang.core.domain.wifiDirect.server.DisposableDnsSdTxtRecordListener;
import com.sokolov.gang.core.domain.wifiDirect.server.LoggableDnsSdTxtRecordListener;
import com.sokolov.gang.core.entity.IGangClient;
import com.sokolov.gang.core.entity.IDevice;
import com.sokolov.gang.core.entity.SafeClosableDevice;
import com.sokolov.gang.core.entity.State;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.sokolov.gang.core.entity.State.CONNECTED;
import static com.sokolov.gang.core.entity.State.CONNECTING;
import static com.sokolov.gang.core.entity.State.DISCOVERING;
import static com.sokolov.gang.core.entity.State.STOPPED;

public class WiFiP2pGangClient implements IGangClient {
    private static final String TAG = "WiFiP2pGangClient";

    private final Context context;
    private final WifiP2pManager manager;
    private final WifiP2pManager.Channel channel;
    private final WiFiP2pGangClientReceiver receiver;
    private final WifiP2pDnsSdServiceRequest serviceRequest;
    private final ExecutorService executor;
    private final Set<Future> futures;

    private IDevice server;
    private State state;

    public WiFiP2pGangClient(Context context, String networkId, IConnectionListener onConnectionRequestListener) {
        this.context = context;
        manager = (WifiP2pManager) this.context.getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this.context, this.context.getMainLooper(), new ChannelListener());
        serviceRequest =
                WifiP2pDnsSdServiceRequest
                        .newInstance(
                                WifiDirectCore.SERVICE_INSTANCE_SERVER + "_" + networkId,
                                WifiDirectCore.SERVICE_REG_TYPE);
        state = STOPPED;
        executor = Executors.newCachedThreadPool();
        futures = new HashSet<>();

        receiver =
                new WiFiP2pGangClientReceiver(
                        new AsyncWiFiP2pGangClientReceiverCallback(
                                new SyncWiFiP2pGangClientReceiverCallback(
                                        new LoggableWiFiP2pGangClientReceiverCallback(
                                                new WiFiP2pGangClientReceiverCallback(
                                                        this,
                                                        device -> {
                                                            this.server = device;
                                                            setIsConnected();
                                                            onConnectionRequestListener.onConnected(device);
                                                        },
                                                        executor))),
                                executor));
    }

    @Override
    public void start() {
        clearManagerCallbacks();

        if (!receiver.isRegistered()) {
            receiver.register(context);
        }
    }

    @Override
    public void stop() {
        Log.d(TAG, "stop() called");
        for (Future future : futures) {
            future.cancel(true);
        }
        futures.clear();
        receiver.unregister(context);
        clearManagerCallbacks();
        executor.shutdown();
        if (server != null) {
            new SafeClosableDevice(server).closeConnections();
        }
        state = STOPPED;
    }

    private void clearManagerCallbacks() {
        Log.d(TAG, "clearManagerCallbacks");
        if (channel == null) {
            return;
        }
        manager.removeGroup(channel, new LoggableWiFiP2pActionListener("removeGroup"));
        manager.cancelConnect(channel, new LoggableWiFiP2pActionListener("cancelConnect"));
        manager.setDnsSdResponseListeners(channel, null, null);
        manager.discoverServices(channel, new LoggableWiFiP2pActionListener("clear discoverServices"));
        manager.requestGroupInfo(channel, null);
        manager.addServiceRequest(channel, serviceRequest, null);
        manager.removeServiceRequest(channel, serviceRequest, null);
    }

    @Override
    public void stopDiscover() {
        Log.d(TAG, "stopDiscover() called");
        manager.setDnsSdResponseListeners(channel, null, null);
        manager.removeServiceRequest(
                channel,
                serviceRequest,
                new LoggableWiFiP2pActionListener("removeServiceRequest"));

    }

    @Override
    public void startDiscovering() {
        Log.d(TAG, "startDiscovering() called");
        manager.setDnsSdResponseListeners(
                channel,
                null,
                new AsyncDnsSdTxtRecordListener(
                        new DisposableDnsSdTxtRecordListener(
                                new LoggableDnsSdTxtRecordListener(
                                        (fullDomainName, txtRecordMap, srcDevice) -> {
                                            synchronized (WiFiP2pGangClient.this) {
                                                if (state == DISCOVERING) {
                                                    stopDiscover();
                                                    state = CONNECTING;
                                                    connect(srcDevice.deviceAddress);
                                                }
                                            }
                                        })),
                        Executors.newSingleThreadExecutor()));

        manager.addServiceRequest(
                channel,
                serviceRequest,
                new LoggableWiFiP2pActionListener(
                        new OnSuccessWifiP2pManagerActionListener() {

                            @Override
                            public void onSuccess() {
                                state = DISCOVERING;
                                futures.add(
                                        executor.submit(() -> {
                                            try {
                                                synchronized (this) {
                                                    while (state == DISCOVERING) {
                                                        wait(2000);
                                                        manager.discoverServices(
                                                                channel,
                                                                new LoggableWiFiP2pActionListener(
                                                                        "discoverServices"));
                                                    }
                                                }
                                            } catch (InterruptedException e) {
                                                Log.e(TAG, "addServiceRequest: ", e);
                                            }
                                        }));
                            }
                        },
                        "addServiceRequest"));
    }

    @Override
    public State getState() {
        return state;
    }

    private void connect(final String address) {
        final WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = address;
        config.wps.setup = WpsInfo.PBC;

        manager.connect(
                channel,
                config,
                new LoggableWiFiP2pActionListener(
                        new WifiP2pManager.ActionListener() {

                            @Override
                            public void onSuccess() {
                                futures.add(
                                        executor.submit(() -> {
                                            try {
                                                synchronized (this) {
                                                    wait(10000);
                                                    if (state == CONNECTING) {
                                                        startDiscovering();
                                                    }
                                                }
                                            } catch (InterruptedException e) {
                                                Log.e(TAG, "run: ", e);
                                            }
                                        }));
                            }

                            @Override
                            public void onFailure(int errorCode) {
                                if (state == CONNECTING) {
                                    startDiscovering();
                                }
                            }
                        },
                        "connect"));

    }

    private void setIsConnected() {
        state = CONNECTED;
        for (Future future : futures) {
            future.cancel(true);
        }
        futures.clear();
    }
}