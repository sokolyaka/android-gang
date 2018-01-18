package com.sokolov.gang.core.domain.interaction.socket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import static com.sokolov.gang.core.domain.interaction.socket.Ports.SERVER_COMMAND_PORT;
import static com.sokolov.gang.core.domain.interaction.socket.Ports.SERVER_PING_PONG_PORT;

public class GangSocketConnectionWithServer implements IGangSocketConnection {
    private static final int CONNECTION_TIMEOUT = 1000;

    private final InetAddress inetAddress;

    public GangSocketConnectionWithServer(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }

    @Override
    public Map<Integer, Socket> establish() throws IOException {
        Map<Integer, Socket> connectedSockets = new HashMap<>();
        int[] portsToConnect = new int[]{SERVER_PING_PONG_PORT, SERVER_COMMAND_PORT};

        try {
            for (int port : portsToConnect) {
                connectedSockets.put(
                        port,
                        new RetryingConnectClientSocket(
                                new ClientSocketBuilder()
                                        .setReuseAddress(true),
                                3)
                                .connect(
                                        new InetSocketAddress(
                                                inetAddress,
                                                port),
                                        CONNECTION_TIMEOUT));
            }
        } catch (IOException e) {
            for (Socket socket : connectedSockets.values()) {
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            throw e;
        }
        return connectedSockets;
    }
}
