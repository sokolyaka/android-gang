package com.sokolov.gang.core.domain.interaction;

import java.io.IOException;
import java.net.Socket;
import java.util.Collection;

public class ClosablePingPongerCallback implements PingPonger.IPingPongerCallback {
    private final Collection<Socket> sockets;

    public ClosablePingPongerCallback(Collection<Socket> sockets) {
        this.sockets = sockets;
    }

    @Override
    public void onException(Exception e) {
        for (Socket socket : sockets) {
            try {
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}
