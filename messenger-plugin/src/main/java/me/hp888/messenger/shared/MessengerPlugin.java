package me.hp888.messenger.shared;

import lombok.RequiredArgsConstructor;
import me.hp888.messenger.api.callback.Callback;
import me.hp888.messenger.api.client.Client;
import me.hp888.messenger.api.packet.Packet;
import me.hp888.messenger.api.packet.PacketHandler;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author hp888 on 09.11.2020.
 */

@RequiredArgsConstructor
public final class MessengerPlugin {

    private final Client client;
    private final Logger logger;

    /* packet sending */

    public void sendPacket(Packet packet) {
        try {
            client.sendPacket(packet);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Cannot send packet \"" + packet.getClass().getSimpleName() + "\"!", ex);
        }
    }

    public void sendPacket(Packet packet, Callback<?> callback) {
        try {
            client.sendPacket(packet, callback);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Cannot send packet \"" + packet.getClass().getSimpleName() + "\"!", ex);
        }
    }

    /* packet registering */

    public void unsubscribePackets() {
        client.unsubscribePackets();
    }

    public void subscribePackets(String... classNames) {
        client.subscribePackets(classNames);
    }

    /* packet handlers */

    public void addPacketHandler(PacketHandler handler) {
        client.addPacketHandler(handler);
    }

    public void removePacketHandler(Class<? extends PacketHandler> handlerClass) {
        client.removePacketHandler(handlerClass);
    }

    public void disconnect() throws IOException {
        client.disconnect();
    }

}