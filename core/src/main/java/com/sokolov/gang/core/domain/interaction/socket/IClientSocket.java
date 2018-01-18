package com.sokolov.gang.core.domain.interaction.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public interface IClientSocket {

    Socket connect(InetSocketAddress endpoint, int connectionTimeout) throws IOException;
}
