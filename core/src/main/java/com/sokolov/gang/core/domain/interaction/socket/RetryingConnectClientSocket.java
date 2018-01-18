package com.sokolov.gang.core.domain.interaction.socket;

import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

public class RetryingConnectClientSocket implements IClientSocket {
    private static final String TAG = "RetryingConnectClientSo";
    private static final long CONNECTION_RETRY_TIMEOUT = 1000L;

    private final ClientSocketBuilder builder;
    private final int attempts;

    public RetryingConnectClientSocket(ClientSocketBuilder socketBuilder, int attempts) {
        this.builder = socketBuilder;
        this.attempts = attempts;
    }

    @Override
    public Socket connect(InetSocketAddress endpoint, int connectionTimeout) throws IOException {
        for (int attempt = 0; attempt < attempts; attempt++) {
            Socket socket = builder.build();
            try {
                socket.connect(endpoint, connectionTimeout);
                return socket;
            } catch (IOException e) {
                try {
                    socket.close();
                } catch (IOException e1) {
                    Log.e(TAG, "connect: ", e1);
                }
                waitBeforeTryAgain();
                Log.e(TAG, "connect: attempt = " + attempt + " failed", e);
            }
        }

        throw new SocketException("Connection attempts have ended.");
    }

    private void waitBeforeTryAgain() {
        synchronized (this) {
            try {
                this.wait(CONNECTION_RETRY_TIMEOUT);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
