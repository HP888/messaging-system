package me.hp888.messenger.thread;

import lombok.RequiredArgsConstructor;
import me.hp888.messenger.MessengerServer;

/**
 * @author hp888 on 09.11.2020.
 */

@RequiredArgsConstructor
public final class ServerShutdownThread extends Thread {

    private final MessengerServer server;

    @Override
    public void run() {
        server.getLogger().info("Shutting down server...");
        server.setRunning(false);
    }

}