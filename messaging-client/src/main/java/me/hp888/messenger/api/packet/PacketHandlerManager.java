package me.hp888.messenger.api.packet;

import java.util.*;

/**
 * @author hp888 on 07.11.2020.
 */

public final class PacketHandlerManager {

    private final Set<String> subscribedPackets = new HashSet<>();
    private final Set<PacketHandler> handlers = new HashSet<>();

    public void subscribePackets(String... classNames) {
        subscribedPackets.addAll(Arrays.asList(classNames));
    }

    public boolean isPacketSubscribed(String className) {
        return subscribedPackets.contains(className);
    }

    public void unsubscribePackets() {
        subscribedPackets.clear();
    }

    public Collection<PacketHandler> getPacketHandlers() {
        return handlers;
    }

    public void removePacketHandler(Class<? extends PacketHandler> handlerClass) {
        handlers.removeIf(handler -> handler.getClass().equals(handlerClass));
    }

    public void addPacketHandler(PacketHandler handler) {
        handlers.add(handler);
    }

}