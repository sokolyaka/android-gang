package com.sokolov.gang.core.domain.vlan.messages;

import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class ConnectMessage implements IDatagramPacketMessage {
    private final String senderIP;
    private final InetSocketAddress address;

    public ConnectMessage(String senderIP, InetSocketAddress address) {
        this.senderIP = senderIP;
        this.address = address;
    }

    @Override
    public DatagramPacket toDatagramPacket() throws SocketException {
        StringBuilder sb = new StringBuilder();
        sb.append("CONNECT").append(":").append(senderIP);
        return
                new DatagramPacket(
                        sb.toString().getBytes(),
                        sb.length(),
                        address);
    }
}
