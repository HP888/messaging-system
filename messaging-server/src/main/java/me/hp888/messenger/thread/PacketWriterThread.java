package me.hp888.messenger.thread;

import me.hp888.messenger.MessengerServer;
import me.hp888.messenger.util.ThreadUtil;

/**
 * @author hp888 on 07.11.2020.
 */

public final class PacketWriterThread extends Thread {

    private final MessengerServer server;

    public PacketWriterThread(MessengerServer server) {
        super("Messenger Packet Writer Thread");
        this.server = server;
    }

    @Override
    public void run() {
        while (server.isRunning()) {
            server.getClientManager().getClients().forEach(client -> {
                while (!client.getOutgoingPackets().isEmpty()) {
                    server.getClientManager().sendPacket(client.getOutgoingPackets().poll());
                }
            });

            ThreadUtil.sleep(1L);
        }
    }

}