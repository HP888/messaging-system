package me.hp888.messenger.connection;

import lombok.Getter;
import me.hp888.messenger.api.packet.CallbackPacket;
import me.hp888.messenger.api.packet.Packet;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Objects;

/**
 * @author hp888 on 08.11.2020.
 */

@Getter
public final class SocketConnection {

    private final Socket socket;

    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;

    public SocketConnection(Socket socket) throws IOException {
        this.socket = socket;
        this.inputStream = new DataInputStream(socket.getInputStream());
        this.outputStream = new DataOutputStream(socket.getOutputStream());
    }

    public void sendPacket(Packet packet, Long callbackId) throws IOException {
        if (packet instanceof CallbackPacket && Objects.nonNull(callbackId)) {
            ((CallbackPacket) packet).setCallbackId(callbackId);
        }

        final byte[] serializedPacket = packet.serialize();
        try (final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); final DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream)) {
            dataOutputStream.writeInt(serializedPacket.length);
            dataOutputStream.write(serializedPacket);
            dataOutputStream.flush();

            outputStream.write(byteArrayOutputStream.toByteArray());
            outputStream.flush();
        }
    }

    public boolean isAlive() {
        return socket.isConnected() && !socket.isClosed();
    }

}