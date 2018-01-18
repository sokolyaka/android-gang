package com.sokolov.gang.core.domain;

import android.content.Context;

import com.sokolov.gang.core.data.IDevicesRepository;
import com.sokolov.gang.core.domain.vlan.BroadcastAddress;
import com.sokolov.gang.core.domain.vlan.CurrentNetwork;
import com.sokolov.gang.core.domain.vlan.server.VLANGangServer;
import com.sokolov.gang.core.domain.wifiDirect.server.WiFiP2PGangServer;
import com.sokolov.gang.core.entity.IGangServer;
import com.sokolov.gang.core.entity.LoggableGangServer;

import java.net.SocketException;
import java.util.Arrays;

public class GangServerBuilder implements IGangServerBuilder {

    private String type;
    private String networkId;
    private IDevicesRepository devicesRepository;
    private Context context;

    @Override
    public IGangServerBuilder setContext(Context context) {
        this.context = context;
        return this;
    }

    @Override
    public IGangServerBuilder setNetworkId(String networkId) {
        this.networkId = networkId;
        return this;
    }

    @Override
    public IGangServerBuilder setType(String type) {
        this.type = type;
        return this;
    }

    @Override
    public IGangServerBuilder setDevicesRepository(IDevicesRepository devicesRepository) {
        this.devicesRepository = devicesRepository;
        return this;
    }

    @Override
    public IGangServer build() {
        if (type.equals("wifiDirect")) {
            return
                    new LoggableGangServer(
                            new WiFiP2PGangServer(
                                    context,
                                    networkId,
                                    devicesRepository));
        } else if (type.equals("VLAN")) {
            try {
                return
                        new LoggableGangServer(
                                new VLANGangServer(
                                        devicesRepository,
                                        networkId,
                                        new BroadcastAddress(
                                                new CurrentNetwork(
                                                        Arrays.asList("wlan0", "eth0")))
                                                .get(),
                                        6711,
                                        6712,
                                        256));
            } catch (SocketException e) {
                throw new RuntimeException(e);
            }
        }
        throw new IllegalArgumentException("undefined type = " + type);
    }
}
