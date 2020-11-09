package me.hp888.messenger.client;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.hp888.messenger.MessengerServer;
import me.hp888.messenger.thread.PacketReaderThread;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

/**
 * @author hp888 on 07.11.2020.
 */

@Getter
@RequiredArgsConstructor
public final class ClientManager {

    private final MessengerServer server;
    private final Set<Client> clients = new HashSet<>();

    private final AtomicInteger threadCounter = new AtomicInteger();

    public void addClient(Client client) {
        clients.add(client);
        threadCounter.incrementAndGet();

        new PacketReaderThread(server, client)
                .start();
    }

    public void removeClient(Client client) {
        clients.remove(client);
        threadCounter.decrementAndGet();
    }

    public void sendPacket(byte[] packet) {
        clients.forEach(client -> {
            try {
                client.sendPacket(packet);
            } catch (IOException ex) {
                server.getLogger().log(Level.SEVERE, "Cannot send packet to client!", ex);
                removeClient(client);
            }
        });
    }

}