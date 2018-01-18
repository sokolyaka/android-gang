package com.sokolov.gang.core.domain.interaction.socket;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;

public interface IGangSocketConnection {
    Map<Integer, Socket> establish() throws IOException;
}
