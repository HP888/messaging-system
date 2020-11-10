package me.hp888.messenger.bungee;

import me.hp888.messenger.MessengerClient;
import me.hp888.messenger.api.client.Client;
import me.hp888.messenger.shared.Messenger;
import me.hp888.messenger.shared.MessengerPlugin;
import me.hp888.messenger.shared.config.Configuration;
import net.md_5.bungee.api.plugin.Plugin;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.logging.Level;

/**
 * @author hp888 on 09.11.2020.
 */

public final class BungeeMessengerPlugin extends Plugin {

    private final Configuration configuration = new Configuration();

    @Override
    public void onEnable() {
        try {
            configuration.load(new File(getDataFolder(), "config.json"));
        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, "Cannot load configuration!", ex);
            getProxy().stop();
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
            getLogger().log(Level.SEVERE, "Cannot connect to remote server!", ex);
            getProxy().stop();
            return;
        }

        Messenger.setInstance(new MessengerPlugin(client, getLogger()));

    }

    @Override
    public void onDisable() {
        final MessengerPlugin messenger = Messenger.getInstance();
        if (Objects.isNull(messenger)) {
            return;
        }

        try {
            messenger.disconnect();
        } catch (IOException ignored) {}
    }

}