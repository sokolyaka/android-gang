package com.sokolov.gang.core.entity;

import java.io.IOException;

public interface IGangClient {
    void start();

    void stop();

    void stopDiscover();

    void startDiscovering();

    State getState();

    interface IConnectionListener {
        void onConnected(IDevice device) throws IOException;
    }

}
