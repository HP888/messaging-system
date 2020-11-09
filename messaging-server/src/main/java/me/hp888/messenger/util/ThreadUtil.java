package me.hp888.messenger.util;

import me.hp888.messenger.MessengerServer;
import java.util.logging.Level;

/**
 * @author hp888 on 07.11.2020.
 */

public final class ThreadUtil {

    private ThreadUtil() {}

    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
            MessengerServer.getInstance().getLogger().log(Level.SEVERE, "Cannot interrupt thread " + Thread.currentThread().getName() + "!", ex);
            Thread.currentThread().stop();
        }
    }

}