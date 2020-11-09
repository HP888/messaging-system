package me.hp888.messenger.util;

import org.nustaq.serialization.FSTConfiguration;

/**
 * @author hp888 on 07.11.2020.
 */

public final class SerializeUtil {

    private static final FSTConfiguration FAST_SERIALIZER = FSTConfiguration.createDefaultConfiguration();

    private SerializeUtil() {}

    public static byte[] serializeObject(Object object) {
        return FAST_SERIALIZER.asByteArray(object);
    }

    @SuppressWarnings("unchecked")
    public static <T> T deserializeObject(byte[] data) {
        return (T) FAST_SERIALIZER.asObject(data);
    }

}