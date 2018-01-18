package com.sokolov.gang.core.domain.vlan.messages;

public class FromClientMessage implements IReceivedMessage {
    private final IReceivedMessage origin;

    public FromClientMessage(IReceivedMessage origin) {
        this.origin = origin;
    }

    public String networkId() {
        return message().trim();
    }

    @Override
    public String hostAddress() {
        return origin.hostAddress();
    }

    @Override
    public String message() {
        return origin.message();
    }
}
