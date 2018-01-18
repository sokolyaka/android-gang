package com.sokolov.gang.core.domain.wifiDirect.server;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;

import com.sokolov.gang.core.data.IDevicesRepository;
import com.sokolov.gang.core.domain.wifiDirect.WifiDirectCore;
import com.sokolov.gang.core.domain.wifiDirect.listeners.ChannelListener;
import com.sokolov.gang.core.domain.wifiDirect.listeners.LoggableWiFiP2pActionListener;
import com.sokolov.gang.core.domain.wifiDirect.listeners.OnSuccessWifiP2pManagerActionListener;
import com.sokolov.gang.core.entity.IDevice;
import com.sokolov.gang.core.entity.SafeClosableDevice;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class WiFiP2PGangServer implements IWiFiP2pGangServer {
    public final static String TAG = "WiFiP2PGangServer";

    private final Context context;
    private final WifiP2pManager manager;
    private final WifiP2pManager.Channel channel;
    private final WiFiP2pGangServerReceiver receiver;
    private final String networkId;
    private final IDevicesRepository devicesRep;
    private final Lock lock;
    private final ExecutorService executor;

    private WifiP2pDnsSdServiceInfo localService;
    private boolean isLocalServiceStarted;

    public WiFiP2PGangServer(Context context, String networkId, IDevicesRepository devicesRep) {
        this.context = context;
        this.networkId = networkId;
        this.devicesRep = devicesRep;
        manager = (WifiP2pManager) this.context.getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this.context, this.context.getMainLooper(), new ChannelListener());
        executor = Executors.newCachedThreadPool();
        receiver =
                new WiFiP2pGangServerReceiver(
                        new AsyncWiFiP2pGangServerReceiverCallback(
                                new SyncWiFiP2pGangServerReceiverCallback(
                                        new LoggableWiFiP2pGangServerReceiverCallback(
                                                new WiFiP2pGangServerReceiverCallback(
                                                        this,
                                                        executor,
                                                        devicesRep))),
                                executor));
        lock = new ReentrantLock();
    }

    @Override
    public void start() {
        receiver.register(context);

        Map<String, String> record = new HashMap<>();
        record.put(TXTRECORD_PROP_AVAILABLE, "visible");
        localService =
                WifiP2pDnsSdServiceInfo
                        .newInstance(
                                WifiDirectCore.SERVICE_INSTANCE_SERVER + "_" + networkId,
                                WifiDirectCore.SERVICE_REG_TYPE,
                                record);

        clearManagerCallbacks();
        preClearServices();
    }

    @Override
    public void enableNewConnections(boolean isEnabled) {
        if (isEnabled) {
            addLocalService(false);
        } else {
            clearLocalServices();
        }
    }

    /**
     * <p>Initial waypoint in WiFi - Direct workflow.<br>
     * The very first thing we need to do is to clear possibly registered services to avid any <br>
     * unexpected circumstance.
     * If services are successfully cleared, then {@link WiFiP2PGangServer#addLocalService} is called.
     * </p>
     */
    @Override
    public void preClearServices() {
        manager.clearLocalServices(
                channel,
                new OnSuccessWifiP2pManagerActionListener() {

                    @Override
                    public void onSuccess() {
                        isLocalServiceStarted = false;
                        addLocalService(true);
                    }
                });
    }

    /**
     * <p>
     * Registers instance of {@link WifiP2pDnsSdServiceInfo} into WiFi-Direct framework.<br>
     *
     * @param prepareGroup if group preparation required.
     */
    private void addLocalService(final boolean prepareGroup) {
        if (isLocalServiceStarted) {
            return;
        }
        if (localService == null) {
            return;
        }
        manager.addLocalService(
                channel,
                localService,
                new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        isLocalServiceStarted = true;
                        if (prepareGroup) {
                            prepareGroup();
                        }
                    }

                    @Override
                    public void onFailure(int reason) {
                        preClearServices();
                    }
                });
    }

    private void prepareGroup() {
        manager.requestGroupInfo(
                channel,
                new WifiP2pManager.GroupInfoListener() {

                    @Override
                    public void onGroupInfoAvailable(WifiP2pGroup group) {
                        if (group != null) {
                            manager.removeGroup(
                                    channel,
                                    new OnSuccessWifiP2pManagerActionListener() {

                                        @Override
                                        public void onSuccess() {
                                            createGroup();
                                        }
                                    });
                        } else {
                            createGroup();
                        }
                    }
                });
    }

    private void createGroup() {
        manager.createGroup(
                channel,
                new WifiP2pManager.ActionListener() {

                    @Override
                    public void onSuccess() {
                        receiver.register(context);
                    }

                    @Override
                    public void onFailure(int reason) {
                        if (reason == 2) {
                            manager.removeGroup(
                                    channel,
                                    new WifiP2pManager.ActionListener() {

                                        @Override
                                        public void onSuccess() {
                                            manager.clearLocalServices(
                                                    channel,
                                                    new WifiP2pManager.ActionListener() {

                                                        @Override
                                                        public void onSuccess() {
                                                            isLocalServiceStarted = false;
                                                            addLocalService(true);
                                                        }

                                                        @Override
                                                        public void onFailure(int reason) {
                                                            preClearServices();
                                                        }
                                                    });
                                        }

                                        @Override
                                        public void onFailure(int reason) {
                                            preClearServices();
                                        }
                                    });
                        }
                    }
                });
    }

    private void clearManagerCallbacks() {
        if (channel == null) {
            return;
        }
        manager.clearLocalServices(channel, null);
        manager.requestGroupInfo(channel, null);
        manager.removeGroup(channel, null);
        manager.createGroup(channel, null);

        if (localService != null) {
            manager.addLocalService(channel, localService, null);
        }
    }

    private void unregisterReceiver() {
        if (context != null) {
            try {
                lock.lock();
                if (context != null) {
                    receiver.unregister(context);
                }
            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    public void stop() {
        localService = null;
        manager.cancelConnect(channel, new LoggableWiFiP2pActionListener("cancelConnect"));
        clearLocalServices();
        unregisterReceiver();
        executor.shutdown();
        for (IDevice device : devicesRep.getAll()) {
            new SafeClosableDevice(device).closeConnections();
        }
    }

    /**
     * Clears registered in WiFi-Direct framework instances of {@link WifiP2pDnsSdServiceInfo}.
     */
    @Override
    public void clearLocalServices() {
        manager.clearLocalServices(
                channel,
                new OnSuccessWifiP2pManagerActionListener() {
                    @Override
                    public void onSuccess() {
                        isLocalServiceStarted = false;
                    }
                });
    }

}