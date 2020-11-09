package me.hp888.messenger.thread;

import me.hp888.messenger.MessengerServer;
import me.hp888.messenger.client.Client;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.logging.Level;

/**
 * @author hp888 on 07.11.2020.
 */

public final class PacketReaderThread extends Thread {

    private final MessengerServer server;
    private final Client client;

    public PacketReaderThread(MessengerServer server, Client client) {
        super("Messenger Packet Reader Thread #" + server.getClientManager().getThreadCounter().get());
        this.server = server;
        this.client = client;
    }

    @Override
    public void run() {
        final DataInputStream inputStream = client.getInputStream();

        try {
            while (client.isConnected()) {
                final byte[] bytes = new byte[inputStream.readInt()];
                inputStream.readFully(bytes);

                client.getOutgoingPackets().add(bytes);
            }
        } catch (IOException ex) {
            if (!ex.getMessage().equals("Connection reset")) {
                server.getLogger().log(Level.SEVERE, "Cannot read packet!", ex);
            }

            server.getClientManager().removeClient(client);
        }
    }

}