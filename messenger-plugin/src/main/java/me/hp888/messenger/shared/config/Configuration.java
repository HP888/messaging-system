package me.hp888.messenger.shared.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Data;
import lombok.Getter;
import me.hp888.messenger.shared.Messenger;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

/**
 * @author hp888 on 09.11.2020.
 */

@Getter
public final class Configuration {

    private String host;
    private Pool pool;
    private int port;

    public void load(File file) throws IOException {
        file.getParentFile().mkdirs();

        if (!file.exists()) {
            Files.copy(Messenger.class.getResourceAsStream("/config.json"), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        final JsonObject jsonObject = new JsonParser().parse(new FileReader(file))
                .getAsJsonObject();

        final JsonObject poolObject = jsonObject.getAsJsonObject("pool");
        if (Objects.isNull(poolObject)) { // Added because of config update.
            Files.delete(file.toPath());
            Files.copy(Messenger.class.getResourceAsStream("/config.json"), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        host = jsonObject.get("host").getAsString();
        port = jsonObject.get("port").getAsInt();

        pool = new Pool(poolObject.get("adjust-size-automatically").getAsBoolean(), poolObject.get("pool-size").getAsInt());
    }

    @Data
    public final class Pool {

        private final boolean adjustSizeAutomatically;
        private final int size;

    }

}