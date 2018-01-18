package com.sokolov.gang.core.domain.interaction.socket;

import java.io.IOException;
import java.net.Socket;

public interface IServerSocket {

    Socket accept() throws IOException;
}
