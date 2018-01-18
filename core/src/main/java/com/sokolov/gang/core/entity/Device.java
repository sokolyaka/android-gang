package com.sokolov.gang.core.entity;

import java.net.Socket;
import java.util.Map;

public class Device implements IDevice {
    private final String address;
    private final Map<Integer, Socket> sockets;

    public Device(String address, Map<Integer, Socket> sockets) {
        this.address = address;
        this.sockets = sockets;
    }

    @Override
    public String address() {
        return address;
    }

    @Override
    public Map<Integer, Socket> sockets() {
        return sockets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Device that = (Device) o;

        return address != null ? address.equals(that.address) : that.address == null;
    }

    @Override
    public int hashCode() {
        return address != null ? address.hashCode() : 0;
    }
}
