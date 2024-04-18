package com.mcresurgence;

import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ServerMOTDConfig {

    private static Configuration serverConfig;
    private static String motd;

    public static void init(File configDir) {
        String serverConfigFilename = "resurgentEssentials.cfg";

        Path serverConfigPath = Paths.get(configDir.toString(), ResurgentEssentialsMod.MODID, serverConfigFilename);

        // Load Server Configuration
        serverConfig = new Configuration(serverConfigPath.toFile());
        loadServerConfig();
    }

    private static void loadServerConfig() {
        serverConfig.load();

        motd = serverConfig.getString("motd", Configuration.CATEGORY_GENERAL, "Welcome to the server!", "Message of the Day displayed to players upon joining.");

        if (serverConfig.hasChanged()) {
            serverConfig.save();
        }
    }

    public static String getMotd() {
        return motd;
    }
}
