package me.hp888.messenger.api.packet;

import me.hp888.messenger.util.SerializeUtil;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;

/**
 * @author hp888 on 07.11.2020.
 */

public interface Packet extends Serializable {

    default byte[] serialize() throws IOException {
        final byte[] packetData = SerializeUtil.serializeObject(this);
        final byte[] className = getClass().getName().getBytes(StandardCharsets.UTF_8);

        final int bufferSize = className.length + packetData.length + 8; // two ints are stored in 8 bytes

        try (final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(bufferSize); final DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream)) {
            dataOutputStream.writeInt(className.length);
            dataOutputStream.write(className);

            dataOutputStream.writeInt(packetData.length);
            dataOutputStream.write(packetData);

            return byteArrayOutputStream.toByteArray();
        }
    }

    default void processPacket(PacketHandler handler) {}

}