package com.sokolov.gang.core.domain.vlan.messages;

import java.net.DatagramPacket;

public class DatagramPacketMessage implements IReceivedMessage {
    private final DatagramPacket packet;

    public DatagramPacketMessage(DatagramPacket packet) {
        this.packet = packet;
    }

    @Override
    public String hostAddress() {
        return packet.getAddress().getHostAddress();
    }

    @Override
    public String message() {
        return new String(packet.getData());
    }

}
