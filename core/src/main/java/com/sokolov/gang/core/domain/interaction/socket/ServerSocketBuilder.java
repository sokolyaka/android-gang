package com.sokolov.gang.core.domain.interaction.socket;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerSocketBuilder {
    private final int port;
    private boolean isReuseAddress;
    private int timeoutMs;

    public ServerSocketBuilder(int port) {
        this.port = port;
    }

    public ServerSocketBuilder setReuseAddress(boolean reuseAddress) {
        isReuseAddress = reuseAddress;
        return this;
    }

    public ServerSocketBuilder setSoTimeout(int timeoutMs) {
        this.timeoutMs = timeoutMs;
        return this;
    }

    public ServerSocket build() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        serverSocket.setReuseAddress(isReuseAddress);
        serverSocket.setSoTimeout(timeoutMs);
        return serverSocket;
    }
}
