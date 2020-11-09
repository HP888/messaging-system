package me.hp888.messenger;

import lombok.Getter;
import me.hp888.messenger.api.callback.Callback;
import me.hp888.messenger.api.callback.CallbackManager;
import me.hp888.messenger.api.client.Client;
import me.hp888.messenger.api.packet.Packet;
import me.hp888.messenger.api.packet.PacketHandler;
import me.hp888.messenger.api.packet.PacketHandlerManager;
import me.hp888.messenger.connection.SocketConnection;
import me.hp888.messenger.packet.HeartbeatPacket;
import me.hp888.messenger.thread.PacketReaderThread;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * @author hp888 on 07.11.2020.
 */

public final class MessengerClient implements Client {

    private final PacketHandlerManager packetHandlers = new PacketHandlerManager();
    private final CallbackManager callbacks = new CallbackManager();

    @Getter
    private final ExecutorService packetHandleExecutor;

    public MessengerClient() {
        this(Runtime.getRuntime().availableProcessors() * 2);
    }

    public MessengerClient(int handlerPoolSize) {
        packetHandleExecutor = Executors.newFixedThreadPool(handlerPoolSize);
    }

    @Getter
    private SocketConnection connection;

    private boolean startedKeepAliveTask;

    @Override
    public void disconnect() throws IOException {
        if (Objects.isNull(connection) || !connection.isAlive()) {
            return;
        }

        connection.getSocket().close();
    }

    @Override
    public void connect(InetSocketAddress address) throws IOException {
        final Socket socket = new Socket();
        socket.setTcpNoDelay(true);
        socket.connect(address, 5000);

        connection = new SocketConnection(socket);
        new PacketReaderThread(this)
                .start();

        if (startedKeepAliveTask) {
            return;
        }

        startedKeepAliveTask = true;

        final Packet heartbeatPacket = new HeartbeatPacket();
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                sendPacket(heartbeatPacket);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }, 1L, 1L, TimeUnit.SECONDS);
    }

    @Override
    public void unsubscribePackets() {
        packetHandlers.unsubscribePackets();
    }

    @Override
    public void subscribePackets(String... classNames) {
        packetHandlers.subscribePackets(classNames);
    }

    @Override
    public boolean isPacketSubscribed(String name) {
        return packetHandlers.isPacketSubscribed(name);
    }

    @Override
    public Collection<PacketHandler> getPacketHandlers() {
        return packetHandlers.getPacketHandlers();
    }

    @Override
    public void addPacketHandler(PacketHandler handler) {
        packetHandlers.addPacketHandler(handler);
    }

    @Override
    public void removePacketHandler(Class<? extends PacketHandler> handlerClass) {
        packetHandlers.removePacketHandler(handlerClass);
    }

    @Override
    public Callback<?> getCallback(long id) {
        return callbacks.getCallback(id);
    }

    @Override
    public void sendPacket(Packet packet) throws IOException {
        if (!checkConnection()) {
            return;
        }

        connection.sendPacket(packet, null);
    }

    @Override
    public void sendPacket(Packet packet, Callback callback) throws IOException {
        if (!checkConnection()) {
            return;
        }

        connection.sendPacket(packet, callbacks.addCallback(callback));
    }

    private boolean checkConnection() {
        if (Objects.isNull(connection) || !connection.isAlive()) {
            Logger.getLogger(getClass().getSimpleName())
                    .warning("Cannot send packet when client is not connected!");

            return false;
        }

        return true;
    }

}