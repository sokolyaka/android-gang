package com.sokolov.gang.core.domain.wifiDirect.server;

import com.sokolov.gang.core.entity.IGangServer;

public interface IWiFiP2pGangServer extends IGangServer {
    String TXTRECORD_PROP_AVAILABLE = "available";

    void preClearServices();

    void clearLocalServices();

}
