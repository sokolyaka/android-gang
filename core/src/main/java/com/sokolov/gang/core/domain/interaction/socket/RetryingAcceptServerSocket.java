package com.sokolov.gang.core.domain.interaction.socket;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class RetryingAcceptServerSocket implements IServerSocket {
    private static final String TAG = "RetryingAcceptServerSoc";

    private final ServerSocket serverSocket;
    private final int attempts;

    public RetryingAcceptServerSocket(ServerSocket serverSocket, int attempts) {
        this.serverSocket = serverSocket;
        this.attempts = attempts;
    }

    @Override
    public Socket accept() throws IOException {
        for (int i = 0; i < attempts; i++) {
            try {
                return serverSocket.accept();
            } catch (IOException e) {
                Log.e(TAG, "accept: attempt = " + i + " failed", e);
            }
        }
        throw new SocketException("accept: attempts have ended");
    }
}
