package com.sokolov.gang.core.entity;

import java.net.Socket;
import java.util.Map;

public interface IDevice {

    String address();

    Map<Integer, Socket> sockets();
}
