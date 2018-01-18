package com.sokolov.gang.core.domain.vlan.messages;

import java.net.DatagramPacket;
import java.net.SocketException;

public interface IDatagramPacketMessage {
    DatagramPacket toDatagramPacket() throws SocketException;
}
