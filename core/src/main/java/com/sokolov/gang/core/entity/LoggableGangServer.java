package com.sokolov.gang.core.entity;

import android.util.Log;

public class LoggableGangServer implements IGangServer {
    private static final String TAG = "LoggableGangServer";
    private final IGangServer origin;

    public LoggableGangServer(IGangServer origin) {
        this.origin = origin;
    }

    @Override
    public void stop() {
        Log.d(TAG, "stop() called");
        origin.stop();
    }

    @Override
    public void start() {
        Log.d(TAG, "start() called");
        origin.start();
    }

    @Override
    public void enableNewConnections(boolean isEnabled) {
        Log.d(TAG, "enableNewConnections() called with: isEnabled = [" + isEnabled + "]");
        origin.enableNewConnections(isEnabled);
    }

}
