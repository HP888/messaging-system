package me.hp888.messenger;

import lombok.Getter;
import lombok.Setter;
import me.hp888.messenger.client.Client;
import me.hp888.messenger.client.ClientManager;
import me.hp888.messenger.thread.PacketWriterThread;
import me.hp888.messenger.thread.ServerShutdownThread;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author hp888 on 07.11.2020.
 */

@Getter
@Setter
public final class MessengerServer {

    @Getter
    private static final MessengerServer instance = new MessengerServer();

    private final Logger logger = Logger.getLogger(MessengerServer.class.getSimpleName());
    private final ClientManager clientManager = new ClientManager(this);

    private boolean running;

    void start() {
        running = true;

        startThreads();
        startServer();
    }

    private void startThreads() {
        new PacketWriterThread(this).start();
        Runtime.getRuntime().addShutdownHook(new ServerShutdownThread(this));
    }

    private void startServer() {
        final ServerSocket serverSocket;

        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(System.getProperty("host", "127.0.0.1"), Integer.parseInt(System.getProperty("port", "9999"))));
            serverSocket.setSoTimeout(10000);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Cannot bind server on " + System.getProperty("host", "127.0.0.1") + ":" + System.getProperty("port", "9999") + ", probably used by other application?", ex);
            return;
        }

        logger.info("Server bound on " + serverSocket.getInetAddress().toString());

        while (!serverSocket.isClosed()) {
            final Socket socket;

            try {
                socket = serverSocket.accept();
            } catch (IOException ex) {
                if (!ex.getMessage().equals("Accept timed out")) {
                    continue;
                }

                logger.log(Level.SEVERE, "Cannot accept new connection!", ex);
                continue;
            }

            try {
                clientManager.addClient(new Client(socket));
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Cannot get I/O stream from recently accepted connection!");
            }

        }

    }

}