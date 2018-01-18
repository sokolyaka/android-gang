package com.sokolov.gang.core.entity;

import android.util.Log;

public class LoggableGangClient implements IGangClient {
    private static final String TAG = "LoggableGangClient";
    private final IGangClient origin;

    public LoggableGangClient(IGangClient origin) {
        this.origin = origin;
    }

    @Override
    public void start() {
        Log.d(TAG, "start() called");
        origin.start();
    }

    @Override
    public void stop() {
        Log.d(TAG, "stop() called");
        origin.stop();
    }

    @Override
    public void stopDiscover() {
        Log.d(TAG, "stopDiscover() called");
        origin.stopDiscover();
    }

    @Override
    public void startDiscovering() {
        Log.d(TAG, "startDiscovering() called");
        origin.startDiscovering();
    }

    @Override
    public State getState() {
        Log.d(TAG, "getState() called");
        return origin.getState();
    }
}
