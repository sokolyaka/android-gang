package com.sokolov.gang.core.entity;

public interface IGangServer {
    void stop();

    void start();

    void enableNewConnections(boolean isEnabled);

}
