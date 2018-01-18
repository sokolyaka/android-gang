package com.sokolov.gang.core.entity;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;


public class SafeClosableDevice implements IDevice {
    private final IDevice origin;

    public SafeClosableDevice(IDevice origin) {
        this.origin = origin;
    }

    @Override
    public String address() {
        return origin.address();
    }

    @Override
    public Map<Integer, Socket> sockets() {
        return origin.sockets();
    }

    public void closeConnections() {
        for (Socket socket : sockets().values()) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
