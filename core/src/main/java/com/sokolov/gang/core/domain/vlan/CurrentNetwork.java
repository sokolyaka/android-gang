package com.sokolov.gang.core.domain.vlan;


import com.sokolov.gang.core.exception.NetworkErrorException;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.List;

public class CurrentNetwork {
    private final List<String> networks;

    public CurrentNetwork(List<String> networks) {
        this.networks = networks;
    }

    public NetworkInterface get() throws SocketException, NetworkErrorException {
        for (String name : networks) {
            NetworkInterface network = NetworkInterface.getByName(name);
            if (network.getInterfaceAddresses().isEmpty()) continue;
            return network;
        }
        throw new NetworkErrorException("No network available");
    }
}
