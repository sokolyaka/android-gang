package com.sokolov.gang.core.domain;

import android.content.Context;

import com.sokolov.gang.core.domain.vlan.BroadcastAddress;
import com.sokolov.gang.core.domain.vlan.CurrentNetwork;
import com.sokolov.gang.core.domain.vlan.LocalAddress;
import com.sokolov.gang.core.domain.vlan.client.VLANGangClient;
import com.sokolov.gang.core.domain.wifiDirect.client.WiFiP2pGangClient;
import com.sokolov.gang.core.entity.IGangClient;
import com.sokolov.gang.core.entity.LoggableGangClient;

import java.net.SocketException;
import java.util.Arrays;

public class GangClientBuilder implements IGangClientBuilder {
    private String networkId;
    private IGangClient.IConnectionListener onConnectionRequestListener;
    private String type;
    private Context context;

    @Override
    public IGangClientBuilder setType(String type) {
        this.type = type;
        return this;
    }

    @Override
    public IGangClientBuilder setContext(Context context) {
        this.context = context;
        return this;
    }

    @Override
    public IGangClientBuilder setOnConnectionRequestListener(IGangClient.IConnectionListener onConnectionRequestListener) {
        this.onConnectionRequestListener = onConnectionRequestListener;
        return this;
    }

    @Override
    public IGangClientBuilder setNetworkId(String networkId) {
        this.networkId = networkId;
        return this;
    }

    @Override
    public IGangClient build() {
        if (type.equals("wifiDirect")) {
            return
                    new LoggableGangClient(
                            new WiFiP2pGangClient(
                                    context,
                                    networkId,
                                    onConnectionRequestListener));

        } else if (type.equals("VLAN")) {
            try {
                return
                        new LoggableGangClient(
                                new VLANGangClient(
                                        networkId,
                                        new BroadcastAddress(
                                                new CurrentNetwork(
                                                        Arrays.asList("wlan0", "eth0")))
                                                .get(),
                                        new LocalAddress(
                                                new CurrentNetwork(
                                                        Arrays.asList("wlan0", "eth0")))
                                                .get(),
                                        6711,
                                        6712,
                                        256,
                                        onConnectionRequestListener));
            } catch (SocketException e) {
                throw new RuntimeException(e);
            }
        }

        throw new IllegalArgumentException("undefined type = " + type);
    }
}
