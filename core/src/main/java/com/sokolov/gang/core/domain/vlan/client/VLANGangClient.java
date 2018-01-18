package com.sokolov.gang.core.domain.vlan.client;

import android.util.Log;

import com.sokolov.gang.core.domain.interaction.ClosablePingPongerCallback;
import com.sokolov.gang.core.domain.interaction.PingPonger;
import com.sokolov.gang.core.domain.interaction.socket.GangSocketConnectionWithServer;
import com.sokolov.gang.core.domain.vlan.messages.SearchServerMessage;
import com.sokolov.gang.core.entity.Device;
import com.sokolov.gang.core.entity.IGangClient;
import com.sokolov.gang.core.entity.IDevice;
import com.sokolov.gang.core.entity.SafeClosableDevice;
import com.sokolov.gang.core.entity.State;
import com.sokolov.gang.core.utils.SafeInterrupt;
import com.sokolov.gang.core.utils.SafeWait;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import static com.sokolov.gang.core.domain.Constants.MESSAGE_PING;
import static com.sokolov.gang.core.domain.interaction.socket.Ports.SERVER_PING_PONG_PORT;
import static com.sokolov.gang.core.entity.State.CONNECTED;
import static com.sokolov.gang.core.entity.State.CONNECTING;
import static com.sokolov.gang.core.entity.State.DISCOVERING;
import static com.sokolov.gang.core.entity.State.STOPPED;

public class VLANGangClient implements IGangClient {
    private static final String TAG = "VLANGangClient";

    private final String networkId;
    private final InetAddress broadcastAddress;
    private final InetAddress localAddress;
    private final int outputBroadcastPort;
    private final int inputBroadcastPort;
    private final int buffLength;
    private final IConnectionListener onConnectionRequestListener;

    private DatagramSocket outputDatagramSocket;
    private DatagramSocket inputDatagramSocket;
    private State state;
    private Thread listener;

    public VLANGangClient(
            String networkId,
            InetAddress broadcastAddress,
            InetAddress localAddress,
            int outputBroadcastPort,
            int inputBroadcastPort,
            int buffLength,
            IConnectionListener onConnectionRequestListener) {

        this.networkId = networkId;
        this.broadcastAddress = broadcastAddress;
        this.localAddress = localAddress;
        this.outputBroadcastPort = outputBroadcastPort;
        this.inputBroadcastPort = inputBroadcastPort;
        this.buffLength = buffLength;
        this.onConnectionRequestListener = onConnectionRequestListener;
    }

    @Override
    public void start() {
        try {
            outputDatagramSocket = new DatagramSocket();
            outputDatagramSocket.setBroadcast(true);

            inputDatagramSocket = new DatagramSocket(inputBroadcastPort, broadcastAddress);
            inputDatagramSocket.setBroadcast(true);
            inputDatagramSocket.setSoTimeout(2000);

            setState(STOPPED);

            listener =
                    new Thread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    IDevice server = null;
                                    try {
                                        while (!Thread.interrupted()) {
                                            try {
                                                DatagramPacket packet = new DatagramPacket(new byte[buffLength], buffLength);
                                                inputDatagramSocket.receive(packet);

                                                if (state == DISCOVERING) {
                                                    setState(CONNECTING);

                                                    String[] data = new String(packet.getData()).trim().split(":");
                                                    if (data[1].equals(localAddress.getHostName())) {
                                                        try {
                                                            server =
                                                                    new Device(
                                                                            packet.getAddress().getHostAddress(),
                                                                            new GangSocketConnectionWithServer(packet.getAddress())
                                                                                    .establish());
                                                            new Thread(
                                                                    new PingPonger(
                                                                            server.sockets().get(SERVER_PING_PONG_PORT),
                                                                            5000,
                                                                            MESSAGE_PING,
                                                                            new ClosablePingPongerCallback(
                                                                                    server.sockets().values())))
                                                                    .start();

                                                            setState(CONNECTED);
                                                            onConnectionRequestListener.onConnected(server);
                                                        } catch (Exception e) {
                                                            Log.e(TAG, "onConnectionRequestListener.onConnected()", e);
                                                            setState(DISCOVERING);
                                                        }
                                                    } else {
                                                        setState(DISCOVERING);
                                                    }
                                                }
                                            } catch (SocketTimeoutException ignored) {
                                                Log.d(TAG, "listener.run: SocketTimeoutException");
                                            }
                                        }
                                    } catch (IOException e) {
                                        Log.e(TAG, "startDiscovering.receiver.run: ", e);
                                        if (server != null) {
                                            new SafeClosableDevice(server).closeConnections();
                                        }
                                    }
                                }
                            }
                    );

            listener.start();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        setState(STOPPED);
        outputDatagramSocket.close();
        inputDatagramSocket.close();
        new SafeInterrupt(listener).interruptAndJoin();
        listener = null;
    }

    @Override
    public void stopDiscover() {
        setState(STOPPED);
    }

    @Override
    public void startDiscovering() {
        setState(DISCOVERING);
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            while (state != CONNECTED || state != STOPPED) {
                                while (state == DISCOVERING) {
                                    outputDatagramSocket.send(
                                            new SearchServerMessage(
                                                    networkId,
                                                    new InetSocketAddress(
                                                            broadcastAddress,
                                                            outputBroadcastPort))
                                                    .toDatagramPacket());
                                    new SafeWait(2000);
                                }
                                new SafeWait(2000);
                            }
                        } catch (IOException e) {
                            Log.e(TAG, "startDiscovering.sender.run: ", e);
                        }
                    }
                }
        ).start();
    }

    private void setState(State state) {
        Log.d(TAG, "setState() called with: state = [" + state + "]");
        this.state = state;
    }

    @Override
    public State getState() {
        return state;
    }
}
