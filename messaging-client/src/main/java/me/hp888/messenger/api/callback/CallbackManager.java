package me.hp888.messenger.api.callback;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author hp888 on 07.11.2020.
 */

public final class CallbackManager {

    private final Map<Long, Callback<?>> callbackMap = new HashMap<>();

    public long addCallback(Callback<?> callback) {
        long id;

        do {
            id = ThreadLocalRandom.current().nextLong(Long.MIN_VALUE, Long.MAX_VALUE);
        } while (callbackMap.containsKey(id));

        callbackMap.put(id, callback);
        return id;
    }

    public Callback<?> getCallback(long id) {
        return callbackMap.remove(id);
    }

}