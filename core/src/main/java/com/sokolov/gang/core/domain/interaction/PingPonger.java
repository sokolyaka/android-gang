package com.sokolov.gang.core.domain.interaction;

import android.util.Log;

import java.io.IOException;
import java.net.Socket;

public class PingPonger implements Runnable {
    private static final String TAG = "PingPonger";

    private final Socket socket;
    private final int soTimeout;
    private final int messagePing;
    private final IPingPongerCallback callback;

    public PingPonger(Socket socket, int soTimeout, int messagePing, IPingPongerCallback callback) throws IOException {
        this.soTimeout = soTimeout;
        this.socket = socket;
        this.messagePing = messagePing;
        this.callback = callback;
    }

    @Override
    public void run() {
        try {
            socket.setSoTimeout(soTimeout);
            while (true) {
                ping();
                pong();
            }
        } catch (Exception e) {
            Log.e(TAG, "run: ", e);
            callback.onException(e);
        }
    }

    private void pong() throws IOException {
        int readPong = socket.getInputStream().read();
        if (readPong != messagePing) {
            throw new IllegalArgumentException(String.valueOf(readPong));
        }
    }

    private void ping() throws IOException {
        socket.getOutputStream().write(messagePing);
    }

    public interface IPingPongerCallback {
        void onException(Exception e);
    }


}
