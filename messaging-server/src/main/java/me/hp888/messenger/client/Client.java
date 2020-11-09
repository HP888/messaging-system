package me.hp888.messenger.client;

import lombok.Data;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author hp888 on 07.11.2020.
 */

@Data
public final class Client {

    private final Socket socket;

    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;

    public Client(Socket socket) throws IOException {
        this.socket = socket;
        this.inputStream = new DataInputStream(socket.getInputStream());
        this.outputStream = new DataOutputStream(socket.getOutputStream());
    }

    private final Queue<byte[]> outgoingPackets = new ConcurrentLinkedQueue<>();

    void sendPacket(byte[] bytes) throws IOException {
        final int bufferSize = bytes.length + 4; // single int value is stored in 4 bytes
        try (final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(bufferSize); final DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream)) {
            dataOutputStream.writeInt(bytes.length);
            dataOutputStream.write(bytes, 0, bytes.length);
            dataOutputStream.flush();

            outputStream.write(byteArrayOutputStream.toByteArray());
            outputStream.flush();
        }
    }

    public boolean isConnected() {
        return socket.isConnected() && !socket.isClosed();
    }

}