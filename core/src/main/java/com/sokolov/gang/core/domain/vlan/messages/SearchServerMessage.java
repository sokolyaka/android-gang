package com.sokolov.gang.core.domain.vlan.messages;

import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class SearchServerMessage implements IDatagramPacketMessage {
    private final String networkId;
    private final InetSocketAddress inetSocketAddress;

    public SearchServerMessage(String networkId, InetSocketAddress inetSocketAddress) {
        this.networkId = networkId;
        this.inetSocketAddress = inetSocketAddress;
    }

    @Override
    public DatagramPacket toDatagramPacket() throws SocketException {
        return new DatagramPacket(networkId.getBytes(), networkId.length(), inetSocketAddress);
    }
}
