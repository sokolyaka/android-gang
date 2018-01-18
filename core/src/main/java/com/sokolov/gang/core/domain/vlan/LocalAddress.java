package com.sokolov.gang.core.domain.vlan;


import com.sokolov.gang.core.exception.NetworkErrorException;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;

public class LocalAddress {

    private final CurrentNetwork currentNetwork;

    public LocalAddress(CurrentNetwork currentNetwork) {
        this.currentNetwork = currentNetwork;
    }

    public InetAddress get() throws NetworkErrorException, SocketException {
        NetworkInterface network = currentNetwork.get();
        for (InterfaceAddress address : network.getInterfaceAddresses()) {
            if (address.getBroadcast() == null) continue;
            return address.getAddress();
        }
        throw new NetworkErrorException();
    }
}
