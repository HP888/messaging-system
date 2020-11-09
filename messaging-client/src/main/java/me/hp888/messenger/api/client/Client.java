package me.hp888.messenger.api.client;

import me.hp888.messenger.api.callback.Callback;
import me.hp888.messenger.api.packet.Packet;
import me.hp888.messenger.api.packet.PacketHandler;
import me.hp888.messenger.connection.SocketConnection;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.concurrent.ExecutorService;

/**
 * @author hp888 on 07.11.2020.
 */

public interface Client {

    SocketConnection getConnection();

    ExecutorService getPacketHandleExecutor();

    /* connection */

    void disconnect() throws IOException;

    void connect(InetSocketAddress address) throws IOException;

    /* packet subscription */

    void unsubscribePackets();

    void subscribePackets(String... classNames);

    boolean isPacketSubscribed(String className);

    /* packet handlers */

    Collection<PacketHandler> getPacketHandlers();

    void addPacketHandler(PacketHandler handler);

    void removePacketHandler(Class<? extends PacketHandler> handlerClass);

    /* callbacks */

    Callback<?> getCallback(long id);

    /* sending packets */

    void sendPacket(Packet packet) throws IOException;

    void sendPacket(Packet packet, Callback callback) throws IOException;

}