package com.sokolov.gang.core.domain.interaction.socket;

import java.io.IOException;
import java.net.Socket;

public class ClientSocketBuilder {

    private boolean isReuseAddress;

    public ClientSocketBuilder setReuseAddress(boolean reuseAddress) {
        isReuseAddress = reuseAddress;
        return this;
    }

    public Socket build() throws IOException {
        Socket socket = new Socket();
        socket.setReuseAddress(isReuseAddress);
        return socket;
    }
}
