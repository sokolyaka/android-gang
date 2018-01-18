package com.sokolov.gang.core.domain.interaction.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import static com.sokolov.gang.core.domain.interaction.socket.Ports.SERVER_COMMAND_PORT;
import static com.sokolov.gang.core.domain.interaction.socket.Ports.SERVER_PING_PONG_PORT;

public class GangSocketConnectionWithClient implements IGangSocketConnection {
    private final static int SOCKET_TIMEOUT_LONG = 1000;

    @Override
    public Map<Integer, Socket> establish() throws IOException {
        Map<Integer, Socket> connectedSockets = new HashMap<>();
        Map<Integer, ServerSocket> connectedServerSockets = new HashMap<>();

        int[] portsToConnect = new int[]{SERVER_PING_PONG_PORT, SERVER_COMMAND_PORT};

        try {
            for (int port : portsToConnect) {
                connectedServerSockets
                        .put(
                                port,
                                new ServerSocketBuilder(port)
                                        .setReuseAddress(true)
                                        .setSoTimeout(5 * SOCKET_TIMEOUT_LONG)
                                        .build());

                connectedSockets
                        .put(
                                port,
                                new RetryingAcceptServerSocket(
                                        connectedServerSockets
                                                .get(port),
                                        2)
                                        .accept());
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
        } finally {
            for (ServerSocket serverSocket : connectedServerSockets.values()) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return connectedSockets;
    }
}
