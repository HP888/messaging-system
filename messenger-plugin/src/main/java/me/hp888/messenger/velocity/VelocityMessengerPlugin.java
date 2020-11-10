package me.hp888.messenger.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import me.hp888.messenger.MessengerClient;
import me.hp888.messenger.api.client.Client;
import me.hp888.messenger.shared.Messenger;
import me.hp888.messenger.shared.MessengerPlugin;
import me.hp888.messenger.shared.config.Configuration;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author hp888 on 10.11.2020.
 */

@Plugin(id = "messenger",
        name = "MessengerPlugin",
        authors = "HP888",
        version = "1.0"
)
public final class VelocityMessengerPlugin {

    private final Configuration configuration;
    private final ProxyServer server;
    private final Path dataFolder;
    private final Logger logger;

    @Inject
    public VelocityMessengerPlugin(ProxyServer server, Logger logger, @DataDirectory Path dataFolder) {
        this.server = server;
        this.logger = logger;
        this.dataFolder = dataFolder;
        this.configuration = new Configuration();
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        try {
            configuration.load(new File(dataFolder.toFile(), "config.json"));
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Cannot load configuration!", ex);
            return;
        }

        final Configuration.Pool pool = configuration.getPool();
        final Client client;

        if (pool.isAdjustSizeAutomatically()) {
            client = new MessengerClient();
        } else {
            client = new MessengerClient(Math.max(2, pool.getSize()));
        }

        try {
            client.connect(new InetSocketAddress(configuration.getHost(), configuration.getPort()));
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Cannot connect to remote server!", ex);
            return;
        }

        Messenger.setInstance(new MessengerPlugin(client, logger));
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        final MessengerPlugin messenger = Messenger.getInstance();
        if (Objects.isNull(messenger)) {
            return;
        }

        try {
            messenger.disconnect();
        } catch (IOException ignored) {}
    }

}