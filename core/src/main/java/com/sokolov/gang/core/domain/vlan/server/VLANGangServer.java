package com.sokolov.gang.core.domain.vlan.server;

import android.util.Log;

import com.sokolov.gang.core.data.IDevicesRepository;
import com.sokolov.gang.core.domain.interaction.PingPonger;
import com.sokolov.gang.core.domain.interaction.socket.GangSocketConnectionWithClient;
import com.sokolov.gang.core.domain.vlan.messages.ConnectMessage;
import com.sokolov.gang.core.domain.vlan.messages.DatagramPacketMessage;
import com.sokolov.gang.core.domain.vlan.messages.FromClientMessage;
import com.sokolov.gang.core.entity.Device;
import com.sokolov.gang.core.entity.IDevice;
import com.sokolov.gang.core.entity.IGangServer;
import com.sokolov.gang.core.entity.SafeClosableDevice;
import com.sokolov.gang.core.entity.State;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.sokolov.gang.core.domain.Constants.MESSAGE_PING;
import static com.sokolov.gang.core.domain.interaction.socket.Ports.SERVER_PING_PONG_PORT;


public class VLANGangServer implements IGangServer {
    private static final String TAG = "VLANGangServer";

    private final IDevicesRepository devicesRep;
    private final String networkId;
    private final InetAddress broadcastAddress;
    private final int inputBroadcastPort;
    private final int outputBroadcastPort;
    private final int buffLength;

    private final ExecutorService executor;

    private DatagramSocket inputDatagramSocket;
    private DatagramSocket outputDatagramSocket;

    private Thread listeningWorker;
    private State state;

    public VLANGangServer(
            IDevicesRepository devicesRep,
            String networkId,
            InetAddress broadcastAddress,
            int inputBroadcastPort,
            int outputBroadcastPort,
            int buffLength) {

        this.devicesRep = devicesRep;
        this.networkId = networkId;
        this.broadcastAddress = broadcastAddress;
        this.inputBroadcastPort = inputBroadcastPort;
        this.outputBroadcastPort = outputBroadcastPort;
        this.buffLength = buffLength;
        executor = Executors.newCachedThreadPool();
    }


    @Override
    public void stop() {
        inputDatagramSocket.close();
        outputDatagramSocket.close();
        listeningWorker.interrupt(); // TODO: 15.12.2017 return SafeInterrupt
        listeningWorker = null;
        executor.shutdownNow();
        for (IDevice device : devicesRep.getAll()) {
            new SafeClosableDevice(device).closeConnections();
        }
    }

    @Override
    public void start() {
        try {
            inputDatagramSocket = new DatagramSocket(inputBroadcastPort, broadcastAddress);
            inputDatagramSocket.setBroadcast(true);
            inputDatagramSocket.setSoTimeout(5000);

            outputDatagramSocket = new DatagramSocket();
            outputDatagramSocket.setReuseAddress(true);

            state = State.DISCOVERING;

            listeningWorker =
                    new Thread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        while (!Thread.interrupted()) {
                                            try {
                                                DatagramPacket packet = new DatagramPacket(new byte[buffLength], buffLength);

                                                inputDatagramSocket.receive(packet);

                                                if (state == State.DISCOVERING) {
                                                    FromClientMessage fromClientMessage =
                                                            new FromClientMessage(
                                                                    new DatagramPacketMessage(packet));

                                                    if (fromClientMessage.networkId().equals(networkId)) {

                                                        if (!devicesRep.contains(fromClientMessage.hostAddress())) {
                                                            outputDatagramSocket.send(
                                                                    new ConnectMessage(
                                                                            fromClientMessage.hostAddress(),
                                                                            new InetSocketAddress(
                                                                                    broadcastAddress,
                                                                                    outputBroadcastPort))
                                                                            .toDatagramPacket());
                                                            try {
                                                                final IDevice device =
                                                                        new Device(
                                                                                fromClientMessage.hostAddress(),
                                                                                new GangSocketConnectionWithClient()
                                                                                        .establish());

                                                                executor.execute(
                                                                        new PingPonger(
                                                                                device.sockets().get(SERVER_PING_PONG_PORT),
                                                                                5000,
                                                                                MESSAGE_PING,
                                                                                new PingPonger.IPingPongerCallback() {
                                                                                    @Override
                                                                                    public void onException(Exception e) {
                                                                                        new SafeClosableDevice(
                                                                                                devicesRep.delete(
                                                                                                        device.address()))
                                                                                                .closeConnections();
                                                                                    }
                                                                                }));
                                                                devicesRep.save(device);
                                                            } catch (IOException e) {
                                                                Log.e(TAG, "processConnectionChangedAction: ", e);
                                                            }
                                                        }
                                                    }
                                                }
                                            } catch (SocketTimeoutException ignored) {
                                                Log.d(TAG, "listeningWorker.run: SocketTimeoutException");
                                            }
                                        }
                                    } catch (Exception e) {
                                        Log.e(TAG, "startListening.run: ", e);
                                    }
                                }
                            });

            listeningWorker.start();

        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void enableNewConnections(boolean isEnabled) {
        state = isEnabled ? State.DISCOVERING : State.STOPPED;
    }

}
