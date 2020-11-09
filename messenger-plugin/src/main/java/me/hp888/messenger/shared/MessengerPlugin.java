package me.hp888.messenger.shared;

import me.hp888.messenger.api.callback.Callback;
import me.hp888.messenger.api.packet.Packet;
import me.hp888.messenger.api.packet.PacketHandler;

/**
 * @author hp888 on 09.11.2020.
 */

public interface MessengerPlugin {

    /* packet sending */

    void sendPacket(Packet packet);

    void sendPacket(Packet packet, Callback<?> callback);

    /* packet registering */

    void unsubscribePackets();

    void subscribePackets(String... classNames);

    /* packet handlers */

    void addPacketHandler(PacketHandler handler);

    void removePacketHandler(Class<? extends PacketHandler> handlerClass);

}