package me.hp888.messenger.thread;

import me.hp888.messenger.api.callback.Callback;
import me.hp888.messenger.api.client.Client;
import me.hp888.messenger.api.packet.CallbackPacket;
import me.hp888.messenger.api.packet.Packet;
import me.hp888.messenger.connection.SocketConnection;
import me.hp888.messenger.util.SerializeUtil;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author hp888 on 08.11.2020.
 */

public final class PacketReaderThread extends Thread {

    private final Client client;

    public PacketReaderThread(Client client) {
        super("Messenger PacketReaderThread");
        this.client = client;
    }

    @Override
    public void run() {
        final SocketConnection connection = client.getConnection();
        final DataInputStream inputStream = connection.getInputStream();

        try {
            while (connection.isAlive()) {
                final int wholePacketSize = inputStream.readInt();
                final byte[] wholePacketData = new byte[wholePacketSize];
                inputStream.readFully(wholePacketData);

                final DataInputStream packetStream = new DataInputStream(new ByteArrayInputStream(wholePacketData));
                final int classNameLength = packetStream.readInt();
                final byte[] className = new byte[classNameLength];
                packetStream.readFully(className);

                final int packetSize = packetStream.readInt();
                if (!client.isPacketSubscribed(new String(className, StandardCharsets.UTF_8))) {
                    inputStream.skipBytes(packetSize);
                    continue;
                }

                final byte[] packetData = new byte[packetSize];
                packetStream.readFully(packetData);

                if (packetData.length == 0) {
                    continue;
                }

                client.getPacketHandleExecutor().execute(() -> {
                    final Packet packet = SerializeUtil.deserializeObject(packetData);
                    if (handleCallback(packet)) {
                        return;
                    }

                    client.getPacketHandlers()
                            .forEach(packet::processPacket);
                });
            }
        } catch (IOException ex) {
            System.out.println("[INFO] Disconnected from messenger! Cause: " + ex.toString());
            ex.printStackTrace();
        }

        throw new ThreadDeath();
    }

    @SuppressWarnings("unchecked")
    private boolean handleCallback(Packet packet) {
        if (packet instanceof CallbackPacket) {
            final CallbackPacket callbackPacket = (CallbackPacket) packet;
            if (callbackPacket.getCallbackId() != 0L && callbackPacket.isResponse()) {
                final Callback callback = client.getCallback(callbackPacket.getCallbackId());
                if (Objects.nonNull(callback)) {
                    if (callbackPacket.isSuccess()) {
                        callback.done(callbackPacket);
                    } else {
                        callback.error(callbackPacket.getErrorMessage());
                    }
                }

                return true;
            }
        }

        return false;
    }

}