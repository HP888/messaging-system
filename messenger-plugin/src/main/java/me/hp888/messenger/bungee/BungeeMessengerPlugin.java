package me.hp888.messenger.bungee;

import me.hp888.messenger.MessengerClient;
import me.hp888.messenger.api.callback.Callback;
import me.hp888.messenger.api.client.Client;
import me.hp888.messenger.api.packet.Packet;
import me.hp888.messenger.api.packet.PacketHandler;
import me.hp888.messenger.shared.Messenger;
import me.hp888.messenger.shared.MessengerPlugin;
import me.hp888.messenger.shared.config.Configuration;
import net.md_5.bungee.api.plugin.Plugin;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.logging.Level;

/**
 * @author hp888 on 09.11.2020.
 */

public final class BungeeMessengerPlugin extends Plugin implements MessengerPlugin {

    private final Configuration configuration = new Configuration();

    private Client messengerClient;

    @Override
    public void onEnable() {
        try {
            configuration.load(new File(getDataFolder(), "config.json"));
        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, "Cannot load configuration!", ex);
            getProxy().stop();
            return;
        }

        Messenger.setInstance(this);

        messengerClient = new MessengerClient();

        try {
            messengerClient.connect(new InetSocketAddress(configuration.getHost(), configuration.getPort()));
        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, "Cannot connect to remote server!", ex);
            getProxy().stop();
        }

    }

    @Override
    public void onDisable() {
        if (Objects.isNull(messengerClient)) {
            return;
        }

        try {
            messengerClient.disconnect();
        } catch (IOException ignored) {}
    }

    @Override
    public void sendPacket(Packet packet) {
        try {
            messengerClient.sendPacket(packet);
        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, "Cannot send packet \"" + packet.getClass().getSimpleName() + "\"!", ex);
        }
    }

    @Override
    public void sendPacket(Packet packet, Callback<?> callback) {
        try {
            messengerClient.sendPacket(packet, callback);
        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, "Cannot send packet \"" + packet.getClass().getSimpleName() + "\"!", ex);
        }
    }

    @Override
    public void unsubscribePackets() {
        messengerClient.unsubscribePackets();
    }

    @Override
    public void subscribePackets(String... classNames) {
        messengerClient.subscribePackets(classNames);
    }

    @Override
    public void addPacketHandler(PacketHandler handler) {
        messengerClient.addPacketHandler(handler);
    }

    @Override
    public void removePacketHandler(Class<? extends PacketHandler> handlerClass) {
        messengerClient.removePacketHandler(handlerClass);
    }

}